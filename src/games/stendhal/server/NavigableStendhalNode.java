/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server;

import games.stendhal.server.entity.Entity;

/**
 * The callback stuff for A*
 *
 * @author Miguel Angel Blanch Lardin
 * @author Matthias Totz
 */
public class NavigableStendhalNode implements Navigable
{
  private Entity entity;
  private Entity dest;
  private StendhalRPZone zone;
  private Pathfinder.Node startNode;
  private Pathfinder.Node endNode;
  /** The best direct distance from the start to the end */
  private double bestDistance;
  /** The max cost the path may have. It is identical to the steps needed. */
  private double maxCost;
  
  /**
   * creates a new callback.
   *
   * @param entity the entity trying to move
   * @param x1 start x
   * @param y1 start x
   * @param x2 dest x
   * @param y2 dest y
   * @param zone the zone we're searching a path in
   */
  public NavigableStendhalNode(Entity entity, int x1, int y1, int x2, int y2, StendhalRPZone zone)
  {
    this.maxCost = 0;
    this.zone=zone;
    this.entity=entity;
    this.startNode = new Pathfinder.Node(x1,y1);
    this.endNode = new Pathfinder.Node(x2,y2);
    this.bestDistance = getHeuristic(startNode,endNode);
  }
  
  public NavigableStendhalNode(Entity entity, Entity dest, StendhalRPZone zone)
  {
    this(entity,entity.getx(), entity.gety(), dest.getx(),dest.gety(),zone);
    this.dest=dest;
  }
  
  public void setMaxCost(double cost)
  {
    this.maxCost = cost;
  }
  
  /** returns true if the pathfinder may use this tile for path calculation */
  public boolean isValid(Pathfinder.Node node)
  {
    // return true if the destination is reached...even if it is an unpassable
    // tile
    if (node.x == endNode.x && node.y == endNode.y)
    {
      return true;
    }
    // if there is a max cost and the current path exceeds this length =>false
    if (maxCost > 0 && (Math.abs(node.x-startNode.x) +  Math.abs(node.y-startNode.y) > maxCost))
      //if (maxCost > 0 && node.parent.g > maxCost)
    {
      return false;
    }
    // Ask the zone if the tile is walkable for our entity
    return !zone.collides(entity, node.getX(),node.getY());
  }
  
  /** returns the cost for the (adjected) tiles parent and child*/
  public double getCost(Pathfinder.Node parent, Pathfinder.Node child)
  {
    //return Math.abs(parent.getX()-child.getX())+Math.abs(parent.getY()-child.getY());
    return 1;
  }
  
  /**
   * Returns the estimated distance from parent to child. Both nodes may be
   * anywhere on the field.
   * Note: a small tie-breaker is added to the estimated distance
   */
  public double getHeuristic(Pathfinder.Node parent, Pathfinder.Node child)
  {
    // use small tie-breaker of 0.001
    //(This is calculated: (min cost of one step) / (max expected path length)
    return 1.001 * (Math.abs(parent.getX()-child.getX())+Math.abs(parent.getY()-child.getY()));
  }
  
  /** generates a unique id for the given node. */
  public int createNodeID(Pathfinder.Node node)
  {
    return node.getY()*zone.getWidth()+node.getX();
  }
}
