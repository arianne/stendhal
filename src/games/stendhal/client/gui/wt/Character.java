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
 * Character.java
 *
 * Created on 19. Oktober 2005, 21:06
 */

package games.stendhal.client.gui.wt;

import games.stendhal.client.Sprite;
import games.stendhal.client.SpriteStore;

/**
 * This is the panel where the character can be outfittet.
 *
 * @author mtotz
 */
public class Character extends Panel
{
  
  /** Creates a new instance of Character */
  public Character()
  {
    super("character", 640-170, 0, 170, 300);
    setTitleBar(true);
    setFrame(true);
    setMoveable(true);
    setMinimizeable(true);
    

    // now add the slots
    SpriteStore st = SpriteStore.get();
    Sprite slot = st.getSprite("data/slot.png");
    
    addChild(new EntitySlot("head",  slot,  50,   0));
    addChild(new EntitySlot("torso", slot,  50,  50));
    addChild(new EntitySlot("lhand", slot,   0,  50));
    addChild(new EntitySlot("rhand", slot, 100,  50));
    addChild(new EntitySlot("lower", slot,  50, 100));
    addChild(new EntitySlot("feet",  slot,  50, 150));
    
  }
  
}
