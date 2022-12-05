/**
 * ColumnNode.java
 *
 * @author Logan Pipes
 * @date 04-12-2022
 *
 * A class representing a column object, used in implementing Knuth's DLX algorithm.
 * Represents a constraint in an exact cover problem.
 */

class ColumnNode implements LLNode<ColumnNode> {
	// Instance variables
	private ColumnNode L; // left neighbour
	private ColumnNode R; // right neighbour
	private LLNode U; // up neighbour
	private LLNode D; // down neighbour

	private int S; // size (not including itself)

	private int digit; // identifier for the digit this constraint represents
	private Constraint type; // identifier for whether this is a row/column/sub-board constraint
	private int index; // identifier for which row/column/sub-board this constraint affects



	// Constructors
	public ColumnNode(int inDigit, Constraint inType, int inIndex) {
		// Set links:
		// Create vertical loop:
		setUp(this); // set up neighbour
		setDown(this); // set down neighbour

		// Create horizontal loop:
		setLeft(this); // set left neighbour
		setRight(this); // set right neighbour

		initializeDefaults(inDigit, inType, inIndex); // Set other ColumnNode properties
	}


	public ColumnNode(int inDigit, Constraint inType, int inIndex, ColumnNode left) {
		// Set links:
		// Create vertical loop:
		setUp(this); // set up neighbour
		setDown(this); // set down neighbour

		// Set horizontal links:
		setLeft(left); // set left neighbour
		setRight(left.getRight()); // set right neighbour
		getLeft().setRight(this); // reset left neighbour's right neighbour
		getRight().setLeft(this); // reset right neighbour's left neighbour

		initializeDefaults(inDigit, inType, inIndex); // Set other ColumnNode properties
	}


	private void initializeDefaults(int inDigit, Constraint inType, int inIndex) {
		S = 0; // size of the column is 0 until things are added to it
		digit = inDigit;
		type = inType;
		index = inIndex;
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


	int getSize() {
		return S;
	}
	void incrementSize() {
		S++;
	}
	void decrementSize() {
		if (S > 0) S--;
		// TODO - maybe put an exception here?
	}


	public String toString() {
		return "" + digit + type + index;
	}



	// Other methods

	// doc comment
	void addToBottom(DataNode newNode) {
		newNode.setUp(getUp()); // set newNode's up neighbour
		newNode.setDown(this); // set newNode's down neighbour
		getUp().setDown(newNode); // reset old bottommost node's down neighbour
		setUp(newNode); // reset this's up neighbour
		incrementSize();
	}


/*
	// doc comment
	// true if successful
	// false otherwise
	boolean removeTopRow() {
		LLNode centre = getDown();
		if (centre == this) return false;
		LLNode cur = centre.getRight();
		while (cur != centre) {
			;
		}
	}
*/





	// doc comment
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

				// The question is, should this all live in a method? Maybe something like removeRow() in DataNode?
				// It definitely shouldn't be in LLNode, because removing the row from within columnNode is a bad idea,
				// but that means that columnCore can't be an LLNode, it has to be a DataNode.
				// But because we're iterating downward, eventually it will hit a ColumnNode, so the type has to be general
				// Maybe something like casting could work, (DataNode)inRow.removeRow();
				// As the programmer, I'm confident (certain) that inRow will always be a dataNode or the while condition would fail and exit,
				// but can I make the code believe that
				//
				// WAIT! What about in ColumnNode, a method boolean removeTopRow();
				// This returns false if getDown() returns a ColumnNode (which would be itself),
				// and otherwise it knows it's a row it can delete, so it does all this inRow stuff
				// then cover can do the "Skip this column" stuff at the top, and then just say while(removeTopRow());
				//
				// I think this is a good paradigm
				//
				// sike, this might be a bad idea...
				// since the loop doesn't actually get rid of each columnCore,
				// doing it via the removeTopRow idea means that you lose a reference
				// I think the perscribed way might actually be better :/, even if not OOP

				inRow.getDown().setUp(inRow.getUp()); // skip inRow going up
				inRow.getUp().setDown(inRow.getDown()); // skip inRow going down
				inRow.getCol().decrementSize(); // inRow is removed from the column, stop couting it toward size

				inRow = inRow.getRight(); // Move to next node in row
			}
			columnCore = columnCore.getDown(); // Move to next row
		}
	}


	// doc comment
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
