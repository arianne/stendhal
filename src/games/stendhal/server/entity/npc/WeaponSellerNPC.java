/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.npc;

import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.rule.defaultruleset.DefaultItem;
import marauroa.common.Log4J;
import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.IRPZone;
import org.apache.log4j.Logger;


public abstract class WeaponSellerNPC extends SpeakerNPC 
  {
  /** the logger instance. */
  private static final Logger logger = Log4J.getLogger(WeaponSellerNPC.class);

  final private double SPEED=0.2;
  private int swordAmount;
  private int clubAmount;
  private int shieldAmount;
  private int armorAmount;
  
  public WeaponSellerNPC() throws AttributeNotFoundException
    {
    super();
    swordAmount=0;
    clubAmount=0;
    shieldAmount=0;
    armorAmount=0;
    put("class","weaponsellernpc");
    }
  
  private boolean sell(Player player, String itemName)
  {
    // find item in stock list
    SellableItem[] items = SellableItem.values();
    String uppercaseItemName = itemName.toUpperCase();
    for (SellableItem itemEnum : items)
    {
      if (itemEnum.name().equals(uppercaseItemName))
      {
        // found it
        Item item = itemEnum.getItem();
        // has the player already one of these items?
        if (!player.hasItem(item.getPossibleSlots(), item.getItemClass()))
        {
          if (player.getXP() < itemEnum.getPrice())
          {
            say("A real pity! You don't have enough XP!");
            return false;
          }
          logger.debug("Selling a "+itemName+" to player "+player.getName());
          
          Item theItem = (Item) item;
          theItem.put("zoneid",player.get("zoneid"));
          IRPZone zone=world.getRPZone(getID());
          zone.assignRPObjectID(theItem);
          
          if (player.equip(item))
          {
            player.setXP(player.getXP()-itemEnum.getPrice());
            world.modify(player);

            say("Congratulations! Here is your "+itemName+"!");
            return true;
          }
          else
          {
            say("Sorry, but you cannot equip the "+itemName+".");
          }
        }
        else
        {
          say("you already have a "+item.getItemClass()+". One is enough for you.");
        }
        // item found, but player cannot equip the item
        return false;
      }
    }
    // item not found
    return false;
  }

  public boolean chat(Player player) throws AttributeNotFoundException
    {
    String text=player.get("text").toLowerCase();
    if(text.contains("offer"))
      {
      say("You pay equipment with XP points: a club(1000), an armor(5000), a shield(10000) and a sword(20000).");
      return true;
      }
    else if(text.contains("buy")||text.contains("purchase"))
      {
      if(text.contains("club") && sell(player,"club"))
        {
        clubAmount++;        
        }
      else if(text.contains("armor") && sell(player,"armor"))
        {
        armorAmount++;        
        }
      else if(text.contains("shield") && sell(player,"shield"))
        {
        shieldAmount++;        
        }
      else if(text.contains("sword") && sell(player,"sword"))
        {
        swordAmount++;        
        }        
        
      return true;
      }
    else if(text.contains("help"))
      {
      say("I sell weapons like SWORD, CLUB, SHIELD and ARMOR, try to BUY one from me.");
      return true;
      }
    else if(text.contains("job")||text.contains("work")||text.contains("quest"))
      {
      say("I sell weapons and armors. My equipment will turn you in an invencible warrior!");
      return true;
      }
    else if(text.contains("sold"))
      {
      say("I have sold "+swordAmount+" sword(s), "+clubAmount+" club(s), "+shieldAmount+" shields), "+armorAmount+" armor(s), .");
      }
    else if(text.contains("bye")||text.contains("cya"))
      {
      say("Bye "+player.get("name"));
      return true;
      }
    else if(text.contains("hi")||text.contains("hello"))
      {
      say("Come here and buy your equipment! Check my OFFER");
      return true;
      }
    
    return false;
    }
  
  /** all item this npc sells */
  private enum SellableItem
  {
    CLUB  (1000 ,DefaultItem.CLUB.getItem()),
    ARMOR (5000 ,DefaultItem.ARMOR.getItem()),
    SHIELD(10000,DefaultItem.SHIELD.getItem()),
    SWORD (20000,DefaultItem.SWORD.getItem());
    
    private int price;
    private Item item;
    
    private SellableItem(int price, Item item)
    {
      this.price = price;
      this.item  = item;
    }

    public int getPrice()
    {
      return price;
    }

    public Item getItem()
    {
      return item;
    }
  }
  }
