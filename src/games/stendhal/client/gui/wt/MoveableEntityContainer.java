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

import games.stendhal.client.GameObjects;

import java.awt.Graphics;
import java.awt.Point;

import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

/** this container is used to drag the entities around */
public class MoveableEntityContainer implements Draggable
{
  private int         x;
  private int         y;

  /** this is the moved object */
  private RPObject    content;
  /** parent(container) of the moved object, may be null */
  private RPObject    parent;
  /** the slot this item is in. makes only sense when parent is != null */
  private String      slot;

  /** need this to find the sprite for each RPObject */
  private GameObjects gameObjects;

  public MoveableEntityContainer(RPObject content, RPObject parent,
      String slot, GameObjects gameObjects)
  {
    this.content = content;
    this.parent = parent;
    this.slot = slot;
    this.gameObjects = gameObjects;
  }

  /** returns the content */
  public RPObject getContent()
  {
    return content;
  }
  
  /** returns the parent */
  public RPObject getParent()
  {
    return parent;
  }

  /** returns the slot */
  public String getSlot()
  {
    return slot;
  }
  
  /** fills the action with the apropiate 'move from' parameters*/
  public void fillRPAction(RPAction action)
  {
    if (parent != null)
    {
      action.put("baseobject",parent.getID().getObjectID());
      action.put("baseslot",slot);
    }
    action.put("baseitem",content.getID().getObjectID());
  }
  
  /** drag started */
  public boolean dragStarted()
  {
    return true;
  }

  /** drag finished */
  public boolean dragFinished(Point p)
  {
    return true;
  }

  /** moved */
  public boolean dragMoved(Point p)
  {
    x = p.x;
    y = p.y;
    return true;
  }

  /**
   * draws the entity
   */
  public void drawDragged(Graphics g)
  {
    gameObjects.spriteType(this.content).draw(g, x, y);
  }

}