/***************************************************************************
 *                   (C) Copyright 2003-2022 - Stendhal                    *
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
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.quest.KillCreaturesTask;
import games.stendhal.server.entity.npc.quest.QuestBuilder;
import games.stendhal.server.entity.npc.quest.QuestManuscript;
import games.stendhal.server.maps.Region;

/**
 * QUEST: Clean Athors underground
 *
 * PARTICIPANTS: <ul>
 * <li> NPC on Athor island
 * <li> one of each creature in Athor underground
 * </ul>
 *
 * STEPS:<ul>
 * <li> John on Athor island asks players to kill some creatures of the dungeon for him, cause he can't explore it otherwise
 * <li> Kill them for him and go back to the NPC to get your reward
 * </ul>
 *
 *
 * REWARD:<ul>
 * <li> 5000 XP
 * <li> 10 greater potion
 * <li> Karma: 11 total (10 + 1)
 * </ul>
 *
 * REPETITIONS: <ul><li>once in a week</ul>
 *
 * @author Vanessa Julius, idea by anoyyou

 */

public class CleanAthorsUnderground implements QuestManuscript {

	@Override
	public QuestBuilder<?> story() {
		QuestBuilder<KillCreaturesTask> quest = new QuestBuilder<>(new KillCreaturesTask());

		quest.info()
			.name("Clean Athor's Underground")
			.description("John and his wife Jane want to explore Athor underground on their vacation, but unfortunately they can't.")
			.internalName("clean_athors_underground")
			.repeatableAfterMinutes(MathHelper.MINUTES_IN_ONE_WEEK)
			.minLevel(70)
			.region(Region.ATHOR_ISLAND)
			.questGiverNpc("John");

		quest.history()
			.whenNpcWasMet("I have met John on Athor island.")
			.whenQuestWasRejected("I am not going to kill the creatures invading the dungeon on Athor island.")
			.whenQuestWasAccepted("I must kill one of each creature of the Athor underground to help John and Jane have a nice vacation!")
			.whenTaskWasCompleted("I've killed some creatures and should return to John.")
			.whenQuestWasCompleted("John and Jane can finally enjoy their vacation!")
			.whenQuestCanBeRepeated("But it was a long time ago that I visited John and Jane on Athor island. Maybe they need my help again now.");

		quest.offer()
			.respondToRequest("My wife Jane and me are on vacation here on Athor island. #Unfortunately we can't explore the whole island because "
					+ "some ugly #creatures step in our way each time. Can you help us by killing some of them to turn our vacation into a good one?")
			.respondToUnrepeatableRequest("These #creatures didn't return so far and we could see some lovely places all over.")
			.respondToRepeatedRequest("Those #creatures returned after the last time you helped us. Will you help us again please?")
			.respondToAccept("Fantastic! We can't wait for your return. Please kill one of each creature you can find in the underground of Athor island. I bet you'll get them all!")
			.respondToReject("Oh never mind. We'll go on sunbathing then. Not that we aren't tired of it...")
			.respondTo("unfortunately").saying("Yes, unfortunately. We wanted to have a great time here but all we did so far was sunbathe at the beach. Will you help?")
			.respondTo("creatures").saying("We just want to visit the first part of the dungeon, it seems to be very interesting. Some of these ugly things jump around there, even some mummies! Will you help?")
			.remind("Please free these lovely places on Athor from ugly creatures!");

		quest.task()
			.requestKill(1, "mummy")
			.requestKill(1, "royal mummy")
			.requestKill(1, "monk")
			.requestKill(1, "darkmonk")
			.requestKill(1, "bat")
			.requestKill(1, "brown slime")
			.requestKill(1, "green slime")
			.requestKill(1, "black slime")
			.requestKill(1, "minotaur")
			.requestKill(1, "blue dragon")
			.requestKill(1, "stone golem");

		quest.complete()
			.greet("Brilliant! You killed some of these ugly creatures as I see! Hopefully they'll not return that fast or we will still not have the chance to explore some places."
					+ " Please take these greater potions as a reward for your help.")
			.rewardWith(new IncreaseXPAction(5000))
			.rewardWith(new IncreaseKarmaAction(10.0))
			.rewardWith(new EquipItemAction("greater potion", 10));

		return quest;
	}

}
