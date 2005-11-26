package games.stendhal.server.maps;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.Portal;

public class rat_dungeon_000 
  {
  public rat_dungeon_000(StendhalRPWorld world, StendhalRPZone zone)
    {
    Portal portal=new Portal();
    zone.assignRPObjectID(portal);
    portal.setx(15);
    portal.sety(19);
    portal.setNumber(0);
    portal.setDestination("forest",1);
    zone.add(portal);
    zone.addPortal(portal);
    }  
  }
