package games.stendhal.server.maps.quests;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.ExamineChatAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.KilledCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
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
 * STEPS: <ul>
 * <li> Talk to Hayunn to activate the quest.
 * <li> He asks you to kill a rat, also offering to teach you how
 * <li> Return and learn how to loot, identify items and heal
 * <li> Return and learn how to double click move, and get some URLs
 * </ul>
 *
 * REWARD: <ul><li> 20 XP <li> 5 gold coins <li> studded shield </ul>
 *
 * REPETITIONS: <ul><li> Get the URLs as much as wanted but you only get the reward once.</ul>
 */
public class MeetHayunn extends AbstractQuest {

	private static final String QUEST_SLOT = "meet_hayunn";

	//This is 1 minute at 300 ms per turn
	private static final int TIME_OUT = 200;


	
	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
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

		final SpeakerNPC npc = npcs.get("Hayunn Naratha");

		// player wants to learn how to attack
		npc.add(
				ConversationStates.ATTENDING,
				ConversationPhrases.YES_MESSAGES,
				new QuestInStateCondition(QUEST_SLOT, "start"),
				ConversationStates.ATTENDING,
				"Well, back when I was a young adventurer, I right-clicked on my enemies and chose ATTACK. I'm sure that will work for you, too. Good luck, and come back once you are done.",
				null);

		//player doesn't want to learn how to attack
		npc.add(
				ConversationStates.ATTENDING,
				ConversationPhrases.NO_MESSAGES,
				new QuestInStateCondition(QUEST_SLOT, "start"),
				ConversationStates.ATTENDING,
				"Fine, you seem like an intelligent type. I'm sure you'll work it out!",
				null);

		//player returns to Hayunn not having killed a rat
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, "start"), new NotCondition(new KilledCondition("rat"))),
				ConversationStates.ATTENDING,
		        "I see you haven't managed to kill a rat yet. Do you need me to tell you how to fight them?",
				null);

		//player returns to Hayunn having killed a rat
		final List<ChatAction> actions = new LinkedList<ChatAction>();
		actions.add(new IncreaseXPAction(10));
		actions.add(new SetQuestAction(QUEST_SLOT, "killed"));

		npc.add(
				ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, "start"), new KilledCondition("rat")),
				ConversationStates.INFORMATION_1,
		        "You killed the rat! Now, you may ask, what is the point behind risking your life to kill things? #Yes?",
				new MultipleActions(actions));

		// player wants to learn more from Hayunn
		npc.add(
			ConversationStates.INFORMATION_1,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.INFORMATION_2,
			"Ah-ha! Well, you can loot items from corpses. You should right-click on a corpse and choose INSPECT. Once you're close enough to the corpse to reach it, you can drag the items into your bag. Do you want to hear how to identify items? #Yes?",
			null);

		npc.add(
			ConversationStates.INFORMATION_2,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.INFORMATION_3,
			"You can right-click on the items and select LOOK to get a description. Now, I know what you're thinking; how are you going to survive without getting killed? Do you want to know?",
			null);

		npc.add(
			ConversationStates.INFORMATION_3,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.INFORMATION_4,
			"You need to eat regularly! By right-clicking a food item - either your bag or on the ground - you can slowly regain your health with each bite. That takes time of course, and there are ways to regain your health instantly... want to hear?",
			null);

		npc.add(
			ConversationStates.INFORMATION_4,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.INFORMATION_5,
			"Once you've earned enough money, you should visit one of the healers in Semos - Carmen or Ilisa - and buy a potion. Potions are very handy when you're alone in Semos dungeons. Do you want to know where Semos is?",
			null);

	   	// The player has had enough info for now. Send them to semos. When they come back they can learn some more tips.

		final List<ChatAction> reward = new LinkedList<ChatAction>();
		reward.add(new EquipItemAction("money", 5));
		reward.add(new IncreaseXPAction(10));
		reward.add(new SetQuestAction(QUEST_SLOT, "taught"));
		reward.add(new ExamineChatAction("monogenes.png", "Monogenes", "North part of Semos city."));

		npc.add(
			ConversationStates.INFORMATION_5,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.IDLE,
			"Follow the path through this village to the east, and you can't miss Semos. If you go and speak to Monogenes, the old man in this picture, he will give you a map. Here's 5 money to get you started. Bye bye!",
			new MultipleActions(reward));

	   	// incase player didn't finish learning everything when he came after killing the rat, he must have another chance. Here it is.
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new QuestInStateCondition(QUEST_SLOT, "killed"),
				ConversationStates.INFORMATION_1,
		        "You ran off pretty fast after coming to tell me you killed that rat! I was about to give you some more hints and tips. Do you want to hear them?",
				null);
		
		// Player has returned to say hi again.
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new QuestInStateCondition(QUEST_SLOT, "taught"),
				ConversationStates.INFORMATION_6,
		        "Hello again. Have you come to learn more from me?",
				null);

		npc.add(
			ConversationStates.INFORMATION_6,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.INFORMATION_7,
			"Perhaps you have found Semos dungeons by now. The corridors are pretty narrow down there, so there's a trick to moving quickly and accurately, if you'd like to hear it. #Yes?",
			null);

		npc.add(
			ConversationStates.INFORMATION_7,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.INFORMATION_8,
			"Simple, really; just double-click the place you want to move to. There's a lot more information than I can relate just off the top of my head... do you want to know where to read more?",
			null);

		final String epilog = "You can find many frequently asked questions are answered at #http://arianne.sourceforge.net/wiki/index.php?title=StendhalFAQ \nYou can find out about experience points and levelling up at #http://arianne.sourceforge.net/wiki/index.php?title=LevelTables \nYou can read about some of the currently most powerful and successful warriors at #http://stendhal.game-host.org\n ";
		
			//This is used if the player returns, asks for #help and then say #yes
			npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.YES_MESSAGES, new QuestCompletedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING, 
			epilog + "You know, you remind me of my younger self...",
			null);

		final List<ChatAction> reward2 = new LinkedList<ChatAction>();
		reward2.add(new EquipItemAction("studded shield"));
		reward2.add(new IncreaseXPAction(20));
		reward2.add(new SetQuestAction(QUEST_SLOT, "done"));

		npc.add(ConversationStates.INFORMATION_8,
				ConversationPhrases.YES_MESSAGES, new QuestNotCompletedCondition(QUEST_SLOT),
				ConversationStates.IDLE, 
				epilog + "Well, good luck in the dungeons! This shield should help you. Here's hoping you find fame and glory, and keep watch for monsters!",
				new MultipleActions(reward2));

		npc.add(new ConversationStates[] { ConversationStates.ATTENDING,
					ConversationStates.INFORMATION_1,
					ConversationStates.INFORMATION_2,
					ConversationStates.INFORMATION_3,
					ConversationStates.INFORMATION_4,
				   	ConversationStates.INFORMATION_5,
				   	ConversationStates.INFORMATION_6,
				   	ConversationStates.INFORMATION_7,
				   	ConversationStates.INFORMATION_8},
				ConversationPhrases.NO_MESSAGES, new NotCondition(new QuestInStateCondition(QUEST_SLOT, "start")), ConversationStates.IDLE,
				"Oh well, I'm sure someone else will stop by for a chat soon. Bye...",
				null);

		npc.setPlayerChatTimeout(TIME_OUT); 
	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		prepareHayunn();
	}

	@Override
	public String getName() {
		return "MeetHayunn";
	}
}
