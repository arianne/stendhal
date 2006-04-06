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
package games.stendhal.server.pathfinder;

import games.stendhal.server.StendhalRPZone;

import java.awt.geom.Rectangle2D;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import marauroa.common.Log4J;
import marauroa.server.game.RPWorld;
import org.apache.log4j.Logger;

import marauroa.common.game.*;
import games.stendhal.common.*;
import games.stendhal.server.*;
import games.stendhal.server.entity.*;
import games.stendhal.server.entity.creature.*;

/**
 * A Thread for finding a path without blocking the main game thread
 *
 * @author Matthias Totz
 */
public class PathfinderThread extends Thread
{
  /** the logger instance. */
  private static final Logger logger = Log4J.getLogger(PathfinderThread.class);
  
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
        path.setPath(Path.searchPath(path.getEntity(), path.getX(),path.getY(), path.getDestination()));
        path.getListener().onPathFinished(path, PathState.PATH_FOUND);
      }
      logger.info("pathfinder terminated, finished="+finished);
    }
    catch (Exception e)
    {
      // log the error
      logger.fatal("Pathfinder loop terminated with exeption (finished="+finished+")",e);
      throw new RuntimeException(e);
    }
  }  
  }
