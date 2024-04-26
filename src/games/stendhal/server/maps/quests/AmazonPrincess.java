/***************************************************************************
 *                   (C) Copyright 2003-2024 - Stendhal                    *
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

import games.stendhal.server.entity.npc.action.EquipRandomAmountOfItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.InflictStatusOnNPCAction;
import games.stendhal.server.entity.npc.action.PlaySoundAction;
import games.stendhal.server.entity.npc.quest.BringItemQuestBuilder;
import games.stendhal.server.entity.npc.quest.QuestManuscript;
import games.stendhal.server.maps.Region;

/**
 * QUEST: The Amazon Princess
 *
 * PARTICIPANTS:
 * <ul>
 * <li>Princess Esclara, the Amazon Princess in a Hut on Amazon Island</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li>The princess asks you for an exotic drink</li>
 * <li>Find someone who serves exotic drinks</li>
 * <li>Take exotic drink back to princess</li>
 * <li>Princess gives you a reward</li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li>Karma +25 in all</li>
 * <li>Some fish pie, random between 2 and 7.</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li>You can repeat it once an hour.</li>
 * </ul>
 */
public class AmazonPrincess implements QuestManuscript {

	@Override
	public BringItemQuestBuilder story() {
		BringItemQuestBuilder quest = new BringItemQuestBuilder();

		quest.info()
			.name("Amazon Princess")
			.description("A thirsty princess wants a drink.")
			.internalName("amazon_princess")
			.repeatableAfterMinutes(60)
			.minLevel(70)
			.region(Region.AMAZON_ISLAND)
			.questGiverNpc("Princess Esclara");

		quest.history()
			.whenNpcWasMet("Princess Esclara welcomed me to her home on Amazon Island.")
			.whenQuestWasRejected("She asked me to fetch her a drink but I didn't think she should have one.")
			.whenQuestWasAccepted("The Princess is thirsty, I promised her an exotic drink.")
			.whenTaskWasCompleted("I found a pina colada for the Princess, I think she'd like that.")
			.whenQuestWasCompleted("Princess Esclara loved the pina colada I gave to her. She gave me fish pies and a kiss!!")
			.whenQuestCanBeRepeated("But I'd bet she's ready for another one. Maybe I'll get more fish pies.");

		quest.offer()
			.respondToRequest("I'm looking for a drink, should be an exotic one. Can you bring me one?")
			.respondToUnrepeatableRequest("I'm sure I'll be too drunk to have another one now.") // TODO:  for at least [time_remaining]
			.respondToRepeatedRequest("The last cocktail you brought me was so lovely. Will you bring me another?")
			.respondToAccept("Thank you! If you have found some, I'll be sure to give you a nice reward.")
			.respondToReject("Oh, never mind. Bye then.")
			.rejectionKarmaPenalty(10.0)
			.remind("I like these exotic drinks, I forget the name of my favorite one.");

		// Get Drink Step : athor/cocktail_bar/BarmanNPC.java he serves drinks to all, not just those with the quest
		quest.task()
			.requestItem(1, "pina colada");

		quest.complete()
			.greet("Ah, I see, you have a §'pina colada' Is it for me?")
			.respondToReject("Well then, hopefully someone else will help.")
			.respondToAccept(null)
			.rewardWith(new IncreaseKarmaAction(15))
			.rewardWith(new EquipRandomAmountOfItemAction("fish pie", 1, 6, 1,
					"Thank you! Take [this_these] [number_item] from my cook, and this kiss from me."))
			.rewardWith(new PlaySoundAction("kiss-female-01"))
			.rewardWith(new InflictStatusOnNPCAction("pina colada"));

		// completions count is stored in 3rd index of quest slot
		quest.setCompletionsIndexes(2);

		return quest;
	}

}
