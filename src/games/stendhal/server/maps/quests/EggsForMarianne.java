/***************************************************************************
 *                    (C) Copyright 2019-2024 - Stendhal                   *
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

import games.stendhal.server.entity.npc.action.EquipRandomItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.quest.BringItemQuestBuilder;
import games.stendhal.server.entity.npc.quest.QuestManuscript;
import games.stendhal.server.maps.Region;

/**
 * QUEST: EggsForMarianne
 *
 * PARTICIPANTS:
 * <ul>
 * <li> Marianne, a little girl looking for eggs</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li> Marianne asks you for eggs for her pancakes</li>
 * <li> You collect a dozen of eggs from chickens</li>
 * <li> You give a dozen of eggs to Marianne.</li>
 * <li> Marianne gives you some flowers in return.<li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li> some pansy or daisies</li>
 * <li> 100 XP</li>
 * <li> Karma: 50</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li> Unlimited, at least 60 minutes have to elapse before repeating</li>
 * </ul>
 */
public class EggsForMarianne implements QuestManuscript {

	@Override
	public BringItemQuestBuilder story() {
		BringItemQuestBuilder quest = new BringItemQuestBuilder();

		quest.info()
			.name("Eggs for Marianne")
			.description("Marianne's mom is going to make some pancakes and she needs some eggs.")
			.internalName("eggs_for_marianne")
			.repeatableAfterMinutes(60)
			.minLevel(0)
			.region(Region.DENIRAN_CITY)
			.questGiverNpc("Marianne");

		quest.history()
			.whenNpcWasMet("I have met Marianne in Deniran City.")
			.whenQuestWasRejected("She asked me to fetch some eggs, but I do not want to help her.")
			.whenQuestWasAccepted("She asked me to fetch 12 eggs")
			.whenTaskWasCompleted("I have found the eggs for Marianne.")
			.whenQuestWasCompleted("I have given Marianne the eggs. She gave me some flowers in return.")
			.whenQuestCanBeRepeated("Marianne needs more eggs again.");

		quest.offer()
			.respondToRequest(
					"I need a dozen of eggs. " +
					"My mom asked me to collect eggs and she is going to make me pancakes! " +
					"I'm afraid of getting close to those chickens! " +
					"Could you please get eggs for me?")
			.respondToUnrepeatableRequest("Thanks! I think the eggs you already brought me will be enough for another while...")
			.respondToRepeatedRequest("My mom needs eggs again! Could you please get a dozen more for me?")
			.respondToAccept(
					"Okay. You can find eggs hunting chickens... I am so afraid of getting near those pesky chickens! " +
					"Please come back when you found enough eggs for me!")
			.respondToReject(
					"Oh dear, what am I going to do with all these flowers? " +
					"Perhaps I'll just leave them around some graves...")
			.rejectionKarmaPenalty(5.0)
			.remind("You promised to collect a dozen of eggs for me ... ");

		quest.task()
			.requestItem(12, "egg");

		quest.complete()
			.greet("Hi again! You've got several eggs, I see. Do you have 12 eggs for me?")
			.respondToReject("Oh... well, I hope you find some quickly. I'm getting hungry!")
			.respondToAccept(null)
			.rewardWith(new IncreaseXPAction(50))
			.rewardWith(new IncreaseKarmaAction(50))
			.rewardWith(new EquipRandomItemAction("pansy=12;daisies=12", false, "Thank you! Here, take [this_these] [number_item]!"));

		return quest;
	}

}
