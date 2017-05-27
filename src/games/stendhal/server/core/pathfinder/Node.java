/**
 *
 */
package games.stendhal.server.core.pathfinder;

public class Node {

	private final int x;

	private final int y;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Node other = (Node) obj;

		return (x == other.x) && (y == other.y);
	}

	public Node(final int x, final int y) {
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
