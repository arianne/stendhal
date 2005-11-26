package games.stendhal.server.maps;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.Portal;

public class valley
  {
  public valley(StendhalRPWorld world, StendhalRPZone zone)
    {
    Portal portal=new Portal();
    zone.assignRPObjectID(portal);
    portal.setx(46);
    portal.sety(1);
    portal.setNumber(0);
    portal.setDestination("city",2);
    zone.add(portal);
    zone.addPortal(portal);
    }  
  }
