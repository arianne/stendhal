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

import java.util.*;

import marauroa.common.game.IRPZone;

/**
 * QUEST: Hat For Monogenes
 * PARTICIPANTS:
 * - Monogenes
 *
 * STEPS:
 * - Monogenes asks you to buy a hat for him.
 * - Xin Blanca sells you a leather_helmet.
 * - Monogenes sees your leather_helmet and asks for it and then thanks you.
 *
 * REWARD:
 * - 10 XP
 *
 * REPETITIONS:
 * - None.
 */
public class HatForMonogenes implements IQuest
  {
  private StendhalRPWorld world;
  private NPCList npcs;

  private void step_1()
    {
    StendhalRPZone zone=(StendhalRPZone)world.getRPZone(new IRPZone.ID("0_semos_city"));

    SpeakerNPC npc=npcs.get("Monogenes");

    npc.add(1,new String[]{"quest","task"},null,60,null,new SpeakerNPC.ChatAction()
	{
        public void fire(Player player,String text,SpeakerNPC engine)
          {
          if (!player.isQuestCompleted("hat_monogenes"))
	    {
            engine.say("Could you bring me a #hat to cover my baldness? Brrrrr! Semos day's are getting colder...");
	    }
          else
	    {
            engine.say("Thanks good friend, but this hat will last five winters at least and I don't really need more than one. If I can help you somehow just say it.");
            engine.setActualState(1);
	    }
          }
        });

    npc.add(60,"yes",null,1,"Thanks, my good friend. I'll be waiting for your return. Now if I can help you in anything just ask.",new SpeakerNPC.ChatAction()
	{
        public void fire(Player player,String text,SpeakerNPC engine)
          {
          player.setQuest("hat_monogenes","start");
          }
        });

    npc.add(60,"no",null,1,"Yes, forget it bud. You surely have more importants things to do and little time. I'll just stay here with my cool not-as-in-slick head. Boohooooo! Sniff... now if I can help you... sniff in anything sniff... just ask.",new SpeakerNPC.ChatAction()
	{
        public void fire(Player player,String text,SpeakerNPC engine)
          {
          player.setQuest("hat_monogenes","rejected");
          }
        });

    npc.add(60,"hat",null,60,"You don't know what a hat is? Anything light like leather that can cover my head. So, will you do it?",null);
    }

  private void step_2()
    {
	//Just buy the leather_helmet from Xin Blanca. It isn't a quest
    }
  private void step_3()
    {

    StendhalRPZone zone=(StendhalRPZone)world.getRPZone(new IRPZone.ID("0_semos_city"));

    SpeakerNPC npc=npcs.get("Monogenes");

    npc.add(0,"hi", new SpeakerNPC.ChatCondition()
      {
      public boolean fire(Player player,SpeakerNPC engine)
        {
        return player.hasQuest("hat_monogenes") && player.getQuest("hat_monogenes").equals("start");
        }
      },62,null,new SpeakerNPC.ChatAction()
          {
          public void fire(Player player, String text, SpeakerNPC engine)
            {
	    if (player.isEquipped("leather_helmet"))
	      {
              engine.say("Hey! Is that hat for me?");
              }
            else
	      {
              engine.say("Hey, my good friend, remember that leather hat I asked you before? I like having fresh ideas but not in this manner... Anyway, what can I do for you?");
	      engine.setActualState(1);
              }
            }
          });

    npc.add(62,"yes",null,1,null,new SpeakerNPC.ChatAction()
      {
      public void fire(Player player, String text,SpeakerNPC engine)
        {
        player.drop("leather_helmet");

        player.addXP(10);

        world.modify(player);
        player.setQuest("hat_monogenes","done");
        engine.say("Bless you, my good friend ! Now I can laugh at the soon coming snowflakes wuahahaha! Ahem... If there's anything I can do for you now just say it.");
        }
      });

    npc.add(62,"no",null,1,"Oh! Ok... I suppose there's someone more fortunate than me that will get his head warm today...Boohoooo! Sniff... How can I help you then?",null);
    }

  public HatForMonogenes(StendhalRPWorld w, StendhalRPRuleProcessor rules)
    {
    this.npcs=NPCList.get();
    this.world=w;

    step_1();
    step_2();
    step_3();
    }
  }