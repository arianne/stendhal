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

import java.util.Arrays;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.mapstuff.spawner.PassiveEntityRespawnPoint;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.quest.BringItemQuestBuilder;
import games.stendhal.server.entity.npc.quest.QuestManuscript;
import games.stendhal.server.maps.Region;

/**
 * QUEST: Plink's Toy
 * <p>
 * PARTICIPANTS: <ul><li> Plink <li> some wolves </ul>
 *
 * STEPS: <ul><li> Plink tells you that he got scared by some wolves and ran away
 * dropping his teddy. <li> Find the teddy in the Park Of Wolves <li> Bring it back to
 * Plink </ul>
 *
 * REWARD: <ul><li> a smile <li> 20 XP <li> 10 Karma </ul>
 *
 * REPETITIONS: <ul><li> None. </ul>
 */
public class PlinksToy implements QuestManuscript {

	@Override
	public BringItemQuestBuilder story() {
		BringItemQuestBuilder quest = new BringItemQuestBuilder();

		quest.info()
			.name("Plink's Toy")
			.description("Plink is a sweet little boy, and like many little boys, is frightened of wolves")
			.internalName("plinks_toy")
			.notRepeatable()
			.minLevel(0)
			.region(Region.SEMOS_SURROUNDS)
			.questGiverNpc("Plink");

		quest.history()
			.whenNpcWasMet("I have met Plink.")
			.whenQuestWasRejected("I do not want to find Plink's toy bear.")
			.whenQuestWasAccepted("Plink begged me to look for his teddy in a garden with lots of wolves.")
			.whenTaskWasCompleted("I have found Plink's toy bear.")
			.whenQuestWasCompleted("I gave the bear to Plink and he was extremly happy.");

		quest.offer()
			.begOnGreeting("*cries* There were wolves in the #park! *sniff* I ran away, but I dropped my #teddy! Please will you get it for me? *sniff* Please?")
			.respondToRequest("*cries* There were wolves in the #park! *sniff* I ran away, but I dropped my #teddy! Please will you get it for me? *sniff* Please?")
			.respondToUnrepeatableRequest("You found my teddy already near by the wolves! I still squeeze and hug it :)")
			.respondToAccept("*sniff* Thanks a lot! *smile*")
			.respondToReject("*sniff* But... but... PLEASE! *cries*")
			.rejectionKarmaPenalty(10.0)
			.remind("I lost my teddy in the #park over east, where all those #wolves are hanging about.")
			.respondTo("wolf", "wolves").saying("They came in from the plains, and now they're hanging around the #park over to the east a little ways. I'm not allowed to go near them, they're dangerous.")
			.respondTo("park").saying("My parents told me not to go to the park by myself, but I got lost when I was playing... Please don't tell them! Can you bring my #teddy back?")
			.respondTo("teddy").saying("Teddy is my favorite toy! Please will you bring him back?");

		final SpeakerNPC npc = NPCList.get().get("Plink");
		npc.addReply(Arrays.asList("wolf", "wolves"), "They came in from the plains, and now they're hanging around the #park over to the east a little ways. I'm not allowed to go near them, they're dangerous.");
		npc.addReply("park", "My parents told me not to go to the park by myself, but I got lost when I was playing... Please don't tell them!");
		npc.addReply("teddy", "Teddy is my favorite toy! Please bring him back to me.");

		quest.task()
			.requestItem(1, "teddy");
		step2();

		quest.complete()
			.greet("You found my teddy! Please, please, may I have him back?")
			.respondToReject("*snifff*")
			.respondToAccept("*hugs teddy* Thank you, thank you! *smile*")
			.rewardWith(new IncreaseXPAction(20))
			.rewardWith(new IncreaseKarmaAction(10));

		return quest;
	}

	private void step2() {
		final StendhalRPZone zone = SingletonRepository.getRPWorld().getZone("0_semos_plains_n");
		final PassiveEntityRespawnPoint teddyRespawner = new PassiveEntityRespawnPoint("teddy", 1500);
		teddyRespawner.setPosition(107, 84);
		teddyRespawner.setDescription("There's a teddy-bear-shaped depression in the sand here.");
		zone.add(teddyRespawner);

		teddyRespawner.setToFullGrowth();
	}
}
