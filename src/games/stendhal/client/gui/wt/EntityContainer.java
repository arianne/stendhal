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
import games.stendhal.client.SpriteStore;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * This is the inventory
 * 
 * @author mtotz
 */
public class EntityContainer extends Panel
{
  /** the panels for each item */
  private List<EntitySlot> slotPanels;

  /** creates the inventory panel */
  public EntityContainer(GameObjects gameObjects, String name, int width,
      int height)
  {
    super(name, 0, 300, 100, 100);
    setTitletext(name);
    setTitleBar(true);
    setFrame(true);
    setMinimizeable(true);
    setCloseable(true);

    SpriteStore st = SpriteStore.get();
    Sprite slotSprite = st.getSprite("data/slot.png");

    int spriteWidth = slotSprite.getWidth();
    int spriteHeight = slotSprite.getHeight();

    slotPanels = new ArrayList<EntitySlot>();
    // add the slots
    for (int x = 0; x < width; x++)
    {
      for (int y = 0; y < height; y++)
      {
        EntitySlot entitySlot = new EntitySlot(name, slotSprite, x
            * spriteWidth + x, y * spriteHeight + y, gameObjects);
        slotPanels.add(entitySlot);
      }
    }

    // resize panel
    this.resizeToFitClientArea(width * spriteWidth + (width - 1), height
        * spriteHeight + (height - 1));

    for (EntitySlot entitySlot : slotPanels)
    {
      addChild(entitySlot);
    }
  }

  /** sets the player entity */
  public void setSlot(RPObject.ID parent, RPSlot rpslot)
  {
    Iterator<RPObject> it = (rpslot != null) ? rpslot.iterator() : null;

    for (EntitySlot entitySlot : slotPanels)
    {
      // be sure to update the name
      entitySlot.setName(rpslot.getName());
      // remove old objects
      entitySlot.clear();
      // tell 'em the the parent
      entitySlot.setParent(parent);
      // add new rpobjects
      if (it != null && it.hasNext())
      {
        entitySlot.add(it.next());
      }
    }
  }

}
