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

import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.EquipRandomItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.quest.BringItemQuestBuilder;
import games.stendhal.server.entity.npc.quest.QuestManuscript;
import games.stendhal.server.maps.Region;

/**
 * QUEST: Campfire
 *
 * PARTICIPANTS:
 * <ul>
 * <li> Sally, a scout sitting next to a campfire near Or'ril</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li> Sally asks you for wood for her campfire</li>
 * <li> You collect 10 pieces of wood in the forest</li>
 * <li> You give the wood to Sally.</li>
 * <li> Sally gives you 10 meat or ham in return.<li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li> 10 meat or ham</li>
 * <li> 50 XP</li>
 * <li> Karma: 10</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li> Unlimited, but 60 minutes of waiting are required between repetitions</li>
 * </ul>
 */
public class Campfire implements QuestManuscript {

	@Override
	public BringItemQuestBuilder story() {
		BringItemQuestBuilder quest = new BringItemQuestBuilder();

		quest.info()
			.name("Campfire")
			.description("Sally wants to build a campfire, but she doesn't have any wood.")
			.internalName("campfire")
			.repeatableAfterMinutes(60)
			.minLevel(0)
			.region(Region.ORRIL)
			.questGiverNpc("Sally");

		quest.history()
			.whenNpcWasMet("I have met Sally south of Orril Castle.")
			.whenQuestWasRejected("But I do not want to help her.")
			.whenQuestWasAccepted("She asked me to fetch 10 pieces of wood to keep her fire going.")
			.whenTaskWasCompleted("I found the wood needed for the fire.")
			.whenQuestWasCompleted("I have given the wood to Sally. She gave me some food and charcoal in return. I also gained 50 xp")
			.whenQuestCanBeRepeated("Sally's fire needs some wood again.");

		quest.offer()
			.respondToRequest("I need more wood to keep my campfire running, But I can't leave it unattended to go get some! Could you please get some from the forest for me? I need ten pieces.")
			.respondToUnrepeatableRequest("Thanks, but I think the wood, you brought, will last [remaining_time].")
			.respondToRepeatedRequest("My campfire needs wood again, ten pieces of #wood will be enough. Could you please get those #wood pieces from the forest for me? Please say yes!")
			.respondToAccept("Okay. You can find wood in the forest north of here. Come back when you get ten pieces of wood!")
			.respondToReject("Oh dear, how am I going to cook all this meat? Perhaps I'll just have to feed it to the animals...")
			.rejectionKarmaPenalty(5.0)
			.remind("Please don't forget that you promised to collect ten pieces of wood for me!");

		quest.task()
			.requestItem(10, "wood");

		quest.complete()
			.greet("Hi again! You've got wood, I see; do you have those 10 pieces of wood I asked about earlier?")
			.respondToReject("Oh... well, I hope you find some quickly; this fire's going to burn out soon!")
			.respondToAccept(null)
			.rewardWith(new IncreaseXPAction(50))
			.rewardWith(new IncreaseKarmaAction(10))
			.rewardWith(new EquipItemAction("charcoal", 10))
			.rewardWith(new EquipRandomItemAction("meat=10;ham=10", false, "Thank you! Here, take [this_these] [number_item] and charcoal!"));

		// completions count is stored in 2nd index of quest slot
		quest.setCompletionsIndexes(2);

		return quest;
	}

}
