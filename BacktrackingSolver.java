/**
 * BacktrackingSolver.java
 *
 * @author Logan Pipes
 * @date 07-11-2022
 *
 * A subclass of SudokuSolver.java that solves Sudoku puzzles via a naive backtracking approach.
 */

public class BacktrackingSolver extends SudokuSolver {
	// Performs a recrusive backtracking algorithm to solve the passed Sudoku puzzle,
	// returning a solved Sudoku object if one exists, or null if no such puzzle exists.
	public Sudoku solve(Sudoku s) throws InstantiationException {
		int[][] board = s.getBoard();
		boolean isSolvable = recurse(board, 0, 0);
		if (isSolvable) return new Sudoku(board); // board holds the solution, so create a new Sudoku with the solution to return
		return null; // Otherwise it's unsolvable
	}

	// Returns an array of integers that can be entered into cell (i,j) without violating any of the rules of sudoku.
	private int[] getPossibleNumbersForIndex(int[][] board, int i, int j) {
		int n = board.length;
		int m = (int)Math.sqrt(n);

		// Mark off the values of entries in the same row/col as (i,j)
		boolean[] disallowed = new boolean[n+1];
		for (int l = 0; l < n; l++) {
			if (l != j) disallowed[board[i][l]] = true; // check all rows
			if (l != i) disallowed[board[l][j]] = true; // check all cols
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

	// The recrusive backtracking method of the solver.
	// If the board can be solved in the given state, returns true and leaves the board in a solved state.
	// If the board cannot be solved in the current state, returns false and leaves the board as it was.
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
