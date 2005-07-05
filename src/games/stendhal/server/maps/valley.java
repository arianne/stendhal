package games.stendhal.server.maps;

import marauroa.common.game.*;
import games.stendhal.server.*;
import games.stendhal.server.entity.*;

public class valley
  {
  public valley(StendhalRPZone zone)
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
