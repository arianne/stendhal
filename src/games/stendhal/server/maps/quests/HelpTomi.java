package games.stendhal.server.maps.quests;

import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.player.Player;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * QUEST: Get tomi his Ice
 * 
 * PARTICIPANTS:
 * <ul>
 * <li>tomi, a captive in hell</li>
 * </ul>
 * 
 * STEPS:
 * <ul>
 * <li>tomi asks cryptic messages about ice</li>
 * <li>if you have an ice sword you get rewarded</li>
 * </ul>
 * 
 * REWARD:
 * <ul>
 * <li>XP</li>
 * <li>amusement</li>
 * </ul>
 * 
 * REPETITIONS:
 * <ul>
 * <li>yes tomi takes as many ice as you please</li>
 * </ul>
 */
public class HelpTomi extends AbstractQuest {

	private static final String QUEST_SLOT = "help_tomi";
	private static final String extraTrigger = "ice";
	private List<String> questTrigger;

	@Override
	public void init(String name) {
		super.init(name, QUEST_SLOT);
	}

	@Override
	public List<String> getHistory(Player player) {
		List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add("FIRST_CHAT");
		String questState = player.getQuest(QUEST_SLOT);
		if (questState.equals("done")) {
			res.add("DONE");
		}
		return res;
	}
	
	private void step1() {
		SpeakerNPC npc = npcs.get("tomi");
		
		// says quest or ice and doesn't have an ice sword and hasn't brought one before
		npc.add(ConversationStates.ATTENDING,
			questTrigger, 
			new AndCondition(new QuestNotCompletedCondition(QUEST_SLOT), new NotCondition(new PlayerHasItemWithHimCondition("ice sword"))),
			ConversationStates.ATTENDING, 
			"my ice? ice plz", null);
		
		// says quest or ice and doesn't have an ice sword and has brought one in the past
		npc.add(ConversationStates.ATTENDING,
			questTrigger,
			new AndCondition(new QuestCompletedCondition(QUEST_SLOT), new NotCondition(new PlayerHasItemWithHimCondition("ice sword"))),
			ConversationStates.ATTENDING, 
			"where is my ice?", null);

		// says quest or ice and has ice sword with him (first time)
		// player gets a karma bonus and some xp
		npc.add(ConversationStates.ATTENDING,
			questTrigger, 
			new AndCondition(new QuestNotCompletedCondition(QUEST_SLOT), new PlayerHasItemWithHimCondition("ice sword")),
			ConversationStates.ATTENDING,
			"my ice :)",
			new MultipleActions(new DropItemAction("ice sword"), new IncreaseXPAction(1000), new IncreaseKarmaAction(30.0), new SetQuestAction(QUEST_SLOT, "done")));
		
		// says quest or ice and has ice sword with him (second+ time)
		// player gets a karma bonus and some xp
		npc.add(ConversationStates.ATTENDING,
			questTrigger, 
			new AndCondition(new QuestCompletedCondition(QUEST_SLOT), new PlayerHasItemWithHimCondition("ice sword")),
			ConversationStates.ATTENDING,
			"my ice :) :) :)",
			new MultipleActions(new DropItemAction("ice sword"), new IncreaseXPAction(3000), new IncreaseKarmaAction(30.0), new SetQuestAction(QUEST_SLOT, "done")));
	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		// want "ice" and quest_messages to have same meaning in this quest
	    questTrigger = new LinkedList<String>(ConversationPhrases.QUEST_MESSAGES);
		questTrigger.add(extraTrigger);
		step1();

	}
}
