package games.stendhal.server.pathfinder;

/**
 * The pathfinder node.
 */
class PathFinderNode {

	/**
	 * The f-value.
	 */
	public double f;

	/**
	 * The g-value.
	 */
	public double g;

	/**
	 * The h-value.
	 */
	public double h;

	/**
	 * The x-position of the node.
	 */
	protected int x;

	/**
	 * The y-position of the node.
	 */
	protected int y;

	/**
	 * The number of children the node has.
	 */
	public int numChildren;

	/**
	 * The node identifier.
	 */
	public Integer nodeNumber;

	/**
	 * The parent of the node.
	 */
	protected PathFinderNode parent;

	PathFinderNode[] children = new PathFinderNode[4];

	/**
	 * The default constructor.
	 */
	PathFinderNode() {
		this(-1, -1);
	}

	/**
	 * The default constructor with positional information.
	 *
	 * @param xx
	 *            the x-position of the node.
	 * @param yy
	 *            the y-position of the node.
	 */
	PathFinderNode(int xx, int yy) {
		x = xx;
		y = yy;
	}

	/**
	 * Resets the node. This involves all f, g and h-values to 0 as well as
	 * removing all children.
	 */
	public void reset() {
		f = g = h = 0.0;
		numChildren = 0;
		for (int i = 0; i < 4; i++) {
			children[i] = null;
		}
	}

	/**
	 * Add a child to the node.
	 *
	 * @param node
	 *            the child node.
	 */
	void addChild(PathFinderNode node) {
		children[numChildren++] = node;
	}

	/**
	 * Return the x-position of the node.
	 *
	 * @return the x-position of the node.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Return the y-position of the node.
	 *
	 * @return the y-position of the node.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Return the parent node.
	 *
	 * @return the parent node.
	 */
	PathFinderNode getParent() {
		return parent;
	}
}