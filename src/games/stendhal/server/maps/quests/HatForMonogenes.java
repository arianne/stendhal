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

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.rp.HOFScore;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.quest.BringItemQuestBuilder;
import games.stendhal.server.entity.npc.quest.QuestManuscript;
import games.stendhal.server.maps.Region;
import games.stendhal.server.maps.semos.city.GreeterNPC;
import games.stendhal.server.util.ResetSpeakerNPC;

/**
 * QUEST: Hat For Monogenes
 *
 * PARTICIPANTS:
 * <ul>
 * <li>Monogenes, an old man in Semos city.</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li> Monogenes asks you to buy a hat for him.</li>
 * <li> Xin Blanca sells you a leather helmet.</li>
 * <li> Monogenes sees your leather helmet and asks for it and then thanks you.</li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li>50 XP</li>
 * <li>Karma: 10</li>
 * </ul>
 *
 * REPETITIONS: - None.
 */
public class HatForMonogenes implements QuestManuscript {

	@Override
	public BringItemQuestBuilder story() {
		BringItemQuestBuilder quest = new BringItemQuestBuilder();

		quest.info()
			.name("Hat for Monogenes")
			.description("Monogenes wants a hat to help him keep warm during the winter.")
			.internalName("hat_monogenes")
			.notRepeatable()
			.minLevel(0)
			.region(Region.SEMOS_CITY)
			.questGiverNpc("Monogenes");

		quest.history()
			.whenNpcWasMet("I have met Monogenes at the spring in Semos village.")
			.whenQuestWasRejected("He asked me for a hat, but I don't want to help.")
			.whenQuestWasAccepted("I have to find a hat, something leather to keep his head warm.")
			.whenTaskWasCompleted("I have found a hat.")
			.whenQuestWasCompleted("I gave the hat to Monogenes to keep his bald head warm.");

		quest.offer()
			.respondToRequest("Could you bring me a #hat to cover my bald head? Brrrrr! The days here in Semos are really getting colder...")
			.respondToUnrepeatableRequest("Thanks for the offer, good friend, but this hat will last me five winters at least, and it's not like I need more than one.")
			.respondToAccept("Thanks, my good friend. I'll be waiting here for your return!")
			.respondToReject("You surely have more importants things to do, and little time to do them in. I'll just stay here and freeze to death, I guess... *sniff*")
			.rejectionKarmaPenalty(5.0)
			.remind("Hey, my good friend, remember that leather hat I asked you about before? It's still pretty chilly here...")
			.respondTo("hat").saying("You don't know what a hat is?! Anything light that can cover my head; like leather, for instance. Now, will you do it?");

		quest.task()
			.requestItem(1, "leather helmet");

		quest.complete()
			.greet("Hey! Is that leather hat for me?")
			.respondToReject("I guess someone more fortunate will get his hat today... *sneeze*")
			.respondToAccept("Bless you, my good friend! Now my head will stay nice and warm.")
			.rewardWith(new IncreaseXPAction(50))
			.rewardWith(new IncreaseKarmaAction(10));

		quest.setBaseHOFScore(HOFScore.EASY);

		return quest;
	}

	public boolean removeFromWorld() {
		final boolean res = ResetSpeakerNPC.reload(new GreeterNPC(), "Monogenes");
		// reload other quests associated with Monogenes
		SingletonRepository.getStendhalQuestSystem().reloadQuestSlots("Monogenes");
		return res;
	}

}
