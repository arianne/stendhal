/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.quests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.CollectRequestedItemsAction;
import games.stendhal.server.entity.npc.action.DropInfostringItemAction;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SayRequiredItemsFromCollectionAction;
import games.stendhal.server.entity.npc.action.SayTextAction;
import games.stendhal.server.entity.npc.action.SayTimeRemainingAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.action.SetQuestToTimeStampAction;
import games.stendhal.server.entity.npc.action.StartRecordingKillsAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.KilledForQuestCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasInfostringItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStateStartsWithCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;
import games.stendhal.server.util.ItemCollection;


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

	private static Logger logger = Logger.getLogger(SadScientist.class);

	private static final String LETTER_DESCRIPTION = "You see a letter for Vasi Elos.";
	private static final String QUEST_SLOT = "sad_scientist";
	private static final int REQUIRED_MINUTES = 20;
	private static final String NEEDED_ITEMS = "emerald=1;obsidian=1;sapphire=1;carbuncle=2;gold bar=20;mithril bar=1";

	@Override
	public String getName() {
		return "TheSadScientist";
	}

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
		final String questState = player.getQuest(QUEST_SLOT);
		// it might have been rejected before Vasi even explained what he wanted.
		if ("rejected".equals(questState)) {
			res.add("Vasi Elos asked me for help, but I'm not really interested to help the scientist.");
			return res;
		}
		res.add("Vasi Elos asked me bring gems, gold and mithril to make a present of jewelled legs for his honey, Vera.");
		if (getConditionForBeingInCollectionPhase().fire(player,null,null)) {
			final ItemCollection missingItems = new ItemCollection();
			missingItems.addFromQuestStateString(questState);
			res.add("The jewelled legs still need " + Grammar.enumerateCollection(missingItems.toStringList()) + ".");
			return res;
		}
		res.add("Vasi Elos needs a base to add those gems I brought, to. He asked for shadow legs.");
		if ("legs".equals(questState)) {
			return res;
		}
		res.add("Vasi Elos is making jewelled legs with the gems I brought.");
		if (questState.startsWith("making")) {
			return res;
		}
		res.add("Vasi Elos asked me to speak to Mayor Sakhs to find out where Vera is.");
		if ("find_vera".equals(questState) && !player.isEquippedWithInfostring("note", QUEST_SLOT)) {
			return res;
		}
		res.add("I have a note with some terrible news on it to give Vasi.");
		if ("find_vera".equals(questState) && player.isEquippedWithInfostring("note", QUEST_SLOT)) {
			return res;
		}
		res.add("Vasi Elos is so sad and angry that Vera is gone. I must kill his own brother and give him a goblet of blood.");
		if (questState.startsWith("kill_scientist") && !new KilledForQuestCondition(QUEST_SLOT, 1).fire(player, null, null)) {
			return res;
		}
		res.add("I killed the Imperial Scientist Sergej Elos and must take the goblet of his blood as proof.");
		if (questState.startsWith("kill_scientist") && new KilledForQuestCondition(QUEST_SLOT, 1).fire(player, null, null)) {
			return res;
		}
		res.add("Vasi Elos is really sad. He's decorating the legs he made...with blood.");
		if (questState.startsWith("decorating")) {
			return res;
		}
		res.add("The new legs, now black legs, darkened with blood and evil, are mine. " +
				"But at what cost?");
        if ("done".equals(questState)){
        	return res;
		}
        // if things have gone wrong and the quest state didn't match any of the above, debug a bit:
		final List<String> debug = new ArrayList<String>();
		debug.add("Quest state is: " + questState);
		logger.error("History doesn't have a matching quest state for " + questState);
		return debug;
	}

	/* (non-Javadoc)
	 * @see games.stendhal.server.maps.quests.AbstractQuest#addToWorld()
	 */
	@Override
	public void addToWorld() {
		fillQuestInfo(
				"A Sad Scientist",
				"Vasi Elos, a lonely scientist, wants to deliver a present to his honey.",
				false);
		prepareQuestSteps();
	}

	private void prepareQuestSteps() {
		prepareScientist();
	}

	private void prepareScientist() {
		final SpeakerNPC scientistNpc = npcs.get("Vasi Elos");
		final SpeakerNPC mayorNpc = npcs.get("Mayor Sakhs");
		startOfQuest(scientistNpc);
		bringItemsPhase(scientistNpc);
		playerReturnsAfterRequestForLegs(scientistNpc);
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
		final ChatCondition condition = new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestStateStartsWithCondition(QUEST_SLOT,"decorating"),
						new TimePassedCondition(QUEST_SLOT, 1, 5)
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
		final ChatCondition notCondition = new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
				new QuestStateStartsWithCondition(QUEST_SLOT,"decorating"),
				new NotCondition( new TimePassedCondition(QUEST_SLOT, 1, 5))
			);
		ChatAction reply = new SayTimeRemainingAction(QUEST_SLOT, 1, 5, "I did not finish decorating the legs. " +
						"Please check back in");
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				notCondition,
				ConversationStates.IDLE,
				null,
				reply);
	}

	private void playerReturnsAfterKillingTheImperialScientist(SpeakerNPC npc) {
		final ChatCondition condition = new AndCondition(
				new QuestStateStartsWithCondition(QUEST_SLOT, "kill_scientist"),
				new KilledForQuestCondition(QUEST_SLOT, 1),
				new PlayerHasInfostringItemWithHimCondition("goblet", QUEST_SLOT)
			);
		ChatAction action = new MultipleActions(
										new SetQuestAction(QUEST_SLOT, "decorating;"),
										new SetQuestToTimeStampAction(QUEST_SLOT, 1),
										new DropInfostringItemAction("goblet", 1, QUEST_SLOT)
										);
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()), condition),
				ConversationStates.ATTENDING,
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
				new GreetingMatchesNameCondition(npc.getName()),
				new QuestStateStartsWithCondition(QUEST_SLOT, "kill_scientist"),
				new NotCondition(
						new AndCondition(
										new KilledForQuestCondition(QUEST_SLOT, 1),
										new PlayerHasInfostringItemWithHimCondition("goblet", QUEST_SLOT)))
			);
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				condition, ConversationStates.IDLE,
				"I am only in pain. Kill my brother and bring me his blood. It's all I want now.",
				null);
	}

	private void playerReturnsWithLetter(final SpeakerNPC npc) {
		final ChatCondition condition = new AndCondition(
				new QuestStateStartsWithCondition(QUEST_SLOT, "find_vera"),
				new PlayerHasInfostringItemWithHimCondition("note", QUEST_SLOT)
			);

		final ChatAction action = new MultipleActions(
					new SetQuestAction(QUEST_SLOT, 0, "kill_scientist"),
					new StartRecordingKillsAction(QUEST_SLOT, 1, "Sergej Elos", 0, 1),
					new DropInfostringItemAction("note", QUEST_SLOT)
				);
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()), condition),
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
				new QuestInStateCondition(QUEST_SLOT, 0, "kill_scientist"),
				ConversationStates.INFORMATION_2,
				"Do it!",
				null);
	}

	private void playerReturnsWithoutLetter(final SpeakerNPC npc) {
		final ChatCondition condition = new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
				new QuestStateStartsWithCondition(QUEST_SLOT, "find_vera"),
				new NotCondition(new PlayerHasInfostringItemWithHimCondition("note", QUEST_SLOT))
			);
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				condition,
				ConversationStates.IDLE,
				"Please ask Mayor Sakhs about my wife Vera.",
				null);
	}

	private void playerVisitsMayorSakhs(final SpeakerNPC npc) {
		final ChatAction action = new ChatAction() {
			@Override
			public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
				final Item item = SingletonRepository.getEntityManager().getItem("note");
				item.setInfoString(QUEST_SLOT);
				item.setDescription(LETTER_DESCRIPTION);
				item.setBoundTo(player.getName());
				player.equipOrPutOnGround(item);
			}
		};

		final String mayor_response = "She was picking arandula for Ilisa (they were friends)" +
				" and she saw the catacombs entrance. 3 months later a" +
				" young hero saw her, and she was a vampirette.";

		// Player has not received note
		npc.add(ConversationStates.ATTENDING, "Vera",
				new AndCondition(
						new QuestStateStartsWithCondition(QUEST_SLOT, "find_vera"),
						new NotCondition(new PlayerHasInfostringItemWithHimCondition("note", QUEST_SLOT))),
				ConversationStates.ATTENDING,
				"What? How do you know her? Well it is a sad story. " +
				mayor_response + " I kept this for her husband. A letter. " +
				"I think he is in Kalavan." ,
				action);

		// Player is already carrying note
		npc.add(ConversationStates.ATTENDING, "Vera",
				new AndCondition(
						new QuestStateStartsWithCondition(QUEST_SLOT, "find_vera"),
						new PlayerHasInfostringItemWithHimCondition("note", QUEST_SLOT)),
				ConversationStates.ATTENDING,
				mayor_response + " Please deliver that letter to her husband. " +
				"I think he is in Kalavan.",
				null);
	}

	private void playerReturnsAfterGivingWhenFinished(final SpeakerNPC npc) {
		final ChatCondition condition = new AndCondition(
				new QuestStateStartsWithCondition(QUEST_SLOT, "making;"),
				new TimePassedCondition(QUEST_SLOT, 1, REQUIRED_MINUTES)
			);
		final ChatAction action = new SetQuestAction(QUEST_SLOT,"find_vera");
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()), condition),
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
		final ChatCondition condition = new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
				new QuestStateStartsWithCondition(QUEST_SLOT, "making;"),
				new NotCondition(new TimePassedCondition(QUEST_SLOT, 1, REQUIRED_MINUTES))
			);
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				condition,
				ConversationStates.IDLE,
				null,
				new SayTimeRemainingAction(QUEST_SLOT, 1, REQUIRED_MINUTES, "Do you think I can work that fast? Go away. " +
								"Come back in"));
	}

	private void bringItemsPhase(final SpeakerNPC npc) {
		//condition for quest being active and in item collection phase
		ChatCondition itemPhaseCondition = getConditionForBeingInCollectionPhase();

		//player returns during item collection phase
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						itemPhaseCondition),
				ConversationStates.QUESTION_1,
				"Hello. Do you have any #items I need for the jewelled legs?",
				null);

		//player asks for items
		npc.add(ConversationStates.QUESTION_1, Arrays.asList("items","item"),
				itemPhaseCondition,
				ConversationStates.QUESTION_1,
				null,
				new SayRequiredItemsFromCollectionAction(QUEST_SLOT, "Please return when you have anything I need for the jewelled legs. I need [items]."));

		//player says no
		npc.add(ConversationStates.QUESTION_1, ConversationPhrases.NO_MESSAGES,
				itemPhaseCondition,
				ConversationStates.IDLE,
				"What a wasteful child.",
				null);

		//player says yes
		npc.add(ConversationStates.QUESTION_1, ConversationPhrases.YES_MESSAGES,
				itemPhaseCondition,
				ConversationStates.QUESTION_1,
				"Fine! So what did you bring with you?",
				null);

		//add transition for each item
		final ItemCollection items = new ItemCollection();
		items.addFromQuestStateString(NEEDED_ITEMS);
		for (final Map.Entry<String, Integer> item : items.entrySet()) {
			npc.add(ConversationStates.QUESTION_1, item.getKey(), null,
					ConversationStates.QUESTION_1, null,
					new CollectRequestedItemsAction(
							item.getKey(), QUEST_SLOT,
							"Good, do you have anything else?",
							"You have already brought that!",
							new MultipleActions(
									new SetQuestAction(QUEST_SLOT,"legs"),
									new SayTextAction("I am a stupid fool too much in love with my wife Vera to remember, of course these legs also need a base to add " +
											"the jewels to. Please return with a pair of shadow legs. Bye.")), ConversationStates.IDLE
							));
		}
	}


//																new SetQuestAction(QUEST_SLOT,"making;"), new SetQuestToTimeStampAction(QUEST_SLOT, 1),
	/**
	 * Creates a condition for quest being active and in item collection phase
	 * @return the condition
	 */
	private AndCondition getConditionForBeingInCollectionPhase() {
		return new AndCondition(
													new QuestActiveCondition(QUEST_SLOT),
													new NotCondition(
															new QuestStateStartsWithCondition(QUEST_SLOT,"making;")
																	),
													new NotCondition(
															new QuestStateStartsWithCondition(QUEST_SLOT,"decorating;")
																	),
													new NotCondition(
															new QuestStateStartsWithCondition(QUEST_SLOT,"find_vera")
																	),
													new NotCondition(
															new QuestStateStartsWithCondition(QUEST_SLOT,"kill_scientist")
																	),
													new NotCondition(
															new QuestStateStartsWithCondition(QUEST_SLOT,"legs")
																					)
															);
	}

	private void playerReturnsAfterRequestForLegs(final SpeakerNPC npc) {
	//player returns without legs
	final AndCondition nolegscondition = new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
									new QuestInStateCondition(QUEST_SLOT, "legs"),
									new NotCondition(new PlayerHasItemWithHimCondition("shadow legs"))
									);
	npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
			nolegscondition,
			ConversationStates.IDLE,
			"Hello again. Please return when you have the shadow legs, a base for me to add jewels to make jewelled legs for Vera.",
			null);

	//player returns with legs
	final AndCondition legscondition = new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
								new QuestInStateCondition(QUEST_SLOT, "legs"),
								new PlayerHasItemWithHimCondition("shadow legs")
								);
	final ChatAction action = new MultipleActions(
	new SetQuestAction(QUEST_SLOT,"making;"),
	new SetQuestToTimeStampAction(QUEST_SLOT, 1),
	new DropItemAction("shadow legs"));
	npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
			legscondition,
			ConversationStates.IDLE,
			"The shadow legs! Wonderful! I will start my work. I can do this in very little time with the help of technology! " +
			"Please come back in 20 minutes.",
			action);
	}

	private void startOfQuest(final SpeakerNPC npc) {
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestNotStartedCondition(QUEST_SLOT)),
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
				"I need an emerald, an obsidian, a sapphire, 2 carbuncles, 20 gold bars and one mithril bar." +
				" Can you do that for my wife?",
				null);

		// #legs
		npc.add(ConversationStates.QUEST_STARTED,
				Arrays.asList("leg","legs"),
				null,
				ConversationStates.QUEST_STARTED,
				"Jewelled legs. I need an emerald, an obsidian, a sapphire, 2 carbuncles, 20 gold bars and one mithril bar." +
				" Can you do that for my wife? Can you bring what I need?",
				null);

		//yes, no after start of quest
		npc.add(ConversationStates.QUEST_STARTED,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.IDLE,
				"I am waiting, Semos man." ,
				new SetQuestAction(QUEST_SLOT, NEEDED_ITEMS));

		npc.add(ConversationStates.QUEST_STARTED,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.QUEST_STARTED,
				"Go away before I kill you!" ,
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -10.0));

		//reject the quest
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"If you change your mind please ask me again..." ,
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -10.0));
	}

	private void playerReturnsAfterCompletingQuest(final SpeakerNPC npc) {
		// after finishing the quest, just tell them to go away, and mean it.
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestCompletedCondition(QUEST_SLOT)),
				ConversationStates.IDLE,
				"Go away!",null);
	}

	// The items and surviving in the basement mean we shouldn't direct them till level 100 or so
	@Override
	public int getMinLevel() {
		return 100;
	}

	@Override
	public String getRegion() {
		return Region.KALAVAN;
	}

	@Override
	public String getNPCName() {
		return "Vasi Elos";
	}
}
