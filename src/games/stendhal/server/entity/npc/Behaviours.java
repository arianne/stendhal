package games.stendhal.server.entity.npc;

import java.util.Map;
import java.util.Iterator;
import games.stendhal.server.*;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.item.*;
import games.stendhal.server.rule.*;
import marauroa.common.*;
import marauroa.common.game.*;
import marauroa.server.game.*;
import org.apache.log4j.Logger;

public class Behaviours 
  {  
  /** the logger instance. */
  private static final Logger logger = Log4J.getLogger(Behaviours.class);

  private static RPServerManager rpman;
  private static StendhalRPRuleProcessor rules;
  private static RPWorld world;

  public static void initialize(RPServerManager rpman, StendhalRPRuleProcessor rules, RPWorld world)
    {
    Behaviours.rpman=rpman;
    Behaviours.rules=rules;
    Behaviours.world=world;
    }
  
  public static void addGreeting(SpeakerNPC npc)
    {
    npc.add(0,new String[]{"hi","hello","greetings"}, 1,"Greetings! How may I help you?",null);
    }

  public static void addGoodbye(SpeakerNPC npc)
    {
    npc.add(-1,new String[]{"bye","farewell","cya"}, 1,"Bye.",null);
    }
 
  public static void addSeller(SpeakerNPC npc, Map<String,Integer> items)
    {
    npc.setBehaviourData("seller",items);
    
    StringBuffer st=new StringBuffer();
    for(String item: items.keySet())
      {
      st.append(item+",");
      }
       
    npc.add(1,"offer", 1,"I sell "+st.toString(),null);
    npc.add(1,"buy",20, null,new SpeakerNPC.ChatAction()
      {
      public void fire(Player player, String text, SpeakerNPC engine)
        {
        Map<String,Integer> sellableItems=engine.getBehaviourData("seller");
        
        int i=text.indexOf(" ");
        String item=text.substring(i+1);
        
        if(sellableItems.containsKey(item))
          {
          int price=sellableItems.get(item);
          engine.setTemporalData(item);

          engine.say(item+" costs "+price+". Do you want to buy?");
          }
        else
          {
          engine.say("Sorry, I don't sell "+item);
          engine.setActualState(1);          
          }
        }
      });
      
    npc.add(20,"yes", 1,"Thanks.",new SpeakerNPC.ChatAction()
      {
      private int playerMoney(Player player)
        {
        int money=0;
        
        Iterator<RPSlot> it=player.slotsIterator();
        while(it.hasNext())
          {
          RPSlot slot=(RPSlot)it.next();
          for(RPObject object: slot)
            {
            if(object instanceof Money)
              {
              money+=((Money)object).getQuantity();
              }
            }
          }
        
        return money;
        }
    
      private boolean chargePlayer(Player player, int amount)
        {
        int left=amount;
        
        Iterator<RPSlot> it=player.slotsIterator();
        while(it.hasNext() && left!=0)
          {
          RPSlot slot=(RPSlot)it.next();
    
          Iterator<RPObject> object_it=slot.iterator();
          while(object_it.hasNext())
            {
            RPObject object=object_it.next();
            if(object instanceof Money)
              {
              int quantity=((Money)object).getQuantity();
              if(left>=quantity)
                {
                slot.remove(object.getID());
                left-=quantity;
                
                object_it=slot.iterator();
                }
              else
                {
                ((Money)object).setQuantity(quantity-left);
                left=0;
                break;
                }
              }
            }
          }
        
        world.modify(player);
        
        return left==0;
        }
    
      public void fire(Player player, String text, SpeakerNPC engine)
        {
        Map<String,Integer> sellableItems=engine.getBehaviourData("seller");                

        EntityManager manager = ((StendhalRPWorld)world).getRuleManager().getEntityManager();
        
        String itemName=engine.getTemporalData();
        
        Item item=manager.getItem(itemName);
        int itemPrice=sellableItems.get(itemName);

        if (playerMoney(player) < itemPrice)
          {
          engine.say("A real pity! You don't have enough money!");
          return;
          }
          
        logger.debug("Selling a "+itemName+" to player "+player.getName());
        
        item.put("zoneid",player.get("zoneid"));
        IRPZone zone=world.getRPZone(engine.getID());
        zone.assignRPObjectID(item);
        
        if(player.equip(item))
          {
          chargePlayer(player,itemPrice);
          engine.say("Congratulations! Here is your "+itemName+"!");
          }
        else
          {
          engine.say("Sorry, but you cannot equip the "+itemName+".");
          }

        }
      });
    npc.add(20,"no", 1,"Ok, how may I help you?",null);
    }
  }
