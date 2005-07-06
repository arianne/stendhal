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

import marauroa.common.*;
import marauroa.common.game.*;
import marauroa.server.game.*;

import games.stendhal.common.*;
import games.stendhal.server.entity.*;

import java.util.*;

public class Path
  {
  private static RPServerManager rpman;
  private static RPWorld world;
  private static StepCallback callback;
  
  /** The maximum time spent on a search for one particular path (in ms) */
  private static final int MAX_PATHFINDING_TIME = 100;
  
  public static int steps;

  public static class Node
    {
    public int x;
    public int y;

    public Node(int x, int y)
      {
      this.x=x;
      this.y=y;
      }
    }

  public static void initialize(RPServerManager rpman, RPWorld world)
    {
    Path.rpman=rpman;
    Path.world=world;
    }
  
  /**
   * Sets the step-callback. This will be called after each step.
   * <b>Note: </b> This is a debug method and not part of the 'official api'.
   */
  public static void setCallback(StepCallback callback)
  {
    Path.callback = callback;
  }

  private static void moveto(RPEntity entity, int x, int y, double speed)
    {
    int rndx=x-entity.getx();
    int rndy=y-entity.gety();

    if(Math.abs(rndx)>Math.abs(rndy))
      {
      if(Math.signum(rndx)<0)
        {
        entity.setDirection(Direction.LEFT);
        entity.setSpeed(speed);
        }
      else
        {
        entity.setDirection(Direction.RIGHT);
        entity.setSpeed(speed);
        }
      }
    else
      {
      if(Math.signum(rndy)<0)
        {
        entity.setDirection(Direction.UP);
        entity.setSpeed(speed);
        }
      else
        {
        entity.setDirection(Direction.DOWN);
        entity.setSpeed(speed);
        }
      }

    world.modify(entity);
    }

  static class NavigableStendhalNode implements Navigable
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

//    public int maxNumberOfNodes()
//      {
//      return (zone.getWidth()*zone.getHeight())/10;
//      }
    }

  /** 
   * Finds a path for the Entity <code>entity</code>.
   * @param entity the Entity
   * @param x start x
   * @param y start y
   * @param destx destination x
   * @param desty destination y
   * @return a list with the path nodes or an empty list if no path is found
   */
  public static List<Node> searchPath(Entity entity, int x, int y, int destx, int desty)
    {
    Logger.trace("Path::searchPath",">");
    long startTime = System.currentTimeMillis();

    Pathfinder path=new Pathfinder();
    NavigableStendhalNode navMap=new NavigableStendhalNode(entity, x,y, destx, desty, (StendhalRPZone)world.getRPZone(entity.getID()));
// You may enable the 'distance-fix' here again    
//    navMap.setMaxCost(20.0);
    path.setNavigable(navMap);
    path.setEndpoints(x,y,destx,desty);

    steps = 0;
    path.init();
    // HACK: Time limited the A* search.
    while(path.getStatus()==Pathfinder.IN_PROGRESS && ((System.currentTimeMillis() - startTime) < MAX_PATHFINDING_TIME))
      {
      path.doStep();
      steps++;
      if (callback != null)
        {
        callback.stepDone(path.getBestNode());
        }
      }

    if(path.getStatus()==Pathfinder.IN_PROGRESS)
      {
      return new LinkedList<Node>();
      }
    
    long endTime = System.currentTimeMillis();
    Logger.trace("Path::searchPathResult","D","Route ("+x+","+y+")-("+destx+","+desty+") S:"+steps+" OL:"+path.getOpen().size()+" CL:"+path.getClosed().size()+" in "+(endTime-startTime)+"ms");
    List<Node> list=new LinkedList<Node>();
    Pathfinder.Node node=path.getBestNode();
    while(node!=null)
      {
      Logger.trace("Path::searchPath","D",node.toString());
      list.add(0,new Node(node.getX(),node.getY()));
      node=node.getParent();
      }

    Logger.trace("Path::searchPath","<");
    return list;
    }

  /** 
   * Finds a path for the Entity <code>entity</code> to the other Entity <code>dest</code>.
   * @param entity the Entity (also start point)
   * @param dest the destination Entity
   * @return a list with the path nodes or an empty list if no path is found
   */
  public static List<Node> searchPath(Entity entity, Entity dest)
    {
    return searchPath(entity, (int)entity.getx(),(int)entity.gety(),(int)dest.getx(),(int)dest.gety());
    }

  public static boolean followPath(RPEntity entity, double speed)
    {
    List<Node> path=entity.getPath();

    if(path.size()==0)
      {
      return true;
      }

    int pos=entity.getPathPosition();

    Node actual=path.get(pos);

    if(entity.distance(actual.x, actual.y)==0)
      {
      Logger.trace("Path::followPath","D","Completed waypoint("+pos+")("+actual.x+","+actual.y+") on Path");
      pos++;
      if(pos<path.size())
        {
        entity.setPathPosition(pos);
        actual=path.get(pos);
        Logger.trace("Path::followPath","D","Moving to waypoint("+pos+")("+actual.x+","+actual.y+") on Path from ("+entity.getx()+","+entity.gety()+")");
        moveto(entity,actual.x, actual.y,speed);
        return false;
        }
      else
        {
        if(entity.isPathLoop())
          {
          entity.setPathPosition(0);
          }
        else
          {
          entity.stop();
          }

        return true;
        }
      }
    else
      {
      Logger.trace("Path::followPath","D","Moving to waypoint("+pos+")("+actual.x+","+actual.y+") on Path from ("+entity.getx()+","+entity.gety()+")");
      moveto(entity,actual.x, actual.y,speed);
      return false;
      }
    }
  
  /** this callback is called after every A* step. */
  public interface StepCallback
    {
    public void stepDone(Pathfinder.Node lastNode);
    }
  }
