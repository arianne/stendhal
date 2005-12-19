package games.stendhal.server.maps;

import games.stendhal.server.*;
import games.stendhal.server.entity.*;
import marauroa.common.game.IRPZone;

public class Afterlife implements IContent
  {
  public Afterlife(StendhalRPWorld world)
    {
    StendhalRPZone zone=(StendhalRPZone)world.getRPZone(new IRPZone.ID("int_afterlife"));
    
    Portal portal=new Portal();
    zone.assignRPObjectID(portal);
    portal.setx(30);
    portal.sety(6);
    portal.setNumber(0);
    portal.setDestination("0_semos_city",1);
    zone.add(portal);
    zone.addPortal(portal);

    portal=new Portal();
    zone.assignRPObjectID(portal);
    portal.setx(31);
    portal.sety(6);
    portal.setNumber(0);
    portal.setDestination("0_semos_city",1);
    zone.add(portal);
    zone.addPortal(portal);

    portal=new Portal();
    zone.assignRPObjectID(portal);
    portal.setx(32);
    portal.sety(6);
    portal.setNumber(0);
    portal.setDestination("0_semos_city",1);
    zone.add(portal);
    zone.addPortal(portal);
    
    Sign sign=new Sign();
    zone.assignRPObjectID(sign);
    sign.setx(29);
    sign.sety(22);
    sign.setText("I regret to tell you that you have died!|You have lost some of your items and 10% of your eXPerience points.|Be more careful next time. On the up side you can now return to city.");    
    zone.add(sign);

    sign=new Sign();
    zone.assignRPObjectID(sign);
    sign.setx(33);
    sign.sety(22);
    sign.setText("I regret to tell you that you have died!|You have lost some of your items and 10% of your eXPerience points.|Be more careful next time. On the up side you can now return to city.");    
    zone.add(sign);

    }
  }
