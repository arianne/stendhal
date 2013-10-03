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
	private static final String FAME_TYPE = QUEST_SLOT.substring(QUEST_SLOT.length() - 1);
	
	private static final int TELEPORT_PENALTY_IN_MINUTES = 10;

	private static final List<String> NPC_IDLE = Arrays.asList("Tad", "Haunchy Meatoch", "Pdiddi", "Ketteh Wehoh");

	private List<String> points = Arrays.asList("Nishiya", "Marcus", "Eheneumniranin", "Balduin", "Rachel", "Fritz", 
												"Alice Farmer", "Elisabeth", "Sue", "Old Mother Helena", "Hazel",
												"Captain Brownbeard", "Jane", "Seremela", "Phalk", "Fidorea");

	private Map<String, String> texts = new HashMap<String, String>();

	private Map<String, String> greetings = new HashMap<String, String>();

	private LoadSignFromHallOfFameAction loadSignFromHallOfFame;


	private void setupGreetings() {
		// Each greeting is said by the previous NPC to point to the NPC in the key.
		greetings.put("Marcus", "My sheep knew that you were on the way to me. ");
		greetings.put("Eheneumniranin", "Long time ago that someone visited me here. Nice that you found me. ");
		greetings.put("Balduin", "Ahh, you found me while gathering sheaves of grain with my sickle. Great! ");
		greetings.put("Rachel", "It's windy here, isn't it? Hope the latest hint how to find me wasn't too easy. ");
		greetings.put("Fritz", "Oh I love customers of Ados bank! They are so sweet! ");
		greetings.put("Alice Farmer", "Smelling fish here, right? That's the spirit of the ocean! ");
		greetings.put("Elisabeth", "Fantastic vacation so far and so much to explore! ");
		greetings.put("Sue", "I love chocolate! You found me, maybe you can bring me a bar next time. ");
		greetings.put("Old Mother Helena", "All of these flowers around give me a warm feeling. Hope you enjoy them too, thanks for visiting me! ");
		greetings.put("Hazel", "Oh hello, so nice that you found me here. Come and join me again soon to let me cook some nice soup for you. ");
		greetings.put("Captain Brownbeard", "The museum really is a lovely place to work at. Wonderful that you found me here. ");
		greetings.put("Jane", "Yaaarrrr! My boatey will bring you over the sea, the sea! *sing* ");
		greetings.put("Seremela", "It's hot here at the beach, hope you used some suntan cream. ");
		greetings.put("Phalk", "Beautiful flowers in this city here! Unfortunately those elves don't appreciate them much. ");
		greetings.put("Fidorea", "Young warrior, you did great things on your journey! Now return to finish it. You must be thirsty! ");
	}
	

	private void setupTexts() {
		texts.put("Marcus", "The next person you should find takes care of thieves and other criminals. "
				  + "He works in a fort near Semos.");
		texts.put("Eheneumniranin", "You'll have to find the half sickling elf on Ados farm, next. He is always busy while gathering grain.");
		texts.put("Balduin", "The next person on your trail sits on top of a really windy mountain.");
		texts.put("Rachel", "The next lady to find works in a bank and can tell you all about her job.");
		texts.put("Fritz", "Please go and find the old fisherman in Ados who can tell you great stories about fish. He also has a daughter named Caroline.");
		texts.put("Alice Farmer", "The next person you'll have to seek out is on vacation in Ados, together with her whole family. She also knows everything about food and drinks.");
		texts.put("Elisabeth", "Now you have to find a young girl who plays on a playground in Kirdneh and loves chocolate."); 
		texts.put("Sue", "Please go and find the nice gardener who owns some greenhouses with tomatoes inside near Kalavan.");
		texts.put("Old Mother Helena", "Now please go and try to find a nice old woman who is really famous for her soups which can keep you warm and healthy. She might ask you about them first, just try to put her off for now :)");
		texts.put("Hazel", "I know a really nice lady who can help you next. She works in a museum and loves her job.");
		texts.put("Captain Brownbeard", "Now you have to travel on a ferry and talk to an old salt who will lead you to the next person to meet up with.");
		texts.put("Jane", "Harrr yarrr the next lady enjoys a sunbath together with her husband on Athor beach.");
		texts.put("Seremela", "It's not long ago that the next person you have to find opened a beautiful flowershop. I've seen lots of long eared creatures walking around her, hidden in a city which lays in a forest.");
		texts.put("Phalk", "The next person you have to find is an old warrior who guards the mines, north to Semos.");
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
		loadSignFromHallOfFame = new LoadSignFromHallOfFameAction(null, "Those who travelled the world on behalf of Fidorea:\n", FAME_TYPE, 2000, true);
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
			"You must ask every person on the trail about the #paper #chase. Your journey starts in Semos Village, where you find a sheep loving and sheep selling man. "
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
			new SetHallOfFameToAgeDiffAction(QUEST_SLOT, 1, FAME_TYPE),
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
