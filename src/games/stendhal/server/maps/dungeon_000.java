package games.stendhal.server.maps;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Portal;

public class dungeon_000 
  {
  public dungeon_000(StendhalRPZone zone)
    {
    Portal portal=new Portal();
    zone.assignRPObjectID(portal);
    portal.setx(27);
    portal.sety(36);
    portal.setNumber(0);
    portal.setDestination("city",0);
    zone.add(portal);
    zone.addPortal(portal);

    portal=new Portal();
    zone.assignRPObjectID(portal);
    portal.setx(42);
    portal.sety(43);
    portal.setNumber(1);
    portal.setDestination("dungeon_001",0);
    zone.add(portal);
    zone.addPortal(portal);
    }  
  }
