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
import java.awt.Graphics;
import java.awt.Point;

import marauroa.common.game.RPObject;

/**
 * This is a container which contains exactly one Entity. The name do not
 * have to be unique. Items can be dropped to this container when it is empty.
 * 
 * Note that the onDrop() method simply informs the server that the item was
 * dropped. Whatever the server decides will be the next content of this
 * EntitySlot
 *
 * @author mtotz
 */
public class EntitySlot extends Panel implements DropTarget
{
  /** the (background) sprite for this slot */
  private Sprite graphic;
  /** the content of the slot */
  private RPObject content;
  
  /** need this to find the sprite for each RPObject */
  private GameObjects gameObjects;
  
  /** Creates a new instance of RPObjectSlot */
  public EntitySlot(String name, Sprite graphic, int x, int y, GameObjects gameObjects)
  {
    super(name, x,y, graphic.getWidth(), graphic.getHeight());
    this.graphic = graphic;
    this.gameObjects = gameObjects;
  }

  /** called when an object is dropped. */
  public boolean onDrop(Draggable droppedObject)
  {
    System.out.println("dropped "+droppedObject);
    
    if (droppedObject instanceof MoveableEntityContainer)
    {
      MoveableEntityContainer container = (MoveableEntityContainer) droppedObject;
      this.content = container.getContent();
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
   * @param g graphics where to render to
   * @return a graphics object for deriving classes to use. It is already 
   *         clipped to the correct client region
   */
  public Graphics draw(Graphics g)
  {
    Graphics childArea = super.draw(g);
    
    // draw the background image
    graphic.draw(childArea,0,0);
    // draw the content (if there is any)
    if (content != null)
    {
      Sprite sprite = gameObjects.spriteType(content);
      // be sure to center the sprite
      int x = (getWidth() - sprite.getWidth()) / 2;
      int y = (getHeight() - sprite.getHeight()) / 2;
      sprite.draw(childArea,x,y);
    }
    
    return childArea;
  }
  
  /**
   * returns a draggable object
   */
  protected Draggable getDragged(int x, int y)
  {
    return (content!= null) ? (new MoveableEntityContainer(content)) : null;
  }
  

  /** this container is used to drag the entities around */
  private class MoveableEntityContainer implements Draggable
  {
    private int startX;
    private int startY;
    
    private int x;
    private int y;
    
    private RPObject content;
    
    public MoveableEntityContainer(RPObject content)
    {
      this.content = content;
    }
    
    /** returns the content */
    public RPObject getContent()
    {
      return content;
    }
    
    /** drag started */
    public boolean dragStarted()
    {
      startX = 0;
      startY = 0;
 
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
      gameObjects.spriteType(this.content).draw(g,startX+x,startY+y);
      
    }
    
  }
  
}
