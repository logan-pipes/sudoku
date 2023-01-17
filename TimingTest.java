/*
 * TimingTest.java
 *
 * @author Logan Pipes
 * @date 12-12-2022
 *
 * Evaluates performance of various SudokuSolvers against each other.
 */

import java.util.*;
import java.io.*;

public class TimingTest {
	public static void main(String[] args) throws IOException, InstantiationException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		ArrayList<Sudoku> puzzles = new ArrayList<>();

		String line = br.readLine();
		while (line != null) { // For every line
			char[] digits = line.split(" ")[1].toCharArray(); // Extract sudoku info
			int[][] board = new int[9][9];
			int row = 0;
			int col = 0;
			for (char ch : digits) { // Parse it into appropriately sized board
				board[row][col++] = ch-'0';
				if (col >= 9) {row++; col = 0;}
			}
			puzzles.add(new Sudoku(board)); // And construct Sudoku object
			line = br.readLine(); // Read next line
		}

		int numPuzzles = puzzles.size(); // total number of puzzles in the file
		Sudoku[] puzzleArr = new Sudoku[numPuzzles];
		for (int i = 0; i < numPuzzles; i++) puzzleArr[i] = puzzles.get(i); // Copy to an array for quicker access

		Sudoku[] DLXSolutions = new Sudoku[numPuzzles];
		Sudoku[] BacktrackingSolutions = new Sudoku[numPuzzles];
		SudokuSolver dlxs = new DLXSolver();
		SudokuSolver bts  = new BacktrackingSolver();


		// Time DLXSolver
		long DLXStart = System.currentTimeMillis(); // Log start time
		for (int i = 0; i < numPuzzles; i++) {
			DLXSolutions[i] = dlxs.solve(puzzleArr[i]); // Solve each puzzle
		}
		long DLXEnd = System.currentTimeMillis(); // Log end time
		System.out.println("DLXSolver          completed all " + numPuzzles + " puzzles in " + (DLXEnd - DLXStart)                   + " milliseconds.");


		// Time BacktrackingSolver
		long BacktrackingStart = System.currentTimeMillis(); // Log start time
		for (int i = 0; i < numPuzzles; i++) {
			BacktrackingSolutions[i] = bts.solve(puzzleArr[i]); // Solve each puzzle
		}
		long BacktrackingEnd = System.currentTimeMillis(); // Log end time
		System.out.println("BacktrackingSolver completed all " + numPuzzles + " puzzles in " + (BacktrackingEnd - BacktrackingStart) + " milliseconds.");
	}
}
