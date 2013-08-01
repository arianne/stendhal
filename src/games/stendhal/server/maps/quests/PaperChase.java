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

import games.stendhal.common.MathHelper;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.events.TeleportListener;
import games.stendhal.server.core.events.TeleportNotifier;
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
import games.stendhal.server.maps.Region;

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
public class PaperChase extends AbstractQuest implements TeleportListener {
	private static final String QUEST_SLOT = "paper_chase_2013";
	private static final int TELEPORT_PENALTY_IN_MINUTES = 10;

	private static final List<String> NPC_IDLE = Arrays.asList("Tad", "Haunchy Meatoch", "Pdiddi", "Ketteh Wehoh");

	private List<String> points = Arrays.asList("Will", "Adena", "Valo", "Balduin", "Gaston", "Lobelia", 
												"Ortiv Milquetoast", "Pam", "Old Mother Helena", 
												"Imorgen", "Anastasia", "Vulcanus", "Wrvil", "Fidorea");

	private Map<String, String> texts = new HashMap<String, String>();

	private Map<String, String> greetings = new HashMap<String, String>();

	private LoadSignFromHallOfFameAction loadSignFromHallOfFame;


	private void setupGreetings() {
		// Each greeting is said by the previous NPC to point to the NPC in the key.
		greetings.put("Adena", "I was waiting for you already :) ");
		greetings.put("Valo", "Ahh there you are! I guess the lovely fresh smell of my veggies brought you here. ");
		greetings.put("Balduin", "Oh you found the way up in this beautiful old Ados church. ");
		greetings.put("Gaston", "It's windy here, isn't it? Hope the latest hint how to find me wasn't too easy. ");
		greetings.put("Lobelia", "Bonjour! You have made your way through these streets in my hometown Rat city as I see. ");
		greetings.put("Ortiv Milquetoast", "Aren't these flowers lovely around me, come and take a deeper look at them! ");
		greetings.put("Pam", "Uh, ah! Phew, it's just you. You got me! ");
		greetings.put("Old Mother Helena", "I knew someone would find me at the beach! ");
		greetings.put("Imorgen", "Oh hello, so nice that you found me here. Come and join me again soon to let me cook some nice soup for you. ");
		greetings.put("Anastasia", "*sing* some herbs into the pot, some more and some more *sing*... ");
		greetings.put("Vulcanus", "Shhh Hughie tries to sleep... ");
		greetings.put("Wrvil", "Did you know my name is derived from Greek? ");
		greetings.put("Fidorea", "*Wroff!* You found me down here, nice!");
	}
	

	private void setupTexts() {
		texts.put("Adena", "The next person you should find runs her own business. "
				  + "She sells fresh vegetables and food from farms near Semos.");
		texts.put("Valo", "The next person on your trail lives and works in a church. He can concoct some useful drinks for you.");
		texts.put("Balduin", "The next person on your trail sits on top of a really windy mountain.");
		texts.put("Gaston", "The next person is a really good baker and can create lovely food from butter and chocolate. Both his name and the food he creates are in a language foreign to me.");
		texts.put("Lobelia", "The next mystical person is gazing intently at the flowerbeds around her and knows a lot about your further travels...");
		texts.put("Ortiv Milquetoast", "You'll need some calm with you, as the next person is really afraid of Assassins and Bandits.");
		texts.put("Pam", "The next lady loves sunbathing on a special island and is a good friend of Zara."); 
		texts.put("Old Mother Helena", "Now please go and try to find a nice old woman who is really famous for her soups which can keep you warm and healthy.");
		texts.put("Imorgen", "The hobbies of the next person you have to find, are singing and brewing special mystical mixtures. She lives surrounded by trees in a small hut, together with her grandma.");
		texts.put("Anastasia", "Please go and see after the next lady who is currently taking care of her ill son. They live in a lovely countryside.");
		texts.put("Vulcanus", "Next you must find the son of a god, who greets you in Greek.");
		texts.put("Wrvil", "Now go and meet the husband of a barmaid which is famous for her strong torcibud drinks.");
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

		@Override
		public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
			final String state = points.get(idx);
			final String next = points.get(idx + 1);
			final String questState = player.getQuest(QUEST_SLOT, 0);

			// player does not have this quest or finished it
			if (questState == null) {
				raiser.say("Please talk to Fidorea in the Mine Town north of Semos to start the paper chase.");
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
		npc.add(ConversationStates.ATTENDING, Arrays.asList("paper", "chase", "paperchase"), new SystemPropertyCondition("stendhal.minetown"),
				ConversationStates.ATTENDING, null, new PaperChasePoint(idx));
		if (NPC_IDLE.contains(state)) {
			npc.add(ConversationStates.ANY, Arrays.asList("paper", "chase", "paperchase"), new SystemPropertyCondition("stendhal.minetown"),
					ConversationStates.ANY, null, new PaperChasePoint(idx));
		}
	}


	private void createHallOfFameSign() {
		loadSignFromHallOfFame = new LoadSignFromHallOfFameAction(null, "Those who travelled the world on behalf of Fidorea:\n", "P", 2000, true);
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
			"You must ask every person on the trail about the #paper #chase. First you must go to Semos Road to find a young boy who warns players about the dangers in Faiumoni."
			+ "And just a warning: you may teleport on your journey, but every teleport will count as " + TELEPORT_PENALTY_IN_MINUTES + " minutes on the high score sign.",
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
			"Very good. You did the complete quest, talking to all those people around the world. I will add your name to the sign for everyone to see. And here are some magic scrolls as reward. They will help you on further travels.",
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
		TeleportNotifier.get().registerListener(this);
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


	@Override
	public void onTeleport(Player player, boolean playerAction) {
		if (!playerAction) {
			return;
		}

		if (player.hasQuest(QUEST_SLOT) && !player.getQuest(QUEST_SLOT, 0).equals("done")) {
			int startAgeWithPenalty = MathHelper.parseIntDefault(player.getQuest(QUEST_SLOT, 1), 0) - TELEPORT_PENALTY_IN_MINUTES;
			player.setQuest(QUEST_SLOT, 1, Integer.toString(startAgeWithPenalty));
		}
	}


	@Override
	public String getNPCName() {
		return "Fidorea";
	}
	
	@Override
	public String getRegion() {
		return Region.SEMOS_SURROUNDS;
	}
}
