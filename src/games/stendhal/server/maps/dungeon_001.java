package games.stendhal.server.maps;

import java.util.*;
import marauroa.common.game.*;
import games.stendhal.server.*;
import games.stendhal.server.entity.*;
import games.stendhal.server.entity.npc.*;
import games.stendhal.server.entity.item.*;
import games.stendhal.server.entity.creature.*;

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

//    NPC npc=new OrcBuyerNPC()
//      {
//      protected void createPath()
//        {
//        List<Path.Node> nodes=new LinkedList<Path.Node>();
//        nodes.add(new Path.Node(67,12));
//        nodes.add(new Path.Node(59,12));
//        nodes.add(new Path.Node(59,16));
//        nodes.add(new Path.Node(67,16));
//        setPath(nodes,true);
//        }
//      };
//    zone.assignRPObjectID(npc);
//    npc.setName("Tor'Koom");
//    npc.setx(67);
//    npc.sety(12);
//    npc.setBaseHP(1000);
//    npc.setHP(npc.getBaseHP());
//    zone.add(npc);    
//    zone.addNPC(npc);
  
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
