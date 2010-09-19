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

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.mapstuff.sign.Sign;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.LoadSignFromHallOfFameAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetHallOfFameToAgeDiffAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestToPlayerAgeAction;
import games.stendhal.server.entity.npc.action.SetQuestToYearAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStartedCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A kind of paper chase.
 *
 * @author hendrik
 */
public class PaperChase extends AbstractQuest {
	private static final String QUEST_SLOT = "paper_chase_2010";

	private static final List<String> NPC_IDLE = Arrays.asList("Hayunn Naratha", "Thanatos", "Pdiddi", "Monogenes");

	private List<String> points = Arrays.asList("Hayunn Naratha", "Thanatos", "Haizen", "Zara", "Leander", "Sally", 
												"Plink", "Jef", "Blacksheep Harry", 
												"Pdiddi", "Monogenes", "Vulcanus", "Saskia");

	private Map<String, String> texts = new HashMap<String, String>();

	private Map<String, String> greetings = new HashMap<String, String>();

	private ChatAction loadSignFromHallOfFame;


	private void setupGreetings() {
		// Each greeting is said by the previous NPC to point to the NPC in the key.
		greetings.put("Thanatos", "Well done for finding me! I knew you'd remember my book! ");
		greetings.put("Haizen", "Well, I guess that was an easy clue Hayunn gave you? ");
		greetings.put("Zara", "I hope Thanatos didn't tell you I'd actually summon that dragon. I'm much too afraid. ");
		greetings.put("Leander", "You found me! Cyk's told me about a pizza chef who delivers all the way to Athor. ");
		greetings.put("Sally", "I'm glad that my business is so well known, that you could find me! My daughter is the next person for you to visit. ");
		greetings.put("Plink", "*smiles* There's another who smiles too. ");
		greetings.put("Jef", "Yay! You're here! This is so exciting! ");
		greetings.put("Blacksheep Harry", "Nice to see you! ");
		greetings.put("Pdiddi", "You got me. ");
		greetings.put("Monogenes", "Good work on finding me. Now, I may look bald but I'm not really, it's just a short hair cut. ");
		greetings.put("Vulcanus", "Did you know my name is derived from Greek? ");
		greetings.put("Saskia", "Well done for finding me. ");
	}
	

	private void setupTexts() {
		texts.put("Thanatos", "The next person you must find, "
				  + "I'd think he 'knows how to kill creatures', because he would see so many getting killed. Many players "
				  + "meet their 'match' when they battle to the 'death' under his watch.");
		texts.put("Haizen", "The next person you should find has magic "
				  + "which can summon something that even I cannot: a giant red dragon.");
		texts.put("Zara", "The next person on your trail also has some kind of magical power, they are slowly turning *themselves* red!");
		texts.put("Leander", "I have no idea where the pizza chef is, but he's the next on your trail!");
		texts.put("Sally", "She's a girl who loves playing with fire.");
		texts.put("Plink", "He's a boy who lives on a farm, he smiles if you can help him with his wolf problem. You should go see him next!");
		texts.put("Jef", "The next person for you to find needs some excitement too! He's been waiting for his"
				  + " mother to return from market for so long now.");
		texts.put("Blacksheep Harry", "Next, visit the sheepish man who makes canned tuna for the whole world!"); 
		texts.put("Pdiddi", "Next you need to see the dodgy geezer who pretends to be running a tavern east of Semos.");
		texts.put("Monogenes", "Your next clue is to find a human who really is bald. He's pretty old, too.");
		texts.put("Vulcanus", "Next you must find the son of a god, who greets you in Greek.");
		texts.put("Saskia", "The final person to talk to, is the one who started all this.");
	}
	
	/**
	 * Handles all normal points in this paper chase (without the first and last.
	 * one)
	 */
	private class PaperChasePoint implements ChatAction {
		private final int idx;

		PaperChasePoint(final int idx) {
			this.idx = idx;
		}

		public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
			final String state = points.get(idx);
			final String next = points.get(idx + 1);
			final String questState = player.getQuest(QUEST_SLOT, 0);

			// player does not have this quest or finished it
			if (questState == null) {
				raiser.say("Please talk to Saskia in the Semos Mine Town to start the paper chase.");
				return;
			}

			final String nextNPC = questState;

			// is the player supposed to speak to another NPC?
			if (!nextNPC.equals(state)) {
				raiser.say("What do you say? \"" + texts.get(nextNPC) + "\" That's obviously not me.");
				return;
			}

			// send player to the next NPC and record it in quest state
			raiser.say(greetings.get(next) + texts.get(next) + " Good luck!");
			player.setQuest(QUEST_SLOT, 0, next);
			player.addXP((idx + 1) * 10);
		}

	}

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	/**
	 * Adds the task to the specified NPC. Note that the start and end of this
	 * quest have to be coded specially.
	 *
	 * @param idx
	 *            index of way point
	 */
	private void addTaskToNPC(final int idx) {
		final String state = points.get(idx);
		final SpeakerNPC npc = npcs.get(state);
		npc.add(ConversationStates.ATTENDING, Arrays.asList("paper", "chase"), null,
				ConversationStates.ATTENDING, null, new PaperChasePoint(idx));
		if (NPC_IDLE.contains(state)) {
			npc.add(ConversationStates.ANY, Arrays.asList("paper", "chase"), null,
					ConversationStates.ANY, null, new PaperChasePoint(idx));
		}
	}


	private void createHallOfFameSign() {
		Sign sign = new Sign();
		sign.setPosition(94, 110);
		StendhalRPZone zone = SingletonRepository.getRPWorld().getZone("0_semos_mountain_n2");
		zone.add(sign);
		loadSignFromHallOfFame = new LoadSignFromHallOfFameAction(sign, "Those who travelled the world:\n", "P", 2000, true);
		loadSignFromHallOfFame.fire(null, null, null);
	}


	private void addToNPCs() {
		SpeakerNPC npc = npcs.get("Saskia");

		ChatAction startAction = new MultipleActions(
			new SetQuestAction(QUEST_SLOT, 0, points.get(0)), 
			new SetQuestToPlayerAgeAction(QUEST_SLOT, 1),
			new SetQuestToYearAction(QUEST_SLOT, 2));

		// Saskia introduces the quests
		npc.add(
			ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new QuestStartedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			"I have nothing to do for you. But thanks for asking",
			null);
		npc.add(
			ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new QuestNotStartedCondition(QUEST_SLOT),
			ConversationStates.QUEST_OFFERED,
			"Those who had to stay at home because of their duties, have prepared a #paper #chase.",
			null);
		npc.add(
			ConversationStates.QUEST_OFFERED,
			Arrays.asList("paper", "chase"),
			null,
			ConversationStates.ATTENDING,
			"You must ask every person on the trail about the #paper #chase. First you must find the beer-loving author of KNOW HOW TO KILL CREATURES.",
			startAction);


		// add normal way points (without first and last)
		for (int i = 0; i < points.size() - 1; i++) {
			addTaskToNPC(i);
		}

		// Saskia does the post processing of this quest
		npc.add(ConversationStates.ATTENDING, Arrays.asList("paper", "chase"), 
			new QuestNotStartedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING, "Oh, that is a nice #quest.", null);
		npc.add(ConversationStates.ATTENDING, Arrays.asList("paper", "chase"), 
			new AndCondition(
					new QuestStartedCondition(QUEST_SLOT), 
					new QuestNotInStateCondition(QUEST_SLOT, 0, "Saskia"),
					new QuestNotInStateCondition(QUEST_SLOT, 0, "done")),
			ConversationStates.ATTENDING, "I guess you still have to talk to some people.", null);

		ChatAction reward = new MultipleActions(
			new IncreaseKarmaAction(15), 
			new IncreaseXPAction(400), 
			new SetQuestAction(QUEST_SLOT, 0, "done"),
			new EquipItemAction("empty scroll", 5),
			new SetHallOfFameToAgeDiffAction(QUEST_SLOT, 1, "P"),
			loadSignFromHallOfFame);
	
		npc.add(ConversationStates.ATTENDING, Arrays.asList("paper", "chase"), 
			new QuestInStateCondition(QUEST_SLOT, 0, "Saskia"),
			ConversationStates.ATTENDING, 
			"Very good. You did the complete quest, talking to all those people around the world. I will add you name to the sign for everyone to see. And here are some magic scrolls as reward. They will help you on further travels.",
			reward);
	}


	@Override
	public void addToWorld() {
		super.addToWorld();
		fillQuestInfo(
				"Paper Chase",
				"Some rumours are going around in Faiumoni. Maybe some of the guys who live around there know something.",
				false);
		setupGreetings();
		setupTexts();
	
		createHallOfFameSign();
		addToNPCs();
	}


	@Override
	public String getName() {
		return "PaperChase";
	}
}
