/*
 * @(#) src/games/stendhal/server/pathfinder/FixedPath.java
 *
 * $Id$
 */

package games.stendhal.server.core.pathfinder;

//
//

import games.stendhal.server.entity.ActiveEntity;

import java.util.List;

import org.apache.log4j.Logger;

/**
 * A path using a fixed route.
 */
public class FixedPath {

	/**
	 * The logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(FixedPath.class);

	/**
	 * The current goal node.
	 */
	protected Node currentGoal;

	/**
	 * Whether to loop the path.
	 */
	protected boolean loop;

	/**
	 * The path nodes.
	 */
	protected List<Node> nodes;

	/**
	 * The current position.
	 */
	protected int pos;

	/**
	 * Create a fixed path from a list. NOTE: The list is not copied, and should
	 * not be modified afterward.
	 * 
	 * @param nodes
	 *            A list of nodes to follow.
	 * @param loop
	 *            Whether the path should loop.
	 */
	public FixedPath(final List<Node> nodes, final boolean loop) {
		this.nodes = nodes;
		this.loop = loop;

		pos = 0;

		if (nodes.isEmpty()) {
			currentGoal = null;
		} else {
			currentGoal = nodes.get(0);
		}

	}

	//
	// FixedPath
	//

	/**
	 * Add a node to the path.
	 * 
	 * @param node
	 *            The node to add.
	 */
	void add(final Node node) {
		nodes.add(node);

		if (currentGoal == null) {
			currentGoal = node;
		}
	}

	/**
	 * Get the current goal.
	 * 
	 * @return The current goal to reach, or <code>null</code>.
	 */
	public Node getCurrentGoal() {
		return currentGoal;
	}

	/**
	 * Get the list of nodes that make up the path. NOTE: The list is not
	 * copied, and should not be modified.
	 * 
	 * @return The node list.
	 */
	public List<Node> getNodeList() {
		return nodes;
	}

	/**
	 * Get the array of nodes that make up the path.
	 * 
	 * @return The nodes.
	 */
	public Node[] getNodes() {
		return nodes.toArray(new Node[nodes.size()]);
	}

	/**
	 * Determine if the path is an infinite loop.
	 * 
	 * @return <code>true</code> if the path loops when the last point is
	 *         reached.
	 */
	public boolean isLoop() {
		return loop;
	}

	/**
	 * Follow this path. This will face the entity into the proper direction to
	 * reach it's next path goal.
	 * 
	 * @param entity
	 *            The entity to direct along the path.
	 * 
	 * @return <code>true</code> if something to follow, <code>false</code>
	 *         if complete.
	 */

	public boolean follow(final ActiveEntity entity) {
		/*
		 * Without goals, we'll never get anywhere in life
		 */
		if (currentGoal == null) {
			return false;
		}

		/*
		 * Met our current goal?
		 */
		if ((currentGoal.getX() == entity.getX())
				&& (currentGoal.getY() == entity.getY())) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Completed waypoint #" + pos + "@" + currentGoal
						+ " on Path");
			}

			if (++pos >= nodes.size()) {
				if (!isLoop()) {
					LOGGER.debug("Completed path");
					return false;
				}

				pos = 0;
			}

			currentGoal = nodes.get(pos);
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Moving to waypoint #" + pos + "@" + currentGoal
					+ " on Path from (" + entity.getX() + "," + entity.getY()
					+ ")");
		}

		entity.faceto(currentGoal.getX(), currentGoal.getY());

		return true;
	}

	/**
	 * Get the final destination point.
	 * 
	 * @return The destination node, or <code>null</code> if there is none
	 *         (i.e. no path, or unbound/infinite movement).
	 */

	public Node getDestination() {
		if (loop || nodes.isEmpty()) {
			return null;
		} else {
			return nodes.get(nodes.size() - 1);
		}
	}

	/**
	 * Determine if the path has finished.
	 * 
	 * @return <code>true</code> if there is no more path to follow.
	 */

	public boolean isFinished() {
		return (currentGoal == null);
	}

	//
	// Object
	//

	/**
	 * Get the string representation.
	 * 
	 * @return The string representation.
	 */
	@Override
	public String toString() {
		final StringBuilder sbuf = new StringBuilder();

		sbuf.append("FixedPath[");
		sbuf.append(nodes);
		sbuf.append("@<");
		sbuf.append(pos);
		sbuf.append(">]");

		return sbuf.toString();
	}
}
