package games.stendhal.server.maps;

import marauroa.common.game.*;
import games.stendhal.server.*;
import games.stendhal.server.entity.*;

public class forest 
  {
  public forest(StendhalRPZone zone)
    {
    Portal portal=new Portal();
    zone.assignRPObjectID(portal);
    portal.setx(103);
    portal.sety(65);
    portal.setNumber(0);
    portal.setDestination("dungeon_001",1);
    zone.add(portal);
    zone.addPortal(portal);
    }  
  }
