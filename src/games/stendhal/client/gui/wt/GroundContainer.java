/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2005 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui.wt;

import java.awt.geom.Point2D;
import java.awt.Point;

import marauroa.common.game.RPAction;

import games.stendhal.client.GameObjects;
import games.stendhal.client.GameScreen;
import games.stendhal.client.StendhalClient;
import games.stendhal.client.entity.Entity;
import games.stendhal.client.entity.PassiveEntity;
import games.stendhal.client.gui.wt.core.Draggable;
import games.stendhal.client.gui.wt.core.Panel;

/**
 * This container is the ground
 * @author mtotz
 */
public class GroundContainer extends Panel
{
  /** list of game objects */
  private GameObjects gameObjects;
  /** the game screen */
  private GameScreen screen;
  
  /** creates a new groundcontainer */
  public GroundContainer(GameScreen screen, GameObjects gameObjects)
  {
    super("ground", 0, 0, (int) screen.getWidthInPixels(), (int) screen.getHeightInPixels());
    setMoveable(false);
    setCloseable(false);
    setFrame(false);
    setTitleBar(false);
    
    this.gameObjects = gameObjects;
    this.screen = screen;
  }

  /**
   * drops an item to the ground
   */
  protected boolean checkDropped(int x, int y, Draggable droppedObject)
  {
    // check all childpanels
    boolean dropped = super.checkDropped(x, y, droppedObject);
    
    if (dropped)
      return true;
    
    // is ot an entity?
    if (droppedObject instanceof MoveableEntityContainer)
    {
      MoveableEntityContainer container = (MoveableEntityContainer) droppedObject;
      
      Point2D point = screen.translate(new Point2D.Double(x,y));
      RPAction action = new RPAction();

      if (container.isContained())
      {
        // looks like an drop
        action.put("type","drop");
      }
      else
      {
        // it is a displace
        action.put("type","displace");
      }
      // fill 'moved from' parameters
      container.fillRPAction(action);
      // tell the server where the item goes to
      action.put("x",(int) point.getX());
      action.put("y",(int) point.getY());
      StendhalClient.get().send(action);
      
      return true;
    }

    // no valid item
    return false;
  }
  
  /** drags an item from the ground */
  protected Draggable getDragged(int x, int y)
  {
    Draggable other = super.getDragged(x, y);
    if (other != null)
      return other;
    
    Point2D point = screen.translate(new Point2D.Double(x,y));
    Entity object = gameObjects.at(point.getX(),point.getY());
    
    // only Items can be dragged
    if (object != null && object instanceof PassiveEntity)
    {
      return new MoveableEntityContainer(object, (int) point.getX(), (int) point.getY(), gameObjects);
    }

    return null;
  }

  public synchronized boolean onMouseDoubleClick(Point p)
  {
    // base class checks if the click is within a child
    if (super.onMouseClick(p))
    {
      // yes, click already processed
      return true;
    }
    
    // doubleclick is outside of all windows
    Point2D point = screen.translate(p);
    Entity entity=gameObjects.at(point.getX(),point.getY());

    if(entity==null)
    {
      RPAction action = new RPAction();
      // moveto action
      action.put("type","moveto");
      action.put("x",(int) point.getX());
      action.put("y",(int) point.getY());
      StendhalClient.get().send(action);
      return true;
    }
    
    return false;
  }
}
