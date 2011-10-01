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

import games.stendhal.common.parser.Sentence;
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
import games.stendhal.server.entity.npc.condition.SystemPropertyCondition;
import games.stendhal.server.entity.player.Player;

import java.util.ArrayList;
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
	private static final String QUEST_SLOT = "paper_chase_2011";

	private static final List<String> NPC_IDLE = Arrays.asList("Tad", "Haunchy Meatoch", "Pdiddi", "Ketteh Wehoh");

	private List<String> points = Arrays.asList("Tad", "Haunchy Meatoch", "Balduin", "John", "Kroip", "Stefan", 
												"Xin Blanca", "Elisabeth", "Andy", 
												"Pdiddi", "Ketteh Wehoh", "Vulcanus", "Fidorea");

	private Map<String, String> texts = new HashMap<String, String>();

	private Map<String, String> greetings = new HashMap<String, String>();

	private LoadSignFromHallOfFameAction loadSignFromHallOfFame;


	private void setupGreetings() {
		// Each greeting is said by the previous NPC to point to the NPC in the key.
		greetings.put("Haunchy Meatoch", "Yay, nice that you found me here in the hostel! ");
		greetings.put("Balduin", "Howdy partner! See you found your way to me! ");
		greetings.put("John", "It's windy here, isn't it? Hope the latest hint how to find me wasn't too easy. ");
		greetings.put("Kroip", "I hope you had a nice sail with the ferry. Thanks for meeting me at the lovely beach of Athor island! ");
		greetings.put("Stefan", "You is here! Me has pizza in oven, is hot. You goes find else people! ");
		greetings.put("Xin Blanca", "Hello in Fado Hotel and my kitchen! Good that you meet me! If you aren't on a run, you might help me cutting some carrots. Ok I see, you're busy ;) ");
		greetings.put("Elisabeth", "Here you are, found me! Good old tavern includes some lucrative business. ");
		greetings.put("Andy", "Yay you found me! :) ");
		greetings.put("Pdiddi", "Nice that you found me here! ");
		greetings.put("Ketteh Wehoh", "There you got me! Got a new delivery of beans some days ago... ");
		greetings.put("Vulcanus", "Nice that you found me here in the townhall. Moved inside, it was a bit windy in the Village. ");
		greetings.put("Fidorea", "Did you know my name is derived from Greek? ");
	}
	

	private void setupTexts() {
		texts.put("Haunchy Meatoch", "The next person you should find has his own business running. "
				  + "He is especially succesful during hot summer days when people meet his stand. Unfortunately he 'runs out of coal' often.");
		texts.put("Balduin", "The next person on your trail sits on top of a really windy mountain.");
		texts.put("John", "His wife and himself are enjoying their holidays: at least they want to!");
		texts.put("Kroip", "One of the ones you can hardly understand, but he is really famous for some tasty food.");
		texts.put("Stefan", "He one is of youngest chef but pretty lonely in huge building he is, Hotel is called. You go find him!");
		texts.put("Xin Blanca", "He sells and buys good weapons and armor but his work isn't really legal. He has a secret together with the blacksmiths assistant.");
		texts.put("Elisabeth", "She loves playing and enjoys a tasty in summer melting meal and no, it's not icecream."); 
		texts.put("Andy", "The next one is sad and wants people to take revenge for his huge lost in life.");
		texts.put("Pdiddi", "Next you need to see the dodgy geezer who pretends to be running a tavern east of Semos.");
		texts.put("Ketteh Wehoh", "She is a really polite lady who knows the rules of behaving nicely. Meet her in an important city building in Semos.");
		texts.put("Vulcanus", "Next you must find the son of a god, who greets you in Greek.");
		texts.put("Fidorea", "The final person to talk to, is the one who started all this.");
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
				raiser.say("Please talk to Fidorea in the Semos Mine Town to start the paper chase.");
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
		npc.add(ConversationStates.ATTENDING, Arrays.asList("paper", "chase"), new SystemPropertyCondition("stendhal.minetown"),
				ConversationStates.ATTENDING, null, new PaperChasePoint(idx));
		if (NPC_IDLE.contains(state)) {
			npc.add(ConversationStates.ANY, Arrays.asList("paper", "chase"), new SystemPropertyCondition("stendhal.minetown"),
					ConversationStates.ANY, null, new PaperChasePoint(idx));
		}
	}


	private void createHallOfFameSign() {
		loadSignFromHallOfFame = new LoadSignFromHallOfFameAction(null, "Those who travelled the world:\n", "P", 2000, true);
		loadSignFromHallOfFame.fire(null, null, null);
	}

	/**
	 * sets the sign to show the hall of fame
	 *
	 * @param sign a Sign or <code>null</code>.
	 */
	public void setSign(Sign sign) {
		loadSignFromHallOfFame.setSign(sign);
		loadSignFromHallOfFame.fire(null, null, null);
	}

	public void addToStarterNPCs() {
		SpeakerNPC npc = npcs.get("Fidorea");

		ChatAction startAction = new MultipleActions(
			new SetQuestAction(QUEST_SLOT, 0, points.get(0)), 
			new SetQuestToPlayerAgeAction(QUEST_SLOT, 1),
			new SetQuestToYearAction(QUEST_SLOT, 2));

		// Fidorea introduces the quests
		npc.add(
			ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new AndCondition(new QuestStartedCondition(QUEST_SLOT), new SystemPropertyCondition("stendhal.minetown")),
			ConversationStates.ATTENDING,
			"I have nothing to do for you. But thanks for asking.",
			null);
		npc.add(
			ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new AndCondition(new QuestNotStartedCondition(QUEST_SLOT), new SystemPropertyCondition("stendhal.minetown")),
			ConversationStates.QUEST_OFFERED,
			"Those who had to stay at home because of their duties, have prepared a #paper #chase.",
			null);
		npc.add(
			ConversationStates.QUEST_OFFERED,
			Arrays.asList("paper", "chase"),
			new SystemPropertyCondition("stendhal.minetown"),
			ConversationStates.ATTENDING,
			"You must ask every person on the trail about the #paper #chase. First you must find a young boy who is a bit ILL and waits for help in a hostel.",
			startAction);


		// add normal way points (without first and last)
		for (int i = 0; i < points.size() - 1; i++) {
			addTaskToNPC(i);
		}

		// Fidorea does the post processing of this quest
		npc.add(ConversationStates.ATTENDING, Arrays.asList("paper", "chase"), 
				new AndCondition(new QuestNotStartedCondition(QUEST_SLOT), new SystemPropertyCondition("stendhal.minetown")),
			ConversationStates.ATTENDING, "Oh, that is a nice #quest.", null);
		npc.add(ConversationStates.ATTENDING, Arrays.asList("paper", "chase"), 
			new AndCondition(
					new QuestStartedCondition(QUEST_SLOT), 
					new QuestNotInStateCondition(QUEST_SLOT, 0, "Fidorea"),
					new QuestNotInStateCondition(QUEST_SLOT, 0, "done"),
					new SystemPropertyCondition("stendhal.minetown")),
			ConversationStates.ATTENDING, "I guess you still have to talk to some people.", null);

		ChatAction reward = new MultipleActions(
			new IncreaseKarmaAction(15), 
			new IncreaseXPAction(400), 
			new SetQuestAction(QUEST_SLOT, 0, "done"),
			new EquipItemAction("empty scroll", 5),
			new SetHallOfFameToAgeDiffAction(QUEST_SLOT, 1, "P"),
			loadSignFromHallOfFame);
	
		npc.add(ConversationStates.ATTENDING, Arrays.asList("paper", "chase"), 
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, 0, "Fidorea"), new SystemPropertyCondition("stendhal.minetown")),
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
	}


	@Override
	public String getName() {
		return "PaperChase";
	}
	
	@Override
	public boolean isVisibleOnQuestStatus() {
		return false;
	}
	
	@Override
	public List<String> getHistory(final Player player) {
		return new ArrayList<String>();
	}
}
