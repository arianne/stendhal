package games.stendhal.server.maps;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;

import games.stendhal.common.Rand;

import games.stendhal.server.Path;

import games.stendhal.server.entity.Door;
import games.stendhal.server.entity.Chest;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.Portal;
import games.stendhal.server.entity.OneWayPortal;
import games.stendhal.server.entity.Sign;
import games.stendhal.server.entity.npc.Behaviours;
import games.stendhal.server.entity.npc.NPC;
import games.stendhal.server.entity.npc.SpeakerNPC;

import marauroa.common.game.IRPZone;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;


public class Semos implements IContent
  {
  private StendhalRPWorld world;
  
  public Semos(StendhalRPWorld world) throws org.xml.sax.SAXException, java.io.IOException
    {
    this.world=world;
    buildSemosCityArea((StendhalRPZone)world.getRPZone(new IRPZone.ID("0_semos_city")));
    buildSemosVillageArea((StendhalRPZone)world.getRPZone(new IRPZone.ID("0_semos_village")));
    buildSemosSouthPlainsArea((StendhalRPZone)world.getRPZone(new IRPZone.ID("0_semos_south_plains")));
    buildSemosTavernArea((StendhalRPZone)world.getRPZone(new IRPZone.ID("int_semos_tavern")));
    }
  
  private void buildSemosTavernArea(StendhalRPZone zone)
    {
    Portal portal=new Portal();
    zone.assignRPObjectID(portal);
    portal.setx(20);
    portal.sety(1);
    portal.setNumber(0);
    portal.setDestination("0_semos_city",0);
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
        sellitems.put("knife",10);
        sellitems.put("small_axe",10);
        sellitems.put("club",10);
        sellitems.put("wooden_shield",15);
        sellitems.put("dress",20);
        sellitems.put("leather_helmet",20);
        sellitems.put("leather_legs",30);

        Map<String,Integer> buyitems=new HashMap<String,Integer>();
        buyitems.put("short_sword",15);
        buyitems.put("sword",60);
        buyitems.put("studded_shield",20);
        
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
    
  private void buildSemosSouthPlainsArea(StendhalRPZone zone)
    {
    Sign sign=new Sign();
    zone.assignRPObjectID(sign);
    sign.setx(118);
    sign.sety(43);
    sign.setText("You are about to leave this area to move to the forest.|You may fatten up your sheep there on wild berries.|Be careful though, these forests crawl with wolves.");
    zone.add(sign);
    
    sign=new Sign();
    zone.assignRPObjectID(sign);
    sign.setx(38);
    sign.sety(3);
    sign.setText("You are about to leave this area to move to the village.|You can buy a new sheep there.");
    zone.add(sign);

    sign=new Sign();
    zone.assignRPObjectID(sign);
    sign.setx(113);
    sign.sety(3);
    sign.setText("You are about to leave this area to move to the city.|You can sell your sheep there.");
    zone.add(sign);
    }

  private void buildSemosVillageArea(StendhalRPZone zone) throws org.xml.sax.SAXException, java.io.IOException
    {
    Sign sign=new Sign();
    zone.assignRPObjectID(sign);
    sign.setx(23);
    sign.sety(61);
    sign.setText("You are about to leave this area and move to the plains.|You may fatten up your sheep there on the wild berries.|Be careful though, wolves roam these plains.");
    zone.add(sign);

    sign=new Sign();
    zone.assignRPObjectID(sign);
    sign.setx(60);
    sign.sety(47);
    sign.setText("You are about to leave this area to move to the city.|You can sell your sheep there.");    
    zone.add(sign);
    
    sign=new Sign();
    zone.assignRPObjectID(sign);
    sign.setx(16);
    sign.sety(35);
    sign.setText("[CLOSED]|The tavern has moved to a much|better and central house in town.|Come buy your weapons, find your|quests and hang out there instead.");
    zone.add(sign);
    
    world.createHouse(0,zone,29,40);
    }

  private void buildSemosCityArea(StendhalRPZone zone)
    {
    Portal portal=new Portal();
    zone.assignRPObjectID(portal);
    portal.setx(42);
    portal.sety(37);
    portal.setNumber(0);
    portal.setDestination("int_semos_tavern",0);
    zone.add(portal);
    zone.addPortal(portal);
    
    portal=new OneWayPortal();
    zone.assignRPObjectID(portal);
    portal.setx(12);
    portal.sety(49);
    portal.setNumber(1);
    zone.add(portal);
    zone.addPortal(portal);    

    Sign sign=new Sign();
    zone.assignRPObjectID(sign);
    sign.setx(4);
    sign.sety(41);
    sign.setText("You are about to leave this area to move to the village.|You can buy a new sheep there.");    
    zone.add(sign);
    
    sign=new Sign();
    zone.assignRPObjectID(sign);
    sign.setx(8);
    sign.sety(47);
    sign.setText("Welcome to Stendhal!| Please report any problems and issues at our webpage.");
    zone.add(sign);

    sign=new Sign();
    zone.assignRPObjectID(sign);
    sign.setx(26);
    sign.sety(40);
    sign.setText("You are about to enter the Dungeons.|But Beware! This area is infested with rats and legend has |it that many Adventurers have died down there...");
    zone.add(sign);

    sign=new Sign();
    zone.assignRPObjectID(sign);
    sign.setx(44);
    sign.sety(62);
    sign.setText("You are about to leave this area and move to the plains.|You may fatten up your sheep there on the wild berries.|Be careful though, wolves roam these plains.");
    zone.add(sign);


    Chest chest=new Chest();
    zone.assignRPObjectID(chest);
    chest.setx(44);
    chest.sety(60);
    chest.add(zone.getWorld().getRuleManager().getEntityManager().getItem("knife"));
    chest.add(zone.getWorld().getRuleManager().getEntityManager().getItem("wooden_shield"));
    chest.add(zone.getWorld().getRuleManager().getEntityManager().getItem("leather_armor"));
    chest.add(zone.getWorld().getRuleManager().getEntityManager().getItem("money"));
    zone.add(chest);
    
    NPC npc=new SpeakerNPC()
      {
      protected void createPath()
        {
        List<Path.Node> nodes=new LinkedList<Path.Node>();
        nodes.add(new Path.Node(22,42));
        nodes.add(new Path.Node(26,42));
        nodes.add(new Path.Node(26,44));
        nodes.add(new Path.Node(31,44));
        nodes.add(new Path.Node(31,42));
        nodes.add(new Path.Node(35,42));
        nodes.add(new Path.Node(35,28));
        nodes.add(new Path.Node(22,28));
        setPath(nodes,true);
        }
        
      protected void createDialog()
        {        
        Behaviours.addGreeting(this);
        Behaviours.addJob(this,"Hehehe! Job! hehehe! Muahahaha!.");
        Behaviours.addHelp(this,"I can't help you, but you can help Stendhal: tell your friends about Stendhal and help us to create maps.");
        Behaviours.addGoodbye(this);
        
        add(1, "quest", 1, null, new SpeakerNPC.ChatAction()
          {
          public void fire(Player player, String text, SpeakerNPC engine)
            {
            switch(Rand.rand(2))
              {
              case 0:        
                say("Ah, quests... just like the old days when I was young! I remember one quest that was about... Oh look, a bird!hmm, what?! Oh, Oops! I forgot it! :(");
                break;
              case 1:
                say("I have been told that on the deepest place of the dungeon under this city someone also buy sheeps, but *it* pays better!.");
                break;
              }
            }
          });
        }
      };
    
    zone.assignRPObjectID(npc);
    npc.setName("Diogenes");
    npc.put("class","beggarnpc");
    npc.setx(24);
    npc.sety(42);
    npc.setBaseHP(100);
    npc.setHP(npc.getBaseHP());
    zone.add(npc);    
    zone.addNPC(npc);
      

    npc=new SpeakerNPC()
      {
      protected void createPath()
        {
        List<Path.Node> nodes=new LinkedList<Path.Node>();
        nodes.add(new Path.Node(5,45));
        nodes.add(new Path.Node(18,45));
        setPath(nodes,true);
        }

      protected void createDialog()
        {
        Behaviours.addGreeting(this);
        Behaviours.addJob(this, "I have healing abilities and I heal wounded players.");
        Behaviours.addHelp(this, "Ask me to heal you and I will help you");
        Behaviours.addHealer(this, 0);
        Behaviours.addGoodbye(this);               
        }
      };
    zone.assignRPObjectID(npc);
    npc.put("class","welcomernpc");
    npc.setName("Carmen");
    npc.setx(5);
    npc.sety(45);
    npc.setBaseHP(100);
    npc.setHP(npc.getBaseHP());
    zone.add(npc);    
    zone.addNPC(npc);
    }
  }
