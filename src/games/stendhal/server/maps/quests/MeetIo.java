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
 * QUEST: Speak with Io
 * PARTICIPANTS:
 * - Io
 *
 * STEPS:
 * - Talk to Io to activate the quest and keep speaking with Io.
 *
 * REWARD:
 * - 10 XP (check that user's level is lesser than 15)
 * - 5 gold coins
 *
 * REPETITIONS:
 * - As much as wanted.
 */
public class MeetIo implements IQuest
  {
  private StendhalRPWorld world;
  private NPCList npcs;

  private void step_1()
    {
    StendhalRPZone zone=(StendhalRPZone)world.getRPZone(new IRPZone.ID("int_semos_temple"));

    SpeakerNPC npc=npcs.get("Io Flotto");

    npc.add(1,"yes",null,50,"Type #/who to know what adventurer's souls are wandering the world of Stendhal. Do you want to know the second basic element of telepathy?",null);

    npc.add(50,"yes",null,51,"Type #/where #user_name to know where in the vast world of Stendhal, the person you're seeking is roaming (use #/where #sheep with your owned sheep ). To understand positioning in Stendhal you should ask #Zynn. Ready for the third?",null);

    npc.add(51,"Zynn",null,51,"His full name is Zynn Iwuhos. He spends most of his time in the library, making maps and writing historical record books. Ready for the third?",null);

    npc.add(51,"yes",null,52,"Type #/tell #user_name #your_text or #/msg #user_name #your_text to talk to the person you wish throughout the entire world of Stendhal, no matter where that person is.  On the other hand, if you're the listener type #// #your_text to reply him. Ready for the fourth?",null);

    npc.add(52,"yes",null,53,"Use #SHIFT #+ #UP #arrow keys to put in your mouth your last spoken sentence and previous ones. Use #CTRL #+ #L in case you can't focus the history of the previous conversations you have had and actions you have done. Ready for the fifth?",null);

    npc.add(53,"yes",null,54,"Type #/support #your_text to try to report something to any administrator who happens to be online at that moment. Besides, you can contact the author #mblanch with an IRC client program. Connect to server: #irc.freenode.net and type #/join #arianne. Ready for the sixth?",null);

    npc.add(54,"yes",null,55,"Use the #ESC key on your keyboard or simply close the window to travel to the astral plane ( and quit the game ). Do you want me to show you how to float in the air like me?",null);

    /** Give the reward to the patient newcomer user */
    npc.add(55,"yes",null,0,null,new SpeakerNPC.ChatAction()
      {
      public void fire(Player player, String text, SpeakerNPC engine)
        {
        int level=player.getLevel();
        String answer;
        if(level<15)
          {
          StackableItem money=(StackableItem)world.getRuleManager().getEntityManager().getItem("money");

          money.setQuantity(10);
          player.equip(money);

          player.addXP(10);

          world.modify(player);

          answer="Sense you. And don't let anything disturb your concentration.";
          }
        else
          {
	  answer="Hey! I know what you're thinking and I don't like it!";
          }

	engine.say("Yawn... Maybe later... I don't want to bore you with so much knowledge at once. Anyway, you can remember briefly all this by typing #/help.\n "+answer);
        }
	});

    npc.add(-1,"no",null,0,"If you ever decide to widen the frontiers of your mind, you are welcome. Bye",null);

    }

 public MeetIo(StendhalRPWorld w, StendhalRPRuleProcessor rules)
    {
    this.npcs=NPCList.get();
    this.world=w;

    step_1();
    }
  }
