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

import games.stendhal.client.Sprite;
import java.awt.Graphics;

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
  /** the sprite for this slot */
  private Sprite graphic;
  
  /** Creates a new instance of RPObjectSlot */
  public EntitySlot(String name, Sprite graphic, int x, int y)
  {
    super(name, x,y, graphic.getWidth(), graphic.getHeight());
    this.graphic = graphic;
  }

  /** called when an object is dropped. */
  public boolean onDrop(Draggable droppedObject)
  {
    return false;
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
    
    graphic.draw(childArea,0,0);
    
    return childArea;
  }
  

  
}
