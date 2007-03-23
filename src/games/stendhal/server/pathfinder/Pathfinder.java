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

import java.util.*;

/**
 * Implements the A* algorithm. Pathing can be done on any class that implements
 * the <code>Navigable</code> interface. See org.generation5.ai.Navigable.
 * 
 * @author James Matthews
 * 
 */
public class Pathfinder {

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
	protected int pathStatus = IN_PROGRESS;

	/**
	 * The open list.
	 */
	protected PriorityQueue<Pathfinder.Node> listOpen = new PriorityQueue<Pathfinder.Node>(16,
	        new Comparator<Pathfinder.Node>() {

		        public int compare(Pathfinder.Node o1, Pathfinder.Node o2) {
			        return (int) Math.signum(o1.f - o2.f);
		        }
	        });

	protected HashMap<Integer, Pathfinder.Node> hashOpen = new HashMap<Integer, Pathfinder.Node>();

	/**
	 * The closed list.
	 */
	protected HashMap<Integer, Pathfinder.Node> hashClosed = new HashMap<Integer, Pathfinder.Node>();

	/**
	 * The goal node.
	 */
	protected Pathfinder.Node nodeGoal = null;

	/**
	 * The start node.
	 */
	protected Pathfinder.Node nodeStart = null;

	/**
	 * The current best node. The best node is taken from the open list after
	 * every iteration of <code>doStep</code>.
	 */
	protected Pathfinder.Node nodeBest = null;

	/**
	 * The current navigable environment.
	 */
	protected Navigable navMap = null;

	/**
	 * Return the current status of the pathfinder.
	 * 
	 * @return the pathfindre status.
	 * @see #pathStatus
	 */
	public int getStatus() {
		return pathStatus;
	}

	/**
	 * Iterate the pathfinder through one step.
	 */
	public void doStep() {
		nodeBest = getBest();
		if (nodeBest == null) {
			pathStatus = PATH_NOT_FOUND;
			return;
		}

		if (navMap.reachedGoal(nodeBest)) {
			pathStatus = PATH_FOUND;
			return;
		}

		createChildren(nodeBest);
	}

	/**
	 * Initialize the pathfinder.
	 */
	public void init() {
		listOpen.clear(); // Clear the open list
		hashOpen.clear();
		hashClosed.clear(); // Clear the closed list

		if ((nodeGoal == null) || (nodeStart == null)) {
			throw new IllegalArgumentException("start/goal not yet set!");
		}
		if (navMap == null) {
			throw new IllegalArgumentException("navigation map not set!");
		}

		// Initialize the node numbers
		nodeStart.nodeNumber = navMap.createNodeID(nodeStart);
		nodeGoal.nodeNumber = navMap.createNodeID(nodeGoal);

		nodeBest = null;
		pathStatus = IN_PROGRESS;
		nodeStart.g = 0;
		nodeStart.h = navMap.getHeuristic(nodeGoal, nodeStart);
		nodeStart.f = nodeStart.g + nodeStart.h;
		nodeStart.reset();

		listOpen.add(nodeStart);
		hashOpen.put(nodeStart.nodeNumber, nodeStart);
	}

	/**
	 * Reset the pathfinder (just calls <code>init</code>).
	 */
	public void reset() {
		init();
	}

	/**
	 * Sets the navigable to use in the pathfinder. The object must implement
	 * the <code>Navigable</code> interface.
	 * 
	 * @param map
	 *            the map (or other Navigable object) to find a path through.
	 */
	public void setNavigable(Navigable map) {
		navMap = map;
	}

	/**
	 * Sets the starting and goal points for the pathfinder.
	 * 
	 * @param sx
	 *            the start x-position.
	 * @param sy
	 *            the start y-position.
	 * @param gx
	 *            the goal x-position.
	 * @param gy
	 *            the goal y-position.
	 */
	public void setEndpoints(int sx, int sy, int gx, int gy) {
		setEndpoints(new Pathfinder.Node(sx, sy), new Pathfinder.Node(gx, gy));
	}

	/**
	 * Set the starting and goal points for the pathfinder. This method uses
	 * <code>Pathfinder.Node</code>'s <i>x</i> and <i>y</i> variables, the
	 * pathfinder sets all the other necessary node parameters.
	 * 
	 * @param start
	 *            the start node.
	 * @param goal
	 *            the goal node.
	 */
	public void setEndpoints(Pathfinder.Node start, Pathfinder.Node goal) {
		nodeStart = start;
		nodeGoal = goal;
	}

	/**
	 * Returns the start node.
	 * 
	 * @return the start node.
	 */
	public Pathfinder.Node getStart() {
		return nodeStart;
	}

	/**
	 * Set the start node.
	 * 
	 * @param start
	 *            the start node.
	 */
	public void setStart(Pathfinder.Node start) {
		nodeStart = start;
	}

	/**
	 * Set the goal node.
	 * 
	 * @param goal
	 *            the goal node.
	 */
	public void setGoal(Pathfinder.Node goal) {
		nodeGoal = goal;
	}

	/**
	 * Returns the goal node.
	 * 
	 * @return the goal node.
	 */
	public Pathfinder.Node getGoal() {
		return nodeGoal;
	}

	/**
	 * Assigns the best node from the open list.
	 * 
	 * @return the best node.
	 */
	protected Pathfinder.Node getBest() {
		if (listOpen.size() == 0) {
			return null;
		}
		Pathfinder.Node first = listOpen.poll();
		hashOpen.remove(first.nodeNumber);

		hashClosed.put(first.nodeNumber, first);

		return first;
	}

	/**
	 * Returns the current best node.
	 * 
	 * @return the best node.
	 */
	public Pathfinder.Node getBestNode() {
		return nodeBest;
	}

	/**
	 * Create the children surrounding the current best node.
	 * 
	 * @param node
	 *            the node to create the children from.
	 */
	protected void createChildren(Pathfinder.Node node) {
		navMap.createChildren(this, node);
	}

	/**
	 * Link the children to the parent node. This method may also update the
	 * parent path if a shorter path is found.
	 * 
	 * @param node
	 *            the parent node.
	 * @param child
	 *            the the new child.
	 */
	public void linkChild(Pathfinder.Node node, Pathfinder.Node child) {
		child.nodeNumber = Integer.valueOf(navMap.createNodeID(child));

		double g = node.g + navMap.getCost(node, child);

		Pathfinder.Node openCheck = checkOpen(child);
		Pathfinder.Node closedCheck = checkClosed(child);

		if (openCheck != null) {
			node.addChild(openCheck);

			if (g < openCheck.g) {
				openCheck.parent = node;
				openCheck.g = g;
				openCheck.f = g + openCheck.h;
			}
		} else if (closedCheck != null) {
			node.addChild(closedCheck);

			if (g < closedCheck.g) {
				closedCheck.parent = node;
				closedCheck.g = g;
				closedCheck.f = g + closedCheck.h;

				updateParents(closedCheck);
			}
		} else {
			child.parent = node;
			child.g = g;
			child.h = navMap.getHeuristic(nodeGoal, child);
			child.f = child.g + child.h;
			// child.nodeNumber = navMap.createNodeID(x,y);

			addToOpen(child);
			node.addChild(child);
		}
	}

	/**
	 * Add the new child to the open list, ordering by the f-value.
	 * 
	 * @param node
	 *            the node to add to the open list.
	 */
	protected void addToOpen(Pathfinder.Node node) {
		listOpen.offer(node);
		hashOpen.put(node.nodeNumber, node);
	}

	/**
	 * Update the parents for the new route.
	 * 
	 * @param node
	 *            the root node.
	 */
	protected void updateParents(Pathfinder.Node node) {
		double g = node.g;
		int c = node.numChildren;
		Stack<Pathfinder.Node> nodeStack = new Stack<Pathfinder.Node>();

		Pathfinder.Node kid = null;
		for (int i = 0; i < c; i++) {
			kid = node.children[i];

			if (g + 1 < kid.g) {
				kid.g = g + 1;
				kid.f = kid.g + kid.h;
				kid.parent = node;

				nodeStack.push(kid);
			}
		}

		Pathfinder.Node parent;
		while (nodeStack.size() > 0) {
			parent = nodeStack.pop();
			c = parent.numChildren;
			for (int i = 0; i < c; i++) {
				kid = parent.children[i];

				if (parent.g + 1 < kid.g) {
					kid.g = parent.g + navMap.getCost(parent, kid);
					kid.f = kid.g + kid.h;
					kid.parent = parent;

					nodeStack.push(kid);
				}
			}
		}
	}

	// private Pathfinder.Node checkList(ListIterator iter, Pathfinder.Node
	// node) {
	// Pathfinder.Node check = null;
	// if (!iter.hasNext()) return null;
	//        
	// do {
	// check = (Pathfinder.Node)iter.next();
	// if (check.nodeNumber == node.nodeNumber)
	// return check;
	//            
	// } while (iter.hasNext());
	//        
	// return null;
	// }
	//    
	/**
	 * Check the open list for a given node.
	 * 
	 * @param node
	 *            the node to check for.
	 * @return the node, if found, otherwise null.
	 */
	protected Pathfinder.Node checkOpen(Pathfinder.Node node) {
		return hashOpen.get(node.nodeNumber);
		// return checkList(listOpen.listIterator(), node);
	}

	/**
	 * Check the closed list for the given node.
	 * 
	 * @param node
	 *            the node to check for.
	 * @return the node, if found, otherwise null.
	 */
	protected Pathfinder.Node checkClosed(Pathfinder.Node node) {
		return hashClosed.get(node.nodeNumber);
		// return checkList(listClosed.listIterator(), node);
	}

	/**
	 * Return the open list.
	 * 
	 * @return the open list.
	 */
	public PriorityQueue getOpen() {
		return listOpen;
	}

	/**
	 * Return the closed list.
	 * 
	 * @return the closed list.
	 */
	public HashMap getClosed() {
		return hashClosed;
	}

	/**
	 * The pathfinder node.
	 */
	public static class Node {

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
		protected Node parent;

		Node[] children = new Node[4];

		/**
		 * The default constructor.
		 */
		public Node() {
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
		public Node(int xx, int yy) {
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
		public void addChild(Node node) {
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
		public Node getParent() {
			return parent;
		}
	}
}
