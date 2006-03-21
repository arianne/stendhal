package games.stendhal.server.maps.quests;

import games.stendhal.server.*;
import games.stendhal.server.maps.*;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.Chest;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.npc.Behaviours;
import games.stendhal.server.entity.npc.NPC;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.pathfinder.Path;
import games.stendhal.server.scripting.ScriptAction;
import games.stendhal.server.scripting.ScriptCondition;

import java.util.*;

import marauroa.common.game.IRPZone;

/**
 * QUEST: Beer For Hayunn
 * PARTICIPANTS:
 * - Hayunn Naratha
 *
 * STEPS:
 * - Hayunn asks you to buy a beer from Margaret.
 * - Margaret sells you a beer.
 * - Hayunn sees your beer asks for it and then thanks you.
 *
 * REWARD:
 * - 10 XP
 * - 20 gold coins
 *
 * REPETITIONS:
 * - None.
 */
public class BeerForHayunn implements IQuest
  {
  private StendhalRPWorld world;
  private NPCList npcs;

  private void step_1()
    {
    StendhalRPZone zone=(StendhalRPZone)world.getRPZone(new IRPZone.ID("0_semos_city"));

    SpeakerNPC npc=npcs.get("Hayunn Naratha");

    npc.add(1,new String[]{"quest","task"},null,60,null,new SpeakerNPC.ChatAction()
	{
        public void fire(Player player,String text,SpeakerNPC engine)
          {
          if (!player.isQuestCompleted("beer_hayunn"))
	    {
            engine.say("My mouth is dry and I can't abandon my place. Could you bring me some #beer from the #tavern?");
	    }
          else
	    {
            engine.say("Thanks bud, but I don't want to abuse beer. I will need to have my senses fully aware if a monster decides to appear. If you need anything from me just say it.");
            engine.setActualState(1);
	    }
          }
        });

    npc.add(60,"yes",null,1,"Thanks, bud. I'll be waiting for your return. Now if I can help you in anything just ask.",new SpeakerNPC.ChatAction()
	{
        public void fire(Player player,String text,SpeakerNPC engine)
          {
          player.setQuest("beer_hayunn","start");
          }
        });

    npc.add(60,"no",null,1,"Yes, forget it bud. Now that I think about it you do not look like you can afford inviting this old guy. Now if I can help you in anything just ask.",new SpeakerNPC.ChatAction()
	{
        public void fire(Player player,String text,SpeakerNPC engine)
          {
          player.setQuest("beer_hayunn","rejected");
          }
        });

    npc.add(60,"tavern",null,60,"You don't know where the inn is? Go and ask monogenes. So, will you do it?",null);

    npc.add(60,"beer",null,60,"A bottle of cool beer from #Margaret will be more than enough. So, will you do it?",null);

    npc.add(60,"Margaret",null,60,"Margaret is the pretty tavernmaid hehehe... Well, definitely... will you do it?",null);
    }

  private void step_2()
    {
	//Just buy the beer from Margaret. It isn't a quest
    }
  private void step_3()
    {

    StendhalRPZone zone=(StendhalRPZone)world.getRPZone(new IRPZone.ID("0_semos_city"));

    SpeakerNPC npc=npcs.get("Hayunn Naratha");

    npc.add(0,"hi", new SpeakerNPC.ChatCondition()
      {
      public boolean fire(Player player,SpeakerNPC engine)
        {
       	return player.hasQuest("beer_hayunn") && player.getQuest("beer_hayunn").equals("start");
        }
      },62,null,new SpeakerNPC.ChatAction()
          {
          public void fire(Player player, String text, SpeakerNPC engine)
            {
	    if (player.isEquipped("beer"))
	      {
              engine.say("Hey! Is that beer for me?");
              }
            else
	      {
              engine.say("Hurry up bud! I am still waiting for that beer! Anyway, what can I do for you?");
	      engine.setActualState(1);
              }
            }
          });

    npc.add(62,"yes",null,1,null,new SpeakerNPC.ChatAction()
      {
      public void fire(Player player, String text,SpeakerNPC engine)
        {
        player.drop("beer");
        StackableItem money=(StackableItem)world.getRuleManager().getEntityManager().getItem("money");
        money.setQuantity(20);
        player.equip(money);

        player.addXP(10);

        world.modify(player);
        player.setQuest("beer_hayunn","done");
        engine.say("Slurp! Thanks for the beer bud! If there is anything I can do for you now just say it.");
        }
      });

    npc.add(62,"no",null,1,"Darn! Ok, but remember I asked you a beer for me too. How can I help you then?",null);
    }

  public BeerForHayunn(StendhalRPWorld w, StendhalRPRuleProcessor rules)
    {
    this.npcs=NPCList.get();
    this.world=w;

    step_1();
    step_2();
    step_3();
    }
  }