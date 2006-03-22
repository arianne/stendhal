package games.stendhal.server.maps;

import games.stendhal.common.Rand;
import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Chest;
import games.stendhal.server.entity.PersonalChest;
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
    buildSemosTownhallArea((StendhalRPZone)world.getRPZone(new IRPZone.ID("int_semos_townhall")));
    }

  private void buildSemosTownhallArea(StendhalRPZone zone)
    {
    for(int i=0;i<5;i++)
      {
      Portal portal=new Portal();
      zone.assignRPObjectID(portal);
      portal.setx(13+i);
      portal.sety(46);
      portal.setNumber(i);
      portal.setDestination("0_semos_city",7);
      zone.addPortal(portal);    
      }
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
    
    for(int i=0;i<4;i++)
      {
      PersonalChest chest=new PersonalChest();
      zone.assignRPObjectID(chest);
      chest.set(2+6*i,2);
      zone.add(chest);

      chest=new PersonalChest();
      zone.assignRPObjectID(chest);
      chest.set(2+6*i,15);
      zone.add(chest);
      }
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

    npc=npcs.add("Ketteh Wehoh",new SpeakerNPC()
      {
      protected void createPath()
        {
        List<Path.Node> nodes=new LinkedList<Path.Node>();
        nodes.add(new Path.Node(21,5));
        nodes.add(new Path.Node(29,5));
        nodes.add(new Path.Node(29,9));
        nodes.add(new Path.Node(21,9));
        setPath(nodes,true);
        }

      protected void createDialog()
        {
        Behaviours.addHelp(this,"I am the good manners and decency observer. I can help you by telling you about obvious and common sense things you should already know like not wandering naked around...");

        Behaviours.addJob(this,"I am committed to keep civilized customs in Semos. I know any kind of protocol ever known and one hundred manners of doing the same thing wrong. Well, I doubt about when it should be used the spoon or the fork but on the other hand nobody uses cutlery in Semos");

        add(1,new String[]{"quest","task"},null,1,"I do not have any task for you right now. If you need anything from me just say it.",null);

        Behaviours.addGoodbye(this);
        }
      });

    zone.assignRPObjectID(npc);
    npc.put("class","elegantladynpc");
    npc.set(21,5);
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

    SpeakerNPC npc=npcs.add("Hackim Easso",new SpeakerNPC()
      {
      protected void createPath()
        {
        List<Path.Node> nodes=new LinkedList<Path.Node>();
        nodes.add(new Path.Node(5,1));
        nodes.add(new Path.Node(8,1));
        nodes.add(new Path.Node(7,1));
        nodes.add(new Path.Node(7,6));
        nodes.add(new Path.Node(16,6));
        nodes.add(new Path.Node(16,1));
        nodes.add(new Path.Node(15,1));
        nodes.add(new Path.Node(16,1));
        nodes.add(new Path.Node(16,6));
        nodes.add(new Path.Node(7,6));
        nodes.add(new Path.Node(7,1));
        setPath(nodes,true);
        }

      protected void createDialog()
        {
      	add(0,"hi",null,1,null, new SpeakerNPC.ChatAction()
                {
                public void fire(Player player,String text,SpeakerNPC engine)
                  {
      		//A little trick to make NPC remember if it has met player before anc react accordingly
      		//NPC_name quest doesn't exist anywhere else neither is used for any other purpose
      	    if (!player.isQuestCompleted("Hackim"))
                    {
                    engine.say("Hi foreigner, I'm Hackim Easso, the blacksmith's assistant. Have you come here to buy weapons?");
                    player.setQuest("Hackim","done");
                    }
                  else
                    {
                    engine.say("Hi again, "+player.getName()+". How can I #help you this time?");
                    }
                  }
      	  });
        Behaviours.addHelp(this,"I'm the blacksmith's assistant. I can help you by sharing my curiosity with you... Have you come here to buy weapons?");

        Behaviours.addJob(this,"I help Xoderos the blacksmith in making weapons for the Deniran's army. I really only bring the coal for the fire but guess who puts the weapons so ordered on the shelves. Yes, it is me.");

        Behaviours.addGoodbye(this);
        }
      });

    zone.assignRPObjectID(npc);
    npc.put("class","naughtyteennpc");
    npc.set(5,1);
    npc.initHP(100);
    zone.addNPC(npc);
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

    SpeakerNPC npc=npcs.add("Zynn Iwuhos",new SpeakerNPC()
      {
      protected void createPath()
        {
        List<Path.Node> nodes=new LinkedList<Path.Node>();
        nodes.add(new Path.Node(15,2));
        nodes.add(new Path.Node(12,2));
        nodes.add(new Path.Node(12,5));
        nodes.add(new Path.Node(13,5));
        nodes.add(new Path.Node(13,6));
        nodes.add(new Path.Node(13,5));
        nodes.add(new Path.Node(15,5));
        nodes.add(new Path.Node(15,6));
        nodes.add(new Path.Node(15,5));
        nodes.add(new Path.Node(17,5));
        nodes.add(new Path.Node(17,6));
        nodes.add(new Path.Node(17,2));
        setPath(nodes,true);
        }

      protected void createDialog()
        {
      	add(0,"hi",null,1,null, new SpeakerNPC.ChatAction()
                {
                public void fire(Player player,String text,SpeakerNPC engine)
                  {
      		//A little trick to make NPC remember if it has met player before anc react accordingly
      		//NPC_name quest doesn't exist anywhere else neither is used for any other purpose
      	    if (!player.isQuestCompleted("Zynn"))
                    {
                    engine.say("Hi, potential reader. Here's recorded all the history of Semos city and some facts about the whole island of Faiumoni in which we are. I can give you an introduction to its #geography and #history. I can report you the latest #news.");
                    player.setQuest("Zynn","done");
                    }
                  else
                    {
                    engine.say("Hi again, "+player.getName()+". How can I #help you this time?");
                    }
                  }
      	  });
        Behaviours.addHelp(this,"I'm a historian. I can help you by sharing my knowledge with you... I can tell you about Faiumoni's #geography and #history. I can report you the latest #news.");

        Behaviours.addJob(this,"I am committed to register every objective fact about Faiumoni. I've written most of the books in this library. Well, except the book \"Know how to kill creatures\" by Hayunn Naratha");

        add(1,new String[]{"quest","task"},null,1,"I do not have any task for you right now. If you need anything from me just say it.",null);

        }
      });
    zone.assignRPObjectID(npc);
    npc.put("class","wisemannpc");
    npc.set(15,2);
    npc.initHP(100);
    zone.addNPC(npc);

    npc=npcs.add("Ceryl",new SpeakerNPC()
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
    npc.put("class","investigatornpc");
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

    npc=npcs.add("Io Flotto",new SpeakerNPC()
      {
      protected void createPath()
        {
        List<Path.Node> nodes=new LinkedList<Path.Node>();
        nodes.add(new Path.Node(8,18));
        nodes.add(new Path.Node(8,19));
        nodes.add(new Path.Node(15,19));
        nodes.add(new Path.Node(15,18));
        nodes.add(new Path.Node(16,18));
        nodes.add(new Path.Node(16,13));
        nodes.add(new Path.Node(15,13));
        nodes.add(new Path.Node(15,12));
        nodes.add(new Path.Node(12,12));
        nodes.add(new Path.Node(8,12));
        nodes.add(new Path.Node(8,13));
        nodes.add(new Path.Node(7,13));
        nodes.add(new Path.Node(7,18));
        setPath(nodes,true);
        }

      protected void createDialog()
        {
      	add(0,"hi",null,1,null, new SpeakerNPC.ChatAction()
                {
                public void fire(Player player,String text,SpeakerNPC engine)
                  {
      		//A little trick to make NPC remember if it has met player before anc react accordingly
      		//NPC_name quest doesn't exist anywhere else neither is used for any other purpose
      	    if (!player.isQuestCompleted("Io"))
                    {
                    engine.say("I waited you, "+player.getName()+". How do I know your name? Easy, I'm Io Flotto, the telepath. Do you want me to show you the six basic elements of telepathy?");
                    player.setQuest("Io","done");
                    }
                  else
                    {
                    engine.say("Hi again, "+player.getName()+". How can I #help you this time? Not that I don't already know...");
                    }
                  }
      	  });
        Behaviours.addHelp(this,"I'm a telepath and telekinetic. I can help you by sharing my mental skills with you... Do you want me to show you the six basic elements of telepathy? I already know the answer but I'm being polite...");

        Behaviours.addJob(this,"I am committed to develop the unknown potential power of the mind. Up to this day I've made great advances in telepathy and telekinesis. However, I can't foresee the future yet and if finally we will be able to destroy Blordrough's dark legion");

        add(1,new String[]{"quest","task"},null,1,"I do not have any task for you right now. If you need anything from me just say it. I think it's simply unkind reading one's mind without permission.",null);

        Behaviours.addGoodbye(this);
        }
      });

    zone.assignRPObjectID(npc);
    npc.put("class","floattingladynpc");
    npc.set(8,18);
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

    SpeakerNPC npc=npcs.add("Margaret",new SpeakerNPC()
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


    npc=npcs.add("Xin Blanca",new SpeakerNPC()
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
	});

    zone.assignRPObjectID(npc);
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
    portal.setDestination("int_semos_tavern_0",0);
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
    portal.setDestination("int_semos_library",0);
    zone.addPortal(portal);

    portal=new Portal();
    zone.assignRPObjectID(portal);
    portal.setx(11);
    portal.sety(22);
    portal.setNumber(4);
    portal.setDestination("int_semos_library",1);
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
    portal.setx(18);
    portal.sety(22);
    portal.setNumber(6);
    portal.setDestination("int_semos_bank",0);
    zone.addPortal(portal);

    for(int i=0;i<3;i++)
      {
      portal=new Portal();
      zone.assignRPObjectID(portal);
      portal.setx(29+i);
      portal.sety(13);
      portal.setNumber(7+i);
      portal.setDestination("int_semos_townhall",2);
      zone.addPortal(portal);
      }

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

    SpeakerNPC npc=npcs.add("Nomyr Ahba",new SpeakerNPC()
      {
      protected void createPath()
        {
        List<Path.Node> nodes=new LinkedList<Path.Node>();
        nodes.add(new Path.Node(46,19));
        nodes.add(new Path.Node(46,20));
        nodes.add(new Path.Node(50,20));
        nodes.add(new Path.Node(50,19));
        nodes.add(new Path.Node(50,20));
        nodes.add(new Path.Node(46,20));
        setPath(nodes,true);
        }

      protected void createDialog()
        {
      	add(0,"hi",null,1,null, new SpeakerNPC.ChatAction()
                {
                public void fire(Player player,String text,SpeakerNPC engine)
                  {
      		//A little trick to make NPC remember if it has met player before anc react accordingly
      		//NPC_name quest doesn't exist anywhere else neither is used for any other purpose
      	    if (!player.isQuestCompleted("Nomyr"))
                    {
                    engine.say("I've heard cries inside and I was just... but you look disoriented, foreigner. Do you want to know what has been happening around here lately?");
                    player.setQuest("Nomyr","done");
                    }
                  else
                    {
                    engine.say("Hi again, "+player.getName()+". How can I #help you this time?");
                    }
                  }
      	  });
        Behaviours.addHelp(this,"I'm a... hmmm... observer. I can help you by sharing my information about rumours with you... Do you want to know what has been happening around here lately?");

        Behaviours.addJob(this,"I am committed to peek every curious fact about Semos. I know any rumor that has ever existed in Semos and I have invented most of them. Well, except that about Hackim smuggling Deniran's army weapons to wandering adventurer's like you");

        add(1,new String[]{"quest","task"},null,1,"I do not have any task for you right now. If you need anything from me just say it.",null);

        Behaviours.addGoodbye(this);
        }
      });

    zone.assignRPObjectID(npc);
    npc.put("class","thiefnpc");
    npc.set(46,19);
    npc.initHP(100);
    zone.addNPC(npc);

    npc=npcs.add("Monogenes",new SpeakerNPC()
      {
      protected void createPath()
        {
        List<Path.Node> nodes=new LinkedList<Path.Node>();
        setPath(nodes,false);
        }

      protected void createDialog()
        {
      	add(0,"hi",null,1,null, new SpeakerNPC.ChatAction()
                {
                public void fire(Player player,String text,SpeakerNPC engine)
                  {
      		//A little trick to make NPC remember if it has met player before anc react accordingly
      		//NPC_name quest doesn't exist anywhere else neither is used for any other purpose
      	    if (!player.isQuestCompleted("Monogenes"))
                    {
                    engine.say("Hi foreigner, don't be surprised if people here are reserved: the fear of the advances of Blordrough's dark legion has affected everybody, including me. Do you want to know how to socialize with Semos' people?");
                    player.setQuest("Monogenes","done");
                    }
                  else
                    {
                    engine.say("Hi again, "+player.getName()+". How can I #help you this time?");
                    }
                  }
      	  });
        Behaviours.addHelp(this,"I'm diogenes' older brother and I don't remember what I did before I retired. Anyway, I can help you by telling you how to treat Semos' people...  Do you want to know how to socialize with them?");

        Behaviours.addJob(this,"I am committed to give directions to foreigners and show them how to talk to people here. However, when I'm in a bad mood I give them misleading directions hehehe... What is not necessarily bad because I can give wrong directions unwillingly anyway and they can result in being the right directions");

        Behaviours.addGoodbye(this);
        }
      });

    zone.assignRPObjectID(npc);
    npc.put("class","oldmannpc");
    npc.set(26,21);
    npc.setDirection(Direction.DOWN);
    npc.initHP(100);
    zone.addNPC(npc);

    npc=npcs.add("Hayunn Naratha",new SpeakerNPC()
      {
      protected void createPath()
        {
        List<Path.Node> nodes=new LinkedList<Path.Node>();
        nodes.add(new Path.Node(27,37));
        nodes.add(new Path.Node(27,38));
        nodes.add(new Path.Node(29,38));
        nodes.add(new Path.Node(29,37));
        nodes.add(new Path.Node(29,38));
        nodes.add(new Path.Node(27,38));
        setPath(nodes,true);
        }

      protected void createDialog()
        {
	add(0,"hi",null,1,null, new SpeakerNPC.ChatAction()
          {
          public void fire(Player player,String text,SpeakerNPC engine)
            {
		//A little trick to make NPC remember if it has met player before anc react accordingly
		//NPC_name quest doesn't exist anywhere else neither is used for any other purpose
	    if (!player.isQuestCompleted("Hayunn"))
              {
              engine.say("Hi. I am Hayunn Naratha, a retired adventurer. Do you want me to tell you how I used to kill creatures?");
              player.setQuest("Hayunn","done");
              }
            else
              {
              engine.say("Hi again, "+player.getName()+". How can I #help you this time?");
              }
            }
	  });
        Behaviours.addHelp(this,"Well, I'm a retired adventurer as I've told you before. I only can help you by sharing my experience with you... Do you want me to tell you how I used to kill creatures?");

        Behaviours.addJob(this,"I've sworn defending with my life the people of Semos from any creature that dares to get out of this dungeon. With all our young people battling Blordrough's dark legion at south, monsters are getting more and more confident to go to the surface.");

        Behaviours.addGoodbye(this);
        }
      });

    zone.assignRPObjectID(npc);
    npc.put("class","oldheronpc");
    npc.set(27,37);
    npc.initHP(100);
    zone.addNPC(npc);

    npc=new SpeakerNPC()
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
            if(player.isAdmin())
              {
              for(String quest: player.getQuests())
                {
                player.removeQuest(quest);
                }
              }
            else
              {
              say("Ummm! No, you clean me! begin with my back!");
              player.setHP(player.getHP()-5);
              world.modify(player);
              }
            }
          });
        }
       };

    zone.assignRPObjectID(npc);
    npc.put("class","beggarnpc");
    npc.setName("Diogenes");
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
        Behaviours.addGreeting(this,null, new SpeakerNPC.ChatAction()
          {
          public void fire(Player player, String text, SpeakerNPC engine)
            {
            engine.say("Ssshh! Come here "+player.getName()+"!. I have a #task for you.");
            }
          });
        Behaviours.addGoodbye(this);
        }
      });

    zone.assignRPObjectID(npc);
    npc.addInitChatMessage(null,new SpeakerNPC.ChatAction()
      {      
      public void fire(Player player, String text, SpeakerNPC engine)
        {
        if(!player.hasQuest("TadFirstChat"))
          {
          player.setQuest("TadFirstChat","done");
          engine.listenTo(player, "hi");
          }
        }
      });
    npc.put("class","childnpc");
    npc.set(7,50);
    npc.setDirection(Direction.RIGHT);
    npc.initHP(100);
    zone.addNPC(npc);
    }
  }