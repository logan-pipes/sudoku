/**
 * LLNode.java
 *
 * @author Logan Pipes
 * @date 02-12-2022
 *
 * An interface specifying the workings of 2 dimensional linked list nodes.
 * That is, every node has a left and right neighbour of the same type as itself,
 * as well as up and down neighbours that are also nodes
 * (but not necessarily the same type of node),
 * and each node belongs to a column, accessed via getCol();
 */

interface LLNode<N extends LLNode<N>> {
	N getLeft();
	N getRight();
	LLNode getUp();
	LLNode getDown();
	ColumnNode getCol();

	void setLeft(N left);
	void setRight(N right);
	void setUp(LLNode up);
	void setDown(LLNode down);
}
