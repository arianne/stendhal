package games.stendhal.server.quests;

import marauroa.common.game.IRPZone;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.StendhalRPRuleProcessor;

import games.stendhal.server.entity.Sign;
import games.stendhal.server.entity.npc.NPC;
import games.stendhal.server.entity.npc.Behaviours;
import games.stendhal.server.entity.npc.SpeakerNPC;

import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.creature.Sheep;

import games.stendhal.server.Path;

import java.util.*;


/** 
 * QUEST: Grow sheep 
 * PARTICIPANTS: 
 * - Nishiya 
 * - Sato 
 * 
 * STEPS: 
 * - Buy a sheep from Nishiya in Village 
 * - Grow sheep in plains 
 * - Sell sheep to Sato in City 
 * 
 * REWARD: 
 * - You get the weight of the sheep * 5 in gold coins.
 *
 * REPETITIONS:
 * - As much as wanted.
 */
public class SheepGrowing implements IQuest
  {
  public SheepGrowing(StendhalRPWorld world, StendhalRPRuleProcessor rules)
    {
    StendhalRPZone zone=(StendhalRPZone)world.getRPZone(new IRPZone.ID("0_semos_village"));
    
    Sign sign=new Sign();
    zone.assignRPObjectID(sign);
    sign.setx(26);
    sign.sety(41);
    sign.setText("Talk to Nishiya to buy a sheep!.|He has the best prices for miles.");
    zone.add(sign);

    NPC npc=new SpeakerNPC()
      {
      protected void createPath()
        {
        List<Path.Node> nodes=new LinkedList<Path.Node>();
        nodes.add(new Path.Node(33,44));
        nodes.add(new Path.Node(33,42));
        nodes.add(new Path.Node(23,42));
        nodes.add(new Path.Node(23,44));
        setPath(nodes,true);
        }
      
      protected void createDialog()
        {        
        class SheepSellerBehaviour extends Behaviours.SellerBehaviour
          {
          SheepSellerBehaviour(Map<String,Integer> items)
            {
            super(items);
            }
            
          public boolean onSell(SpeakerNPC seller, Player player, String itemName, int itemPrice)
            {
            if(!player.hasSheep())
              {
              seller.say("Congratulations! Here is your sheep!Keep it safe!");
              StendhalRPZone zone=(StendhalRPZone)world.getRPZone(seller.getID());
              
              Sheep sheep=new Sheep(player);
              zone.assignRPObjectID(sheep);

              sheep.setx(seller.getx());
              sheep.sety(seller.gety()+2);
              
              world.add(sheep);
              
              player.setSheep(sheep);        
              world.modify(player);

              chargePlayer(player,itemPrice);
              return true;
              }
            else
              {
              say("You already have a sheep. Take care of it first!");
              return false;
              }
            }
          }
          
        Map<String,Integer> items=new HashMap<String,Integer>();
        items.put("sheep",30);
        
        Behaviours.addGreeting(this);
        Behaviours.addJob(this,"I work as a sheep seller.");
        Behaviours.addHelp(this,"I just sell sheeps. Just tell me buy sheep and I will sell you a nice sheep!.");
        Behaviours.addGoodbye(this);
        Behaviours.addSeller(this,new SheepSellerBehaviour(items));
        }
      };
      
    zone.assignRPObjectID(npc);
    npc.put("class","sellernpc");
    npc.setName("Nishiya");
    npc.setx(33);
    npc.sety(44);
    npc.setBaseHP(100);
    npc.setHP(npc.getBaseHP());
    zone.add(npc);    
    rules.addNPC(npc);


    zone=(StendhalRPZone)world.getRPZone(new IRPZone.ID("0_semos_city"));

    sign=new Sign();
    zone.assignRPObjectID(sign);
    sign.setx(43);
    sign.sety(40);
    sign.setText("Talk to Sato to sell your sheep!.|He probably won't give you a fair price but this is a small village...|The price he will offer you depends on the weight of your sheep.");
    zone.add(sign);

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
                
                payPlayer(player,itemPrice*(sheep.getWeight()/sheep.MAX_WEIGHT));
                
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
    rules.addNPC(npc);
    }
  }
