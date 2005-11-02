package games.stendhal.server.maps;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.npc.NPC;
import games.stendhal.server.entity.npc.Behaviours;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.Chest;
import games.stendhal.server.entity.Portal;
import games.stendhal.server.entity.Sign;
import java.util.*;
import games.stendhal.common.Rand;
import games.stendhal.server.Path;

public class city 
  {
  public city(StendhalRPZone zone)
    {
    Portal portal=new Portal();
    zone.assignRPObjectID(portal);
    portal.setx(28);
    portal.sety(38);
    portal.setNumber(0);
    portal.setDestination("dungeon_000",0);
    zone.add(portal);
    zone.addPortal(portal);

    portal=new Portal();
    zone.assignRPObjectID(portal);
    portal.setx(42);
    portal.sety(37);
    portal.setNumber(1);
    portal.setDestination("tavern",0);
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
    sign.setx(43);
    sign.sety(40);
    sign.setText("Talk to Sato to sell your sheep!.|He probably won't give you a fair price but this is a small village...|The price he will offer you depends on the weight of your sheep.");
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
    chest.add(zone.getWorld().getRuleManager().getEntityManager().getItem("sword"));
    chest.add(zone.getWorld().getRuleManager().getEntityManager().getItem("shield"));
    chest.add(zone.getWorld().getRuleManager().getEntityManager().getItem("armor"));
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
        nodes.add(new Path.Node(40,44));
        nodes.add(new Path.Node(58,44));
        nodes.add(new Path.Node(58,21));
        nodes.add(new Path.Node(39,21));
        nodes.add(new Path.Node(39,14));
        nodes.add(new Path.Node(20,14));
        nodes.add(new Path.Node(20,21));
        nodes.add(new Path.Node(23,21));
        nodes.add(new Path.Node(23,44));
        setPath(nodes,true);
        }

      protected void createDialog()
        {        
        class SheepBuyerBehaviour extends Behaviours.BuyerBehaviour
          {
          SheepBuyerBehaviour(Map<String,Integer> items)
            {
            super(items);
            }
            
          public boolean onBuy(SpeakerNPC seller, Player player, String itemName, int itemPrice)
            {
            if(player.hasSheep())
              {
              Sheep sheep=(Sheep)world.get(player.getSheep());
              if(seller.distance(sheep)>5*5)
                {
                seller.say("Ya sheep is too far away. I can't see it from here. Go and bring it here.");
                }
              else
                {
                say("Thanks! Here is your money.");
      
                rp.removeNPC(sheep);
                world.remove(sheep.getID());
                player.removeSheep(sheep);
                
                payPlayer(player,itemPrice);
                
                world.modify(player);
                return true;
                }
              }
            else
              {
              seller.say("You ain't got a sheep!! What game you trying to play, "+player.get("name")+"?");
              }
            
            return false;
            }
          }

        Map<String,Integer> buyitems=new HashMap<String,Integer>();
        buyitems.put("sheep",150);

        Behaviours.addGreeting(this);
        Behaviours.addJob(this,"I work as the main Semos' sheep buyer.");
        Behaviours.addHelp(this,"I just buy sheeps. Just tell me sell sheep and I will buy your nice sheep!.");
        Behaviours.addBuyer(this,new SheepBuyerBehaviour(buyitems));
        Behaviours.addGoodbye(this);
        }
      };
    zone.assignRPObjectID(npc);
    npc.setName("Sato");
    npc.put("class","buyernpc");
    npc.setx(40);
    npc.sety(44);
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
