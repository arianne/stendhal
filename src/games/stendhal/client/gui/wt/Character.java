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
import games.stendhal.client.StendhalClient;
import games.stendhal.client.entity.Money;
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
  /** the player */
  private RPObject player;
  /** the money we have */
  int money;
  
  /** need this to find the sprite for each RPObject */
  private GameObjects gameObjects;
  
  /** Creates a new instance of Character */
  public Character(GameObjects gameObjects)
  {
    super("character", 640-132, 0, 132, 265);
    this.gameObjects = gameObjects;
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
    
    player = StendhalClient.get().getPlayer();
  }
  
  
  /** sets the player entity */
  public void setPlayer(RPObject player)
  {
    this.player = player;
  }
  
  /** refreshes the player stats and updates the text/slot panels */
  private void refreshPlayerStats()
  {
    if (player == null)
    {
      return;
    }
    

    money = 0;

    // taverse all slots
    for (RPSlot slot : player.slots())
    {
      String slotName = slot.getName();
      
      EntitySlot entitySlot = slotPanels.get(slotName);
      if (entitySlot != null)
      {
        entitySlot.clear();
        // found a gui element for this slot
        for (RPObject content : slot)
        {
          entitySlot.add(content);
        }
      }
      
      // found a gui element for this slot
      for (RPObject content : slot)
      {
        if (content.get("class").equals("money") && content.has("quantity"))
        {
          money += content.getInt("quantity");
        }
      }
      
    }
    
    setName(player.get("name"));
    statsPanel.set("hp"   ,player.get("hp"));
    statsPanel.set("maxhp",player.get("base_hp"));
    statsPanel.set("atk"  ,player.get("atk"));
    statsPanel.set("def"  ,player.get("def"));
    statsPanel.set("atkxp",player.get("atk_xp"));
    statsPanel.set("defxp",player.get("def_xp"));
    statsPanel.set("xp"   ,player.get("xp"));
    statsPanel.set("money",money);
    
  }
  
  /** refreshes the player stats and draws them */
  public Graphics draw(Graphics g)
  {
    player = StendhalClient.get().getPlayer();
    refreshPlayerStats();
    return super.draw(g);
  }
}
