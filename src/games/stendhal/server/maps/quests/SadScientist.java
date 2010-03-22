package games.stendhal.server.maps.quests;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestToTimeStampAction;
import games.stendhal.server.entity.npc.action.StartRecordingKillsAction;
import games.stendhal.server.entity.npc.action.StateTimeRemainingAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.KilledCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStateStartsWithCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;


/**
 * QUEST: The Sad Scientist.
 * 
 * PARTICIPANTS:
 * <ul>
 * <li>Vasi Elos, a scientist in Kalavan</li>
 * <li>Mayor Sakhs, the mayor of semos</li>
 * </ul>
 * 
 * STEPS:
 * <ul>
 * 		<li>Talk to Vasi Elos, a lonely scientist.</li>
 * 		<li>Give him all stuff he needs for a present for his honey.</li>
 * 		<li>Talk to semos mayor.</li>
 * 		<li>Bring Elos mayor's letter.</li>
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
	
	private static final String LETTER_DESCRIPTION = "You see a letter for Vasi Elos.";
	private static final String QUEST_SLOT = "sad_scientist";
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
	}

	private void prepareScientist() {
		final SpeakerNPC scientistNpc = npcs.get("Vasi Elos");
		final SpeakerNPC mayorNpc = npcs.get("Mayor Sakhs");
		startOfQuest(scientistNpc);
		playerReturnsAfterStartWithItems(scientistNpc);
		playerReturnsAfterStartWithoutItems(scientistNpc);
		playerReturnsAfterGivingTooEarly(scientistNpc);
		playerReturnsAfterGivingWhenFinished(scientistNpc);
		playerReturnsWithoutLetter(scientistNpc);
		playerVisitsMayorSakhs(mayorNpc);
		playerReturnsWithLetter(scientistNpc);
		playerReturnsWithoutKillingTheImperialScientistOrWithoutGoblet(scientistNpc);
		playerReturnsAfterKillingTheImperialScientist(scientistNpc);
		playerReturnsToFetchReward(scientistNpc);
		playerReturnsAfterCompletingQuest(scientistNpc);
	}

	private void playerReturnsToFetchReward(SpeakerNPC npc) {
		// time has passed
		final ChatCondition condition = new AndCondition(
						new QuestStateStartsWithCondition(QUEST_SLOT,"decorating"),
						new TimePassedCondition(QUEST_SLOT, 5, 1)
					);
		final ChatAction action = new MultipleActions(
											new SetQuestAction(QUEST_SLOT,"done"),
											new IncreaseKarmaAction(20),
											new IncreaseXPAction(10000),
											// here, true = bind them to player
											new EquipItemAction("black legs", 1, true)
										);
		npc.add(ConversationStates.IDLE, 
				ConversationPhrases.GREETING_MESSAGES, 
				condition, 
				ConversationStates.IDLE, 
				"Here are the black legs. Now I beg you to wear them. The symbol of my pain is done. Fare thee well.",
				action);
		// time has not yet passed
		final ChatCondition notCondition = new AndCondition(
				new QuestStateStartsWithCondition(QUEST_SLOT,"decorating"),
				new NotCondition( new TimePassedCondition(QUEST_SLOT, 5, 1))
			);
		ChatAction reply = new StateTimeRemainingAction(QUEST_SLOT, "I did not finish decorating the legs. " +
				"Please check back in", 5, 1);
		npc.add(ConversationStates.IDLE, 
				ConversationPhrases.GREETING_MESSAGES, 
				notCondition, 
				ConversationStates.IDLE, 
				null, 
				reply);
	}
	
	private void playerReturnsAfterKillingTheImperialScientist(
			SpeakerNPC npc) {
		final ChatCondition condition = new AndCondition(
				new QuestStateStartsWithCondition(QUEST_SLOT, "kill_scientist"),
				new KilledCondition("Sergej Elos"),
				new PlayerHasItemWithHimCondition("goblet")
			);
		ChatAction action = new MultipleActions(
										new SetQuestAction(QUEST_SLOT, "decorating;"),
										new SetQuestToTimeStampAction(QUEST_SLOT, 1),
										new DropItemAction("goblet",1)
										);
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				condition, ConversationStates.ATTENDING, 
				"Ha, ha, ha! I will cover those jewelled legs with this blood and they will transform " +
				"into a #symbol of pain.", 
				null);
		npc.add(ConversationStates.ATTENDING, "symbol",
				condition, ConversationStates.IDLE, 
				"I am going to create a pair of black legs. Come back in 5 minutes.", 
				action);
	}


	private void playerReturnsWithoutKillingTheImperialScientistOrWithoutGoblet(
			SpeakerNPC npc) {
		final ChatCondition condition = new AndCondition(
				new QuestStateStartsWithCondition(QUEST_SLOT, "kill_scientist"),
				new NotCondition( 
						new AndCondition( 
										new KilledCondition("Sergej Elos"),
										new PlayerHasItemWithHimCondition("goblet")))
			);
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				condition, ConversationStates.IDLE, 
				"I am only in pain. Kill my brother and bring me his blood. It's all I want now.", 
				null);
	}
	
	private void playerReturnsWithLetter(final SpeakerNPC npc) {
		final ChatCondition condition = new AndCondition(
				new QuestStateStartsWithCondition(QUEST_SLOT, "find_vera"),
				new PlayerHasItemWithHimCondition("note")
			);
		final ChatAction action = new MultipleActions(
					new SetQuestAction(QUEST_SLOT, "kill_scientist"),
					new StartRecordingKillsAction("Sergej Elos"),
					new DropItemAction("note")
				);
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				condition,
				ConversationStates.INFORMATION_2,
				"Hello! Do you have anything for me?",
				null);
		npc.add(ConversationStates.INFORMATION_2, Arrays.asList("letter", "yes", "note"),
				condition,
				ConversationStates.ATTENDING,
				"Oh no! I feel the pain. I do not need to create those beautiful jewelled legs now. " +
				"I want to transform them. I want to make them a symbol of pain. You! Go kill my brother, " +
				"the Imperial Scientist Sergej Elos. Give me his blood.",
				action);
		npc.add(ConversationStates.ATTENDING, ConversationPhrases.GOODBYE_MESSAGES,
				new QuestInStateCondition(QUEST_SLOT, "kill_scientist"),
				ConversationStates.INFORMATION_2,
				"Do it!",
				null);
	}
	
	private void playerReturnsWithoutLetter(final SpeakerNPC npc) {
		final ChatCondition condition = new AndCondition(
				new QuestStateStartsWithCondition(QUEST_SLOT, "find_vera"),
				new NotCondition(new PlayerHasItemWithHimCondition("note"))
			);
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				condition,
				ConversationStates.IDLE,
				"Please ask Mayor Sakhs about my wife Vera.",
				null);
	}

	private void playerVisitsMayorSakhs(final SpeakerNPC npc) {
		final ChatAction action = new ChatAction() {
			public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
				final Item item = SingletonRepository.getEntityManager().getItem("note");
				item.setInfoString(player.getName());
				item.setDescription(LETTER_DESCRIPTION);
				item.setBoundTo(player.getName());
				player.equipOrPutOnGround(item);
			}
		};
		npc.add(ConversationStates.ATTENDING, "Vera",
				new QuestStateStartsWithCondition(QUEST_SLOT, "find_vera"),
				ConversationStates.ATTENDING, 
				"What? How do you know her? Well it is a sad story." +
				" She was picking arandula for Ilisa (they were friends)" +
				" and she saw the catacombs entrance. 3 months later a" +
				" young hero saw her, and she was a vampirette. What a" +
				" sad story. I kept this for her husband. A letter. " +
				"I think he is in Kalavan." ,
				action);
	}

	private void playerReturnsAfterGivingWhenFinished(final SpeakerNPC npc) {
		final ChatCondition condition = new AndCondition(
				new QuestStateStartsWithCondition(QUEST_SLOT, "making;"),
				new TimePassedCondition(QUEST_SLOT, REQUIRED_MINUTES, 1)
			);
		final ChatAction action = new SetQuestAction(QUEST_SLOT,"find_vera");
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				condition,
				ConversationStates.INFORMATION_1, 
				"I finished the legs. But I cannot trust you. Before I give the" +
				" jewelled legs to you, I need a message from my darling. Ask Mayor" +
				" Sakhs for Vera. Can you do that for me?",
				null);
		npc.add(ConversationStates.INFORMATION_1, ConversationPhrases.YES_MESSAGES,
				condition,
				ConversationStates.IDLE, 
				"Oh, thank you. I am waiting.",
				action);
		npc.add(ConversationStates.INFORMATION_1, ConversationPhrases.NO_MESSAGES,
				condition,
				ConversationStates.IDLE, 
				"Pah! Bye!",
				null);
	}

	private void playerReturnsAfterGivingTooEarly(final SpeakerNPC npc) {
		final ChatCondition condition = new AndCondition(
				new QuestStateStartsWithCondition(QUEST_SLOT, "making;"),
				new NotCondition(new TimePassedCondition(QUEST_SLOT, REQUIRED_MINUTES, 1))
			);
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				condition,
				ConversationStates.IDLE, 
				null,
				new StateTimeRemainingAction(QUEST_SLOT, "Do you think I can work that fast? Go away. " +
						"Come back in", REQUIRED_MINUTES, 1));
	}

	private void playerReturnsAfterStartWithoutItems(final SpeakerNPC npc) {
		final ChatCondition condition = new AndCondition(
													new QuestInStateCondition(QUEST_SLOT, "start"),
													new NotCondition(new AndCondition(
															new PlayerHasItemWithHimCondition("emerald"), 
															new PlayerHasItemWithHimCondition("obsidian"),
															new PlayerHasItemWithHimCondition("sapphire"),
															new PlayerHasItemWithHimCondition("carbuncle",2),
															new PlayerHasItemWithHimCondition("gold bar",20),
															new PlayerHasItemWithHimCondition("mithril bar"),
															new PlayerHasItemWithHimCondition("shadow legs")
														)
													)
												);
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				condition,
				ConversationStates.IDLE, 
				"Hello. Please return when you have everything I need for the jewelled legs. I need an emerald, " +
				"an obsidian, a sapphire, 2 carbuncles, 20 gold bars, one mithril bar, and I need a pair of shadow " +
				"legs as the base to add the gems to.",
				null);
	}

	private void playerReturnsAfterStartWithItems(final SpeakerNPC npc) {
		//player returns after start
		final AndCondition condition = new AndCondition(
									new QuestInStateCondition(QUEST_SLOT, "start"),
									new PlayerHasItemWithHimCondition("emerald"), 
									new PlayerHasItemWithHimCondition("obsidian"),
									new PlayerHasItemWithHimCondition("sapphire"),
									new PlayerHasItemWithHimCondition("carbuncle",2),
									new PlayerHasItemWithHimCondition("gold bar",20),
									new PlayerHasItemWithHimCondition("mithril bar"),
									new PlayerHasItemWithHimCondition("shadow legs")
									);
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				condition,
				ConversationStates.ATTENDING, 
				"Hello. Did you bring what I need?",
				null);
		final ChatAction action = new MultipleActions(
									new SetQuestAction(QUEST_SLOT,"making;"),
									new SetQuestToTimeStampAction(QUEST_SLOT, 1),
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
				"Wonderful! I will start my work. I can do this in very little time with the help of technology! " +
				"Please come back in 20 minutes.",
				action);
		npc.add(ConversationStates.ATTENDING, ConversationPhrases.NO_MESSAGES,
				condition,
				ConversationStates.IDLE, 
				"What a wasteful child.",
				null);
	}

	private void startOfQuest(final SpeakerNPC npc) {
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES, 
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"Go away!",null);

		//offer the quest
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, 
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.QUEST_OFFERED,
				"So...looks like you want to help me?",null);
		//accept the quest
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.QUEST_STARTED,
				"My wife is living in Semos City. She loves gems. Can you bring me some #gems that I need to make a " +
				"pair of precious #legs?" ,
				null);
		// #gems
		npc.add(ConversationStates.QUEST_STARTED,
				Arrays.asList("gem","gems"),
				null,
				ConversationStates.QUEST_STARTED,
				"I need an emerald, an obsidian, a sapphire, 2 carbuncles, 20 gold bars, one mithril bar, and I need " +
				"a pair of shadow legs as the base to add the gems to. Can you do that for my wife? " ,
				null);
		// #legs
		npc.add(ConversationStates.QUEST_STARTED,
				Arrays.asList("leg","legs"),
				null,
				ConversationStates.QUEST_STARTED,
				"Jewelled legs. I need an emerald, an obsidian, a sapphire, 2 carbuncles, 20 gold bars, " +
				"one mithril bar, and I need a pair of shadow legs as the base to add the gems to. Can you " +
				"do that for my wife? Can you bring what I need? " ,
				null);
		//yes, no after start of quest
		npc.add(ConversationStates.QUEST_STARTED,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.IDLE,
				"I am waiting, Semos man." ,
				new SetQuestAction(QUEST_SLOT, "start"));
		
		npc.add(ConversationStates.QUEST_STARTED,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.QUEST_STARTED,
				"Go away before I kill you!" ,
				null);
		//reject the quest
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"If you change your mind please ask me again..." ,
				null);
	}
	
	private void playerReturnsAfterCompletingQuest(final SpeakerNPC npc) {
		// after finishing the quest, just tell them to go away, and mean it.
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES, 
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.IDLE,
				"Go away!",null);
	}
	
}
