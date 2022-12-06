/**
 * Constraint.java
 *
 * @author Logan Pipes
 * @date 05-12-2022
 *
 * A class representing the types of constraints imposed on a sudoku puzzle.
 * Has a nested enumerated type ConstraintType that corresponds to whether the imposed constraint represents
 * ROW:			that each digit must appear exactly once in each row,
 * COLUMN:		that each digit must appear exactly once in each column,
 * SUBBOARD:	that each digit must appear exactly once in each subboard,
 * CELL:		that each cell must contain exactly one digit.
 * Has two ints as instance variables that serve two meanings.
 * If type is CELL, then the constraint corresponds to the cell (A,B) being filled in the sudoku.
 * Otherwise, A refers to the digit, and B refers to the index of the ROW, COLUMN, or SUBBOARD that the Constraint represents.
 */

class Constraint {
	// Instance variables
	private ConstraintType type;
	private int A; // first identifier
	private int B; // second identifier



	// Constructors
	Constraint(ConstraintType inType, int identifierA, int identifierB) {
		type = inType;
		A = identifierA;
		B = identifierB;
	}



	// Accessor methods
	public ConstraintType getType() {
		return type;
	}
	public int[] getIdentifiers() {
		return new int[] {A, B};
	}

	public String toString() {
		if (type == ConstraintType.CELL) return type.toString() + A + ',' + B;
		return A + type.toString() + B;
	}



	// enum -- TODO add more comments
	enum ConstraintType {
		ROW, COLUMN, SUBBOARD, CELL
	}
}
