/*
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

package games.stendhal.server.pathfinder;

import games.stendhal.common.CollisionDetection;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Entity;

import java.awt.geom.Rectangle2D;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Stack;

import marauroa.common.game.RPObject;

/**
 * Implements the A* algorithm. Pathing can be done on any class that implements
 * the <code>Navigable</code> interface. See org.generation5.ai.Navigable.
 * 
 * @author James Matthews
 * 
 */
class Pathfinder {
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
	private PriorityQueue<TreeNode> listOpen = new PriorityQueue<TreeNode>(16,
			new Comparator<TreeNode>() {

				public int compare(TreeNode o1, TreeNode o2) {
					return (int) Math.signum(o1.weight - o2.weight);
				}
			});

	private HashMap<Integer, TreeNode> nodeRegistry = new HashMap<Integer, TreeNode>();

	/**
	 * The goal node.
	 */
	private TreeNode nodeGoal;

	/**
	 * The start node.
	 */
	private TreeNode nodeStart;

	/**
	 * The current best node. The best node is taken from the open list after
	 * every iteration of <code>doStep</code>.
	 */
	private TreeNode nodeBest;

	/**
	 * The entity searching a path
	 */
	private Entity entity;

	/**
	 * The zone a path is searched
	 */
	private StendhalRPZone zone;

	/**
	 * The goal
	 */
	private Rectangle2D goalArea;

	private boolean checkEntities;

	/**
	 * contains the collision data for entities
	 */
	private CollisionDetection collisionMap;

	/**
	 * The maximum distance for the path. It is comared with the f value of the
	 * node The deafult is 40 The minimum for working pathfinding is
	 * heuristicFromStartNode + 1
	 */
	private double maxDistance;

	// private Rectangle maxBoundary;
	Pathfinder(Entity entity, StendhalRPZone zone, int startX, int startY,
			Rectangle2D destination, double maxDist, boolean checkEntities) {
		this.entity = entity;
		this.zone = zone;
		this.goalArea = destination;

		this.nodeStart = new TreeNode(startX, startY);
		this.nodeGoal = new TreeNode((int) (destination.getCenterX()),
				(int) (destination.getCenterY()));

		// calculate shortest distance and allow a variance of X percent
		double startF = 1.1 * nodeStart.getHeuristic(nodeGoal) + 1;
		this.maxDistance = Math.max(maxDist, startF);

		/*
		 * int minXBoundary = Math.max(0, Math.min(nodeStart.getX() -
		 * (int)(int)maxDistance, nodeGoal.getX() - (int)maxDistance)); int
		 * maxXBoundary = Math.min(zone.getWidth(), Math.max(nodeStart.getX() +
		 * (int)maxDistance, nodeGoal.getX() + (int)maxDistance)); int
		 * minYBoundary = Math.max(0, Math.min(nodeStart.getY() -
		 * (int)maxDistance, nodeGoal.getY() - (int)maxDistance)); int
		 * maxYBoundary = Math.min(zone.getHeight(), Math.max(nodeStart.getY() +
		 * (int)maxDistance, nodeGoal.getY() + (int)maxDistance));
		 * 
		 * this.maxBoundary = new Rectangle(minXBoundary, minYBoundary,
		 * (maxXBoundary-minXBoundary), (maxYBoundary-minYBoundary));
		 */

		this.checkEntities = checkEntities;

		init();
	}

	/**
	 * Initialize the pathfinder.
	 */
	private void init() {
		listOpen.clear(); // Clear the open list
		nodeRegistry.clear();

		nodeBest = null;
		pathStatus = IN_PROGRESS;

		listOpen.offer(nodeStart);
		nodeRegistry.put(nodeStart.nodeNumber, nodeStart);

		if (checkEntities) {
			createEntityCollisionMap();
		}
	}

	/**
	 * Return the current status of the pathfinder.
	 * 
	 * @return the pathfinder status.
	 * @see #pathStatus
	 */
	public int getStatus() {
		return pathStatus;
	}

	public final List<Node> getPath() {
		List<Node> list = new LinkedList<Node>();
		/* */
		if (unrechableGoal()) {
			return list;
		}

		while (pathStatus == Pathfinder.IN_PROGRESS) {
			doStep();
		}

		if (pathStatus == Pathfinder.PATH_FOUND) {
			TreeNode node = nodeBest;
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
		nodeBest = getBest();
		if (nodeBest == null) {
			pathStatus = PATH_NOT_FOUND;
			return;
		}

		if (reachedGoal(nodeBest)) {
			pathStatus = PATH_FOUND;
			return;
		}

		nodeBest.createChildren();
	}

	/**
	 * Assigns the best node from the open list.
	 * 
	 * @return the best node.
	 */
	private TreeNode getBest() {
		if (listOpen.isEmpty()) {
			return null;
		}

		TreeNode first = listOpen.poll();
		first.setOpen(false);

		return first;
	}

	/**
	 * cerates collision data for entities the positions with entities are only
	 * considered as not valid if they - are next to the start position or -
	 * have stopped
	 */
	private void createEntityCollisionMap() {
		collisionMap = new CollisionDetection();
		collisionMap.init(zone.getWidth(), zone.getHeight());
		for (Iterator<RPObject> it = zone.iterator(); it.hasNext();) {
			Entity otherEntity = (Entity) it.next();
			if (!entity.getID().equals(otherEntity.getID())
					&& otherEntity.isObstacle(entity)
					&& (otherEntity.stopped() || otherEntity.nextTo(
							nodeStart.getX(), nodeStart.getY(), 0.25))) {

				Rectangle2D area = otherEntity.getArea(otherEntity.getX(),
						otherEntity.getY());
				collisionMap.setCollide(area, true);
			}
		}
	}

	/**
	 * checks if the goal is reached
	 * 
	 * @param nodeBest
	 *            the currently best node
	 * @return true if the goal is reached
	 */
	private boolean reachedGoal(TreeNode nodeBest) {
		return goalArea.contains(nodeBest.getX(), nodeBest.getY());
	}

	/**
	 * Checks if the goal is unreachable. Only the outer nodes of the goal are
	 * checked. There could be other reasons, why a goal is unreachable.
	 * 
	 * @return true checks if the goal is unreachable
	 */
	protected boolean unrechableGoal() {
		int w = (int) goalArea.getWidth() - 1;
		int h = (int) goalArea.getHeight() - 1;
		int x = (int) goalArea.getX();
		int y = (int) goalArea.getY();

		for (int i = 0; i <= w; i++) {
			for (int j = 0; j <= h; j++) {
				if ((i == 0) || (j == 0) || (i == w) || (j == h)) {
					if (new TreeNode(x + i, y + j).isValid()) {
						return false;
					}
				}
			}
		}

		return true;
	}

	/**
	 * calculates the manhattan distance between to positions
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
	private static int manhattanDistance(int x1, int y1, int x2, int y2) {
		return Math.abs(x1 - x2) + Math.abs(y1 - y2);
	}

	/**
	 * calculates the square distance between to positions
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
	private static int squareDistance(int x1, int y1, int x2, int y2) {
		return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
	}

	/**
	 * The pathfinder node.
	 */
	private class TreeNode {

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
		private int x;

		/**
		 * The y-position of the node.
		 */
		private int y;

		/**
		 * The number of children the node has.
		 */
		private int numChildren;

		/**
		 * The node identifier.
		 */
		private Integer nodeNumber;

		/**
		 * The parent of the node.
		 */
		private TreeNode parent;

		private TreeNode[] children = new TreeNode[4];

		private boolean open = true;

		/**
		 * The default constructor with positional information.
		 * 
		 * @param xx
		 *            the x-position of the node.
		 * @param yy
		 *            the y-position of the node.
		 */
		public TreeNode(int xx, int yy) {
			this.x = xx;
			this.y = yy;

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
		private void addChild(TreeNode child) {
			this.children[numChildren++] = child;

			updateChild(child);
		}

		/**
		 * Add a child to the node.
		 * 
		 * @param child
		 *            the child node.
		 */
		private void updateChild(TreeNode child) {
			child.parent = this;
			child.g = this.g + 1;
			child.weight = child.g + child.getHeuristic(nodeGoal);
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

		/*
		 * calculates the heuristic for the move form node1 to node2 the right
		 * heuristic is very importand for A* - a over estimated heuristic will
		 * turn A* in to bsf - a under estimated heuristic will turn A* in to
		 * Dijkstra's so the manhattan distance seams to be the optimal
		 * heuristic here. But it has one disadvantage. It will expand to much.
		 * Sevreal nodes will have the same f value It will search the area of
		 * the size (abs(startX - goalX) + 1) * (abs(startY - goalY) + 1) So a
		 * tie-breaker is needed. 1% square distace seems to work fine. A* will
		 * prefer nodes closer to the goal.
		 */
		public double getHeuristic(TreeNode nodeGoal) {
			double heuristic = manhattanDistance(x, y, nodeGoal.x, nodeGoal.y);
			double tieBreaking = 0.01 * squareDistance(x, y, nodeGoal.x,
					nodeGoal.y);

			return heuristic + tieBreaking;
		}

		public double getWeight() {
			return weight;
		}

		/**
		 * checks if the entity could stand on a position
		 * 
		 * @param node
		 *            the position to be checked
		 * @return true if the the entity could stand on the position
		 */
		public boolean isValid() {
			boolean result = !zone.simpleCollides(entity, x, y);
			if (checkEntities && result) {
				Rectangle2D entityArea = entity.getArea(x, y);
				result = !collisionMap.collides(entityArea);
			}

			return result;
		}

		/**
		 * checks if the entity could stand on a position
		 * 
		 * @param node
		 *            the position to be checked
		 * @return true if the the entity could stand on the position
		 */
		public boolean isValid(int x1, int y1) {
			boolean result = !zone.simpleCollides(entity, x1, y1);
			if (checkEntities && result) {
				Rectangle2D entityArea = entity.getArea(x1, y1);
				result = !collisionMap.collides(entityArea);
			}

			return result;
		}

		/**
		 * crates valid cild nodes, the cild nodes have to be - a valid position -
		 * a f value less than maxDistance (checked against the given node)
		 * 
		 * @param node
		 *            the node
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
		 * Link the children to the parent node. This method may also update the
		 * parent path if a shorter path is found.
		 * 
		 * @param parent
		 *            the parent node.
		 * @param child
		 *            the the new child.
		 */
		private void linkChild(int x1, int y1) {
			if (!isValid(x1, y1)) {
				return;
			}

			// search for original child node
			TreeNode child = nodeRegistry.get(createNodeID(x1, y1));
			if (child == null) {
				// if not found original child node then create a new one
				child = new TreeNode(x1, y1);

				addChild(child);

				listOpen.offer(child);
				child.setOpen(true);

				nodeRegistry.put(child.nodeNumber, child);
			} else {
				// note:
				// - working on closed nodes is stoped but they may own a better
				// parent
				// so they will also be added to this node (parent)
				if (child.g > (this.g + 1)) {
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
		private void updateSubTree(TreeNode node) {
			int c = node.numChildren;
			Stack<TreeNode> nodeStack = new Stack<TreeNode>();

			nodeStack.push(node);

			TreeNode parentTemp, child;
			while (nodeStack.size() > 0) {
				parentTemp = nodeStack.pop();
				c = parentTemp.numChildren;
				for (int i = 0; i < c; i++) {
					child = parentTemp.children[i];

					if (parentTemp.g + 1 < child.g) {
						parentTemp.updateChild(child);

						nodeStack.push(child);
					}
				}
			}
		}

		/**
		 * calculates the node id
		 * 
		 * @param node
		 *            the node
		 * @return the id of the node
		 */
		private int createNodeID(int x, int y) {
			return x + y * zone.getWidth();
		}

		public final boolean isOpen() {
			return open;
		}

		public final void setOpen(boolean open) {
			this.open = open;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof TreeNode) {
				TreeNode treeN = (TreeNode) obj;
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
