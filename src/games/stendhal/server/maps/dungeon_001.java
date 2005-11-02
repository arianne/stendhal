package games.stendhal.server.maps;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.Portal;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.npc.NPC;
import games.stendhal.server.entity.npc.Behaviours;
import games.stendhal.server.entity.npc.SpeakerNPC;
import java.util.*;
import games.stendhal.server.Path;

public class dungeon_001 
  {
  public dungeon_001(StendhalRPZone zone)
    {
    Portal portal=new Portal();
    zone.assignRPObjectID(portal);
    portal.setx(5);
    portal.sety(7);
    portal.setNumber(0);
    portal.setDestination("dungeon_000",1);
    zone.add(portal);
    zone.addPortal(portal);

    portal=new Portal();
    zone.assignRPObjectID(portal);
    portal.setx(67);
    portal.sety(118);
    portal.setNumber(1);
    portal.setDestination("forest",0);
    zone.add(portal);
    zone.addPortal(portal);

    NPC npc=new SpeakerNPC()
      {
      protected void createPath()
        {
        List<Path.Node> nodes=new LinkedList<Path.Node>();
        nodes.add(new Path.Node(67,12));
        nodes.add(new Path.Node(59,12));
        nodes.add(new Path.Node(59,16));
        nodes.add(new Path.Node(67,16));
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
                seller.say("*drool* Sheep flesh! Bring da sheep here!");
                }
              else
                {
                say("*LOVELY*. Take dis money!.");
      
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
              seller.say("Sell what? Don't cheat me or I might 'ave to hurt you!");
              }
            
            return false;
            }
          }

        Map<String,Integer> buyitems=new HashMap<String,Integer>();
        buyitems.put("sheep",1500);

        Behaviours.addGreeting(this);
        Behaviours.addJob(this,getName()+" du buy cheepz frrom humanz.");
        Behaviours.addHelp(this,getName()+" buy sheep! Sell me sheep! "+getName()+" is hungry!");
        Behaviours.addBuyer(this,new SheepBuyerBehaviour(buyitems));
        Behaviours.addGoodbye(this);
        }
      };
    zone.assignRPObjectID(npc);
    npc.put("class","orcbuyernpc");
    npc.setName("Tor'Koom");
    npc.setx(67);
    npc.sety(12);
    npc.setBaseHP(1000);
    npc.setHP(npc.getBaseHP());
    zone.add(npc);    
    zone.addNPC(npc);
  
    Item itemo = zone.getWorld().getRuleManager().getEntityManager().getItem("sword");
    zone.assignRPObjectID(itemo);
    itemo.setx(72);
    itemo.sety(48);
    zone.add(itemo);

    itemo = zone.getWorld().getRuleManager().getEntityManager().getItem("shield");
    zone.assignRPObjectID(itemo);
    itemo.setx(75);
    itemo.sety(83);
    zone.add(itemo);
    }  
  }
