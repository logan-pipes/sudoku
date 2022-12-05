/**
 * DataNode.java
 *
 * @author Logan Pipes
 * @date 02-12-2022
 *
 * A class representing a data object, an important piece of Knuth's DLX algorithm's implementation.
 * Represents the satisfaction of a constraint in an exact cover problem.
 */

class DataNode implements LLNode<DataNode> {
	// Instance variables
	private DataNode L; // left neighbour
	private DataNode R; // right neighbour
	private LLNode U; // up neighbour
	private LLNode D; // down neighbour
	private ColumnNode C; // column header



	// Constructors
	DataNode(ColumnNode col) {
		// Set vertical links:
		setCol(col); // set column
		col.addToBottom(this); // assign links within column

		// Create horizontal loop:
		setLeft(this); // set left neighbour
		setRight(this); // set right neighbour
	}


	DataNode(ColumnNode col, DataNode left) {
		// Set vertical links:
		setCol(col); // set column
		col.addToBottom(this); // assign links within column

		// Set horizontal links:
		setLeft(left); // set left neighbour
		setRight(left.getRight()); // set right neighbour
		getLeft().setRight(this); // reset left neighbour's right neighbour
		getRight().setLeft(this); // reset right neighbour's left neighbour
	}



	// Accessor methods
	public DataNode getLeft() {
		return L;
	}
	public DataNode getRight() {
		return R;
	}
	public LLNode getUp() {
		return U;
	}
	public LLNode getDown() {
		return D;
	}
	public ColumnNode getCol() {
		return C;
	}


	public void setLeft(DataNode left) {
		L = left;
	}
	public void setRight(DataNode right) {
		R = right;
	}
	public void setUp(LLNode up) {
		U = up;
	}
	public void setDown(LLNode down) {
		D = down;
	}

	private void setCol(ColumnNode col) {
		C = col;
	}
}
