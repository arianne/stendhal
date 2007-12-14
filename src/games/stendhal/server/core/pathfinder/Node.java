/**
 *
 */
package games.stendhal.server.core.pathfinder;

public class Node {

	private int x;

	private int y;

	public Node(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Get the X coordinate.
	 * 
	 * @return The X coordinate.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Get the Y coordinate.
	 * 
	 * @return The Y coordinate.
	 */
	public int getY() {
		return y;
	}

	@Override
	public String toString() {
		return "(" + x + "," + y + ")";
	}
}
