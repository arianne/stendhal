package games.stendhal.server.maps;

import marauroa.common.game.*;
import games.stendhal.server.*;
import games.stendhal.server.entity.*;
import games.stendhal.server.entity.npc.*;
import java.util.*;

public class tavern 
  {
  public tavern(StendhalRPZone zone)
    {
    Portal portal=new Portal();
    zone.assignRPObjectID(portal);
    portal.setx(20);
    portal.sety(1);
    portal.setNumber(0);
    portal.setDestination("city",1);
    zone.add(portal);
    zone.addPortal(portal);

    NPC npc=new TavernBarMaidNPC();
    zone.assignRPObjectID(npc);
    npc.setName("Margaret");
    npc.setx(17);
    npc.sety(12);
    npc.setBaseHP(100);
    npc.setHP(npc.getBaseHP());
    zone.add(npc);    
    zone.addNPC(npc);

    npc=new WeaponSellerNPC()
      {
      protected void createPath()
        {
        List<Path.Node> nodes=new LinkedList<Path.Node>();
        nodes.add(new Path.Node(2,14));
        nodes.add(new Path.Node(2,15));
        nodes.add(new Path.Node(5,15));
        nodes.add(new Path.Node(5,14));
        setPath(nodes,true);
        }
      };

    zone.assignRPObjectID(npc);
    npc.setName("Xin Blanca");
    npc.setx(2);
    npc.sety(14);
    npc.setBaseHP(100);
    npc.setHP(npc.getBaseHP());
    zone.add(npc);    
    zone.addNPC(npc);
    }  
  }
