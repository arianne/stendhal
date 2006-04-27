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
import marauroa.common.game.RPClass;
import games.stendhal.server.StendhalRPAction;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.events.UseEvent;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.Player;


public class Scroll extends ConsumableItem implements UseEvent
  {
  public Scroll(String name, String clazz, String subclass,
      Map<String, String> attributes)
    {
    super(name,clazz,subclass,attributes);
    }

  public boolean consumed()
    {
    return (true);
    }

  public boolean isStackable(Stackable other)
    {
    StackableItem otheri = (StackableItem) other;
    if(getItemSubclass().equals("black"))
      {
      // black scroll can only be stacked if they refer to the same location
      if(has("infostring") && otheri.has("infostring"))
        return (get("infostring").equals(otheri.get("infostring")));
      return (false);
      }
    return getItemClass().equals(otheri.getItemClass())
        && getItemSubclass().equals(otheri.getItemSubclass());
    }

  public void onUsed(RPEntity user)
    {
    StendhalRPZone zone;
    Player player = (Player) user;
    player.consumeItem((ConsumableItem) this);
    if(getName().equals("empty_scroll"))
      {
      Item item = getWorld().getRuleManager().getEntityManager().getItem(
          "marked_scroll");
      zone = (StendhalRPZone) getWorld().getRPZone(player.get("zoneid"));
      zone.assignRPObjectID(item);
      item.setx(player.getx());
      item.sety(player.gety());
      item.put("infostring","" + player.getID().getZoneID() + " "
          + player.getx() + " " + player.gety());
      zone.add(item);
      } else
      {
      zone = (StendhalRPZone) world.getRPZone("0_semos_city");
      int x = 30, y = 40;
      if(has("infostring"))
        {
        String infostring = get("infostring");
        java.util.StringTokenizer st = new java.util.StringTokenizer(infostring);
        if(st.countTokens() == 3)
          {
          zone = (StendhalRPZone) world.getRPZone(st.nextToken());
          x = Integer.parseInt(st.nextToken());
          y = Integer.parseInt(st.nextToken());
          }
        }
      if(StendhalRPAction.placeat(zone,player,x,y))
        {
        StendhalRPAction.changeZone(player,zone.getID().getID());
        StendhalRPAction.transferContent(player);
        }
      }
    getWorld().modify(player);
    }

  }