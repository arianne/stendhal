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

import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.quest.KillCreaturesTask;
import games.stendhal.server.entity.npc.quest.QuestBuilder;
import games.stendhal.server.entity.npc.quest.QuestManuscript;
import games.stendhal.server.maps.Region;

/**
 * QUEST: CleanStorageSpace
 * <p>
 * PARTICIPANTS:
 * <li> Eonna
 * <p>
 * STEPS:
 * <li> Eonna asks you to clean her storage space.
 * <li> You go kill at least a rat, a cave rat and a cobra.
 * <li> Eonna checks your kills and then thanks you.
 * <p>
 * REWARD:
 * <li> 100 XP, karma
 * <p>
 * REPETITIONS:
 * <li> None.
 */

public class CleanStorageSpace implements QuestManuscript {

	@Override
	public QuestBuilder<?> story() {
		QuestBuilder<KillCreaturesTask> quest = new QuestBuilder<>(new KillCreaturesTask());

		quest.info()
			.name("Clean the Storage Space")
			.description("Eonna is too scared to go into her underground storage space, as it is filled with rats and snakes.")
			.internalName("clean_storage")
			.notRepeatable()
			.minLevel(0)
			.region(Region.SEMOS_CITY)
			.questGiverNpc("Eonna");

		quest.history()
			.whenNpcWasMet("I have met Eonna at her house in Semos next to the bakery.")
			.whenQuestWasRejected("I do not want to clear her storage space of creatures.")
			.whenQuestWasAccepted("I promised Eonna to kill the rats and snakes in her basement.")
			.whenTaskWasCompleted("I have cleaned out Eonna's storage space.")
			.whenQuestWasCompleted("Wow, Eonna thinks I am her hero. *blush*");

		quest.offer()
			.respondToRequest("My #basement is absolutely crawling with rats. Will you help me?")
			.respondToUnrepeatableRequest("Thanks again! I think it's still clear down there.")
			.respondToAccept("Oh, thank you! I'll wait up here, and if any try to escape I'll hit them with the broom!")
			.respondToReject("*sigh* Oh well, maybe someone else will be my hero...")
			.respondTo("basement", "storage space").saying("Yes, it's just down the stairs, over there. A whole bunch of nasty-looking rats; I think I saw a snake as well! You should be careful... still want to help me?")
			.remind("Don't you remember promising to clean out the rats from my #basement?");

		final SpeakerNPC npc = NPCList.get().get("Eonna");
		npc.addReply("basement", "Down the stairs, like I said. Please get rid of all those rats, and see if you can find the snake as well!");

		quest.task()
			.requestKill(1, "rat")
			.requestKill(1, "caverat")
			.requestKill(1, "snake");

		quest.complete()
			.greet("A hero at last! Thank you!")
			.rewardWith(new IncreaseKarmaAction(5.0))
			.rewardWith(new IncreaseXPAction(100));

		return quest;
	}

}
