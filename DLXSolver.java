/**
 * DLXSolver.java
 *
 * @author Logan Pipes
 * @date 05-12-2022
 *
 * A subclass of SudokuSolver.java that solves Sudoku puzzles via Knuth's DLX algorithm.
 */

public class DLXSolver extends SudokuSolver {
	public Sudoku solve(Sudoku s) throws InstantiationException {
		int[][] board = s.getBoard(); // Get the board in a more usable form
		int numEmptyCells = 0; // Count the number of cells to be filled in
		for (int row = 0; row < board.length; row++)
			for (int col = 0; col < board[0].length; col++)
				if (board[row][col] == Sudoku.UNKNOWN) numEmptyCells++;

		LLNode[] solution = new LLNode[numEmptyCells]; // Create an array to hold the colution
		ColumnNode head = createMatrix(board); // Create the exact cover formulation of the board
		boolean solved = search(0, head, solution); // Solve the puzzle recursively (if possible)
		// If a solution existed, solved is true and parts of the correct assignment are stored in solution
		if (!solved) return null;

		for (int index = 0; index < numEmptyCells; index++) {
			LLNode cur = solution[index];
			// cur is a DataNode corresponding to the satisfaction of one of the constraints via filling in a cell
			// All the nodes in the same row as cur correspond to satisfying different constraints by filling in the same cell,
			// so scan right until cur corresponds to satisfying the cell constraint (so the coordinates of the cell are accessible)
			while (cur.getCol().getConstraint().getType() != Constraint.ConstraintType.CELL) cur = cur.getRight();
			int[] coordinates = cur.getCol().getConstraint().getIdentifiers();
			cur = cur.getRight(); // Move to any of the other DataNodes
			// cur now corresponds to a ROW, COLUMN, or SUBBOARD ConstraintType, whose 0th identifier is the digit filled in
			int digit = cur.getCol().getConstraint().getIdentifiers()[0];
			board[coordinates[0]][coordinates[1]] = digit; // Fill in the cell in the board
		}
		return new Sudoku(board);
	}


	// initially invoked on k=0
	private boolean search(int k, ColumnNode root, LLNode[] solution) {
		if (root.getRight() == root) { // No more columns to cover,
			return true; // O is a solution!
		}

		// Find the column with the fewest options
		ColumnNode toCover = root.getRight(); // Assume the first one is smallest until otherwise found
		int minSize = toCover.getSize();
		ColumnNode cur = root.getRight(); // (to iterate with)
		while (cur != root) { // For each column still needing covered
			if (cur.getSize() < minSize) { // If this column has the least options so far
				toCover = cur; // Then it's the column worth covering
				minSize = cur.getSize();
			}
			cur = cur.getRight(); // Try the next column
		}

		toCover.cover(); // Cover the column
		LLNode row = toCover.getDown(); // Starting with the top row covering toCover
		while (row != toCover) { // For each row that covers the column
			solution[k] = row; // Try covering the column with this row

			LLNode inRow = row.getRight(); // Starting with the node to the right of row
			while (inRow != row) { // For all nodes in the row (except row itself)
				inRow.getCol().cover(); // Mark off each other column covered by this row
				inRow = inRow.getRight(); // Move to the next node in the row
			}

			if (search(k+1, root, solution)) return true; // If this leads to a solution, great, pass it on!

			// Otherwise, it didn't lead to a solution, so undo and try another row
			inRow = row.getLeft(); // Starting with the node to the left of row
			while (inRow != row) { // For all nodes in the row (except row itself)
				inRow.getCol().uncover(); // Unmark this column, it's no longer covered by row
				inRow = inRow.getLeft(); // Move on to next node in row
			}

			row = row.getDown(); // Move to the next row that covers the column toCover (i.e. try another row)
		}
		toCover.uncover(); // No solution found here...
		return false;
	}



	// returns root
	private static ColumnNode createMatrix(int[][] sudokuBoard) {
		int boardLength = sudokuBoard.length; // Get the size of the board
		int subBoardLength = (int)Math.sqrt(boardLength); // Get the size of the sub-board
		if (subBoardLength*subBoardLength != boardLength) subBoardLength++; // Account for potential floating point rounding error

		// Precompute some things for efficiency
		boolean[][] inRow = new boolean[boardLength][boardLength+1]; // Whether or not a given row has a given digit
		boolean[][] inCol = new boolean[boardLength][boardLength+1]; // Whether or not a given column has a given digit
		boolean[][] inSubBoard = new boolean[boardLength][boardLength+1]; // Whether or not a given sub-board has a given digit

		for (int row = 0; row < boardLength; row++) { // For every row
			for (int col = 0; col < boardLength; col++) { // For every column
				if (sudokuBoard[row][col] != Sudoku.UNKNOWN) { // If the entry is filled in already
					inRow[row][sudokuBoard[row][col]] = true; // That digit is already satisfied in the row
					inCol[col][sudokuBoard[row][col]] = true; // That digit is already satisfied in the col
					inSubBoard[col/subBoardLength + subBoardLength*(row/subBoardLength)][sudokuBoard[row][col]] = true; // That digit is already satisfied in the sub-board
				}
			}
		}


		ColumnNode[][] rowConstraints      = new ColumnNode[boardLength][boardLength]; // Store the row constraints
		ColumnNode[][] colConstraints      = new ColumnNode[boardLength][boardLength]; // Store the column constraints
		ColumnNode[][] subBoardConstraints = new ColumnNode[boardLength][boardLength]; // Store the sub-board constraints
		ColumnNode[][] cellConstraints     = new ColumnNode[boardLength][boardLength]; // Store the cell constraints

		ColumnNode head = new ColumnNode(new Constraint(null, -1, -1)); // An initial node for passing the problem instance around
		ColumnNode prev = head; // A reference to construct new nodes from

		// Construct all of the ColumnNodes representing each type of constraint:
		// Row:
		for (int digit = 1; digit <= boardLength; digit++) { // For every digit
			for (int row = 0; row < boardLength; row++) { // For every place the constraint can affect
				if (!inRow[row][digit]) { // If this constraint is not satisfied by default
					// Create a new ColumnNode representing that constraint, linked to the previous constraint
					rowConstraints[digit-1][row] = new ColumnNode(new Constraint(Constraint.ConstraintType.ROW, digit, row), prev);
					prev = rowConstraints[digit-1][row]; // Store a reference to the newly created one to be used when creating more
				}
			}
		}
		// Column:
		for (int digit = 1; digit <= boardLength; digit++) {
			for (int col = 0; col < boardLength; col++) {
				if (!inCol[col][digit]) {
					colConstraints[digit-1][col] = new ColumnNode(new Constraint(Constraint.ConstraintType.COLUMN, digit, col), prev);
					prev = colConstraints[digit-1][col];
				}
			}
		}
		// Sub-board:
		for (int digit = 1; digit <= boardLength; digit++) {
			for (int subBoard = 0; subBoard < boardLength; subBoard++) {
				if (!inSubBoard[subBoard][digit]) {
					subBoardConstraints[digit-1][subBoard] = new ColumnNode(new Constraint(Constraint.ConstraintType.SUBBOARD, digit, subBoard), prev);
					prev = subBoardConstraints[digit-1][subBoard];
				}
			}
		}
		// Cell:
		for (int row = 0; row < boardLength; row++) {
			for (int col = 0; col < boardLength; col++) {
				if (sudokuBoard[row][col] == Sudoku.UNKNOWN) {
					cellConstraints[row][col] = new ColumnNode(new Constraint(Constraint.ConstraintType.CELL, row, col), prev);
					prev = cellConstraints[row][col];
				}
			}
		}


		// Construct all the data nodes representing ways to satisfy the constraints
		for (int row = 0; row < boardLength; row++) { // For every row
			for (int col = 0; col < boardLength; col++) { // For every column
				if (sudokuBoard[row][col] != Sudoku.UNKNOWN) continue; // Can't place a number here if it's already filled
				for (int digit = 1; digit <= boardLength; digit++) { // For every digit
					// If placing this digit here doesn't violate the sudoku constraint (i.e. if it's permissible to put this digit here)
					if (!inRow[row][digit] && !inCol[col][digit] && !inSubBoard[col/subBoardLength + subBoardLength*(row/subBoardLength)][digit]) {
						// Set up the four DataNodes under their respective ColumnNodes and in their own row to represent placing this digit in this position
						DataNode      rowDataNode = new DataNode(     rowConstraints[digit-1][row]);
						DataNode      colDataNode = new DataNode(     colConstraints[digit-1][col], rowDataNode);
						DataNode subBoardDataNode = new DataNode(subBoardConstraints[digit-1][col/subBoardLength + subBoardLength*(row/subBoardLength)], colDataNode);
						DataNode     cellDataNode = new DataNode(    cellConstraints[row][col], subBoardDataNode);
					}
				}
			}
		}

		// Now the board should be ready for solving
		return head;
	}
}
