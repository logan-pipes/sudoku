/**
 * TestSudoku.java
 *
 * @author Logan Pipes
 * @date 23-11-2022
 *
 */

import java.io.*;

public class TestSudoku {
	/*
	 * Note that this is called with a file name as a command line argument,
	 * and will not read the contents of System.in to construct a Sudoku.
	 */
	public static void main(String[] args) throws InstantiationException {
		System.out.println("Constructing new File at path" + args[0]);
		File f = new File(args[0]);

		System.out.println("Constructing new Sudoku from File");
		Sudoku raw = new Sudoku(f);

		System.out.println("Sudoku is parsed as follows:");
		System.out.println();
		System.out.println(raw);
		System.out.println();

		System.out.println("Constructing new SudokuSolver of subclass BacktrackingSolver");
		SudokuSolver bts = new BacktrackingSolver();
		System.out.println("Constructing new SudokuSolver of subclass DLXSolver");
		SudokuSolver dlxs = new DLXSolver();

		System.out.println("Solving passed Sudoku via DLX...");
		long startDLX = System.currentTimeMillis();
		Sudoku dlxSolved = dlxs.solve(raw);
		long endDLX = System.currentTimeMillis();
		System.out.println();
		if (dlxSolved.isSolved()) {
			System.out.println("DLXSolver solved Sudoku successfully in " + (endDLX - startDLX) + " milliseconds:");
			System.out.println();
			System.out.println(dlxSolved);
		} else {
			System.out.println("DLXSolver did not solve Sudoku successfully after " + (endDLX - startDLX) + " milliseconds. Attempt:");
			System.out.println();
			System.out.println(dlxSolved);
		}
		System.out.println();


		System.out.println("Solving passed Sudoku via Backtracking...");
		long startBacktracking = System.currentTimeMillis();
		Sudoku btSolved = bts.solve(raw);
		long endBacktracking = System.currentTimeMillis();
		System.out.println();
		if (btSolved.isSolved()) {
			System.out.println("BacktrackingSolver solved Sudoku successfully in " + (endBacktracking - startBacktracking) + " milliseconds:");
			System.out.println();
			System.out.println(btSolved);
		} else {
			System.out.println("BacktrackingSolver did not solve Sudoku successfully after " + (endBacktracking - startBacktracking) + " milliseconds. Attempts:");
			System.out.println();
			System.out.println(btSolved);
		}
	}
}
