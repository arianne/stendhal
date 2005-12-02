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

import games.stendhal.common.Direction;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;

import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;

import marauroa.common.Log4J;
import marauroa.server.game.RPWorld;

import org.apache.log4j.Logger;


public class Path
  {
  /** the logger instance. */
  private static final Logger logger = Log4J.getLogger(Path.class);

  private static StendhalRPWorld world;
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
    public String toString()
      {
      return "("+x+","+y+")";
      }
    }

  public static void initialize(RPWorld world)
    {
    Path.world = (StendhalRPWorld) world;
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

  /** 
   * Finds a path for the Entity <code>entity</code>.
   * @param entity the Entity
   * @param x start x
   * @param y start y
   * @param destx destination x
   * @param desty destination y
   * @return a list with the path nodes or an empty list if no path is found
   */
  public static List<Node> searchPath(Entity entity, int x, int y, Rectangle2D destination)
    {
    return searchPath(entity, x, y, destination, -1.0);
    }

  /** 
   * Finds a path for the Entity <code>entity</code>.
   * @param entity the Entity
   * @param x start x
   * @param y start y
   * @param destination the destination area
   * @param maxDistance the maximum distance (air line) a possible path may be
   * @return a list with the path nodes or an empty list if no path is found
   */
  public static List<Node> searchPath(Entity entity, int x, int y, Rectangle2D destination, double maxDistance)
    {
    Log4J.startMethod(logger, "searchPath");
    long startTime = System.currentTimeMillis();

    Pathfinder path=new Pathfinder();
    NavigableStendhalNode navMap=new NavigableStendhalNode(entity, x,y, destination, (StendhalRPZone)world.getRPZone(entity.getID()));

    navMap.setMaxCost(maxDistance);
    path.setNavigable(navMap);
    path.setStart(new Pathfinder.Node(x,y));
    path.setGoal(new Pathfinder.Node((int) destination.getX(),(int) destination.getY()));
    

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
    logger.debug("Route ("+x+","+y+")-("+destination+") S:"+steps+" OL:"+path.getOpen().size()+" CL:"+path.getClosed().size()+" in "+(endTime-startTime)+"ms");

    List<Node> list=new LinkedList<Node>();
    Pathfinder.Node node=path.getBestNode();
    while(node!=null)
      {
      list.add(0,new Node(node.getX(),node.getY()));
      node=node.getParent();
      }

    Log4J.finishMethod(logger, "searchPath");
    return list;
    }

  /** 
   * Finds a path for the Entity <code>entity</code> to the other Entity <code>dest</code>.
   * @param entity the Entity (also start point)
   * @param dest the destination Entity
   * @param maxPathRadius the maximum radius in which a path is searched
   * @return a list with the path nodes or an empty list if no path is found
   */
  public static void searchPathAsynchonous(RPEntity entity, Entity dest, double maxPathRadius)
    {
    world.checkPathfinder();

    boolean result = world.getPathfinder().queuePath(
             new QueuedPath(
                 new SimplePathListener(entity), 
                 entity, 
                 entity.getx(),
                 entity.gety(),
                 dest.getArea(dest.getx(),dest.gety()),
                 maxPathRadius
                 )
            );
    
    if (!result)
      {
      logger.warn("Pathfinder queue is full...path not added");
      }
    }
  
  /** 
   * Finds a path for the Entity <code>entity</code> to the other Entity <code>dest</code>.
   * @param entity the Entity (also start point)
   * @param dest the destination Entity
   * @return a list with the path nodes or an empty list if no path is found
   */
  public static List<Node> searchPath(Entity entity, Entity dest)
    {
    return searchPath(entity, (int)entity.getx(),(int)entity.gety(),dest.getArea(dest.getx(),dest.gety()));
    }
  
  /** 
   * Finds a path for the Entity <code>entity</code> to the other Entity <code>dest</code>.
   * @param entity the Entity (also start point)
   * @param dest the destination Entity
   * @param maxDistance the maximum distance (air line) a possible path may be
   * @return a list with the path nodes or an empty list if no path is found
   */
  public static List<Node> searchPath(Entity entity, Entity dest, double maxDistance)
    {
    return searchPath(entity, (int)entity.getx(),(int)entity.gety(),dest.getArea(dest.getx(),dest.gety()), maxDistance);
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
      logger.debug("Completed waypoint("+pos+")("+actual.x+","+actual.y+") on Path");
      pos++;
      if(pos<path.size())
        {
        entity.setPathPosition(pos);
        actual=path.get(pos);
        logger.debug("Moving to waypoint("+pos+")("+actual.x+","+actual.y+") on Path from ("+entity.getx()+","+entity.gety()+")");
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
      logger.debug("Moving to waypoint("+pos+")("+actual.x+","+actual.y+") on Path from ("+entity.getx()+","+entity.gety()+")");
      moveto(entity,actual.x, actual.y,speed);
      return false;
      }
    }
  
  /** this callback is called after every A* step. */
  public interface StepCallback
    {
    public void stepDone(Pathfinder.Node lastNode);
    }
  
  /** the threaded-pathfinder callback */
  private static class SimplePathListener implements PathListener
    {
    /** the entity the path belongs to */
    private RPEntity entity;
    
    /** 
     * creates a new instance of SimplePathLister 
     * @param entity the entity the path belongs to
     */
    public SimplePathListener(RPEntity entity)
      {
      this.entity = entity;
      }
    
    /** simply appends the calculated path to the entitys path */
    public void onPathFinished(QueuedPath path, PathState state)
      {
      if (state == PathState.PATH_FOUND)
        {
        entity.addToPath(path.getPath());
        }
      }
    }
  }
