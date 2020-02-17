/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.ExamineChatAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.KilledCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

/**
 * QUEST: Kill Dark Elves
 * <p>
 *
 * PARTICIPANTS:
 * <ul>
 * <li> Maerion
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li> Maerion asks you fix his dark elf problem
 * <li> You go kill at least a dark elf archer, captain, and thing
 * <li> The thing drops an amulet
 * <li> Maerion checks your kills, takes the amulet and gives you a ring of life
 * as reward
 * </ul>
 * REWARD: <ul><li> emerald ring <li> 10000 XP
 * <li>10 karma in total</ul>
 *
 * REPETITIONS: - None.
 */
public class KillDarkElves extends AbstractQuest {
	private static final String QUEST_SLOT = "kill_dark_elves";
	protected final List<String> creatures =
		Arrays.asList("dark elf captain",
				      "dark elf general",
				      "dark elf knight",
				      "dark elf wizard",
				      "dark elf sacerdotist",
				      "dark elf viceroy",
				      "dark elf matronmother",
					  "dark elf elite archer",
				      "dark elf archer");

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	private void step_1() {
		final SpeakerNPC npc = npcs.get("Maerion");

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"I already asked you to kill every dark elf in the tunnel below the secret room. And bring me the amulet from the thing.",
				null);


		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"Thanks for your help. I am relieved to have the amulet back.",
				null);

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.QUEST_OFFERED,
				"I have a problem with some dark elves. I used to be in league with them... now they are too strong. There is access to their lair from a #secret #room in this hall.",
				null);

		final List<ChatAction> actions = new LinkedList<ChatAction>();
		//actions.add(new StartRecordingKillsAction("dark elf archer", "dark elf captain", "thing"));
		actions.add(new SetQuestAction(QUEST_SLOT, "started"));
		actions.add(new ExamineChatAction("dark-elves-wanted.png", "Wanted!", ""));

		npc.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"Good. Please kill every dark elf down there and get the amulet from the mutant thing.",
			new MultipleActions(actions));

		npc.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"Then I fear for the safety of the Nalwor elves...",
			new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -5.0));

		npc.add(
			ConversationStates.QUEST_OFFERED,
			Arrays.asList("secret", "room", "secret room"),
			null,
			ConversationStates.QUEST_OFFERED,
			"It's that room downstairs with a grey roof and the evil face on the door. Inside you'll find what the dark elves were making, a mutant thing. Will you help?",
			null);
	}

	private void step_2() {
		// Go kill the dark elves and get the amulet from the thing
	}

	private void step_3() {

		final SpeakerNPC npc = npcs.get("Maerion");

		// support for old-style quest

		// the player returns to Maerion after having started the quest.
		// Maerion checks if the player has killed one of enough dark elf types
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestInStateCondition(QUEST_SLOT, "start"),
						new NotCondition(new KilledCondition("dark elf archer", "dark elf captain", "thing"))),
				ConversationStates.QUEST_STARTED,
				"Don't you remember promising to sort out my dark elf problem? Kill every dark elf in the #secret room below - especially the snivelling dark elf captain and any evil dark elf archers you find! And bring me the amulet from the mutant thing.",
				null);

		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestInStateCondition(QUEST_SLOT, "start"),
						new KilledCondition("dark elf archer", "dark elf captain", "thing"),
						new NotCondition(new PlayerHasItemWithHimCondition("amulet")))
				, ConversationStates.QUEST_STARTED
				, "What happened to the amulet? Remember I need it back!"
				, null);

		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestInStateCondition(QUEST_SLOT, "start"),
						new KilledCondition("dark elf archer", "dark elf captain", "thing"),
						new PlayerHasItemWithHimCondition("amulet"))
				, ConversationStates.ATTENDING
				, "Many, many thanks. I am relieved to have that back. Here, take this ring. It can revive the powers of the dead.",
				new MultipleActions(new DropItemAction("amulet"),
						new EquipItemAction("emerald ring", 1, true),
						new IncreaseXPAction(10000),
						new IncreaseKarmaAction(5.0),
						new SetQuestAction(QUEST_SLOT, "done")));


		// support for new-style quest

		// building string for completed quest state
		StringBuilder sb = new StringBuilder("started");
		for(int i=0;i<creatures.size();i++) {
			sb.append(";");
			sb.append(creatures.get(i));
		}
		final String completedQuestState = sb.toString();

		// the player returns to Maerion after having started the quest.
		// Maerion checks if the player has killed one of enough dark elf types
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(
						new QuestInStateCondition(QUEST_SLOT,0,"started"),
						new NotCondition(
								new QuestInStateCondition(QUEST_SLOT, completedQuestState))),
				ConversationStates.QUEST_STARTED,
				"Don't you remember promising to sort out my dark elf problem?"+
				" Kill every dark elf in the #secret room below - especially"+
				" the ones who command, do magic or are archers." +
				"  Don't forget the evil matronmother too."+
				" And bring me the amulet from the mutant thing.",
				new ExamineChatAction("dark-elves-wanted.png", "Wanted!", ""));

		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestInStateCondition(QUEST_SLOT, completedQuestState),
						new NotCondition(
								new PlayerHasItemWithHimCondition("amulet")))
				, ConversationStates.QUEST_STARTED
				, "What happened to the amulet? Remember I need it back!"
				, null);

		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestInStateCondition(QUEST_SLOT, completedQuestState),
						new PlayerHasItemWithHimCondition("amulet"))
				, ConversationStates.ATTENDING
				, "Many, many thanks. I am relieved to have that back. Here, take this ring. It can revive the powers of the dead.",
				new MultipleActions(new DropItemAction("amulet"),
						new EquipItemAction("emerald ring", 1, true),
						new IncreaseXPAction(10000),
						new IncreaseKarmaAction(5.0),
						new SetQuestAction(QUEST_SLOT, "done")));

		npc.add(
			ConversationStates.QUEST_STARTED,
			Arrays.asList("secret", "room", "secret room"),
			null,
			ConversationStates.ATTENDING,
			"The room is below us. It has a grey roof and a evil face for a door. I need you to kill all the dark elves and bring me the amulet from the mutant thing.",
			null);
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Kill Dark Elves",
				"Maerion, leader of elves, wants to kill dark elves in the secret room to hide his plots and get back his amulet.",
				false);
		step_1();
		step_2();
		step_3();
	}

	@Override
	public String getName() {
		return "KillDarkElves";
	}

	/**
	 * function return list of drow creatures to kill
	 * @return - list of dark elves to kill
	 */
	public List<String> getDrowCreaturesList() {
		return creatures;
	}

	// Killing the thing probably requires a level even higher than this - but they can get help
	@Override
	public int getMinLevel() {
		return 100;
	}

	@Override
	public List<String> getHistory(final Player player) {
 		LinkedList<String> history = new LinkedList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return history;
		}
		final String questState = player.getQuest(QUEST_SLOT, 0);

		if ("rejected".equals(questState)) {
			history.add("I do not want to help Maerion.");
			return history;
		}
		if ("done".equals(questState)) {
			history.add("I completed Maerion's quest and got an emerald ring of life!");
			return history;
		}

		// we can be here only if player accepted this quest.
		history.add("I agreed to help Maerion.");

		boolean ak=true;

		if("started".equals(player.getQuest(QUEST_SLOT, 0))) {
			// checking which creatures player killed.
			for(int i = 0; i<creatures.size();i++) {
				final boolean sp = creatures.get(i).equals(player.getQuest(QUEST_SLOT, i+1));
				ak = ak & sp;
				if(!sp) {
					history.add("I have not yet killed the "+creatures.get(i)+" in the secret room.");
				}
			}
			for(int i = 0; i<creatures.size();i++) {
				final boolean sp = creatures.get(i).equals(player.getQuest(QUEST_SLOT, i+1));
				if(sp) {
					history.add("I have killed the "+creatures.get(i)+" in the secret room.");
				}
			}

			// all killed
			if (ak) {
				history.add("I have killed all required creatures.");
			}
		}

		// here is support for old-style quest
		if ("start".equals(player.getQuest(QUEST_SLOT, 0))) {
			final boolean osp1 = player.hasKilled("dark elf captain");
			final boolean osp2 = player.hasKilled("dark elf archer");
			final boolean osp3 = player.hasKilled("thing");
			// first add killed creatures
			if (osp1) {
				history.add("I have killed the dark elf captain in the secret room.");
			}
			if (osp2) {
				history.add("I have killed the dark elf archer in the secret room.");
			}
			if (osp3) {
				history.add("I have killed the thing.");
			}

			// now add non-killed
			if (!osp1) {
				history.add("I have not yet killed the dark elf captain in the secret room.");
			}
			if (!osp2) {
				history.add("I have not yet killed the dark elf archer in the secret room.");
			}
			if (!osp3) {
				history.add("I have not yet killed the thing.");
			}

			// all killed
			if (osp1 && osp2 && osp3) {
				history.add("I have killed all required creatures.");
				ak=true;
			}
		}

		// for both old- and new-style quests
		final boolean am = player.isEquipped("amulet");
		if (am) {
			history.add("I have the amulet with me.");
		} else {
			history.add("I have no amulet with me.");
		}

		if (am && ak) {
			history.add("It's time to go back to Maerion for a reward.");
		}

		return history;
	}

	@Override
	public String getRegion() {
		return Region.NALWOR_CITY;
	}

	@Override
	public String getNPCName() {
		return "Maerion";
	}
}
