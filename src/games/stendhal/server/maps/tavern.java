package games.stendhal.server.maps;

import marauroa.common.game.*;
import games.stendhal.server.*;
import games.stendhal.server.entity.*;
import games.stendhal.server.entity.npc.*;

public class tavern 
  {
  public tavern(StendhalRPZone zone)
    {
    Portal portal=new Portal();
    zone.assignRPObjectID(portal);
    portal.setx(20);
    portal.sety(1);
    portal.setNumber(0);
    portal.setDestination("village",0);
    zone.add(portal);
    zone.addPortal(portal);

    NPC npc=new BeggarNPC();
    zone.assignRPObjectID(npc);
    npc.setName("Margaret");
    npc.setx(17);
    npc.sety(12);
    npc.setbaseHP(100);
    zone.add(npc);    
    zone.addNPC(npc);
    }  
  }
