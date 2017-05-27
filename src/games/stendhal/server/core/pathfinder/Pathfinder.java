/*
 * Based on:
 *
 * AStarPathfinder.java
 * Created on 20 October 2004, 13:33
 *
 * Copyright 2004, Generation5. All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */

package games.stendhal.server.core.pathfinder;


import java.awt.geom.Rectangle2D;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Stack;

/**
 * Implements the A* algorithm. Pathing can be done on any class that implements
 * the <code>Navigable</code> interface. See org.generation5.ai.Navigable.
 *
 * @author James Matthews
 *
 */
public abstract class Pathfinder {
	/**
	 * Returned by <code>getStatus</code> if a path <i>cannot</i> be found.
	 *
	 * @see #getStatus
	 */
	public static final int PATH_NOT_FOUND = -1;

	/**
	 * Returned by <code>getStatus</code> if a path has been found.
	 *
	 * @see #getStatus
	 */
	public static final int PATH_FOUND = 1;

	/**
	 * Returned by <code>getStatus</code> if the pathfinder is still running.
	 *
	 * @see #getStatus
	 */
	public static final int IN_PROGRESS = 0;

	/**
	 * Node weight bonus for nodes that do not change the walking direction.
	 */
	protected static final double STRAIGHT_PATH_PREFERENCE_FACTOR = 0.2;

	/**
	 * The current status of the pathfinder.
	 *
	 * @see #PATH_FOUND
	 * @see #PATH_NOT_FOUND
	 * @see #IN_PROGRESS
	 */
	private int pathStatus = IN_PROGRESS;
	/**
	 * The open list.
	 */
	private final PriorityQueue<TreeNode> openList = new PriorityQueue<TreeNode>(16,
			new Comparator<TreeNode>() {
		@Override
		public int compare(final TreeNode o1, final TreeNode o2) {
			return (int) Math.signum(o1.weight - o2.weight);
		}
	});

	private final HashMap<Integer, TreeNode> nodeRegistry = new HashMap<Integer, TreeNode>();

	/**
	 * The goal node.
	 */
	protected TreeNode goalNode;

	/**
	 * The start node.
	 */
	protected TreeNode startNode;

	/**
	 * The current best node. The best node is taken from the open list after
	 * every iteration of <code>doStep</code>.
	 */
	private TreeNode bestNode;

	/**
	 * The maximum distance for the path. It is compared with the f value of the
	 * node. The minimum for working pathfinding is
	 * heuristicFromStartNode + 1
	 */
	private double maxDistance;

	/**
	 * The goal.
	 */
	private final Rectangle2D goalArea;

	/** Initialization data */
	private final int startX, startY;
	/** Initialization data */
	private final Rectangle2D destination;
	/** Initialization data */
	private final double initMaxDist;

	protected Pathfinder(final int startX, final int startY, final Rectangle2D destination, final double maxDist) {
		this.goalArea = destination;

		// Setup the initialization data needed for node creation
		this.startX = startX;
		this.startY = startY;
		this.destination = destination;
		this.initMaxDist = maxDist;

		openList.clear();
		nodeRegistry.clear();

		bestNode = null;
		pathStatus = IN_PROGRESS;
	}

	/**
	 * Initialization that can not be done safely in the constructor.
	 */
	protected void init() {
		/*
		 * createNode is defined in child classes, so it may require
		 * work in the child's constructor.
		 */
		startNode = createNode(startX, startY);
		goalNode = createNode((int) (destination.getCenterX()),
				(int) (destination.getCenterY()));
		openList.offer(startNode);
		nodeRegistry.put(startNode.nodeNumber, startNode);

		// calculate shortest distance and allow a variance of X percent
		final double startF = 1.1 * startNode.getHeuristic(goalNode) + 1;
		this.maxDistance = Math.max(initMaxDist, startF);
	}

	/**
	 * Return the current status of the pathfinder.
	 *
	 * @return the pathfinder status.
	 * @see #pathStatus
	 */
	protected int getStatus() {
		return pathStatus;
	}

	public final List<Node> getPath() {
		init();
		final List<Node> list = new LinkedList<Node>();

		if (unreachableGoal()) {
			return list;
		}

		while (pathStatus == Pathfinder.IN_PROGRESS) {
			doStep();
		}

		if (pathStatus == Pathfinder.PATH_FOUND) {
			TreeNode node = bestNode;
			while (node != null) {
				list.add(0, new Node(node.getX(), node.getY()));
				node = node.getParent();
			}
		}
		/* */

		return list;
	}

	/**
	 * Iterate the pathfinder through one step.
	 */
	private void doStep() {
		bestNode = getBest();
		if (bestNode == null) {
			pathStatus = PATH_NOT_FOUND;
			return;
		}

		if (reachedGoal(bestNode)) {
			pathStatus = PATH_FOUND;
			return;
		}

		bestNode.createChildren();
	}

	/**
	 * Assigns the best node from the open list.
	 *
	 * @return the best node.
	 */
	private TreeNode getBest() {
		if (openList.isEmpty()) {
			return null;
		}

		final TreeNode first = openList.poll();
		first.setOpen(false);

		return first;
	}

	/**
	 * Checks if the goal is reached.
	 *
	 * @param nodeBest
	 *            the currently best node
	 * @return true if the goal is reached
	 */
	private boolean reachedGoal(final TreeNode nodeBest) {
		return goalArea.contains(nodeBest.getX(), nodeBest.getY());
	}

	/**
	 * Checks if the goal is unreachable. Only the outer nodes of the goal are
	 * checked. There could be other reasons, why a goal is unreachable.
	 *
	 * @return true checks if the goal is unreachable
	 */
	protected boolean unreachableGoal() {
		final int w = (int) goalArea.getWidth() - 1;
		final int h = (int) goalArea.getHeight() - 1;
		final int x = (int) goalArea.getX();
		final int y = (int) goalArea.getY();

		for (int i = 0; i <= w; i++) {
			for (int j = 0; j <= h; j++) {
				if ((i == 0) || (j == 0) || (i == w) || (j == h)) {
					if (createNode(x + i, y + j).isValid()) {
						return false;
					}
				}
			}
		}

		return true;
	}

	/**
	 * Create a new TreeNode
	 *
	 * @param x x coordinate of the node
	 * @param y y coordinate of the node
	 * @return TreeNode
	 */
	// A workaround for java lacking proper generics
	public abstract TreeNode createNode(final int x, final int y);


	/**
	 * Calculates the manhattan distance between to positions.
	 *
	 * @param x1
	 *            x value for position 1
	 * @param y1
	 *            y value for position 1
	 * @param x2
	 *            x value for position 2
	 * @param y2
	 *            y value for position 2
	 * @return manhattan distance between to positions
	 */
	private static int manhattanDistance(final int x1, final int y1, final int x2, final int y2) {
		return Math.abs(x1 - x2) + Math.abs(y1 - y2);
	}

	/**
	 * Calculates the square distance between to positions.
	 *
	 * @param x1
	 *            x value for position 1
	 * @param y1
	 *            y value for position 1
	 * @param x2
	 *            x value for position 2
	 * @param y2
	 *            y value for position 2
	 * @return square distance between to positions
	 */
	private static int squareDistance(final int x1, final int y1, final int x2, final int y2) {
		return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
	}


	/**
	 * The pathfinder node.
	 */
	protected abstract class TreeNode {

		/**
		 * The f-value.
		 */
		private double weight;

		/**
		 * The g-value.
		 */
		private double g;

		/**
		 * The x-position of the node.
		 */
		private final int x;

		/**
		 * The y-position of the node.
		 */
		private final int y;

		/**
		 * The number of children the node has.
		 */
		private int numChildren;

		/**
		 * The node identifier.
		 */
		private final Integer nodeNumber;

		/**
		 * The parent of the node.
		 */
		private TreeNode parent;

		private final TreeNode[] children = new TreeNode[4];

		private boolean open = true;

		/**
		 * The default constructor with positional information.
		 *
		 * @param x
		 *            the x-position of the node.
		 * @param y
		 *            the y-position of the node.
		 */
		protected TreeNode(final int x, final int y) {
			this.x = x;
			this.y = y;

			this.nodeNumber = createNodeID(x, y);

			init();
		}

		/**
		 * Resets the node. This involves all f, g and h-values to 0 as well as
		 * removing all children.
		 */
		private void init() {
			this.weight = 0.0;
			this.g = 0.0;
			this.numChildren = 0;
			for (int i = 0; i < 4; i++) {
				this.children[i] = null;
			}

			this.open = true;
		}

		/**
		 * Add a child to the node.
		 *
		 * @param child
		 *            the child node.
		 */
		private void addChild(final TreeNode child) {
			this.children[numChildren++] = child;

			updateChild(child);
		}

		/**
		 * Add a child to the node.
		 *
		 * @param child
		 *            the child node.
		 */
		private void updateChild(final TreeNode child) {
			child.parent = this;
			child.g = this.g + child.getCost();

			child.weight = calculateChildWeight(child);
		}

		/**
		 * Calculate node weight for a child node.
		 *
		 * @param child the child to be calculated
		 * @return weight for the child node
		 */
		private double calculateChildWeight(final TreeNode child) {
			double childweight = child.g + child.getHeuristic(goalNode);

			// Prefer nodes that do not result in direction change
			if (parent != null) {
				final int incx = parent.x - x;
				final int incy = parent.y - y;

				final int incx2 = x - child.x;
				final int incy2 = y - child.y;

				if ((incx == incx2) && (incy == incy2)) {
					childweight -= STRAIGHT_PATH_PREFERENCE_FACTOR;
				}
			}

			return childweight;
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
		public TreeNode getParent() {
			return parent;
		}

		/**
		 * The cost of moving to this node.
		 *
		 * @return movement cost
		 */
		protected double getCost() {
			return 1.0;
		}

		/**
		 * Calculates the heuristic for the move form node1 to node2. <p> The right
		 * heuristic is very important for A* - a over estimated heuristic will
		 * turn A* in to bsf - a under estimated heuristic will turn A* in to
		 * Dijkstra's so the manhattan distance seams to be the optimal
		 * heuristic here. But it has one disadvantage. It will expand to much.
		 * Several nodes will have the same f value It will search the area of
		 * the size (abs(startX - goalX) + 1) * (abs(startY - goalY) + 1) So a
		 * tie-breaker is needed. 1% square distace seems to work fine. A* will
		 * prefer nodes closer to the goal.
		 * @param nodeGoal
		 * @return heuristic value for move
		 */
		public double getHeuristic(final TreeNode nodeGoal) {
			final double heuristic = manhattanDistance(x, y, nodeGoal.x, nodeGoal.y);
			final double tieBreaking = 0.01 * squareDistance(x, y, nodeGoal.x,
					nodeGoal.y);

			return heuristic + tieBreaking;
		}

		/**
		 * Checks if the entity could stand on the position of this node.
		 *
		 * @return true if the the entity could stand on the position
		 */
		public boolean isValid() {
			return isValid(x, y);
		}

		/**
		 * Checks if the entity could stand on the given by the coordinates.
		 * @param x coordinate of the position to be checked
		 * @param y coordinate of the position to be checked
		 *
		 * @return true if the the entity could stand on the position
		 */
		public abstract boolean isValid(int x, int y);

		/**
		 * Create a new <code>TreeNode</code>.
		 *
		 * @param x x coordinate of the created node
		 * @param y y coordinate of the created node
		 * @return a <code>TreeNode</code>
		 */
		// A workaround for java lacking proper generics
		public abstract TreeNode createNode(int x, int y);

		/**
		 * Creates valid child nodes.
		 * <p>
		 * The child nodes have to be
		 * <ul>
		 * <li> a valid position
		 * <li> a f value less than maxDistance (checked against the given node)
		 * </ul>
		 *
		 */
		public void createChildren() {
			if (g < maxDistance) {
				linkChild(x - 1, y + 0);
				linkChild(x + 1, y + 0);
				linkChild(x + 0, y - 1);
				linkChild(x + 0, y + 1);
			}
		}

		/**
		 * Links the children to this parent node  and may also update the
		 * parent path, if a shorter path is found.
		 * @param x1
		 * @param y1
		 */
		private void linkChild(final int x1, final int y1) {
			if (!isValid(x1, y1)) {
				return;
			}

			// search for original child node
			TreeNode child = nodeRegistry.get(createNodeID(x1, y1));
			if (child == null) {
				// if not found original child node then create a new one
				child = createNode(x1, y1);

				addChild(child);

				openList.offer(child);
				child.setOpen(true);

				nodeRegistry.put(child.nodeNumber, child);
			} else {
				// note:
				// - working on closed nodes is stopped but they may own a better
				// parent
				// so they will also be added to this node (parent)
				if (child.g > (this.g + child.getCost())) {
					updateChild(child);
				}

				// update parents for closed nodes only
				if (!child.isOpen()) {
					updateSubTree(child);
				}
			}
		}

		/**
		 * Update the parents for the new route.
		 *
		 * @param node
		 *            the root node.
		 */
		private void updateSubTree(final TreeNode node) {
			int c = node.numChildren;
			final Stack<TreeNode> nodeStack = new Stack<TreeNode>();

			nodeStack.push(node);

			TreeNode parentTemp;
			TreeNode child;
			while (nodeStack.size() > 0) {
				parentTemp = nodeStack.pop();
				c = parentTemp.numChildren;
				for (int i = 0; i < c; i++) {
					child = parentTemp.children[i];

					if (parentTemp.g + child.getCost() < child.g) {
						parentTemp.updateChild(child);

						nodeStack.push(child);
					}
				}
			}
		}

		/**
		 * Calculates the node id.
		 * @param x of the node
		 * @param y of the node
		 *
		 * @return the id of the node
		 */
		protected abstract int createNodeID(int x, int y);

		public final boolean isOpen() {
			return open;
		}

		public final void setOpen(final boolean open) {
			this.open = open;
		}

		@Override
		public boolean equals(final Object obj) {
			if (obj instanceof TreeNode) {
				final TreeNode treeN = (TreeNode) obj;
				return this.nodeNumber.intValue() == treeN.nodeNumber.intValue();
			}
			return false;
		}

		@Override
		public int hashCode() {
			return nodeNumber.hashCode();
		}
	}
}
