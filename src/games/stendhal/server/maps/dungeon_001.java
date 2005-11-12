package games.stendhal.server.maps;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.Portal;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.npc.NPC;
import games.stendhal.server.entity.npc.Behaviours;
import games.stendhal.server.entity.npc.SpeakerNPC;
import java.util.*;
import games.stendhal.server.Path;

public class dungeon_001 
  {
  public dungeon_001(StendhalRPZone zone)
    {
    Portal portal=new Portal();
    zone.assignRPObjectID(portal);
    portal.setx(5);
    portal.sety(7);
    portal.setNumber(0);
    portal.setDestination("dungeon_000",1);
    zone.add(portal);
    zone.addPortal(portal);

    portal=new Portal();
    zone.assignRPObjectID(portal);
    portal.setx(67);
    portal.sety(118);
    portal.setNumber(1);
    portal.setDestination("forest",0);
    zone.add(portal);
    zone.addPortal(portal);

    Item itemo = zone.getWorld().getRuleManager().getEntityManager().getItem("sword");
    zone.assignRPObjectID(itemo);
    itemo.setx(72);
    itemo.sety(48);
    zone.add(itemo);

    itemo = zone.getWorld().getRuleManager().getEntityManager().getItem("shield");
    zone.assignRPObjectID(itemo);
    itemo.setx(75);
    itemo.sety(83);
    zone.add(itemo);
    }  
  }
