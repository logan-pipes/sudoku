/**
 * Constraint.java
 *
 * @author Logan Pipes
 * @date 29-11-2022
 *
 * An enumerated type corresponding to the types of constraints imposed on a sudoku puzzle.
 * ROW corresponds to a constraint specifying that there may only be one instance of a given digit per row,
 * COLUMN corresponds to a constraint specifying that there may only be one instance of a given digit per column,
 * and SUBBOARD corresponds to a constraint specifying that there may only be one instance of a given digit per sub-board.
 */

public enum Constraint {
	ROW,
	COLUMN,
	SUBBOARD;
}
