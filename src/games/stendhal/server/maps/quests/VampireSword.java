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

import static games.stendhal.server.entity.npc.ConversationBuilder.actor;
import static games.stendhal.server.entity.npc.ConversationBuilder.conversation;
import static games.stendhal.server.entity.npc.action.DropItemAction.dropItem;
import static games.stendhal.server.entity.npc.action.EquipItemAction.equipBoundItem;
import static games.stendhal.server.entity.npc.action.EquipItemAction.equipItem;
import static games.stendhal.server.entity.npc.action.IncreaseKarmaAction.increaseKarma;
import static games.stendhal.server.entity.npc.action.IncreaseXPAction.increaseXP;
import static games.stendhal.server.entity.npc.action.SayTimeRemainingAction.sayTimeRemaining;
import static games.stendhal.server.entity.npc.action.SetQuestAction.setQuest;
import static games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction.setQuestAndModifyKarma;
import static games.stendhal.server.entity.npc.action.SetQuestToTimeStampAction.setQuestToTimestamp;
import static games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition.greetingMatchesName;
import static games.stendhal.server.entity.npc.condition.KilledCondition.playerHasKilled;
import static games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition.playerCarriesItem;
import static games.stendhal.server.entity.npc.condition.QuestActiveCondition.questActive;
import static games.stendhal.server.entity.npc.condition.QuestCompletedCondition.questCompleted;
import static games.stendhal.server.entity.npc.condition.QuestInStateCondition.questInState;
import static games.stendhal.server.entity.npc.condition.QuestNotStartedCondition.questNotStarted;
import static games.stendhal.server.entity.npc.condition.QuestStateStartsWithCondition.questStateStartsWith;
import static games.stendhal.server.entity.npc.condition.TimePassedCondition.timePassed;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.ProducerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.ProducerBehaviour;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;
/**
 * QUEST: The Vampire Sword
 * <p>
 * PARTICIPANTS:
 * <ul>
 * <li>Hogart, a retired master dwarf smith, forgotten below the dwarf mines in
 * Orril.</li>
 * <li>Markovich, a sick vampire who will fill the goblet.</li>
 * </ul>
 * <p>
 * STEPS:
 * <ul>
 * <li>Hogart tells you the story of the Vampire Lord.</li>
 * <li>He offers to forge a Vampire Sword for you if you bring him what it
 * needs.</li>
 * <li>Go to the catacombs, kill 7 vampirettes to get to the 3rd level, kill 7
 * killer bats and the vampire lord to get the required items to fill the
 * goblet.</li>
 * <li>Fill the goblet and come back.</li>
 * <li>You get some items from the Catacombs and kill the Vampire Lord.</li>
 * <li>You get the iron needed in the usual way by collecting iron ore and
 * casting in Semos.</li>
 * <li>Hogart forges the Vampire Sword for you.</li>
 * </ul>
 * <p>
 * REWARD:
 * <ul>
 * <li>Vampire Sword</li>
 * <li>5,000 XP</li>
 * <li>some karma</li>
 * </ul>
 * <p>
 * REPETITIONS:
 * <ul>
 * <li>None</li>
 * </ul>
 */
public class VampireSword extends AbstractQuest {

	private static final int REQUIRED_IRON = 10;
	private static final int REQUIRED_MINUTES = 10;
	private static final int SLOT_TIME_INDEX = 1;
	private static final String QUEST_SLOT = "vs_quest";

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	private void prepareQuestOfferingStep() {

		final SpeakerNPC npc = npcs.get("Hogart");

		// Player asks about quests, and had previously rejected or never asked: offer it
		conversation(actor(npc)
			.respondsTo(ConversationPhrases.QUEST_MESSAGES)
			.saying("I can forge a powerful life stealing sword for you. You will need to go to the Catacombs below Semos Graveyard and fight the Vampire Lord. Are you interested?")
			.inState(ConversationStates.ATTENDING)
			.when(questNotStarted(QUEST_SLOT))
			.changingStateTo(ConversationStates.QUEST_OFFERED));

		// Player asks about quests, but has finished this quest
		conversation(actor(npc)
			.respondsTo(ConversationPhrases.QUEST_MESSAGES)
			.saying("What are you bothering me for now? You've got your sword, go and use it!")
			.inState(ConversationStates.ATTENDING)
			.when(questCompleted(QUEST_SLOT)));

		// Player asks about quests, but has not finished this quest
		conversation(actor(npc)
			.respondsTo(ConversationPhrases.QUEST_MESSAGES)
			.saying("Why are you bothering me when you haven't completed your quest yet?")
			.inState(ConversationStates.ATTENDING)
			.when(questActive(QUEST_SLOT)));

		// Player wants to do the quest
		conversation(actor(npc)
			.respondsTo(ConversationPhrases.YES_MESSAGES)
			.inState(ConversationStates.QUEST_OFFERED)
			.saying("Then you need this #goblet. Take it to the Semos #Catacombs.")
			.doing(equipItem("empty goblet"),
					setQuest(QUEST_SLOT, "start"))
			.changingStateTo(ConversationStates.ATTENDING));

		// Player doesn't want to do the quest; remember this, but they can ask again to start it.
		conversation(actor(npc)
			.respondsTo(ConversationPhrases.NO_MESSAGES)
			.saying("Oh, well forget it then. You must have a better sword than I can forge, huh? Bye.")
			.inState(ConversationStates.QUEST_OFFERED)
			.changingStateTo(ConversationStates.IDLE)
			.doing(setQuestAndModifyKarma(QUEST_SLOT, "rejected", -5.0)));

		npc.addReply("catacombs", "The Catacombs of north Semos of the ancient #stories.");

		npc.addReply("goblet", "Go fill it with the blood of the enemies you meet in the #Catacombs.");
	}

	private void prepareGobletFillingStep() {

		final SpeakerNPC npc = npcs.get("Markovich");

		npc.addGoodbye("*cough* ... farewell ... *cough*");
		npc.addReply(
			Arrays.asList("blood", "vampirette entrails", "bat entrails"),
			"I need blood. I can take it from the entrails of the alive and undead. I will mix the bloods together for you and #fill your #goblet, if you let me drink some too. But I'm afraid of the powerful #lord.");

		npc.addReply(Arrays.asList("lord", "vampire", "skull ring"),
			"The Vampire Lord rules these Catacombs! And I'm afraid of him. I can only help you if you kill him and bring me his skull ring with the #goblet.");

		npc.addReply(
			Arrays.asList("empty goblet", "goblet"),
			"Only a powerful talisman like this cauldron or a special goblet should contain blood.");

		// The sick vampire is only a producer. He doesn't care if your quest slot is active, or anything.
		// So to ensure that the vampire lord must have been killed, we made the skull ring a required item
		// Which the vampire lord drops if the quest is active as in games.stendhal.server.maps.semos.catacombs.VampireLordCreature
		// But, it could have been done other ways using quests slot checks and killed conditions
		final Map<String, Integer> requiredResources = new TreeMap<String, Integer>();
		requiredResources.put("vampirette entrails", 7);
		requiredResources.put("bat entrails", 7);
		requiredResources.put("skull ring", 1);
		requiredResources.put("empty goblet", 1);
		final ProducerBehaviour behaviour = new ProducerBehaviour(
				"sicky_fill_goblet", "fill", "goblet", requiredResources,
				5 * 60, true);
		new ProducerAdder().addProducer(npc, behaviour,
			"Please don't try to kill me...I'm just a sick old #vampire. Do you have any #blood I could drink? If you have an #empty goblet I will #fill it with blood for you in my cauldron.");

	}

	private void prepareForgingStep() {

		final SpeakerNPC npc = npcs.get("Hogart");

		// Player returned with goblet and had killed the vampire lord, and has iron, so offer to forge the sword.
		conversation(actor(npc)
			.respondsTo(ConversationPhrases.GREETING_MESSAGES)
			.saying("You've brought everything I need to make the vampire sword. Come back in "
					+ REQUIRED_MINUTES
					+ " minutes and it will be ready")
			.inState(ConversationStates.IDLE)
			.when(greetingMatchesName(npc.getName())
					.and(questInState(QUEST_SLOT, "start"))
					.and(playerCarriesItem("goblet"))
					.and(playerHasKilled("vampire lord"))
					.and(playerCarriesItem("iron", REQUIRED_IRON)))
			.doing(dropItem("goblet"),
					dropItem("iron", REQUIRED_IRON),
					increaseKarma(5.0),
					setQuest(QUEST_SLOT, "forging;"),
					setQuestToTimestamp(QUEST_SLOT, SLOT_TIME_INDEX)));

		// Player returned with goblet and had killed the vampire lord, so offer to forge the sword if iron is brought
		conversation(actor(npc)
			.respondsTo(ConversationPhrases.GREETING_MESSAGES)
			.saying("You have battled hard to bring that goblet. I will use it to #forge the vampire sword")
			.inState(ConversationStates.IDLE)
			.when(greetingMatchesName(npc.getName())
					.and(questInState(QUEST_SLOT, "start"))
					.and(playerCarriesItem("goblet"))
					.and(playerHasKilled("vampire lord"))
					.unless(playerCarriesItem("iron", REQUIRED_IRON)))
			.changingStateTo(ConversationStates.QUEST_ITEM_BROUGHT));

		// Player has only an empty goblet currently, remind to go to Catacombs
		conversation(actor(npc)
				.respondsTo(ConversationPhrases.GREETING_MESSAGES)
				.saying("Did you lose your way? The Catacombs are in North Semos. "
						+ "Don't come back without a full goblet! Bye!")
				.inState(ConversationStates.IDLE)
				.when(greetingMatchesName(npc.getName())
						.and(questInState(QUEST_SLOT, "start"))
						.and(playerCarriesItem("empty goblet"))
						.unless(playerCarriesItem("goblet"))));

		// Player has a goblet (somehow) but did not kill a vampire lord
		conversation(actor(npc)
				.respondsTo(ConversationPhrases.GREETING_MESSAGES)
				.inState(ConversationStates.IDLE)
				.saying("Hm, that goblet is not filled with vampire blood; it can't be, you have not killed the vampire lord. You must slay him.")
				.when(greetingMatchesName(npc.getName())
						.and(questInState(QUEST_SLOT, "start"))
						.and(playerCarriesItem("goblet"))
						.unless(playerHasKilled("vampire lord"))));

		// Player lost the empty goblet?
		conversation(actor(npc)
				.respondsTo(ConversationPhrases.GREETING_MESSAGES)
				.inState(ConversationStates.IDLE)
				.saying("I hope you didn't lose your goblet! Do you need another?")
				.when(greetingMatchesName(npc.getName())
						.and(questInState(QUEST_SLOT, "start"))
						.unless(playerCarriesItem("empty goblet")
								.or(playerCarriesItem("goblet"))))
				.changingStateTo(ConversationStates.QUESTION_1));

		// Player lost the empty goblet, wants another
		conversation(actor(npc)
				.respondsTo(ConversationPhrases.YES_MESSAGES)
				.inState(ConversationStates.QUESTION_1)
				.saying("You stupid ..... Be more careful next time. Bye!")
				.changingStateTo(ConversationStates.IDLE)
				.doing(equipItem("empty goblet")));

		// Player doesn't have the empty goblet but claims they don't need another.
		conversation(actor(npc)
				.respondsTo(ConversationPhrases.NO_MESSAGES)
				.inState(ConversationStates.QUESTION_1)
				.saying("Then why are you back here? Go slay some vampires! Bye!")
				.changingStateTo(ConversationStates.IDLE));

		// Returned too early; still forging
		conversation(actor(npc)
				.respondsTo(ConversationPhrases.GREETING_MESSAGES)
				.inState(ConversationStates.IDLE)
				.when(greetingMatchesName(npc.getName())
						.and(questStateStartsWith(QUEST_SLOT, "forging;"))
						.unless(timePassed(QUEST_SLOT, SLOT_TIME_INDEX, REQUIRED_MINUTES)))
				.doing(sayTimeRemaining(QUEST_SLOT, SLOT_TIME_INDEX, REQUIRED_MINUTES, "I haven't finished forging the sword. Please check back in")));

		conversation(actor(npc)
				.respondsTo(ConversationPhrases.GREETING_MESSAGES)
				.inState(ConversationStates.IDLE)
				.saying("I have finished forging the mighty Vampire Sword. You deserve this. Now i'm going back to work, goodbye!")
				.when(greetingMatchesName(npc.getName())
						.and(questStateStartsWith(QUEST_SLOT, "forging;"))
						.and(timePassed(QUEST_SLOT, SLOT_TIME_INDEX, REQUIRED_MINUTES)))
				.doing(setQuest(QUEST_SLOT, "done"),
						equipBoundItem("vampire sword"),
						increaseXP(5000),
						increaseKarma(15.0)));

		conversation(actor(npc)
				.respondsTo("forge")
				.inState(ConversationStates.QUEST_ITEM_BROUGHT)
				.saying("Bring me " + REQUIRED_IRON
				+ " #iron bars to forge the sword with. Don't forget to bring the goblet too."));

		conversation(actor(npc)
				.respondsTo("iron")
				.inState(ConversationStates.QUEST_ITEM_BROUGHT)
				.saying("You know, collect the iron ore lying around and get it cast! Bye!")
				.changingStateTo(ConversationStates.IDLE));
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Vampire Sword",
				"Hogart tells a thrilling story of vampires and betrayal. This inspires the idea of a life stealing sword he can forge.",
				false);
		prepareQuestOfferingStep();
		prepareGobletFillingStep();
		prepareForgingStep();
	}

	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add("I have met Hogart at the dwarf blacksmith.");
		final String questState = player.getQuest(QUEST_SLOT);
		if (questState.equals("rejected")) {
			res.add("I do not want to earn the vampire sword");
		}
		if (player.isQuestInState(QUEST_SLOT, "start", "done")) {
			res.add("I want the life stealing sword. I need to return to Hogart with a goblet of blood");
		}
		if (questState.equals("start") && player.isEquipped("goblet")
				|| questState.equals("done")) {
			res.add("I have filled the goblet and now I need to bring Hogart the materials he needs.");
		}
		if (player.getQuest(QUEST_SLOT).startsWith("forging;")) {
			res.add("I took 10 iron and the goblet to Hogart. Now he's forging my sword.");
		}
		if (questState.equals("done")) {
			res.add("Finally I earned the vampire sword.");
		}
		return res;
	}

	@Override
	public String getName() {
		return "VampireSword";
	}

	@Override
	public int getMinLevel() {
		return 50;
	}

	@Override
	public String getNPCName() {
		return "Hogart";
	}

	@Override
	public String getRegion() {
		return Region.ORRIL_MINES;
	}
}
