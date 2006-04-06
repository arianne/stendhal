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
 * QUEST: Speak with Ketteh
 * PARTICIPANTS:
 * - Ketteh
 *
 * STEPS:
 * - Talk to Ketteh to activate the quest and keep speaking with Ketteh.
 *
 * REWARD:
 * - No XP
 * - No money
 * REPETITIONS:
 * - As much as wanted.
 */
public class MeetKetteh implements IQuest
  {
  private StendhalRPWorld world;
  private NPCList npcs;

  private void step_1()
    {
    StendhalRPZone zone=(StendhalRPZone)world.getRPZone(new IRPZone.ID("int_semos_storage_0"));

    SpeakerNPC npc=npcs.get("Ketteh Wehoh");

    npc.add(0,"hi",new SpeakerNPC.ChatCondition()
      {
      public boolean fire(Player player, SpeakerNPC engine)
        {
	    return true; //player.equals(0);
        }
      },1,null, new SpeakerNPC.ChatAction()
        {
        public void fire(Player player,String text,SpeakerNPC engine)
          {
	//A little trick to make NPC remember if it has met player before anc react accordingly
	//NPC_name quest doesn't exist anywhere else neither is used for any other purpose
          if (!player.isQuestCompleted("Ketteh"))
            {
            engine.say("Who are you? Aiiieeeee!!! You're naked! Right-click on you and choose SET OUTFIT! Shhh! Don't even think on clicking on the white bar at the bottom and write to reply to me! And if you happen to talk to anyone in the city you'd better begin the conversation saying HI. And don't be gross and just leave: say BYE to end the conversation. And use #CTRL #+ #arrow to turn around and face me when I'm talking to you! Wait! I am sure you are a friend of that onlooker Nomyr who's always peeking at the windows! Now use the #arrows and go out!");
            player.setQuest("Ketteh","done");
            }
          else
            {
            engine.say("Hi again, "+player.getName()+". How can I #shout at you this time?");
            }
          }
	});

    npc.add(1,"no",null,0,"Ok, don't move. I'm calling the law enforcers!",null);

   }
 public MeetKetteh(StendhalRPWorld w, StendhalRPRuleProcessor rules)
    {
    this.npcs=NPCList.get();
    this.world=w;

    step_1();
    }
  }
