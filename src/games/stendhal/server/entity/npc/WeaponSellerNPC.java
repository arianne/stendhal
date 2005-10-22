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
import games.stendhal.server.rule.defaultruleset.DefaultItem;
import games.stendhal.server.entity.item.*;
import marauroa.common.game.*;
import java.util.*;
import marauroa.common.Log4J;
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
  
  private int playermoney(Player player)
    {
    int money=0;
    
    Iterator<RPSlot> it=player.slotsIterator();
    while(it.hasNext())
      {
      RPSlot slot=(RPSlot)it.next();
      for(RPObject object: slot)
        {
        if(object instanceof Money)
          {
          money+=((Money)object).getQuantity();
          }
        }
      }
    
    return money;
    }

  private boolean chargePlayer(Player player, int amount)
    {
    int left=amount;
    
    Iterator<RPSlot> it=player.slotsIterator();
    while(it.hasNext() && left!=0)
      {
      RPSlot slot=(RPSlot)it.next();
      for(RPObject object: slot)
        {
        if(object instanceof Money)
          {
          int quantity=((Money)object).getQuantity();
          if(left>=quantity)
            {
            slot.remove(object.getID());
            left-=quantity;
            }
          else
            {
            ((Money)object).setQuantity(quantity-left);
            left=0;
            break;
            }
          }
        }
      }
    
    world.modify(player);
    
    return left==0;
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

        if (playermoney(player) < itemEnum.getSellPrice())
          {
          say("A real pity! You don't have enough money!");
          return false;
          }
          
        logger.debug("Selling a "+itemName+" to player "+player.getName());
        
        Item theItem = (Item) item;
        theItem.put("zoneid",player.get("zoneid"));
        IRPZone zone=world.getRPZone(getID());
        zone.assignRPObjectID(theItem);
        
        if(player.equip(item))
          {
          chargePlayer(player,itemEnum.getSellPrice());

          say("Congratulations! Here is your "+itemName+"!");
          return true;
          }
        else
          {
          say("Sorry, but you cannot equip the "+itemName+".");
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
      // Build the offer dinamically
      StringBuffer st=new StringBuffer("I can offer you: ");

      for(SellableItem item: SellableItem.values())
        {
        st.append("a "+item);
        st.append("("+item.getSellPrice()+"),");
        }
        
      say(st.toString());
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
          
    CLUB  (10 ,3  ,DefaultItem.CLUB.getItem()),
    ARMOR (50 ,15 ,DefaultItem.ARMOR.getItem()),
    SHIELD(100,30 ,DefaultItem.SHIELD.getItem()),
    SWORD (200,60 ,DefaultItem.SWORD.getItem());
    
    private int sellPrice;
    private int buyPrice;
    private Item item;
    
    private SellableItem(int sellPrice, int buyPrice, Item item)
    {
      this.sellPrice = sellPrice;
      this.buyPrice = buyPrice;
      this.item  = item;
    }

    public int getSellPrice()
    {
      return sellPrice;
    }

    public int getBuyPrice()
    {
      return buyPrice;
    }

    public Item getItem()
    {
      return item;
    }
  }
  }
