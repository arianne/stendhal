package games.stendhal.server.maps.quests;

import games.stendhal.server.*;
import games.stendhal.server.maps.*;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.npc.Behaviours;
import games.stendhal.server.entity.npc.NPC;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.pathfinder.Path;

import java.util.*;

import marauroa.common.game.IRPZone;

/** 
 * QUEST: Look book for Ceryl
 * PARTICIPANTS: 
 * - Ceryl
 * - Jynath
 * 
 * STEPS: 
 * - Talk with Ceryl to activate the quest.
 * - Talk with Jynath for the book.
 * - Return the book to Ceryl
 *
 * REWARD: 
 * - 100 XP
 * - 50 gold coins
 *
 * REPETITIONS:
 * - As much as wanted.
 */
public class LookBookforCeryl implements IQuest 
  {
  private StendhalRPWorld world;
  private NPCList npcs;
  
  private void step_1()
    {
    StendhalRPZone zone=(StendhalRPZone)world.getRPZone(new IRPZone.ID("int_semos_library"));

    SpeakerNPC npc=npcs.get("Ceryl");
    
    npc.add(1, new String[]{"task","quest"},null,
        1,null,new SpeakerNPC.ChatAction()
      {
      public void fire(Player player, String text, SpeakerNPC engine)
        {
        if(player.isQuestCompleted("introduce_players"))
          {
          engine.say("I have nothing for you now.");
          }
        else
          {
          engine.say("I am looking for a very special #book");
          }
        }
      });
    
    /** In case Quest is completed */
    npc.add(1,"book",new SpeakerNPC.ChatCondition()
      {
      public boolean fire(Player player, SpeakerNPC npc)
        {
        return player.isQuestCompleted("ceryl_book");
        }
      },
        1,"I already got the book. Thank you!",null);
        
    /** If quest is not started yet, start it. */      
    npc.add(1,"book",new SpeakerNPC.ChatCondition()
      {
      public boolean fire(Player player, SpeakerNPC npc)
        {
        return !player.hasQuest("ceryl_book");
        }
      },
        60,"Could you ask #Jynath for a #book that I am looking?",null);
        
    npc.add(60,"yes",null,
        1,null,new SpeakerNPC.ChatAction()
      {
      public void fire(Player player, String text, SpeakerNPC engine)
        {
        engine.say("Great!. Start the quest now!");
        player.setQuest("ceryl_book","start");
        }
      });

    npc.add(60,"no",null,1,"Oh! Ok :(",null);

    npc.add(60,"jynath",null,60,"Jynath is a witch that lives at south of Or'ril castle. So will you get me the #book?",null);

    /** Remind player about the quest */
    npc.add(1,"book",new SpeakerNPC.ChatCondition()
      {
      public boolean fire(Player player, SpeakerNPC npc)
        {
        return player.hasQuest("ceryl_book") && player.getQuest("ceryl_book").equals("start");
        }
      },
        1,"I really need that #book now!. Go to talk with #Jynath.",null);

    npc.add(1,"jynath",null,1,"Jynath is a witch that lives at south of Or'ril castle.",null);
    }
  
  private void step_2()
    {
    StendhalRPZone zone=(StendhalRPZone)world.getRPZone(new IRPZone.ID("int_orril_jynath_house"));

    SpeakerNPC npc=npcs.get("Jynath");    
    
    /** If player has quest and is in the correct state, just give him the book. */
    npc.add(0,"hi",new SpeakerNPC.ChatCondition()
      {
      public boolean fire(Player player, SpeakerNPC npc)
        {
        return player.hasQuest("ceryl_book") && player.getQuest("ceryl_book").equals("start");
        }
      },
        1,null,new SpeakerNPC.ChatAction()
      {
      public void fire(Player player, String text, SpeakerNPC engine)
        {
        player.setQuest("ceryl_book","jynath");
        engine.say("I see you talked with Ceryl. Here you have the book he is looking for.");

        Item item=world.getRuleManager().getEntityManager().getItem("book_black");            

        if(!player.equip(item))
          {
          StendhalRPZone zone=(StendhalRPZone)world.getRPZone(player.getID());
          
          zone.assignRPObjectID(item);
          item.setx(player.getx());
          item.sety(player.gety());
          zone.add(item);
          }
        
        }
      });

    /** If player keep asking for book, just tell him to hurry up */
    npc.add(0,"hi",new SpeakerNPC.ChatCondition()
      {
      public boolean fire(Player player, SpeakerNPC npc)
        {
        return player.hasQuest("ceryl_book") && player.getQuest("ceryl_book").equals("jynath");
        }
      },
        1,"Hurry up! Grab the book to #Ceryl.", null);

    npc.add(1,"ceryl",null,1,"Ceryl is the book keeper at Semos's library",null);

    /** Finally if player didn't started the quest, just ignore him/her */
    npc.add(1,"book",new SpeakerNPC.ChatCondition()
      {
      public boolean fire(Player player, SpeakerNPC npc)
        {
        return !player.hasQuest("ceryl_book");
        }
      },
        1,"Shhhh!!! I am working on a new potion!.", null);      
    }
  
  private void step_3()
    {
    StendhalRPZone zone=(StendhalRPZone)world.getRPZone(new IRPZone.ID("int_semos_library"));

    SpeakerNPC npc=npcs.get("Ceryl");
        
    /** Complete the quest */        
    npc.add(0,"hi",new SpeakerNPC.ChatCondition()
      {
      public boolean fire(Player player, SpeakerNPC npc)
        {
        return player.hasQuest("ceryl_book") && player.getQuest("ceryl_book").equals("jynath");
        }
      },
        1,null,new SpeakerNPC.ChatAction()
      {
      public void fire(Player player, String text, SpeakerNPC engine)
        {
        Item item=player.drop("book_black");
        if(item!=null)
          {
          engine.say("OH! The book! Thanks!");
          StackableItem money=(StackableItem)world.getRuleManager().getEntityManager().getItem("money");            

          money.setQuantity(50);
          player.equip(money);
          player.addXP(100);

          world.modify(player);

          player.setQuest("ceryl_book","done");
          }
        else
          {
          engine.say("Where did you put #Jynath's #book?. You need to start again the search.");
          player.removeQuest("ceryl_book");
          }              
        }
      });
    }
    
  public LookBookforCeryl(StendhalRPWorld w, StendhalRPRuleProcessor rules)
    {
    this.npcs=NPCList.get();
    this.world=w;
    
    step_1();
    step_2();
    step_3();
    }
  }
    
