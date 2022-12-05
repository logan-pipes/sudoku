/**
 * DLXSolver.java
 *
 * @author Logan Pipes
 * @date 04-12-2022
 *
 * A subclass of SudokuSolver.java that solves Sudoku puzzles via Knuth's DLX algorithm.
 */

public class DLXSolver extends SudokuSolver {
	private static final String[] CONSTRAINTS = {"R", "C", "SB"};

	public Sudoku solve(Sudoku s) throws InstantiationException {
		int[][] board = s.getBoard();
		ColumnNode head = createMatrix(board);
		LLNode[] soln = new LLNode[1000];
		boolean solved = search(0, head, soln);
		System.out.println(solved);
		if (solved) {
			int index = 0;
			while (index < 1000 && soln[index] != null) {
				LLNode cur = soln[index].getRight();
				while (cur != soln[index]) {
					System.out.print(cur.getCol());
					System.out.print(' ');
					cur = cur.getRight();
				}
				System.out.println(soln[index].getCol());
				index++;
			}
		}


		return null;
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
		int boardLength = sudokuBoard.length; // Get the length of the board
		Constraint[] constraintTypes = {Constraint.ROW, Constraint.COLUMN, Constraint.SUBBOARD}; // this literal statement replaces Constraint.values() -- TODO Ask someone about this
		ColumnNode[][][] columns = new ColumnNode[boardLength][constraintTypes.length][boardLength]; // Store the columns

		ColumnNode head = new ColumnNode(-1, null, -1); // An initial node for passing the instance around
		ColumnNode prev = head; // For constructing new nodes

		for (int constraintTypeIndex = 0; constraintTypeIndex < constraintTypes.length; constraintTypeIndex++) { // For every constraint type
			for (int digit = 1; digit <= boardLength; digit++) { // For every digit
				for (int index = 0; index < boardLength; index++) { // For every place the constraint of the given type and digit can affect
					columns[digit-1][constraintTypeIndex][index] = new ColumnNode(digit, constraintTypes[constraintTypeIndex], index, prev); // Create a new ColumnNode representing that constraint, linked to the previous constraint
					prev = columns[digit-1][constraintTypeIndex][index]; // Store a reference to the newly created one to be used when creating more
				}
			}
		}

		int subBoardLength = (int)Math.sqrt(boardLength); // Get the size of the subboard
		if (subBoardLength*subBoardLength != boardLength) subBoardLength++; // Account for potential floating point rounding error

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

		for (int row = 0; row < boardLength; row++) { // For every row
			for (int col = 0; col < boardLength; col++) { // For every column
				for (int digit = 1; digit <= boardLength; digit++) { // For every digit
					// If placing this digit here doesn't violate the sudoku constraint (i.e. if it's permissible to put this digit here)
					if (!inRow[row][digit] && !inCol[col][digit] && !inSubBoard[col/subBoardLength + subBoardLength*(row/subBoardLength)][digit]) {
						// Set up the three DataNodes under their respective ColumnNodes and in their own row to represent placing this digit in this position
						DataNode      rowDataNode = new DataNode(columns[digit-1][0][row]);
						DataNode      colDataNode = new DataNode(columns[digit-1][1][col], rowDataNode);
						DataNode subBoardDataNode = new DataNode(columns[digit-1][2][col/subBoardLength + subBoardLength*(row/subBoardLength)], colDataNode);
					}
				}
			}
		}

		// Now the board should be ready for solving
		return head;
	}

	public static void main(String[] args) throws InstantiationException {
		int[][] sudokuBoard = new int[4][4];
		//sudokuBoard[0][0] = 1;
		//sudokuBoard[0][1] = 2;
		//sudokuBoard[0][2] = 3;
		//sudokuBoard[0][3] = 4;

		//sudokuBoard[1][1] = 3;
		//sudokuBoard[3][3] = 2;
		ColumnNode root = createMatrix(sudokuBoard);

		//ColumnNode cur = root.getRight();
		//while (cur != root) {
		//	if (cur.getSize() != 0) System.out.println(cur);
		//	cur = cur.getRight();
		//}

		Sudoku s = new Sudoku(sudokuBoard);
		(new DLXSolver()).solve(s);
	}
}
