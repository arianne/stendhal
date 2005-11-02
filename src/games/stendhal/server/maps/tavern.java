package games.stendhal.server.maps;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Portal;
import games.stendhal.server.entity.npc.NPC;
import games.stendhal.server.entity.npc.Behaviours;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.Path;
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

    NPC npc=new SpeakerNPC()
      {
      protected void createPath()
        {
        List<Path.Node> nodes=new LinkedList<Path.Node>();
        nodes.add(new Path.Node(17,12));
        nodes.add(new Path.Node(17,13));
        nodes.add(new Path.Node(16,8));
        nodes.add(new Path.Node(13,8));
        nodes.add(new Path.Node(13,6));
        nodes.add(new Path.Node(13,10));
        nodes.add(new Path.Node(23,10));
        nodes.add(new Path.Node(23,10));
        nodes.add(new Path.Node(23,13));
        nodes.add(new Path.Node(23,10));
        nodes.add(new Path.Node(17,10));
        setPath(nodes,true);
        }
      
      protected void createDialog()
        {        
        Behaviours.addGreeting(this);
        Behaviours.addJob(this,"I am the bar maid for this fair tavern. We sell fine beers and food.");
        Behaviours.addHelp(this,"At the tavern you can get drinks and take a break to meet new people!.");
        Behaviours.addGoodbye(this);
        
        add(1,"beer",1,"Beer! Excellent choice! Coming right up!",null);
        add(1,"food",1,"Sure thing, a big strong adventurer like you will need lots of food!",null);
        }
      };
    
    zone.assignRPObjectID(npc);
    npc.setName("Margaret");
    npc.put("class","tavernbarmaidnpc");
    npc.setx(17);
    npc.sety(12);
    npc.setBaseHP(100);
    npc.setHP(npc.getBaseHP());
    zone.add(npc);    
    zone.addNPC(npc);


    npc=new SpeakerNPC()
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

      protected void createDialog()
        {        
        Map<String,Integer> sellitems=new HashMap<String,Integer>();
        sellitems.put("club",10);
        sellitems.put("armor",50);
        sellitems.put("shield",100);
        sellitems.put("sword",200);

        Map<String,Integer> buyitems=new HashMap<String,Integer>();
        buyitems.put("club",3);
        buyitems.put("armor",15);
        buyitems.put("shield",30);
        buyitems.put("sword",60);
        
        Behaviours.addGreeting(this);
        Behaviours.addJob(this,"Shhh! I sell adventurers stuff.");
        Behaviours.addHelp(this,"I buy and sell several items, ask me for my offer");
        Behaviours.addSeller(this,new Behaviours.SellerBehaviour(sellitems));
        Behaviours.addBuyer(this,new Behaviours.BuyerBehaviour(buyitems));
        Behaviours.addGoodbye(this);
        }
      };

    zone.assignRPObjectID(npc);
    npc.setName("Xin Blanca");
    npc.put("class","weaponsellernpc");
    npc.setx(2);
    npc.sety(14);
    npc.setBaseHP(100);
    npc.setHP(npc.getBaseHP());
    zone.add(npc);    
    zone.addNPC(npc);
    }  
  }
