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

import java.util.HashMap;
import java.util.Map;

/**
 * This is the inventory
 * 
 * @author mtotz
 */
public class Inventory extends Panel
{
  /** width of the inventory panel in slots */
  private static final int WIDTH = 3;
  /** height of the inventory panel in slots */
  private static final int HEIGHT = 4;
  
  
  /** the slots */
  private Map<String,EntitySlot> slotPanels;

  /** creates the inventory panel */
  public Inventory(GameObjects gameObjects)
  {
    super("inventory", 0, 300, 100, 100);
    setTitletext("Inventory");
    setTitleBar(true);
    setFrame(true);
    setMinimizeable(true);
    setCloseable(true);
    
    SpriteStore st = SpriteStore.get();
    Sprite slotSprite = st.getSprite("data/slot.png");
    
    int width = slotSprite.getWidth();
    int height = slotSprite.getHeight();

    slotPanels = new HashMap<String,EntitySlot>();
    // add the slots
    for (int x = 0; x < WIDTH; x++)
    {
      for (int y = 0; y < HEIGHT; y++)
      {
        String name = "bag-"+x+","+y;
        slotPanels.put(name, new EntitySlot(name,  slotSprite, x*width+x, y*height+y, gameObjects));        
      } 
    }

    this.resizeToFitClientArea(WIDTH*width+(WIDTH-1),HEIGHT*height+(HEIGHT-1));

    for (EntitySlot slot : slotPanels.values())
    {
      addChild(slot);
    }
  }

}
