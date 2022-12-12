/*
 * Sudoku.java
 *
 * @author Logan Pipes
 * @date 11-12-2022
 *
 * Holds the relevant data for an immutable generalized m^2 by m^2 sudoku puzzle.
 */

import java.io.*;
import java.util.*;

public class Sudoku {
	// Class variables
	public static final int UNKNOWN = 0;
	public static final char DEFAULT_UNKNOWN_CHAR = '.';
	public static final String DEFAULT_DELIMITER = " ";

	// Instance variables
	private int boardSize;
	private int subBoardSize;
	private int[][] board;



	// Constructors

	/**
	 * Constructs a Sudoku from an explicit integer array.
	 *
	 * @param	inBoard					a 2d integer array representing the state of the board
	 * @throws	InstantiationException	if the values do not correspond to a valid sudoku
	 */
	public Sudoku(int[][] inBoard) throws InstantiationException {
		initializeBoard(inBoard);
	}


	/**
	 * Constructs a Sudoku from a File.
	 * <p>
	 * Assumes that the first line of the file contains the alphabet of characters that the puzzle is formatted in.
	 * That is, the first character of the first line is the unknown character,
	 * and the next n characters correspond to the digits comprising the n by n puzzle.
	 * Then the puzzle follows in the next n lines, one character per cell.
	 *
	 * @param	f						a file containing the specification of a sudoku board
	 * @throws	InstantiationException	if the file is not of the specified format
	 * 									or if the values do not correspond to a valid sudoku
	 */
	public Sudoku(File f) throws InstantiationException {
		try (BufferedReader br = new BufferedReader(new FileReader(f))) { // Try with resources
			char[] symbols = br.readLine().toCharArray(); // Get the alphabet
			int numSymbols = symbols.length-1;
			HashMap<Character, Integer> lookup = new HashMap<>(); // Create a lookup table
			lookup.put(symbols[0], UNKNOWN);
			for (int i = 1; i <= numSymbols; i++) lookup.put(symbols[i], i);

			char[][] lines = new char[numSymbols][]; // Hold the lines before parsing them

			int lineNumber = 0;
			String line = br.readLine(); // Read first line
			int width = line.length();
			if (width < numSymbols) throw new InstantiationException("Too many symbols.");
			else if (width > numSymbols) throw new InstantiationException("Not enough symbols.");

			while (lineNumber < numSymbols && line != null) { // While there are lines left to read
				lines[lineNumber] = line.toCharArray(); // Store line in array
				if (lines[lineNumber].length != width) throw new InstantiationException("Board not rectangular."); // Check its size
				lineNumber++;
				line = br.readLine(); // Read next line
			}

			int height = lineNumber;
			if (width != height || line != null) throw new InstantiationException("Board not square.");

			// Port char[][] of input to a more accessible form
			int[][] inputBoard = new int[height][width];
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					inputBoard[i][j] = lookup.get(lines[i][j]);
				}
			}

			initializeBoard(inputBoard); // Finish initializing the Sudoku
		} catch (IllegalArgumentException e) {
			throw new InstantiationException(e.getMessage());
		} catch (IOException e) {
			throw new InstantiationException("Error parsing file.");
		}
	}


	/**
	 * Internally assigns values to instance variables according to the passed board.
	 *
	 * @param	sudokuBoard					a 2d integer array containing the details of the Sudoku
	 * @throws	IllegalArgumentException	if the file is not of the specified format
	 * 										or if the values do not correspond to a valid sudoku
	 */
	private void initializeBoard(int[][] sudokuBoard) throws IllegalArgumentException {
		if (!isValidBoard(sudokuBoard)) {
			// The constructor fails because the board is invalid for some reason
			throw new IllegalArgumentException("Invalid board.");
		}
		// Otherwise, the board is valid
		boardSize = sudokuBoard.length;
		subBoardSize = (int)Math.sqrt(boardSize);
		if (subBoardSize*subBoardSize != boardSize) subBoardSize++; // Account for potential floating point rounding error
		// Store a defensive copy of the passed board
		board = new int[boardSize][boardSize];
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				board[i][j] = sudokuBoard[i][j];
			}
		}
	}



	// Accessor methods

	/**
	 * Returns the size of the board.
	 *
	 * @return	the size of the board, a nonnegative perfect square
	 */
	public int getSize() {
		return boardSize;
	}


	/**
	 * Returns the size of the sub-board.
	 *
	 * @return	the size of the sub-board, a nonnegative integer
	 */
	public int getSubBoardSize() {
		return subBoardSize;
	}


	/**
	 * Returns the entry at the given position of the board.
	 *
	 * @param	i							the row index of the desired entry
	 * @param	j							the column index of the desired entry
	 * @return								the (i,j) entry of the board
	 * @throws	IndexOutOfBoundsException	if the index is out of range
	 */
	public int getEntry(int i, int j) throws IndexOutOfBoundsException {
		if (i < 0 || j < 0 || boardSize <= i || boardSize <= j) {
			throw new IndexOutOfBoundsException("Indices " + i + ", " + j + " out of bounds for Sudoku of size " + boardSize + ".");
		}
		return board[i][j];
	}


	/**
	 * Returns a more accessible form of the board as a defensive copy.
	 *
	 * @return	a defensive copy of the board
	 */
	public int[][] getBoard() {
		int[][] defensiveCopy = new int[boardSize][boardSize];
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				defensiveCopy[i][j] = board[i][j];
			}
		}
		return defensiveCopy;
	}


	/**
	 * Returns the state of the puzzle in a standard, human readable format.
	 *
	 * @return	a String representing the sudoku puzzle
	 * 			with the standard delimiter separating entries
	 * 			and the standard unknown character representing blank cells
	 */
	@Override
	public String toString() {
		return toString(DEFAULT_UNKNOWN_CHAR, DEFAULT_DELIMITER);
	}


	/**
	 * Returns the state of the puzzle in a customizable, human readable format.
	 *
	 * @param	unknownChar	a character representing how to display a blank cell
	 * @param	delimiter	a String to separate cells in the same with
	 * @return				a String representing the sudoku puzzle
	 * 						where unknownChar represents blank cells
	 * 						and delimiter separates entries
	 */
	public String toString(char unknownChar, String delimiter) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < boardSize; i++) { // for every row
			if (board[i][0] == UNKNOWN) sb.append(unknownChar); // append the first number, or the unknown character if not filled in
			else sb.append(board[i][0]);

			for (int j = 1; j < boardSize; j++) { // for every column except the first
				sb.append(delimiter); // separate numbers with the delimiter
				if (board[i][j] == UNKNOWN) sb.append(unknownChar); // append the number in the given position, or the unknown char if not filled in
				else sb.append(board[i][j]);
			}
			sb.append(System.lineSeparator()); // separate rows with newlines
		}
		if (sb.length() != 0) sb.setLength(sb.length()-1); // remove trailing newline
		return sb.toString();
	}



	// Other methods

	/**
	 * Returns whether this Sudoku is complete and correct.
	 *
	 * @return	<code>true</code> if the board represents a solved sudoku puzzle
	 * 			with no missing entries and that satisfies all the rules of sudoku;
	 * 			<code>false</code> otherwise
	 */
	public boolean isSolved() {
		for (int i = 0; i < boardSize; i++) { // for all rows/cols
			boolean[] row = new boolean[boardSize+1];
			boolean[] col = new boolean[boardSize+1];
			for (int j = 0; j < boardSize; j++) { // for all cols/rows
				if (board[i][j] == UNKNOWN) return false; // any missing entry means unsolved
				if (col[board[j][i]]) return false; // if that number is already in the column
				col[board[j][i]] = true;
				if (row[board[i][j]]) return false; // if that number is already in the row
				row[board[i][j]] = true;
			}
		}

		for (int subBoardRow = 0; subBoardRow < subBoardSize; subBoardRow++) {
			for (int subBoardCol = 0; subBoardCol < subBoardSize; subBoardCol++) { // for all sub-boards
				boolean[] subBoard = new boolean[boardSize+1];
				for (int i = 0; i < subBoardSize; i++) {
					for (int j = 0; j < subBoardSize; j++) { // for all positions in that sub-board
						if (subBoard[board[subBoardRow*subBoardSize + i][subBoardCol*subBoardSize + j]]) return false; // if that number is already in the sub-board
						subBoard[board[subBoardRow*subBoardSize + i][subBoardCol*subBoardSize + j]] = true;
					}
				}
			}
		}

		return true; // If the board didn't violate any rules and has no unknown entries, it's solved
	}


	/**
	 * Validates the structure of the specified sudoku board.
	 *
	 * @param	sudokuBoard	the board to validate
	 * @return				<code>true</code> if the board has dimensions m^2 by m^2 for some nonnegative integer m,
	 * 						and each entry of the board is an integer from 1 to m^2 inclusive (or the UNKNOWN value);
	 * 						<code>false</code> if any of these constraints are violated
	 */
	private boolean isValidBoard(int[][] sudokuBoard) {
		int size = sudokuBoard.length;
		int sq = (int)Math.sqrt(size);
		if (sq*sq != size) sq++; // Account for potential floating point rounding error
		if (sq*sq != size) return false; // Board not m^2 by m^2
		for (int i = 0; i < size; i++) {
			if (sudokuBoard[i].length != size) return false; // Board not square
			for (int j = 0; j < size; j++) {
				if (sudokuBoard[i][j] != UNKNOWN && (sudokuBoard[i][j] < 1 || size < sudokuBoard[i][j])) return false; // Invalid entry in board
			}
		}
		return true; // No conditions violated, so board is therefore valid
	}
}
