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

import games.stendhal.client.GameObjects;
import games.stendhal.client.Sprite;
import games.stendhal.client.SpriteStore;
import games.stendhal.client.entity.Player;

import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * This is the panel where the character can be outfittet.
 *
 * @author mtotz
 */
public class Character extends Panel
{
  /** the stats panel */
  private TextPanel statsPanel;
  /** the stats panel */
  private Map<String, EntitySlot> slotPanels;
  /** cached player entity */
  private Player playerEntity; 
  
  /** the money we have */
  int money;
  
  /** Creates a new instance of Character */
  public Character(GameObjects gameObjects)
  {
    super("character", 640-132, 0, 132, 265);
    setTitleBar(true);
    setFrame(true);
    setMoveable(true);
    setMinimizeable(true);
    
    slotPanels = new HashMap<String,EntitySlot>();

    // now add the slots
    SpriteStore st = SpriteStore.get();
    Sprite slotSprite = st.getSprite("data/slot.png");
    
    int dist = 42; // the distance of the slot images with each other
    
    slotPanels.put("head", new EntitySlot("head",  slotSprite, dist*1, 0      , gameObjects));
    slotPanels.put("armor",new EntitySlot("armor", slotSprite, dist*1, dist   , gameObjects));
    slotPanels.put("lhand",new EntitySlot("lhand", slotSprite,      0, dist+10, gameObjects));
    slotPanels.put("rhand",new EntitySlot("rhand", slotSprite, dist*2, dist+10, gameObjects));
    slotPanels.put("lower",new EntitySlot("lower", slotSprite, dist  , dist*2 , gameObjects));
    slotPanels.put("feet", new EntitySlot("feet",  slotSprite, dist  , dist*3 , gameObjects));
    
    for (EntitySlot slot : slotPanels.values())
    {
      addChild(slot);
    }
    
    
    statsPanel = new TextPanel("stats",  5, dist*4, 170,100,"HP: ${hp}/${maxhp}\nATK: ${atk} (${atkxp})\nDEF: ${def} (${defxp})\nXP:${xp}\nCash: $${money}");
    statsPanel.setFrame(false);
    statsPanel.setTitleBar(false);
    addChild(statsPanel);
  }
  
  
  /** sets the player entity */
  public void setPlayer(Player playerEntity)
  {
    this.playerEntity = playerEntity;
  }
  
  /** refreshes the player stats and updates the text/slot panels */
  private void refreshPlayerStats()
  {
    if (playerEntity == null)
    {
      return;
    }
    money = 0;

    // taverse all slots
    for (RPSlot slot : playerEntity.getSlots())
    {
      String slotName = slot.getName();
      
      EntitySlot entitySlot = slotPanels.get(slotName);
      if (entitySlot != null)
      {
        entitySlot.clear();
        entitySlot.setParent(playerEntity);
        // found a gui element for this slot
        for (RPObject content : slot)
        {
          entitySlot.add(content);
        }
      }
      
      // count all money
      for (RPObject content : slot)
      {
        if (content.get("class").equals("money") && content.has("quantity"))
        {
          money += content.getInt("quantity");
        }
      }
    }

    setTitletext(playerEntity.getName());
    statsPanel.set("hp"   ,playerEntity.getHP());
    statsPanel.set("maxhp",playerEntity.getBase_hp());
    statsPanel.set("atk"  ,playerEntity.getAtk());
    statsPanel.set("def"  ,playerEntity.getDef());
    statsPanel.set("atkxp",playerEntity.getAtkXp());
    statsPanel.set("defxp",playerEntity.getDefXp());
    statsPanel.set("xp"   ,playerEntity.getXp());
    statsPanel.set("money",money);
    
  }
  
  /** refreshes the player stats and draws them */
  public Graphics draw(Graphics g)
  {
    refreshPlayerStats();
    return super.draw(g);
  }
}
