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

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import marauroa.server.game.RPWorld;

/**
 * A Thread for finding a path without blocking the main game thread
 *
 * @author Matthias Totz
 */
public class PathfinderThread extends Thread
{
  /** The maximum time spent on a search for one particular path (in ms) */
  private static final int MAX_PATHFINDING_TIME = 100;
  /** Max size of the queue */
  private static final int QUEUE_SIZE = 100;
  /** A blocking queue to hold the path requests. */
  private BlockingQueue<QueuedPath> pathQueue;
  /** the world */
  private RPWorld world;
  /** flag indicating that the tread should finish */
  private boolean finished;
  
  /** Creates a new instance of PathfinderThread */
  public PathfinderThread(RPWorld world)
  {
    super("Pathfinder");
    this.setDaemon(true);
    
    this.world = world;
    this.finished = false;
    pathQueue = new ArrayBlockingQueue<QueuedPath>(QUEUE_SIZE);
    
  }
  
  /**
   * puts a path on the queue
   *
   * @return true if the path is added to the queue, false if the queue is full
   */
  public boolean queuePath(QueuedPath path)
  {
    return pathQueue.offer(path);
  }
  
  /** polls the queue and calculates pending path */
  public void run()
  {
    try
    {
      while (!finished)
      {
        // get a path and wait until one is available
        QueuedPath path = pathQueue.take();
        searchPath(path);
      }
    }
    catch (InterruptedException ie)
    {
      throw new RuntimeException(ie);
    }
  }
  
  /**
   * Finds a path. The PathListener is called with the result.
   *
   * @param path the path definition
   */
  private void searchPath(QueuedPath path)
  {
    // Entity entity, int x, int y, int destx, int desty
    long startTime = System.currentTimeMillis();
    
    Pathfinder pathfinder = new Pathfinder();
    NavigableStendhalNode navMap=new NavigableStendhalNode(path.getEntity(), path.getX(),path.getY(),path.getDestX(), path.getDestY(), (StendhalRPZone) world.getRPZone(path.getEntity().getID()));
// You may enable the 'distance-fix' here again
//    navMap.setMaxCost(20.0);
    pathfinder.setNavigable(navMap);
    pathfinder.setEndpoints(path.getX(),path.getY(),path.getDestX(), path.getDestY());
    
    int steps = 0;
    pathfinder.init();
    // HACK: Time limited the A* search.
    while(pathfinder.getStatus() == Pathfinder.IN_PROGRESS && ((System.currentTimeMillis() - startTime) < MAX_PATHFINDING_TIME))
    {
      pathfinder.doStep();
      steps++;
//      if (callback != null)
//      {
//        callback.stepDone(path.getBestNode());
//      }
    }
    
    long endTime = System.currentTimeMillis();
    List<Path.Node> list = new LinkedList<Path.Node>();
    Pathfinder.Node node = pathfinder.getBestNode();
    while(node!=null)
    {
      list.add(0,new Path.Node(node.getX(),node.getY()));
      node = node.getParent();
    }
    // Set the calculated path
    path.setPath(list);

    PathState state;
    switch (pathfinder.getStatus())
    {
      case Pathfinder.PATH_FOUND:
        state = PathState.PATH_FOUND;
        break;
      case Pathfinder.PATH_NOT_FOUND:
        state = PathState.PATH_NOT_FOUND;
        break;
      case Pathfinder.IN_PROGRESS:
        state = PathState.TIMEOUT_ON_SEARCH;
        break;
      default:
        state = PathState.PATH_NOT_FOUND;
    }
    
    path.getListener().onPathFinished(path, state);
    
  }
  
  
}
