package games.stendhal.server.maps;

import java.util.*;
import marauroa.common.game.*;
import games.stendhal.server.*;
import games.stendhal.server.entity.*;
import games.stendhal.server.entity.npc.*;

public class city 
  {
  public city(StendhalRPZone zone)
    {
    Portal portal=new Portal();
    zone.assignRPObjectID(portal);
    portal.setx(28);
    portal.sety(24);
    portal.setNumber(0);
    portal.setDestination("dungeon_000",0);
    zone.add(portal);
    zone.addPortal(portal);

    Sign sign=new Sign();
    zone.assignRPObjectID(sign);
    sign.setx(4);
    sign.sety(21);
    sign.setText("You are about to leave this area to move to the village.|You can buy a new sheep there.");    
    zone.add(sign);
    
    sign=new Sign();
    zone.assignRPObjectID(sign);
    sign.setx(8);
    sign.sety(33);
    sign.setText("Welcome to Stendhal!| Please report any problems and issues at our webpage.");
    zone.add(sign);

    sign=new Sign();
    zone.assignRPObjectID(sign);
    sign.setx(26);
    sign.sety(26);
    sign.setText("You are about to enter the Dungeons.|But Beware! This area is infested with rats and legend has |it that many Adventurers have died down there...");
    zone.add(sign);

    sign=new Sign();
    zone.assignRPObjectID(sign);
    sign.setx(43);
    sign.sety(26);
    sign.setText("Talk to Sato to sell your sheep!.|He probably won't give you a fair price but this is a small village...|The price he will offer you depends on the weight of your sheep.");
    zone.add(sign);

    sign=new Sign();
    zone.assignRPObjectID(sign);
    sign.setx(44);
    sign.sety(48);
    sign.setText("You are about to leave this area and move to the plains.|You may fatten up your sheep there on the wild berries.|Be careful though, wolves roam these plains.");
    zone.add(sign);
    
    NPC npc=new BeggarNPC();
    zone.assignRPObjectID(npc);
    npc.setName("Diogenes");
    npc.setx(24);
    npc.sety(28);
    npc.setbaseHP(100);
    zone.add(npc);    
    zone.addNPC(npc);
      
    npc=new BuyerNPC()
      {
      protected void createPath()
        {
        List<Path.Node> nodes=new LinkedList<Path.Node>();
        nodes.add(new Path.Node(40,30));
        nodes.add(new Path.Node(58,30));
        nodes.add(new Path.Node(58,7));
        nodes.add(new Path.Node(39,7));
        nodes.add(new Path.Node(39,0));
        nodes.add(new Path.Node(20,0));
        nodes.add(new Path.Node(20,7));
        nodes.add(new Path.Node(23,7));
        nodes.add(new Path.Node(23,30));
        setPath(nodes,true);
        }
      };
    zone.assignRPObjectID(npc);
    npc.setName("Sato");
    npc.setx(40);
    npc.sety(30);
    npc.setbaseHP(100);
    zone.add(npc);    
    zone.addNPC(npc);

    npc=new WelcomerNPC()
      {
      protected void createPath()
        {
        List<Path.Node> nodes=new LinkedList<Path.Node>();
        nodes.add(new Path.Node(5,31));
        nodes.add(new Path.Node(18,31));
        setPath(nodes,true);
        }
      };
    zone.assignRPObjectID(npc);
    npc.setName("Carmen");
    npc.setx(5);
    npc.sety(31);
    npc.setbaseHP(100);
    zone.add(npc);    
    zone.addNPC(npc);
    }  
  }
