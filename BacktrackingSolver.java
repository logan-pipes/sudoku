/*
 * BacktrackingSolver.java
 *
 * @author Logan Pipes
 * @date 11-12-2022
 *
 * A subclass of SudokuSolver.java that solves Sudoku puzzles via a naive backtracking approach.
 */

public class BacktrackingSolver extends SudokuSolver {
	/**
	 * Return a solved instance of the specified Sudoku puzzle computed via a recursive backtracking algorithm.
	 *
	 * @return							a solved Sudoku object (that is, one whose <code>isSolved</code> method returns <code>true</code>)
	 * 									or <code>s</code> if no such solution exists
	 * @throws	InstantiationException	if an error occurs when instantiating the solution Sudoku
	 */
	@Override
	public Sudoku solve(Sudoku s) throws InstantiationException {
		int[][] board = s.getBoard();
		boolean isSolvable = recurse(board, 0, 0);
		if (isSolvable) return new Sudoku(board); // board holds the solution, so create a new Sudoku with the solution to return
		return s; // Otherwise it's unsolvable
	}


	/**
	 * Lists the numbers that are permissible for the specified cell.
	 *
	 * @param	board	the sudoku board as a 2d integer array
	 * @param	i		the row index of the cell
	 * @param	j		the column index of the cell
	 * @return			a (potentially empty) array of integers
	 * 					that wouldn't violate the Sudoku constraint if placed in the given cell
	 */
	private int[] getPossibleNumbersForIndex(int[][] board, int i, int j) {
		int n = board.length;
		int m = (int)Math.sqrt(n);
		if (m*m != n) m++; // Account for potential floating point rounding error

		// Mark off the values of entries in the same row/col as (i,j)
		boolean[] disallowed = new boolean[n+1];
		for (int e = 0; e < n; e++) {
			if (e != j) disallowed[board[i][e]] = true; // check all rows
			if (e != i) disallowed[board[e][j]] = true; // check all cols
		}

		// Mark  off the value of entries in the same sub-board as (i,j)
		int y = i - (i%m);
		int x = j - (j%m);
		for (int di = 0; di < m; di++) {
			for (int dj = 0; dj < m; dj++) { // check all entries in the sub-board
				if (y+di != i && x+dj != j) disallowed[board[y+di][x+dj]] = true;
			}
		}

		int numAvailable = 0;
		for (int l = 1; l <= n; l++) if (!disallowed[l]) numAvailable++; // Count the remaining allowed values
		// Record the remaining allowed values
		int[] available = new int[numAvailable];
		int index = 0;
		for (int l = 1; l <= n; l++) if (!disallowed[l]) available[index++] = l;
		return available;
	}


	/**
	 * Recursively attempts to complete the sudoku board, returning <code>true</code> if possible and <code>false</code> otherwise.
	 * <p>
	 * If the board cannot be solved from the given state, when the method returns,
	 * <code>board</code> remains in the same state as when the method was called.
	 * However, if the board can be solved from the given state,
	 * <code>board</code> is filled with the lexicographically smallest solution when the method concludes.
	 *
	 * @param	board	the sudoku board to be completed
	 * @param	i		the row index of a cell the algorithm starts on
	 * @param	j		the column index of a cell the algorithm starts on
	 * @return			<code>true</code> if the board is solvable from the given state;
	 * 					<code>false</code> otherwise
	 */
	private boolean recurse(int[][] board, int i, int j) {
		int n = board.length;
		if (j >= n) { j=0; i++; } // If the column index is too large, move to the start of the next row
		if (i >= n) return true; // If the row index is too large, the puzzle must be solved
		while (board[i][j] != Sudoku.UNKNOWN) { // Look for next empty cell
			if (++j >= n) { j=0; i++; } // Check for out-of-bounds again
			if (i >= n) return true;
		} // Now board[i][j] = Sudoku.UNKNOWN

		int[] options = getPossibleNumbersForIndex(board, i, j);
		for (int option : options) { // For all options,
			board[i][j] = option; // if that option yields a solved puzzle,
			if (recurse(board, i, j+1)) return true; // return true without erasing that option
		}
		board[i][j] = Sudoku.UNKNOWN; // otherwise no options yield a solved puzzle, so erase the option
		return false; // and return false
	}
}
