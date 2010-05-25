package games.stendhal.server.maps.semos.guardhouse;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.StartRecordingKillsAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.maps.quests.BeerForHayunn;

import java.util.LinkedList;
import java.util.List;


/**
 * An old hero (original name: Hayunn Naratha) who players meet when they enter the semos guard house.
 *
 * @see games.stendhal.server.maps.quests.BeerForHayunn
 * @see games.stendhal.server.maps.quests.MeetHayunn
 */
//TODO: take NPC definition elements which are currently in XML and include here
public class RetiredAdventurerNPC extends SpeakerNPCFactory {
	private final String QUEST_SLOT="meet_hayunn";
	
	@Override
	public void createDialog(final SpeakerNPC npc) {
		// A little trick to make NPC remember if it has met
	    // player before and react accordingly
		// NPC_name quest doesn't exist anywhere else neither is
		// used for any other purpose

		final List<ChatAction> actions = new LinkedList<ChatAction>();
		actions.add(new SetQuestAction(QUEST_SLOT, 0, "start"));
		actions.add(new StartRecordingKillsAction(QUEST_SLOT, 1, "rat", 0, 1));

		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new QuestNotStartedCondition("meet_hayunn"),
				ConversationStates.ATTENDING,
		        "Hi. I bet you've been sent here to learn about adventuring from me. First, lets see what you're made of. Go and kill a rat outside, you should be able to find one easily. Do you want to learn how to attack it, before you go?",
				new MultipleActions(actions));

	   	npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new QuestCompletedCondition("meet_hayunn"), new NotCondition(new QuestActiveCondition(BeerForHayunn.QUEST_SLOT))),
				ConversationStates.ATTENDING,
				"Hi again, how can I #help you this time?",
				null);
				 
		npc.addHelp("As I say, I'm a retired adventurer, and now I teach people. Do you want me to teach you what I know?");
		npc.addJob("My job was to guard the people of Semos from any creature that might escape from vile dungeons. I have now retired, and with all our young people away battling Blordrough's evil legions to the south, the monsters down there are getting more confident about coming to the surface. Semos will need help from people like your good self. Ask the Mayor for what task he needs doing.");
		npc.addGoodbye();
		// further behaviour is defined in quests.
	}
}
