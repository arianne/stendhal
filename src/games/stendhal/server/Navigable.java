/*
 * Navigable.java
 * Created on 19 August 2004, 19:40
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

package games.stendhal.server;

import games.stendhal.server.Pathfinder.Node;

/**
 * A simple interface to allow pathfinders like the A* algorithm to navigate
 * through the environment.
 * 
 * @author James Matthews
 */
public interface Navigable {
    /**
     * Determines whether the given node is valid.
     * @param node the node.
     * @return the validity of the node.
     */    
    public boolean isValid(Pathfinder.Node node);
    /**
     * Return the cost to travel from node 1 (parent) to node 2 (child). Note
     * that the nodes are always adjected. If traveling from one node to another
     * costs the same all over the map you may return <code>1</code> here.
     * Note also that two calls getCost(node1, node2) must return the same cost
     * for all node1/node2.
     *
     * @param parent the parent node. This is the node we're traveling from.
     *               (<b>not</b> the start node)
     * @param child the child node. This is the (adjected) node we want to 
     *              travel to.
     * @return the cost required to travel.
     */    
    public double getCost(Pathfinder.Node parent, Pathfinder.Node child);
    /**
     * Return the estimated distance between the node 1 and node 2. This 
     * distance should never be an underestimation. Note that the nodes are not
     * nessarily adjected.
     * 
     * Original Note:
     * Note that "distance" is not always in terms of Manhattan or Eucledian
     * distances.
     *
     * @param n1 the first node.
     * @param n2 the second node.
     * @return the (estimated) distance between the two nodes.
     */    
    public double getHeuristic(Pathfinder.Node n1, Pathfinder.Node n2);
    
    /**
     * Generate a unique ID for a given node. Note that the ID must be tied to 
     * its properties, such as positional information. Nodes with the same 
     * information should be assigned the same ID.
     * @param node the node.
     * @return the node's ID.
     * 
     * @deprecated no need to assign ids to nodes. @see reachedGoal(Node); 
     */    
    public int createNodeID(Pathfinder.Node node);

    /**
     * Checks if the calculated node can be considerd a goal. This way a "goal
     * area" can be archived.
     * 
     * @param nodeBest the current best node
     * @return true when <i>nodeBest</i> can be considered a "goal"
     */
    public boolean reachedGoal(Node nodeBest);
    
}
