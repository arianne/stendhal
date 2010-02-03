package games.stendhal.server.maps.quests;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.StateTimeRemainingAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStateStartsWithCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;

import java.util.Arrays;
/**
 * QUEST: The Sad Scientist.
 * 
 * PARTICIPANTS:
 * <ul>
 * <li>Boris Karlova, a scientist in Kalavan</li>
 * <li>Mayor Sakhs, the mayor of semos</li>
 * </ul>
 * 
 * STEPS:
 * <ul>
 * 		<li>Talk to Boris Karlova, a lonely scientist.</li>
 * 		<li>Give him all stuff he needs for a present for his honey.</li>
 * 		<li>Talk to semos mayor.</li>
 * 		<li>Bring Karlova mayor's letter.</li>
 * 		<li>Kill the Imperial Scientist.</li>
 *		<li>Give him the flask with his brother's blood.</li> 
 * </ul>
 * 
 * REWARD:
 * <ul>
 * 		<li>a pair of black legs</li>
 * 		<li>20 Karma</li>
 * 		<li>10000 XP</li>
 * </ul>
 * 
 * REPETITIONS:
 * <ul>
 * 		<li>None</li>
 * </ul>
 */
public class SadScientist extends AbstractQuest {
	
	public static final String QUEST_SLOT = "sad_scientist";
	private static final int REQUIRED_MINUTES = 20;


	@Override
	public String getName() {
		return "TheSadScientist";
	}

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	/* (non-Javadoc)
	 * @see games.stendhal.server.maps.quests.AbstractQuest#addToWorld()
	 */
	@Override
	public void addToWorld() {
		super.addToWorld();
		prepareQuestSteps();
	}

	private void prepareQuestSteps() {
		prepareScientist();
		prepareMayor();
	}

	private void prepareScientist() {
		SpeakerNPC scientistNpc = npcs.get("Boris Karlova");
		startOfQuest(scientistNpc);
		playerReturnsAfterStartWithItems(scientistNpc);
		playerReturnsAfterStartWithoutItems(scientistNpc);
		playerReturnsAfterGivingTooEarly(scientistNpc);
		playerReturnsAfterGivingWhenFinished(scientistNpc);
	}

	private void playerReturnsAfterGivingWhenFinished(SpeakerNPC npc) {
		// TODO Auto-generated method stub
		
	}

	private void playerReturnsAfterGivingTooEarly(SpeakerNPC npc) {
		ChatCondition condition = new AndCondition(
				new QuestStateStartsWithCondition(QUEST_SLOT, "making;"),
				new NotCondition(new TimePassedCondition(QUEST_SLOT, REQUIRED_MINUTES, 1)),
				new QuestNotActiveCondition("mithril_cloak")
			);
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				condition,
				ConversationStates.IDLE, 
				"Hello. Please return when you have everything what I need for the jewelled legs.",
				new StateTimeRemainingAction(QUEST_SLOT, "Do you think I can work that fast? Go away. Come back in", REQUIRED_MINUTES, 1));
	}

	private void playerReturnsAfterStartWithoutItems(SpeakerNPC npc) {
		ChatCondition condition = new AndCondition(
										new QuestInStateCondition(QUEST_SLOT, "start"),
										new NotCondition( new PlayerHasItemWithHimCondition("emerald")), 
										new NotCondition( new PlayerHasItemWithHimCondition("obsidian")),
										new NotCondition( new PlayerHasItemWithHimCondition("sapphire")),
										new NotCondition( new PlayerHasItemWithHimCondition("carbuncle",2)),
										new NotCondition( new PlayerHasItemWithHimCondition("gold bar",20)),
										new NotCondition( new PlayerHasItemWithHimCondition("mithril bar")),
										new NotCondition( new PlayerHasItemWithHimCondition("shadow legs")),
										new QuestNotActiveCondition("mithril_cloak")
									);
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				condition,
				ConversationStates.IDLE, 
				"Hello. Please return when you have everything what I need for the jewelled legs.",
				null);
	}

	private void playerReturnsAfterStartWithItems(SpeakerNPC npc) {
		//player returns after start
		AndCondition condition = new AndCondition(
									new QuestInStateCondition(QUEST_SLOT, "start"),
									new PlayerHasItemWithHimCondition("emerald"), 
									new PlayerHasItemWithHimCondition("obsidian"),
									new PlayerHasItemWithHimCondition("sapphire"),
									new PlayerHasItemWithHimCondition("carbuncle",2),
									new PlayerHasItemWithHimCondition("gold bar",20),
									new PlayerHasItemWithHimCondition("mithril bar"),
									new PlayerHasItemWithHimCondition("shadow legs"),
									new QuestNotActiveCondition("mithril_cloak")
									);
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				condition,
				ConversationStates.ATTENDING, 
				"Hello. Did you bring what I need?",
				null);
		ChatAction action = new MultipleActions(
									new SetQuestAction(QUEST_SLOT,"making;"+System.currentTimeMillis()),
									new DropItemAction("emerald"),
									new DropItemAction("obsidian"),
									new DropItemAction("sapphire"),
									new DropItemAction("carbuncle",2),
									new DropItemAction("gold bar",20),
									new DropItemAction("mithril bar"),
									new DropItemAction("shadow legs"));
		npc.add(ConversationStates.ATTENDING, ConversationPhrases.YES_MESSAGES,
				condition,
				ConversationStates.IDLE, 
				"Wonderful! I will start my work. I can do this at a very little time with the help of technology! Please come back in 20 minutes.",
				action);
		npc.add(ConversationStates.ATTENDING, ConversationPhrases.NO_MESSAGES,
				condition,
				ConversationStates.IDLE, 
				"What a wasteful child.",
				null);
	}

	private void startOfQuest(SpeakerNPC npc) {
		//offer the quest
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, 
				new AndCondition(new QuestNotStartedCondition(QUEST_SLOT),new QuestNotActiveCondition("mithril_cloak")),
				ConversationStates.QUEST_OFFERED,
				"So...looks like you want to help me?",null);
		//accept the quest
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				new QuestNotActiveCondition("mithril_cloak"),
				ConversationStates.QUEST_STARTED,
				"My wife is living in Semos City. She loves gems. Can you bring me some #gems that I need to make a pair of precious #legs?" ,
				null);
		// #gems
		npc.add(ConversationStates.QUEST_STARTED,
				Arrays.asList("gem","gems"),
				new QuestNotActiveCondition("mithril_cloak"),
				ConversationStates.QUEST_STARTED,
				"I need an emerald, an obsidian, a sapphire, 2 carbuncles, 20 gold bars, one mithril bar, and I need a pair of shadow legs as the base to add the gems to. Can you do that for my wife? " ,
				null);
		// #legs
		npc.add(ConversationStates.QUEST_STARTED,
				Arrays.asList("leg","legs"),
				new QuestNotActiveCondition("mithril_cloak"),
				ConversationStates.QUEST_STARTED,
				"Jewelled legs. I need an emerald, an obsidian, a sapphire, 2 carbuncles, 20 gold bars, one mithril bar, and I need a pair of shadow legs as the base to add the gems to. Can you do that for my wife? Can you bring what I need? " ,
				null);
		//yes, no after start of quest
		npc.add(ConversationStates.QUEST_STARTED,
				ConversationPhrases.YES_MESSAGES,
				new QuestNotActiveCondition("mithril_cloak"),
				ConversationStates.ATTENDING,
				"I am waiting, semos man." ,
				new SetQuestAction(QUEST_SLOT, "start"));
		npc.add(ConversationStates.QUEST_STARTED,
				ConversationPhrases.NO_MESSAGES,
				new QuestNotActiveCondition("mithril_cloak"),
				ConversationStates.QUEST_STARTED,
				"Go away before I kill you!" ,
				null);
		//reject the quest
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES,
				new QuestNotActiveCondition("mithril_cloak"),
				ConversationStates.ATTENDING,
				"If you change your mind please ask me again..." ,
				null);
	}

	private void prepareMayor() {
		// TODO Auto-generated method stub
		
	}
	
}
