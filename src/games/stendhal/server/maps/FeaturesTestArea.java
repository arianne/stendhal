package games.stendhal.server.maps;

import games.stendhal.common.Rand;
import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.*;
import games.stendhal.server.rule.defaultruleset.*;

import marauroa.common.game.IRPZone;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;


public class FeaturesTestArea implements IContent
  {
  private StendhalRPWorld world;
  
  public FeaturesTestArea(StendhalRPWorld world) throws org.xml.sax.SAXException, java.io.IOException
    {
    StendhalRPZone zone=(StendhalRPZone)world.getRPZone(new IRPZone.ID("int_pathfinding"));

    Portal portal=new Door();
    zone.assignRPObjectID(portal);
    portal.setx(50);
    portal.sety(10);
    portal.setNumber(0);
    portal.setDestination("int_pathfinding",1);
    zone.addPortal(portal);

    portal=new Portal();
    zone.assignRPObjectID(portal);
    portal.setx(50);
    portal.sety(12);
    portal.setNumber(1);
    portal.setDestination("int_pathfinding",0);
    zone.addPortal(portal);
    
    DefaultItem item=new DefaultItem("key","golden","key_golden",-1);
    item.setWeight(1);
    List<String> slots=new LinkedList<String>();
    slots.add("bag");
    item.setEquipableSlots(slots);
    }
  }
