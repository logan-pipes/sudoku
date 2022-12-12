/*
 * ColumnNode.java
 *
 * @author Logan Pipes
 * @date 11-12-2022
 *
 * A class representing a column object, used in implementing Donald Knuth's DLX algorithm.
 * Represents a constraint in an exact cover problem.
 */

class ColumnNode implements LLNode<ColumnNode> {
	// Instance variables
	private ColumnNode L; // left neighbour
	private ColumnNode R; // right neighbour
	private LLNode U; // up neighbour
	private LLNode D; // down neighbour

	private int S; // size (not including itself)

	private Constraint constr; // identifier for what constraint this column represents



	// Constructors

	/**
	 * Constructs a standalone ColumnNode from a given Constraint.
	 * Sets all neighbour references to itself.
	 *
	 * @param	constr	the Constraint this ColumnNode should represent
	 */
	public ColumnNode(Constraint constr) {
		// Set links:
		// Create vertical loop:
		setUp(this); // set up neighbour
		setDown(this); // set down neighbour

		// Create horizontal loop:
		setLeft(this); // set left neighbour
		setRight(this); // set right neighbour

		this.constr = constr;
	}


	/**
	 * Constructs a ColumnNode from a given Constraint, to the right of the given ColumnNode.
	 * Sets all neighbour references according to its position in the row as the specified by the left neighbour.
	 *
	 * @param	constr	the Constraint this ColumnNode should represent
	 * @param	left	the ColumnNode it should be constructed adjacent to
	 */
	public ColumnNode(Constraint constr, ColumnNode left) {
		// Set links:
		// Create vertical loop:
		setUp(this); // set up neighbour
		setDown(this); // set down neighbour

		// Set horizontal links:
		setLeft(left); // set left neighbour
		setRight(left.getRight()); // set right neighbour
		getLeft().setRight(this); // reset left neighbour's right neighbour
		getRight().setLeft(this); // reset right neighbour's left neighbour

		this.constr = constr;
	}



	// Accessor methods
	public ColumnNode getLeft() {
		return L;
	}
	public ColumnNode getRight() {
		return R;
	}
	public LLNode getUp() {
		return U;
	}
	public LLNode getDown() {
		return D;
	}
	public ColumnNode getCol() {
		return this;
	}

	public void setLeft(ColumnNode left) {
		L = left;
	}
	public void setRight(ColumnNode right) {
		R = right;
	}
	public void setUp(LLNode up) {
		U = up;
	}
	public void setDown(LLNode down) {
		D = down;
	}


	/**
	 * Returns the Constraint this ColumnNode represents.
	 *
	 * @return	the Constraint this ColumnNode represents
	 */
	public Constraint getConstraint() {
		return constr;
	}


	/**
	 * Returns the number of DataNodes in this column, or equivalently, the number of ways to satisfy this constraint.
	 *
	 * @return	the number of DataNodes in this column
	 */
	int getSize() {
		return S;
	}


	/**
	 * Increases the value representing the number of DataNodes in this column by 1.
	 */
	void incrementSize() {
		S++;
	}


	/**
	 * Decreases the value representing the number of DataNodes in this column by 1.
	 *
	 * @throws	IllegalStateException	if size is not positive
	 */
	void decrementSize() throws IllegalStateException {
		if (S > 0) S--;
		else throw new IllegalStateException("Cannot decrement size below 0.");
	}


	/**
	 * Returns a human readable String representation of the constraint this ColumnNode corresponds to.
	 *
	 * @return	a human readable String representation of the constraint this ColumnNode corresponds to
	 */
	@Override
	public String toString() {
		return constr.toString();
	}



	// Other methods

	/**
	 * Adds a DataNode to the bottom of this column and updates all relevant references.
	 *
	 * @param	newNode	the DataNode to add to the column
	 */
	void addToBottom(DataNode newNode) {
		newNode.setUp(getUp()); // set newNode's up neighbour
		newNode.setDown(this); // set newNode's down neighbour
		getUp().setDown(newNode); // reset old bottommost node's down neighbour
		setUp(newNode); // reset this's up neighbour
		incrementSize();
	}


	/**
	 * Marks this column as covered, or that the constraint this represents is satisfied.
	 * <p>
	 * Removes this column from the list (row) of ColumnNodes still not satisfied,
	 * and removes each row that satisfies this column's constraint (the row containing each DataNode in this column)
	 * from consideration for satisfying any other constraints (makes their respective columns skip over them).
	 */
	void cover() {
		// Skip this column in the ColumnNode list
		ColumnNode r = getRight();
		ColumnNode l = getLeft();
		r.setLeft(l); // skip over this going leftward
		l.setRight(r); // skip over this going rightward

		// Remove all rows that cover this column
		LLNode columnCore = getDown();
		while (columnCore != this) { // For every node in the column (except the header)
			LLNode inRow = columnCore.getRight();
			while (inRow != columnCore) { // For every node in the same row as columnCore (except columnCore itself)
				inRow.getDown().setUp(inRow.getUp()); // skip inRow going up
				inRow.getUp().setDown(inRow.getDown()); // skip inRow going down
				inRow.getCol().decrementSize(); // inRow is removed from the column, stop couting it toward size

				inRow = inRow.getRight(); // Move to next node in row
			}
			columnCore = columnCore.getDown(); // Move to next row
		}
	}


	/**
	 * Marks this column as uncovered, or that the constraint this represents is no longer satisfied.
	 * <p>
	 * Adds this column back into the list (row) of ColumnNodes still not satisfied, between the correct neighbours.
	 * Adds each row that satisfies this column's constraint (the row containing each DataNode in this column)
	 * back into consideration for satisfying the other constraints (uses the saved references to add each node back into it's respective column).
	 */
	void uncover() {
		// Add back each row in the column
		LLNode columnCore = getUp();
		while (columnCore != this) { // For every node in the column (except the header)
			LLNode inRow = columnCore.getLeft();
			while (inRow != columnCore) { // For every node in the same row as columnCore (except columnCore itself)
				inRow.getCol().incrementSize(); // Including inRow back in its column means increasing the size of that column
				inRow.getDown().setUp(inRow); // Set below pointer to reference inRow
				inRow.getUp().setDown(inRow); // Set above pointer to reference inRow

				inRow = inRow.getLeft(); // Move to next node in row
			}
			columnCore = columnCore.getUp(); // Move to next row
		}

		// Don't skip this column anymore
		getRight().setLeft(this);
		getLeft().setRight(this);
	}
}
