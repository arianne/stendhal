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
 * QUEST: Speak with Monogenes
 * PARTICIPANTS:
 * - Monogenes
 *
 * STEPS:
 * - Talk to Monogenes to activate the quest and keep speaking with Monogenes.
 *
 * REWARD:
 * - 10 XP (check that user's level is lesser than 5)
 * - No money
 * REPETITIONS:
 * - As much as wanted.
 */
public class MeetMonogenes implements IQuest
  {
  private StendhalRPWorld world;
  private NPCList npcs;

  private void step_1()
    {
    StendhalRPZone zone=(StendhalRPZone)world.getRPZone(new IRPZone.ID("int_semos_blacksmith"));

    SpeakerNPC npc=npcs.get("Monogenes");

    npc.add(1,"yes",null,50,"Only ask them about what they bring the conversation around to: the WORDS that are bolded in blue color. Otherwise, you can get a harsh answer although you usually can ask about their #job , #help, #offer or #quest . Do you want to know where city's main buildings are?",null);

    npc.add(50,"yes",null,1,"Sometimes it is helpful to read the city's wooden signs by right-clicking on them and choosing LOOK. I can direct you to the #bank, the #library, the #tavern, the #temple, the #blacksmith or the #village.",null);

    npc.add(1,"bank",null,1,"The bank is precisely this building next to me. I thought the big chest on the front would have given you a clue.",null);

    npc.add(1,"library",null,1,"The library is west from here, following the path. There's an OPEN BOOK AND A FEATHER sign over one of the two doors.",null);

    npc.add(1,"tavern",null,1,"The tavern is southeast from here, following the path. You'll see a big INN sign over the door. You can't miss it.",null);

    npc.add(1,"temple",null,1,"The temple is the second building southeast from here, following the path. There's a small CROSS over the roof.",null);

    npc.add(1,"blacksmith",null,1,"The blacksmith's shop is southwest from here, following the path. There's a small SWORD sign over the door.",null);

    npc.add(1,"village",null,1,"The village is southwest from here, following the path. There you can buy sheeps to breed.",null);

    /** Give the reward to the polite newcomer user */
    npc.add(1,"bye",null,0,null,new SpeakerNPC.ChatAction()
      {
      public void fire(Player player, String text, SpeakerNPC engine)
        {

        int level=player.getLevel();
        if(level<15)
          {
          engine.say("Bye, my friend. I hope my indications have been helpful...");

          player.addXP(10);

          world.modify(player);

          }
        else
          {
          engine.say("It's curious... Now that I think about it, I would have betted I had seen you in Semos before...");
          }
        }
      });

    npc.add(1,"no",null,0,"And how are you supposed to know what's happening? By reading the Semos tribune? Bye!",null);
    npc.add(50,"no",null,0,"Oh I see... You are of that kind of persons that don't like asking for directions huh? Well, good luck finding the secretly hidden mmhmmhmm!",null);
    }

 public MeetMonogenes(StendhalRPWorld w, StendhalRPRuleProcessor rules)
    {
    this.npcs=NPCList.get();
    this.world=w;

    step_1();
    }
  }
