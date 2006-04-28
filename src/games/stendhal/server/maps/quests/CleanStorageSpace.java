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
 * QUEST: CleanStorageSpace PARTICIPANTS: - Eonna
 * 
 * STEPS: - Eonna asks you to clean her storage_space. - You go kill at least a
 * rat, a cave rat and a cobra. - Eoanna checks your kills and then thanks you.
 * 
 * REWARD: - 25 XP
 * 
 * REPETITIONS: - None.
 */
public class CleanStorageSpace implements IQuest
  {
  private StendhalRPWorld world;
  private NPCList npcs;

  private void step_1()
    {
    SpeakerNPC npc = npcs.get("Eonna");

    npc.add(1,new String[] { "quest", "task" },null,60,null,
        new SpeakerNPC.ChatAction()
          {
            public void fire(Player player, String text, SpeakerNPC engine)
              {
              if(!player.isQuestCompleted("clean_storage"))
                {
                engine.say("My #storage_space it is crawling with rats. Will you #help me?");
                } else
                {
                engine.say("Thanks again, I don't think it needs to be cleaned again yet. If I can help you somehow just say it.");
                engine.setActualState(1);
                }
              }
          });

    npc.add(
        60,
        "yes",
        null,
        1,
        "Thank you! I'll be waiting for your return. Now if I can help you in anything just ask.",
        new SpeakerNPC.ChatAction()
          {
            public void fire(Player player, String text, SpeakerNPC engine)
              {
              player.setQuest("clean_storage","start");
              player.removeKill("rat");
              player.removeKill("cobra");
              player.removeKill("caverat");
              }
          });

    npc.add(
        60,
        "no",
        null,
        1,
        "Maybe you are not the hero I thought you would be. *sighs* Now if I can help you in anything *sighs* just ask.",
        new SpeakerNPC.ChatAction()
          {
            public void fire(Player player, String text, SpeakerNPC engine)
              {
              player.setQuest("clean_storage","rejected");
              }
          });

    npc.add(
        60,
        "storage_space",
        null,
        60,
        "yes it down the stairs, there some rats and I think I saw a snake too so be careful. So, will you do it?",
        null);

    }

  private void step_2()
    {
    // Go kill at least a rat, a cave rat and a cobra.
    }

  private void step_3()
    {

    StendhalRPZone zone = (StendhalRPZone) world.getRPZone(new IRPZone.ID(
        "0_semos_city"));

    SpeakerNPC npc = npcs.get("Eonna");

    npc.add(0,"hi",new SpeakerNPC.ChatCondition()
      {
        public boolean fire(Player player, SpeakerNPC engine)
          {
          return player.hasQuest("clean_storage")
              && player.getQuest("clean_storage").equals("start");
          }
      },70,null,new SpeakerNPC.ChatAction()
      {
        public void fire(Player player, String text, SpeakerNPC engine)
          {
          if(player.hasKilled("rat") && player.hasKilled("caverat")
              && player.hasKilled("cobra"))
            {
            engine.say("Oh wow! A fine hero at last! Thank you! Now can I help you with anything?");
            player.addXP(25);
            player.setQuest("clean_storage","done");
            engine.setActualState(1);
            } else
            {
            engine.say("Don't you remember... you promised to clean my #storage_space.");
            }
          }
      });
    npc.add(
        70,
        "storage_space",
        null,
        1,
        "Did you forget? It's down the stairs, there some rats and I think I saw a snake too so be careful. Please hurry.",
        null);

    }

  public CleanStorageSpace(StendhalRPWorld w, StendhalRPRuleProcessor rules)
    {
    this.npcs = NPCList.get();
    this.world = w;

    step_1();
    step_2();
    step_3();
    }
  }