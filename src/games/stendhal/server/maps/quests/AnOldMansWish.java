/***************************************************************************
 *                    Copyright © 2003-2022 - Arianne                      *
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

import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DecreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.NPCEmoteAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.LevelLessThanCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;


/** Quest to increase number of bag slots.
 *
 *  NPCs:
 *  - Elias Breland
 *  - Niall Breland
 *  - Marianne
 */
public class AnOldMansWish extends AbstractQuest {

	public static final String QUEST_SLOT = "an_old_mans_wish";
	private static final int min_level = 100;

	private final SpeakerNPC elias = npcs.get("Elias Breland");


	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public String getName() {
		return "AnOldMansWish";
	}

	@Override
	public String getRegion() {
		return Region.DENIRAN;
	}

	@Override
	public String getNPCName() {
		return elias.getName();
	}

	@Override
	public int getMinLevel() {
		return min_level;
	}

	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<>();

		res.add(elias.getName() + " wishes to know what has become of his"
			+ " estranged grandson.");

		final String quest_state = player.getQuest(QUEST_SLOT);
		if (quest_state.equals("rejected")) {
			res.add("I have no time for senile old men.");
		} else {
			res.add("I have agreed to investigate.");
			if (quest_state.equals("done")) {
				res.add("Elias and his grandson have been"
					+ " reunited.");
			}
		}

		return res;
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
			"An Old Man's Wish",
			elias.getName() + " is grieved over the loss of his grandson.",
			false
		);
		prepareElias();
		prepareMarianne();
		prepareCompleteStep();
	}

	private void prepareElias() {
		// requests quest but does not meet minimum level requirement
		elias.add(
			ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new AndCondition(
				new QuestNotStartedCondition(QUEST_SLOT),
				new LevelLessThanCondition(min_level)),
			ConversationStates.ATTENDING,
			"My grandson disappeared over a year ago. But I need help from a"
				+ " more experienced adventurer.",
			null);

		// requests quest
		elias.add(
			ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new AndCondition(
				new QuestNotStartedCondition(QUEST_SLOT),
				new NotCondition(new LevelLessThanCondition(min_level))),
			ConversationStates.QUEST_OFFERED,
			"My grandson disappeared over a year ago. I fear the worst and"
				+ " have nearly given up all hope. What I would give to just"
				+ " know what happened to him! If you learn anything will"
				+ " you bring me the news?",
			null);

			// already accepted quest
			elias.add(
				ConversationStates.ANY,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"Thank you for accepting my plea for help. Please tell me if"
					+ " you hear any news about what has become of my grandson."
					+ " He used to play with a little girl named #Marianne.",
				null);

			// already completed quest
			elias.add(
				ConversationStates.ANY,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"Thank you for returning my grandson to me. I am overfilled"
					+ " with joy!",
				null);

			// rejects quest
			elias.add(
				ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Alas! What has become of my grandson!?",
				new MultipleActions(
					new SetQuestAction(QUEST_SLOT, "rejected"),
					new DecreaseKarmaAction(15)));

			// accepts quest
			elias.add(
				ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				ConversationStates.ATTENDING,
				"Oh thank you! My grandson's name is #Niall. You could talk"
					+ " to #Marianne. They used to play together.",
				new MultipleActions(
					new SetQuestAction(QUEST_SLOT, "Marianne"),
					new IncreaseKarmaAction(15)));

			// ask about Niall
			elias.add(
				ConversationStates.ANY,
				Arrays.asList("Niall", "grandson"),
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"Niall is my grandson. I am so distraught over his"
					+ " disappearance. Ask the girl #Marianne. The often played"
					+ " together.",
				null);

			// ask about Marianne
			elias.add(
				ConversationStates.ANY,
				"Marianne",
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"Marianne lives here in Deniran. Ask her about #Niall.",
				null);
	}

	private void prepareMarianne() {
		final SpeakerNPC marianne = npcs.get("Marianne");

		final ChatCondition m1 = new QuestInStateCondition(QUEST_SLOT, "Marianne");

		marianne.add(
			ConversationStates.ATTENDING,
			"Niall",
			m1,
			ConversationStates.ATTENDING,
			"Oh! My friend Niall! I haven't seen him in a long time. Every"
				+ " time I go to his grandfather's house to #play, he is not"
				+ " home.",
			new NPCEmoteAction("suddenly looks very melancholy."));

		marianne.add(
			ConversationStates.ATTENDING,
			"play",
			m1,
			ConversationStates.ATTENDING,
			"Not only was he fun to play with, but he was also very helpful."
				+ " He used to help me gather chicken eggs whenever I was too"
				+ " #afraid to do it myself.",
			new NPCEmoteAction("looks even more melancholy."));

		marianne.add(
			ConversationStates.ATTENDING,
			"afraid",
			m1,
			ConversationStates.ATTENDING,
			"Know what he told me once? He said he wanted to go all the way"
				+ " to Semos to see the #graveyard there. Nuh uh! No way! That"
				+ " sounds more scary than chickens.",
			new NPCEmoteAction("shivers."));

		marianne.add(
			ConversationStates.ATTENDING,
			Arrays.asList("graveyard", "cemetary"),
			m1,
			ConversationStates.ATTENDING,
			"I hope he didn't go to that scary graveyard. Who knows what kind"
				+ " of monsters are there.",
			null);
	}

	private void prepareCompleteStep() {
	}
}
