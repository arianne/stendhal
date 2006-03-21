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
 * QUEST: Speak with Hayunn
 * PARTICIPANTS:
 * - Hayunn Naratha
 *
 * STEPS:
 * - Talk to Hayunn to activate the quest and keep speaking with Hayunn.
 *
 * REWARD:
 * - 10 XP (check that user's level is lower than 15)
 * - 5 gold coins
 *
 * REPETITIONS:
 * - As much as wanted.
 */
public class MeetHayunn implements IQuest
  {
  private StendhalRPWorld world;
  private NPCList npcs;

  private void step_1()
    {
    StendhalRPZone zone=(StendhalRPZone)world.getRPZone(new IRPZone.ID("0_semos_city"));

    SpeakerNPC npc=npcs.get("Hayunn Naratha");

    npc.add(1,"yes",null,50,"I right-clicked on creatures and chose ATTACK. But why killing creatures and risking my life if I didn't get paid for it you ask. Hmmm? Hmmm?",null);

    npc.add(50,"yes",null,51,"I right-clicked on the corpses of the creatures and chose INSPECT. Then I dragged the items I found to my bag but I had to be NEXT to the corpse. Can you guess how I identified the items in my bag or on the ground?",null);

    npc.add(51,"no",null,52,"I right-clicked on the items and chose LOOK. You may be wondering how I made my way back to here in Semos despite of the injuries received from the monsters... Hmmm?",null);

    npc.add(52,"yes",null,53,"I right-clicked on the food in my bag or on the ground and chose USE. But food doesn't recover you instantly: you gain health points over time. Do you want to know how to heal instantly?",null);

    npc.add(53,"yes",null,54, "Once you get enough money you should buy at least one potion from Carmen the healer. A potion can be very handy when you're deep in the heart of the dungeon. Have I told you where the dungeon is?",null);

    npc.add(54,"no",null,55,"Do you see this big hole on the ground next to me? It is the entrance to the dungeons. Do you want to know how to move accurately through its narrow corridors?",null);

    npc.add(55,"yes",null,56,"You just have to left-double-click to the place you want to move. Do you want to know where you can find yet another relevant information to become the best adventurer?",null);

    npc.add(56,"yes",null,0,null,
   new SpeakerNPC.ChatAction()
      {
      public void fire(Player player, String text, SpeakerNPC engine)
        {

        int level=player.getLevel();
        String answer;
        if(level<15)
          {
          answer="Well... Fame and glory await you, depart and don't get killed in the dungeons my young friend!";
          StackableItem money=(StackableItem)world.getRuleManager().getEntityManager().getItem("money");
          money.setQuantity(5);
	  player.equip(money);

          player.addXP(10);

          world.modify(player);

          }
        else
          {
          answer="You look like me when I was younger... only that weaker. Bye";
          }
	  engine.say("You can find a list of all the known and unknown creatures that inhabit the world of stendhal at #http://arianne.sourceforge.net/wiki/index.php?title=StendhalBestiary You can find how many experience points you need to level up at #http://arianne.sourceforge.net/wiki/index.php?title=LevelTables You can find today's most reputed adventurers at #http://stendhal.game-server.cc/template_site/\n "+answer);
	 }
       });

     npc.add(new int[]{1,50,52,53,55},"no",null,1,"Oh well. I'm sure someone else will be interested. Tell me what I can do for you.",null);
     npc.add(new int[]{51,54},"yes",null,1,"Oh... I see... You think I have nothing to teach you...Ok, tell me what you want from me.",null);
    }

  public MeetHayunn(StendhalRPWorld w, StendhalRPRuleProcessor rules)
    {
    this.npcs=NPCList.get();
    this.world=w;

    step_1();
    }
  }

