/*
 * Pathfinder.java
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
 * Implements the A* algorithm. Pathing can be done on any class that implements the
 * <code>Navigable</code> interface.
 * @author James Matthews
 * @see org.generation5.ai.Navigable
 */
public class Pathfinder
  {
    /**
     * Returned by <code>getStatus</code> if a path <i>cannot</i> be found.
     * @see #getStatus
     */    
    public static final int PATH_NOT_FOUND = -1;
    /**
     * Returned by <code>getStatus</code> if a path has been found.
     * @see #getStatus
     */    
    public static final int PATH_FOUND = 1;
    /**
     * Returned by <code>getStatus</code> if the pathfinder is still running.
     * @see #getStatus
     */    
    public static final int IN_PROGRESS = 0;
    /**
     * The current status of the pathfinder.
     * @see #PATH_FOUND
     * @see #PATH_NOT_FOUND
     * @see #IN_PROGRESS
     */    
    protected int pathStatus = IN_PROGRESS;
    /**
     * The open list.
     */    
    protected LinkedList<Node> listOpen = new LinkedList<Node>();
    /**
     * The closed list.
     */    
    protected LinkedList<Node> listClosed = new LinkedList<Node>();
    /**
     * The goal node.
     */    
    protected Node nodeGoal = null;
    /**
     * The start node.
     */    
    protected Node nodeStart = null;
    /**
     * The current best node. The best node is taken from the open list after every
     * iteration of <code>doStep</code>.
     */    
    protected Node nodeBest = null;
    /**
     * The current navigable environment.
     */    
    protected Navigable navMap = null;
    
    /** Creates a new instance of Pathfinder */
    public Pathfinder() {
    }
    
    /**
     * Return the current status of the pathfinder.
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
        listOpen.clear();       // Clear the open list
        listClosed.clear();     // Clear the closed list
        
        if (nodeGoal == null || nodeStart == null)
            throw new IllegalArgumentException("start/goal not yet set!");
        if (navMap == null)
            throw new IllegalArgumentException("navigation map not set!");
        
        // Initialize the node numbers
//        nodeStart.nodeNumber = navMap.createNodeID(nodeStart);
//        nodeGoal.nodeNumber  = navMap.createNodeID(nodeGoal);

        nodeBest = null;
        pathStatus = IN_PROGRESS;
        nodeStart.g = 0;
        nodeStart.h = navMap.getHeuristic(nodeGoal, nodeStart);
        nodeStart.f = nodeStart.g + nodeStart.h;
        nodeStart.reset();
        
        listOpen.add(nodeStart);
    }
    
    /**
     * Reset the pathfinder (just calls <code>init</code>).
     */    
    public void reset() {
        init();
    }
    
    /**
     * Sets the navigable  to use in the pathfinder. The object must implement the
     * <code>Navigable</code> interface.
     * @param map the map (or other Navigable object) to find a path through.
     */    
    public void setNavigable(Navigable map) {
        navMap = map;
    }

    /**
     * Sets the starting and goal points for the pathfinder.
     * @param sx the start x-position.
     * @param sy the start y-position.
     * @param gx the goal x-position.
     * @param gy the goal y-position.
     * 
     */    
    public void setEndpoints(int sx, int sy, int gx, int gy) {
        setEndpoints(new Node(sx, sy), new Node(gx, gy));
    }
    
    /**
     * Set the starting and goal points for the pathfinder. This method uses
     * <code>Node</code>'s <i>x</i> and <i>y</i> variables, the pathfinder sets all
     * the other necessary node parameters.
     * @param start the start node.
     * @param goal the goal node.
     * 
     */    
    public void setEndpoints(Node start, Node goal) {
        nodeStart = start;
        nodeGoal  = goal;
    }
    
    /**
     * Returns the start node.
     * @return the start node.
     */    
    public Node getStart() {
        return nodeStart;
    }
    
    /**
     * Set the start node.
     * @param start the start node.
     */    
    public void setStart(Node start) {
        nodeStart = start;
    }
    
    /**
     * Set the goal node.
     * @param goal the goal node.
     */    
    public void setGoal(Node goal) {
        nodeGoal = goal;
    }
    
    /**
     * Returns the goal node.
     * @return the goal node.
     */    
    public Node getGoal() {
        return nodeGoal;
    }
    
    /**
     * Assigns the best node from the open list.
     * @return the best node.
     */    
    protected Node getBest() {
        if (listOpen.size() == 0) return null;
        
        Node first = (Node)listOpen.getFirst();
        
        listOpen.removeFirst();
        listClosed.addFirst(first);
        
        return first;
    }
    
    /**
     * Returns the current best node.
     * @return the best node.
     */    
    public Node getBestNode() {
        return nodeBest;
    }
    
    /**
     * Create the children surrounding the current best node.
     * @param node the node to create the children from.
     */    
    protected void createChildren(Node node) {
        int x = node.x, y = node.y;
        Pathfinder.Node tempNode = new Pathfinder.Node();
        
        tempNode.x = x-1;
        tempNode.y = y;
        tempNode.parent = node;

        if (navMap.isValid(tempNode) == true)
          {
          linkChild(node, x-1, y);
          }

        tempNode.x = x+1;
        tempNode.y = y;

        if (navMap.isValid(tempNode) == true)
          {
          linkChild(node, x+1, y);
          }

        tempNode.x = x;
        tempNode.y = y-1;

        if (navMap.isValid(tempNode) == true)
          {
          linkChild(node, x, y-1);
          }

        tempNode.x = x;
        tempNode.y = y+1;

        if (navMap.isValid(tempNode) == true)
          {
          linkChild(node, x, y+1);
          }
    }
    
    /**
     * Link the children to the parent node. This method may also update the parent
     * path if a shorter path is found.
     * @param node the parent node.
     * @param x the x-position of the new child.
     * @param y the y-position of the new child.
     */    
    protected void linkChild(Node parent, int x, int y) {
        Node child = new Node(x, y);
//        child.nodeNumber = navMap.createNodeID(child); // generate unique id
        
        // get cost for traversing from the parent node to the new node and
        // sum the cost
        double g = parent.g + navMap.getCost(parent, child);
        // find the list the (new) node is in
        Node openCheck   = checkOpen(child);
        Node closedCheck = checkClosed(child);
        
        // node is still open
        if (openCheck != null) {
            parent.addChild(openCheck); // Add child to parent
            
            // Has the cost improved for the node (have we found a better path?)
            if (g < openCheck.g) {
                // Yes, update the node with the current path
                openCheck.parent = parent;
                openCheck.g = g;
                openCheck.f = g + openCheck.h;
            }
        } else if (closedCheck != null) {
            // node is closed already
            parent.addChild(closedCheck); // keep track of parent
            
            // Has the cost improved for the node (have we found a better path?)
            if (g < closedCheck.g) {
                // Yes we have, update the node with the current path
                closedCheck.parent = parent;
                closedCheck.g = g;
                closedCheck.f = g + closedCheck.h;
                // reconfigure the parents (update full path)
                updateParents(closedCheck);
            }
        } else {
            // not open and not closed...so this node is new.
            // pre-calculate the heuristic (estimated distance) for this node.
            // This is only done once for each node.
            child.parent = parent;
            child.g = g;
            child.h = navMap.getHeuristic(nodeGoal, child);
            child.f = child.g + child.h;
            
            // Add the new child to the open list and to the current path
            addToOpen(child);
            parent.addChild(child);
        }
    }
    
    /**
     * Add the new child to the open list, ordering by the f-value. No check is
     * made if the node is already in the list.
     * @param node the node to add to the open list.
     */    
    protected void addToOpen(Node node) {
        int index = 0;
        Node openNode = null;
        ListIterator iter = listOpen.listIterator();
        
        if (listOpen.size() == 0) {
            listOpen.addFirst(node);
            return;
        }
        
        do {
            openNode = (Node)iter.next();
            if (node.f < openNode.f) {
                listOpen.add(index,  node);
                return;
            }
            index = index + 1;
        } while (iter.hasNext());
        
        listOpen.addLast(node);
    }
    
    /**
     * Update the parents for the new route. This will replace from an already 
     * calculated path from the beginning to this (closed) node. All nodes from
     * this to the end of the tree will get a newly generated cost based on the
     * current path.
     *
     * @param node the root node.
     */    
    protected void updateParents(Node node) {
        double g = node.g;
        int c = node.numChildren;
        Stack<Node> nodeStack = new Stack<Node>();
        
        // find all children with bad cost
        Node kid = null;
        for (int i=0; i<c; i++) {
            kid = node.children[i];
            
            // note: the g+1 may be dangerous when getCost(node,kid) returns a 
            // cost other than 1
            if (g+1 < kid.g) {
                kid.g = g+1;
                kid.f = kid.g + kid.h;
                kid.parent = node;
                
                nodeStack.push(kid);
            }
        }
        
        Node parent;
        while (nodeStack.size() > 0) {
            parent = (Node)nodeStack.pop();
            c = parent.numChildren;
            for (int i=0; i<c; i++) {
                kid = parent.children[i];
                
                if (parent.g+1 < kid.g) {
                    kid.g = parent.g + navMap.getCost(parent, kid);
                    kid.f = kid.g + kid.h;
                    kid.parent = parent;
                    
                    nodeStack.push(kid);
                }
            }
        }
    }
    
    /**
     * Checks if the given node is in the list.
     * @param iter the list iterator to check. The iterator should be at the
     *             start of the list (unless you know what you are doing)
     * @param node this node will be the node to check. Note that the check is
     *             based on the <code>nodeNumber</code>-property, not on the object-instance
     * @return the node from the list or <code>null</code> if the node is not in the list
     */
    private Node checkList(ListIterator iter, Node node) {
        Node check = null;
        if (!iter.hasNext()) return null;
        
        do {
            check = (Node)iter.next();
//            if (check.nodeNumber == node.nodeNumber)
            if (check == node)            
                return check;
            
        } while (iter.hasNext());
        
        return null;
    }
    
    /**
     * Check the open list for a given node.
     * @param node the node to check for.
     * @return the node, if found, otherwise null.
     */    
    protected Node checkOpen(Node node) {
        return checkList(listOpen.listIterator(), node);
    }
    
    /**
     * Check the closed list for the given node.
     * @param node the node to check for.
     * @return the node, if found, otherwise null.
     */    
    protected Node checkClosed(Node node) {
        return checkList(listClosed.listIterator(), node);
    }
    
    /**
     * Return the open list.
     * @return the open list.
     */    
    public LinkedList<Node> getOpen() {
        return listOpen;
    }
    
    /**
     * Return the closed list.
     * @return the closed list.
     */    
    public LinkedList<Node> getClosed() {
        return listClosed;
    }
    
    /**
     * The pathfinder node.
     */    
    public static class Node {
        
        /**
         * The f-value.
         * The sum of g and h.
         * It is the (estimated) cost of the full path.
         */
        public double f;
        
        /**
         * The g-value.
         * This is the cost from the start point to this point.
         */
        public double g;
        
        /**
         * The h-value (heuristic).
         * This is the estimated cost from this point to the goal point.
         */        
        public double h;
        
        /**
         * The x-position of the node.
         */
        protected int x;
        
        /**
         * The y-position of the node.
         */        
        protected int  y;
        /**
         * The number of children the node has.
         */        
        public int numChildren;
        /**
         * The node identifier.
         */        
//        public int nodeNumber;
        /**
         * The parent of the node.
         */        
        protected Node  parent;
        /**
         * Array with the children, one for each direction
         */
        Node[] children = new Node[4];
        
        /**
         * The default constructor.
         */        
        public Node() {
            this(-1, -1);
        }
        
        /**
         * The default constructor with positional information.
         * @param xx the x-position of the node.
         * @param yy the y-position of the node.
         */        
        public Node(int xx, int yy) {
            x = xx;
            y = yy;
        }
        
        /**
         * Resets the node. This involves all f, g and h-values to 0 as well as removing all
         * children.
         */        
        public void reset() {
            f = g = h = 0.0;
            numChildren = 0;
            for (int i=0; i<4; i++) 
                children[i] = null;
        }
        
        /**
         * Add a child to the node.
         * @param node the child node.
         */        
        public void addChild(Node node) {
            children[numChildren++] = node;
        }
        
        /**
         * Return the x-position of the node.
         * @return the x-position of the node.
         */        
        public int getX() {
            return x;
        }
        
        /**
         * Return the y-position of the node.
         * @return the y-position of the node.
         */        
        public int getY() {
            return y;
        }
        
        /**
         * Return the parent node.
         * @return the parent node.
         */        
        public Node getParent() {
            return parent;
        }
        
        public String toString() {
            return "("+x+","+y+")";
        }
          
    }
}
