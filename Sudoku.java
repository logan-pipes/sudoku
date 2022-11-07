/**
 * Sudoku.java
 *
 * @author Logan Pipes
 * @date 07-11-2022
 *
 * Holds the relevant data for an immutable generalized m^2 by m^2 sudoku puzzle.
 */

public class Sudoku {
	// Class variables
	public static final int UNKNOWN = 0;
	public static final char DEFAULT_UNKNOWN_CHAR = '.';
	public static final char DEFAULT_DELIMITER = ' ';

	// Instance variables
	private int boardSize;
	private int subBoardSize;
	private int[][] board;



	// Constructors
	public Sudoku(int[][] inBoard) throws InstantiationException {
		if (!isValidBoard(inBoard)) {
			// The constructor fails because the board is invalid for some reason
			throw new InstantiationException("Invalid board.");
		}
		// Otherwise, the board is valid
		boardSize = inBoard.length;
		subBoardSize = (int)Math.sqrt(boardSize);
		// Store a defensive copy of the passed board
		board = new int[boardSize][boardSize];
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				board[i][j] = inBoard[i][j];
			}
		}
	}



	// Accessor methods
	public int getSize() {
		return boardSize;
	}

	public int getSubBoardSize() {
		return subBoardSize;
	}

	// Returns the entry at the (i,j)th position of the board if such an entry exists,
	// or throws an IndexOutOfBoundsException otherwise.
	public int getEntry(int i, int j) throws IndexOutOfBoundsException {
		if (i < 0 || j < 0 || boardSize <= i || boardSize <= j) {
			throw new IndexOutOfBoundsException("Indices " + i + ", " + j + " out of bounds for Sudoku of size " + boardSize + ".");
		}
		return board[i][j];
	}

	// Returns a defensive copy of the board
	public int[][] getBoard() {
		int[][] defensiveCopy = new int[boardSize][boardSize];
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				defensiveCopy[i][j] = board[i][j];
			}
		}
		return defensiveCopy;
	}

	// Returns true if the board represents a solved sudoku puzzle with no missing entries and that satisfies all the rules. 
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

	// Returns a String representing the sudoku puzzle,
	// with the standard delimiter between entries and the standard character representing a blank cell
	public String toString() {
		return toString(DEFAULT_UNKNOWN_CHAR, DEFAULT_DELIMITER);
	}

	// Returns a String representing the sudoku puzzle,
	// with the specified delimiter and specified character to represent blank cells
	public String toString(char unknownChar, char delimiter) {
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

	// Returns true if the board has dimensions m^2 x m^2 for some non-negative integer m,
	// and each entry of the board is an integer from 1 to m^2 inclusive (or the UNKNOWN value),
	// and returns false if one of these constraints is violated.
	private boolean isValidBoard(int[][] board) {
		int size = board.length;
		int sq = (int)Math.sqrt(size);
		if (sq*sq != size) return false; // Board not m^2 by m^2
		for (int i = 0; i < size; i++) {
			if (board[i].length != size) return false; // Board not square
			for (int j = 0; j < size; j++) {
				if (board[i][j] != UNKNOWN && (board[i][j] < 1 || size < board[i][j])) return false; // Invalid entry in board
			}
		}
		return true;
	}
}
