package games.stendhal.server.maps.quests;

import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.player.Player;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * QUEST: Speak with Hayunn 
 * <p>
 * PARTICIPANTS: <ul><li> Hayunn Naratha</ul>
 *
 * STEPS: <ul><li> Talk to Hayunn to activate the quest and keep speaking with Hayunn.</ul>
 *
 * REWARD: <ul><li> 10 XP <li> 5 gold coins</ul>
 *
 * REPETITIONS: <ul><li> As much as wanted, but you only get the reward once.</ul>
 */
public class MeetHayunn extends AbstractQuest {

	private static final String QUEST_SLOT = "meet_hayunn";

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
		if (isCompleted(player)) {
			res.add("DONE");
		}
		return res;
	}

	private void prepareHayunn() {

		SpeakerNPC npc = npcs.get("Hayunn Naratha");

		npc.add(
			ConversationStates.ATTENDING,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.INFORMATION_1,
			"Well, back when I was a young adventurer, I right-clicked on my enemies and chose ATTACK. But, you may ask, what is the point behind risking my life to kill things? Yes?",
			null);

		npc.add(
			ConversationStates.INFORMATION_1,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.INFORMATION_2,
			"Ah-ha! Well, what I did next was to click on the corpse of my slain opponent and choose INSPECT. Then, after making sure I was close enough to the corpse to reach it, I dragged the items there into my bag. Can you guess how I identified what these objects were, and what they did?",
			null);

		npc.add(
			ConversationStates.INFORMATION_2,
			"no",
			null,
			ConversationStates.INFORMATION_3,
			"Well, it's obvious really; I right-clicked on the items and selected LOOK to get a description. Now, I know what you're thinking; how did I manage to survive for so long in the dungeons without getting killed?",
			null);

		npc.add(
			ConversationStates.INFORMATION_3,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.INFORMATION_4,
			"By making sure I ate regularly! By right-clicking a food item - either in my bag or on the ground - I was able to slowly regain my health with each bite. That takes time of course, and there are ways to regain your health instantly... want to hear?",
			null);

		npc.add(
			ConversationStates.INFORMATION_4,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.INFORMATION_5,
			"Once you've earned enough money, you should visit one of the local healers - Carmen or Ilisa - and buy a potion. Potions are very handy when you're alone in the deep dungeons. Did I tell you where the dungeon is yet?",
			null);

		npc.add(
			ConversationStates.INFORMATION_5,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.INFORMATION_6,
			"See this hole behind me? That's the entrance to the dungeons. The corridors are pretty narrow down there, so there's a trick to moving quickly and accurately, if you'd like to hear it.",
			null);

		npc.add(
			ConversationStates.INFORMATION_6,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.INFORMATION_7,
			"Simple, really; just double-click the place you want to move to. There's a lot more information than I can relate just off the top of my head... do you want to know where to read more?",
			null);

		String epilog = "You can find a list of all sorts of animals, monsters, and other foes at #http://arianne.sourceforge.net/wiki/index.php?title=StendhalBestiary\nYou can find out about experience points and levelling up at #http://arianne.sourceforge.net/wiki/index.php?title=LevelTables\nYou can read about some of the currently most powerful and successful warriors at #http://stendhal.game-host.org\n ";
		
		npc.add(ConversationStates.INFORMATION_7,
			ConversationPhrases.YES_MESSAGES, new QuestCompletedCondition(QUEST_SLOT),
			ConversationStates.IDLE, 
			epilog + "You know, you remind me of my younger self...",
			null);
		
		List<SpeakerNPC.ChatAction> reward = new LinkedList<SpeakerNPC.ChatAction>();
		reward.add(new EquipItemAction("money", 5));
		reward.add(new IncreaseXPAction(10));
		reward.add(new SetQuestAction(QUEST_SLOT, "done"));

		npc.add(ConversationStates.INFORMATION_7,
				ConversationPhrases.YES_MESSAGES, new QuestNotCompletedCondition(QUEST_SLOT),
				ConversationStates.IDLE, 
				epilog + "Well, good luck in the dungeons! Here's hoping you find fame and glory, and keep watch for monsters!",
				new MultipleActions(reward));

		npc.add(new int[] { ConversationStates.ATTENDING,
					ConversationStates.INFORMATION_1,
					ConversationStates.INFORMATION_3,
					ConversationStates.INFORMATION_4,
					ConversationStates.INFORMATION_6 },
				"no", null, ConversationStates.ATTENDING,
				"Oh well, I'm sure someone else will stop by for a chat soon.",
				null);

		npc.add(new int[] { ConversationStates.INFORMATION_2,
				ConversationStates.INFORMATION_5 },
				"yes", null, ConversationStates.ATTENDING,
				"Hmm. You think I have nothing to teach you, eh? I see.", null);
	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		prepareHayunn();
	}
}
