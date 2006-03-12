package games.stendhal.server.maps;

import games.stendhal.common.Rand;
import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Chest;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.Portal;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.OneWayPortal;
import games.stendhal.server.entity.Sign;
import games.stendhal.server.entity.npc.Behaviours;
import games.stendhal.server.entity.npc.NPC;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.pathfinder.Path;

import marauroa.common.game.IRPZone;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;


public class Semos implements IContent
  {
  private StendhalRPWorld world;
  private NPCList npcs;
  private ShopList shops;
  
  public Semos(StendhalRPWorld world) throws org.xml.sax.SAXException, java.io.IOException
    {
    this.npcs=NPCList.get();
    this.shops=ShopList.get();
    this.world=world;
    
    buildSemosCityArea((StendhalRPZone)world.getRPZone(new IRPZone.ID("0_semos_city")));
    buildSemosVillageArea((StendhalRPZone)world.getRPZone(new IRPZone.ID("0_semos_village_w")));
    buildSemosSouthPlainsArea((StendhalRPZone)world.getRPZone(new IRPZone.ID("0_semos_plains_s")));
    buildSemosTavernArea();
    buildSemosBlacksmithArea((StendhalRPZone)world.getRPZone(new IRPZone.ID("int_semos_blacksmith")));
    buildSemosTempleArea((StendhalRPZone)world.getRPZone(new IRPZone.ID("int_semos_temple")));
    buildSemosLibraryArea((StendhalRPZone)world.getRPZone(new IRPZone.ID("int_semos_library"))); 
    buildSemosStorageArea(); 
    buildSemosBankArea((StendhalRPZone)world.getRPZone(new IRPZone.ID("int_semos_bank"))); 
    }
  
  private void buildSemosBankArea(StendhalRPZone zone)
    {
    Portal portal=new Portal();
    zone.assignRPObjectID(portal);
    portal.setx(9);
    portal.sety(30);
    portal.setNumber(0);
    portal.setDestination("0_semos_city",6);
    zone.addPortal(portal);

    portal=new Portal();
    zone.assignRPObjectID(portal);
    portal.setx(10);
    portal.sety(30);
    portal.setNumber(1);
    portal.setDestination("0_semos_city",6);
    zone.addPortal(portal);
    }

  private void buildSemosStorageArea()
    {
    StendhalRPZone zone=(StendhalRPZone)world.getRPZone(new IRPZone.ID("int_semos_storage_0"));
    
    Portal portal=new Portal();
    zone.assignRPObjectID(portal);
    portal.setx(9);
    portal.sety(14);
    portal.setNumber(0);
    portal.setDestination("0_semos_city",5);
    zone.addPortal(portal);

    portal=new Portal();
    zone.assignRPObjectID(portal);
    portal.setx(16);
    portal.sety(2);
    portal.setNumber(1);
    portal.setDestination("int_semos_storage_-1",0);
    zone.addPortal(portal);

    SpeakerNPC npc=npcs.add("Eonna",new SpeakerNPC() 
      {
      protected void createPath()
        {
        List<Path.Node> nodes=new LinkedList<Path.Node>();
        nodes.add(new Path.Node(4,12));  //its around the table with the beers and to the furnance
        nodes.add(new Path.Node(15,12));
        nodes.add(new Path.Node(15,12));
        nodes.add(new Path.Node(15,8));
        nodes.add(new Path.Node(10,8));
        nodes.add(new Path.Node(10,12));
        setPath(nodes,true);
        }

      protected void createDialog()
        {
        Behaviours.addGreeting(this, "Hi there, could you #help me");
        Behaviours.addJob(this, "I'm just a regular housewife");
        Behaviours.addHelp(this, "I can't help you with anything, but you can help me clean my #storage_space it is crawling with rats");
        Behaviours.addReply(this, "storage_space", "yes it down the stairs, there some rats and I think I saw a snake too so be careful");
        Behaviours.addReply(this, "task", "I don't have a task for you just a favor to ask");
        Behaviours.addGoodbye(this);
        }
      });

    zone.assignRPObjectID(npc);
    npc.put("class","welcomernpc"); //unclear wat to put there.
    npc.set(4,12);
    npc.initHP(100);
    zone.addNPC(npc);

    zone=(StendhalRPZone)world.getRPZone(new IRPZone.ID("int_semos_storage_-1"));
    
    portal=new Portal();
    zone.assignRPObjectID(portal);
    portal.setx(26);
    portal.sety(10);
    portal.setNumber(0);
    portal.setDestination("int_semos_storage_0",1);
    zone.addPortal(portal);
    }

  private void buildSemosBlacksmithArea(StendhalRPZone zone)
    {
    Portal portal=new Portal();
    zone.assignRPObjectID(portal);
    portal.setx(15);
    portal.sety(14);
    portal.setNumber(0);
    portal.setDestination("0_semos_city",2);
    zone.addPortal(portal);
    }

  private void buildSemosLibraryArea(StendhalRPZone zone)
    {
    Portal portal=new Portal();
    zone.assignRPObjectID(portal);
    portal.setx(8);
    portal.sety(30);
    portal.setNumber(0);
    portal.setDestination("0_semos_city",3);
    zone.addPortal(portal);

    portal=new Portal();
    zone.assignRPObjectID(portal);
    portal.setx(21);
    portal.sety(30);
    portal.setNumber(1);
    portal.setDestination("0_semos_city",4);
    zone.addPortal(portal);

    NPC npc=npcs.add("Ceryl",new SpeakerNPC()
      {
      protected void createPath()
        {
        List<Path.Node> nodes=new LinkedList<Path.Node>();
        nodes.add(new Path.Node(28,11));
        nodes.add(new Path.Node(28,20));
        setPath(nodes,true);
        }
      
      protected void createDialog()
        {        
        Behaviours.addGreeting(this);
        Behaviours.addJob(this,"I am the librarian.");
        Behaviours.addHelp(this,"Read!.");
        Behaviours.addGoodbye(this);
        }
      });
    
    zone.assignRPObjectID(npc);
    npc.setOutfit("0");
    npc.set(28,11);
    npc.initHP(100);
    zone.addNPC(npc);
    }

  private void buildSemosTempleArea(StendhalRPZone zone)
    {
    Portal portal=new Portal();
    zone.assignRPObjectID(portal);
    portal.setx(10);
    portal.sety(23);
    portal.setNumber(0);
    portal.setDestination("0_semos_city",1);
    zone.addPortal(portal);

    portal=new Portal();
    zone.assignRPObjectID(portal);
    portal.setx(11);
    portal.sety(23);
    portal.setNumber(1);
    portal.setDestination("0_semos_city",1);
    zone.addPortal(portal);

    portal=new Portal();
    zone.assignRPObjectID(portal);
    portal.setx(12);
    portal.sety(23);
    portal.setNumber(2);
    portal.setDestination("0_semos_city",1);
    zone.addPortal(portal);

    portal=new Portal();
    zone.assignRPObjectID(portal);
    portal.setx(13);
    portal.sety(23);
    portal.setNumber(3);
    portal.setDestination("0_semos_city",1);
    zone.addPortal(portal);


    SpeakerNPC npc=npcs.add("Ilisa",new SpeakerNPC()
      {
      protected void createPath()
        {
        List<Path.Node> nodes=new LinkedList<Path.Node>();
        nodes.add(new Path.Node(9,5));
        nodes.add(new Path.Node(14,5));
        setPath(nodes,true);
        }

      protected void createDialog()
        {
        Behaviours.addGreeting(this);
        Behaviours.addJob(this, "I have healing abilities and I heal wounded players. I also sell potions and antidotes.");
        Behaviours.addHelp(this, "Ask me to #heal you and I will help you or ask me #offer and I will show my shop's stuff.");
        Behaviours.addSeller(this, new Behaviours.SellerBehaviour(shops.get("healing")));
        Behaviours.addHealer(this, 0);
        Behaviours.addGoodbye(this);
        }
      });
      
    zone.assignRPObjectID(npc);
    npc.put("class","welcomernpc");
    npc.set(9,5);
    npc.initHP(100);
    zone.addNPC(npc);    
    }

  private void buildSemosTavernArea()
    {
    StendhalRPZone zone=(StendhalRPZone)world.getRPZone(new IRPZone.ID("int_semos_tavern_0"));
    Portal portal=new Portal();
    zone.assignRPObjectID(portal);
    portal.setx(22);
    portal.sety(17);
    portal.setNumber(0);
    portal.setDestination("0_semos_city",0);
    zone.addPortal(portal);

    NPC npc=npcs.add("Margaret",new SpeakerNPC()
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
        Behaviours.addHelp(this,"At the tavern you can get a #offer of drinks and take a break to meet new people!.");
        Behaviours.addSeller(this, new Behaviours.SellerBehaviour(shops.get("food&drinks")));
        Behaviours.addGoodbye(this);
        }
      });
    
    zone.assignRPObjectID(npc);
    npc.put("class","tavernbarmaidnpc");
    npc.set(17,12);
    npc.initHP(100);
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
        Behaviours.addGreeting(this);
        Behaviours.addJob(this,"Shhh! I sell adventurers stuff.");
        Behaviours.addHelp(this,"I buy and sell several items, ask me for my offer");
        Behaviours.addSeller(this,new Behaviours.SellerBehaviour(shops.get("sellstuff")));
        Behaviours.addBuyer(this,new Behaviours.BuyerBehaviour(shops.get("buystuff")));
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
    zone.addNPC(npc);

    npc=npcs.add("Ouchit",new SpeakerNPC()
      {
      protected void createPath()
        {
        List<Path.Node> nodes=new LinkedList<Path.Node>();
        nodes.add(new Path.Node(24,3));
        nodes.add(new Path.Node(24,5));
        nodes.add(new Path.Node(28,5));
        nodes.add(new Path.Node(28,3));
        setPath(nodes,true);
        }

      protected void createDialog()
        {        
        Behaviours.addGreeting(this);
        Behaviours.addJob(this,"I sell bows and arrows stuff.");
        Behaviours.addHelp(this,"I sell several items, ask me for my #offer");
        Behaviours.addSeller(this,new Behaviours.SellerBehaviour(shops.get("sellrangedstuff")));
        Behaviours.addGoodbye(this);
        }
      });

    zone.assignRPObjectID(npc);
    npc.put("class","weaponsellernpc");
    npc.set(24,3);;
    npc.initHP(100);
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
    }

  private void buildSemosCityArea(StendhalRPZone zone)
    {
    Portal portal=new Portal();
    zone.assignRPObjectID(portal);
    portal.setx(42);
    portal.sety(37);
    portal.setNumber(0);
    portal.setDestination("int_semos_tavern",0);
    zone.addPortal(portal);
    
    portal=new Portal();
    zone.assignRPObjectID(portal);
    portal.setx(53);
    portal.sety(37);
    portal.setNumber(1);
    portal.setDestination("int_semos_temple",2);
    zone.addPortal(portal);

    portal=new Portal();
    zone.assignRPObjectID(portal);
    portal.setx(15);
    portal.sety(41);
    portal.setNumber(2);
    portal.setDestination("int_semos_blacksmith",0);
    zone.addPortal(portal);

    portal=new Portal();
    zone.assignRPObjectID(portal);
    portal.setx(6);
    portal.sety(22);
    portal.setNumber(3);
    portal.setDestination("int_semos_library",1);
    zone.addPortal(portal);

    portal=new Portal();
    zone.assignRPObjectID(portal);
    portal.setx(11);
    portal.sety(22);
    portal.setNumber(4);
    portal.setDestination("int_semos_library",0);
    zone.addPortal(portal);

    portal=new Portal();
    zone.assignRPObjectID(portal);
    portal.setx(52);
    portal.sety(19);
    portal.setNumber(5);
    portal.setDestination("int_semos_storage_0",0);
    zone.addPortal(portal);

    portal=new Portal();
    zone.assignRPObjectID(portal);
    portal.setx(32);
    portal.sety(21);
    portal.setNumber(6);
    portal.setDestination("int_semos_bank",0);
    zone.addPortal(portal);

    portal=new OneWayPortal();
    zone.assignRPObjectID(portal);
    portal.setx(12);
    portal.sety(49);
    portal.setNumber(60);
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
        
        add(1, "quest", null, 1, null, new SpeakerNPC.ChatAction()
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
          
        add(1, "cleanme!", null, 1, "What?", new SpeakerNPC.ChatAction()
          {
          public void fire(Player player, String text, SpeakerNPC engine)
            {
            for(String quest: player.getQuests())
              {
              player.removeQuest(quest);
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
        Behaviours.addJob(this, "I have healing abilities and I heal wounded players. I also sell potions and antidotes.");
        Behaviours.addHelp(this, "Ask me to heal you and I will help you or ask me offer and I will show my shop's stuff.");
        Behaviours.addSeller(this,new Behaviours.SellerBehaviour(shops.get("healing")));
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
    zone.addNPC(npc);
    
    npc=npcs.add("Tad",new SpeakerNPC()
      {
      protected void createPath()
        {
        List<Path.Node> nodes=new LinkedList<Path.Node>();
        setPath(nodes,false);
        }

      protected void createDialog()
        {
        Behaviours.addGreeting(this,"Ssshh! I have a #task for you.");
        Behaviours.addGoodbye(this);
        }
      });
      
    zone.assignRPObjectID(npc);
    npc.setOutfit("0");
    npc.set(7,50);
    npc.setDirection(Direction.RIGHT);
    npc.initHP(100);
    zone.addNPC(npc);
    }
  }
