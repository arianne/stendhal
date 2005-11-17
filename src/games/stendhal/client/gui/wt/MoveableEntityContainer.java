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
import games.stendhal.client.Sprite;
import games.stendhal.client.entity.Entity;
import games.stendhal.client.gui.wt.core.Draggable;

import java.awt.Graphics;
import java.awt.Point;

import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

/** this container is used to drag the entities around */
public class MoveableEntityContainer implements Draggable
{
  /** current x-pos of the dragged item */
  private int      x;
  /** current y-pos of the dragged item */
  private int      y;
  /** the sprite */
  private Sprite   sprite;

  /** id of the moved object */
  private int      content;
  /** parent(container) of the moved object, may be null */
  private Entity   parent;
  /** the slot this item is in. makes only sense when parent is != null */
  private String   slot;

  /** x-pos of the item on the ground */
  private int      objectx;
  /** y-pos of the item on the ground */
  private int      objecty;

  /** constuctor to use when the item is inside a container */
  public MoveableEntityContainer(RPObject content, Entity parent,
      String slot, GameObjects gameObjects)
  {
    this.content = content.getID().getObjectID();
    this.parent  = parent;
    this.slot    = slot;
    this.sprite  = gameObjects.spriteType(content);
  }

  /** constuctor to use when the item is on the ground */
  public MoveableEntityContainer(Entity content, int x, int y,
      GameObjects gameObjects)
  {
    this.content = content.getID().getObjectID();
    this.objectx = x;
    this.objecty = y;
    this.parent  = null;
    this.sprite  = content.getSprite();
  }
  
  /** returns true when the item represented by this container is inside a slot */
  public boolean isContained()
  {
    return (parent != null);
  }

  /** fills the action with the appropiate 'move from' parameters */
  public void fillRPAction(RPAction action)
  {
    if (parent != null)
    {
      // the item is inside a container
      action.put("baseobject", parent.getID().getObjectID());
      action.put("baseslot", slot);
    } else
    {
      // the item is on the ground
      action.put("x", objectx);
      action.put("y", objecty);
    }
    action.put("baseitem", content);
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
    sprite.draw(g, x, y);
  }

}