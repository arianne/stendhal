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
/*
 * EntitySlot.java
 *
 * Created on 19. Oktober 2005, 21:14
 */

package games.stendhal.client.gui.wt;

import games.stendhal.client.GameObjects;
import games.stendhal.client.Sprite;
import games.stendhal.client.StendhalClient;

import java.awt.Graphics;

import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

/**
 * This is a container which contains exactly one Entity. The name do not have
 * to be unique. Items can be dropped to this container when it is empty. Note
 * that the onDrop() method simply informs the server that the item was dropped.
 * Whatever the server decides will be the next content of this EntitySlot
 * 
 * @author mtotz
 */
public class EntitySlot extends Panel implements DropTarget
{
  /** the (background) sprite for this slot */
  private Sprite      graphic;
  /** the content of the slot */
  private RPObject    content;
  /** the parent of the slot */
  private RPObject    parent;

  /** need this to find the sprite for each RPObject */
  private GameObjects gameObjects;

  /** Creates a new instance of RPObjectSlot */
  public EntitySlot(String name, Sprite graphic, int x, int y,
      GameObjects gameObjects)
  {
    super(name, x, y, graphic.getWidth(), graphic.getHeight());
    this.graphic = graphic;
    this.gameObjects = gameObjects;
  }

  /** */
  public void setParent(RPObject parent)
  {
    this.parent = parent;
  }

  /** called when an object is dropped. */
  public boolean onDrop(Draggable droppedObject)
  {
    if (droppedObject instanceof MoveableEntityContainer && parent != null)
    {
      MoveableEntityContainer container = (MoveableEntityContainer) droppedObject;
      RPAction action = new RPAction();

      // looks like an equip
      action.put("type","equip");
      // fill 'moved from' parameters
      container.fillRPAction(action);
      // tell the server who we are
      action.put("targetobject",parent.getID().getObjectID());
      action.put("targetslot",getName());
      StendhalClient.get().send(action);
    }

    return false;
  }

  /** clears the content of this slit */
  public void clear()
  {
    content = null;
  }

  /** adds an object to this slot */
  public void add(RPObject object)
  {
    content = object;
  }

  /**
   * draws the panel into the graphics object
   * 
   * @param g
   *          graphics where to render to
   * @return a graphics object for deriving classes to use. It is already
   *         clipped to the correct client region
   */
  public Graphics draw(Graphics g)
  {
    Graphics childArea = super.draw(g);

    // draw the background image
    graphic.draw(childArea, 0, 0);
    // draw the content (if there is any)
    if (content != null)
    {
      Sprite sprite = gameObjects.spriteType(content);
      // be sure to center the sprite
      int x = (getWidth() - sprite.getWidth()) / 2;
      int y = (getHeight() - sprite.getHeight()) / 2;
      sprite.draw(childArea, x, y);
    }

    return childArea;
  }

  /**
   * returns a draggable object
   */
  protected Draggable getDragged(int x, int y)
  {
    if (content != null)
      return new MoveableEntityContainer(content, parent, getName(),gameObjects);

    return null;
  }

}
