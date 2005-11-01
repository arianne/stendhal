package games.stendhal.server.entity.npc;

import java.util.Map;
import java.util.Set;
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
  private static StendhalRPWorld world;

  public static void initialize(RPServerManager rpman, StendhalRPRuleProcessor rules, RPWorld world)
    {
    Behaviours.rpman=rpman;
    Behaviours.rules=rules;
    Behaviours.world=(StendhalRPWorld)world;
    }
  
  public static void addGreeting(SpeakerNPC npc)
    {
    npc.add(0,new String[]{"hi","hello","greetings"}, 1,"Greetings! How may I help you?",null);
    }

  public static void addGoodbye(SpeakerNPC npc)
    {
    npc.add(-1,new String[]{"bye","farewell","cya"}, 1,"Bye.",null);
    }
  
  public static class SellerBehaviour
    {
    private Map<String,Integer> items;
    private String choosenItem;
    
    public SellerBehaviour(Map<String,Integer> items)
      {
      this.items=items;
      }
      
    public Set<String> getItems()
      {
      return items.keySet();
      }
      
    public boolean hasItem(String item)
      {
      return items.containsKey(item);
      }
      
    public int getPrice(String item)
      {
      return items.get(item);
      }
    
    public void setChoosenItem(String item)
      {
      choosenItem=item;
      }
    
    public String getChoosenItem()
      {
      return choosenItem;
      }
      
    public int playerMoney(Player player)
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
  
    public boolean chargePlayer(Player player, int amount)
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

    public boolean onSell(SpeakerNPC seller, Player player, String itemName, int itemPrice)
      {
      EntityManager manager = world.getRuleManager().getEntityManager();
       
      Item item=manager.getItem(itemName);
      item.put("zoneid",player.get("zoneid"));
      IRPZone zone=world.getRPZone(player.getID());
      zone.assignRPObjectID(item);
      
      if(player.equip(item))
        {
        chargePlayer(player,itemPrice);
        seller.say("Congratulations! Here is your "+itemName+"!");
        return true;
        }
      else
        {
        seller.say("Sorry, but you cannot equip the "+itemName+".");
        return false;
        }
      }
    }
 
  public static void addSeller(SpeakerNPC npc, SellerBehaviour items)
    {
    npc.setBehaviourData("seller",items);
    
    StringBuffer st=new StringBuffer();
    for(String item: items.getItems())
      {
      st.append(item+",");
      }
       
    npc.add(1,"offer", 1,"I sell "+st.toString(),null);
    npc.add(1,"buy",20, null,new SpeakerNPC.ChatAction()
      {
      public void fire(Player player, String text, SpeakerNPC engine)
        {
        SellerBehaviour sellableItems=(SellerBehaviour)engine.getBehaviourData("seller");
        
        int i=text.indexOf(" ");
        String item=text.substring(i+1);
        
        if(sellableItems.hasItem(item))
          {
          int price=sellableItems.getPrice(item);
          sellableItems.setChoosenItem(item);

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
      public void fire(Player player, String text, SpeakerNPC engine)
        {
        SellerBehaviour sellableItems=(SellerBehaviour)engine.getBehaviourData("seller");

        String itemName=sellableItems.getChoosenItem();        
        int itemPrice=sellableItems.getPrice(itemName);

        if(sellableItems.playerMoney(player)<itemPrice)
          {
          engine.say("A real pity! You don't have enough money!");
          return;
          }
          
        logger.debug("Selling a "+itemName+" to player "+player.getName());
        
        sellableItems.onSell(engine,player,itemName, itemPrice);
        }
      });
    npc.add(20,"no", 1,"Ok, how may I help you?",null);
    }
  }
