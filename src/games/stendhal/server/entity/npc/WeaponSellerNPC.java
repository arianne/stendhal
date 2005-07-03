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

import games.stendhal.common.*;
import marauroa.common.*;
import marauroa.common.game.*;
import marauroa.server.game.*;
import games.stendhal.server.*;
import java.util.*;

import games.stendhal.server.entity.*;
import games.stendhal.server.entity.item.*;
import games.stendhal.server.entity.creature.*;
import games.stendhal.server.entity.npc.*;

public abstract class WeaponSellerNPC extends SpeakerNPC 
  {
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
    if(itemName.equals("club") && !player.hasWeapon())
      {      
      if(player.getXP()<1000)
        {
        say("A real pity! You don't have enough XP!");
        return false;
        }
        
      Item item=new Club();
      Logger.trace("WeaponSellerNPC::chat","D","Selling a "+itemName+" to player");
      
      item.put("zoneid",player.get("zoneid"));
      player.equip(item);      
      
      player.setXP(player.getXP()-1000);
      world.modify(player);
  
      say("Congratulations! Here is your "+itemName+"!");
      return true;
      }        
    else if(itemName.equals("armor") && !player.hasArmor())
      {      
      if(player.getXP()<5000)
        {
        say("A real pity! You don't have enough XP!");
        return false;
        }
        
      Item item=new Armor();
      Logger.trace("WeaponSellerNPC::chat","D","Selling a "+itemName+" to player");
  
      item.put("zoneid",player.get("zoneid"));
      player.equip(item);      
      
      player.setXP(player.getXP()-5000);
      world.modify(player);
  
      say("Congratulations! Here is your "+itemName+"!");
      return true;
      }        
    else if(itemName.equals("shield") && !player.hasShield())
      {      
      if(player.getXP()<10000)
        {
        say("A real pity! You don't have enough XP!");
        return false;
        }
        
      Item item=new Shield();
      Logger.trace("WeaponSellerNPC::chat","D","Selling a "+itemName+" to player");
  
      item.put("zoneid",player.get("zoneid"));
      player.equip(item);      
      
      player.setXP(player.getXP()-10000);
      world.modify(player);
  
      say("Congratulations! Here is your "+itemName+"!");
      return true;
      }        
    else if(itemName.equals("sword") && !player.hasWeapon())
      {      
      if(player.getXP()<20000)
        {
        say("A real pity! You don't have enough XP!");
        return false;
        }
        
      Item item=new Sword();
      Logger.trace("WeaponSellerNPC::chat","D","Selling a "+itemName+" to player");
  
      item.put("zoneid",player.get("zoneid"));
      player.equip(item);      
      
      player.setXP(player.getXP()-20000);
      world.modify(player);
  
      say("Congratulations! Here is your "+itemName+"!");
      return true;
      }        
    
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
  }
