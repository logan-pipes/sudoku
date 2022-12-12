/*
 * SudokuSolver.java
 *
 * @author Logan Pipes
 * @date 11-12-2022
 * 
 * An abstract class specifying that sudoku solvers must implement a solve method.
 */

public abstract class SudokuSolver {
	/**
	 * Return a solved instance of the specified Sudoku puzzle.
	 *
	 * @return							a solved Sudoku object (that is, one whose <code>isSolved</code> method returns <code>true</code>)
	 * 									or null if no such solution exists
	 * @throws	InstantiationException	if an error occurs when instantiating the solution Sudoku
	 */
	public abstract Sudoku solve(Sudoku s) throws InstantiationException;
}
