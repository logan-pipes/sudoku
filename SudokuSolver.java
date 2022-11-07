/**
 * SudokuSolver.java
 *
 * @author Logan Pipes
 * @date 07-11-2022
 * 
 * An abstract class specifying that sudoku solvers must implement a solve method.
 */

public abstract class SudokuSolver {
	// Returns a solved Sudoku puzzle if the passed Sudoku is solvable,
	// and returns null if the given Sudoku is not solvable.
	public abstract Sudoku solve(Sudoku s);
}
