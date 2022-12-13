/*
 * Constraint.java
 *
 * @author Logan Pipes
 * @date 11-12-2022
 *
 * A class representing the types of constraints imposed on a sudoku puzzle.
 * Has a nested enumerated type ConstraintType that corresponds to whether the imposed constraint represents
 * ROW:			that each digit must appear exactly once in each row,
 * COLUMN:		that each digit must appear exactly once in each column,
 * SUBBOARD:	that each digit must appear exactly once in each subboard,
 * CELL:		that each cell must contain exactly one digit.
 * Has two integers as instance variables that serve two meanings.
 * If type is CELL, then the constraint corresponds to the cell (A,B) being filled in the sudoku.
 * Otherwise, A refers to the digit, and B refers to the index of the ROW, COLUMN, or SUBBOARD that the Constraint represents.
 */

class Constraint {
	// Instance variables
	private ConstraintType type;
	private int A; // first identifier
	private int B; // second identifier



	// Constructors

	/**
	 * Constructs a Constraint of specified type and identifiers.
	 * <p>
	 * Each Constraint has two identifiers.
	 * <code>identifierA</code> corresponds to
	 * <ul>
	 * 		<li>the digit under constraint if <code>inType</code> is {@link ConstraintType#ROW},</li>
	 * 		<li>the digit under constraint if <code>inType</code> is {@link ConstraintType#COLUMN},</li>
	 * 		<li>the digit under constraint if <code>inType</code> is {@link ConstraintType#SUBBOARD},</li>
	 * 		<li>or the row index of the constrained cell if <code>inType</code> is {@link ConstraintType#CELL}.</li>
	 * </ul>
	 * <code>identifierB</code> corresponds to
	 * <ul>
	 * 		<li>the row under constraint if <code>inType</code> is {@link ConstraintType#ROW},</li>
	 * 		<li>the column under constraint if <code>inType</code> is {@link ConstraintType#COLUMN},</li>
	 * 		<li>the sub-board under constraint if <code>inType</code> is {@link ConstraintType#SUBBOARD},</li>
	 * 		<li>or the column index of the constrained cell if <code>inType</code> is {@link ConstraintType#CELL}.</li>
	 * </ul>
	 *
	 * @param	inType		the ConstraintType this Constraint represents
	 * @param	identifierA	the digit corresponding to this Constraint if <code>inType</code> is any of
	 * 						{@link ConstraintType#ROW}, {@link ConstraintType#COLUMN}, or {@link ConstraintType#SUBBOARD};
	 * 						or the row index of the cell if <code>inType</code> is {@link ConstraintType#CELL}
	 * @param	identifierB	the index corresponding to this Constraint if <code>inType</code> is any of
	 * 						{@link ConstraintType#ROW}, {@link ConstraintType#COLUMN}, or {@link ConstraintType#SUBBOARD};
	 * 						or the column index of the cell if <code>inType</code> is {@link ConstraintType#CELL}
	 */
	Constraint(ConstraintType inType, int identifierA, int identifierB) {
		type = inType;
		A = identifierA;
		B = identifierB;
	}



	// Accessor methods

	/**
	 * Returns the type of the constraint.
	 *
	 * @return	the type of the constraint
	 */
	public ConstraintType getType() {
		return type;
	}


	/**
	 * Returns the identifiers corresponding to this constraint.
	 *
	 * @return	a tuple of the two identifiers this constraint represents
	 */
	public int[] getIdentifiers() {
		return new int[] {A, B};
	}


	/**
	 * Returns a human readable String representation of this constraint.
	 *
	 * @return	a human readable String representation of this constraint
	 */
	@Override
	public String toString() {
		if (type == ConstraintType.CELL) return type.toString() + A + ',' + B;
		return A + type.toString() + B;
	}



	// Nested classes

	/**
	 * An enumerated type holding the constraints applicable to a sudoku puzzle.
	 * <p>
	 * Valid ConstraintTypes include
	 * <ul>
	 * </ul>
	 * 		<li>{@link ConstraintType#ROW},</li>
	 * 		<li>{@link ConstraintType#COLUMN},</li>
	 * 		<li>{@link ConstraintType#SUBBOARD},</li>
	 * 		<li>and {@link ConstraintType#CELL}.</li>
	 * </ul>
	 */
	enum ConstraintType {
		/**
		 * A constraint representing that exactly one instance of each number must appear in each row.
		 */
		ROW,

		/**
		 * A constraint representing that exactly one instance of each number must appear in each column.
		 */
		COLUMN,

		/**
		 * A constraint representing that exactly one instance of each number must appear in each sub-board.
		 */
		SUBBOARD,

		/**
		 * A constraint representing that exactly one number must appear in each cell.
		 */
		CELL;
	}
}
