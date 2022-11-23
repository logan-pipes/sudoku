/**
 * TestSudoku.java
 *
 * @author Logan Pipes
 * @date 23-11-2022
 *
 */

import java.io.*;

public class TestSudoku {
	public static void main(String[] args) throws InstantiationException {
		System.out.println("Constructing new File at path" + args[0]);
		File f = new File(args[0]);

		System.out.println("Constructing new Sudoku from File");
		Sudoku raw = new Sudoku(f);

		System.out.println("Sudoku is parsed as follows:");
		System.out.println();
		System.out.println(raw);
		System.out.println();

		System.out.println("Constructing new SudokuSolver of subclass BackTrackingSolver");
		SudokuSolver ss = new BacktrackingSolver();

		System.out.println("Solving passed Sudoku");
		Sudoku solved = ss.solve(raw);

		if (solved.isSolved()) {
			System.out.println("Sudoku solved successfully");
			System.out.println();
			System.out.println(solved);
		} else {
			System.out.println("Sudoku not solved successfully");
			System.out.println();
			System.out.println(solved);
		}
	}
}
