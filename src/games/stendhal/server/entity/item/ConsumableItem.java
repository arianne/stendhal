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
package games.stendhal.server.entity.item;

import java.util.Map;
import java.util.List;
import games.stendhal.server.events.UseEvent;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.Player;


public class ConsumableItem extends StackableItem implements UseEvent
  {
  private int left;
  
  public ConsumableItem(String name, String clazz, String subclass, Map<String, String> attributes)
    {
    super(name,clazz, subclass, attributes);
    left=getAmount();
    }
  
  public int getAmount()
    {
    return getInt("amount");
    }
  
  public int getFrecuency()
    {
    return getInt("frequency");
    }

  public int getRegen()
    {
    return getInt("regen");
    }  
  
  public void consume()
    {
    left-=getRegen();
    }
  
  public boolean consumed()
    {
    if(getRegen()>0)
      {
      return left<=0;
      }
    else
      {
      return left>=0;
      }
    }

  public void onUsed(RPEntity user)
    {
    Player player=(Player)user;
    player.consumeItem((ConsumableItem)this);
    getWorld().modify(player);
    }  
  }