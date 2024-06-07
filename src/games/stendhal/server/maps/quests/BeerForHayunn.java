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
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.quest.BringItemQuestBuilder;
import games.stendhal.server.entity.npc.quest.QuestManuscript;
import games.stendhal.server.maps.Region;
import games.stendhal.server.maps.semos.guardhouse.RetiredAdventurerNPC;
import games.stendhal.server.util.ResetSpeakerNPC;

/**
 * QUEST: Beer For Hayunn
 *
 * PARTICIPANTS:
 * <ul>
 * <li>Hayunn Naratha (the veteran warrior in Semos)</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li>Hayunn asks you to buy a beer from Margaret.</li>
 * <li>Margaret sells you a beer.</li>
 * <li>Hayunn sees your beer, asks for it and then thanks you.</li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li>50 XP</li>
 * <li>20 gold coins</li>
 * <li>Karma: 10</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li>None</li>
 * </ul>
 */
public class BeerForHayunn implements QuestManuscript {

	public static final String QUEST_SLOT = "beer_hayunn";


	@Override
	public BringItemQuestBuilder story() {
		BringItemQuestBuilder quest = new BringItemQuestBuilder();

		quest.info()
			.name("Beer for Hayunn")
			.description("Hayunn Naratha, the great warrior in Semos Guard House, wants a beer.")
			.internalName(QUEST_SLOT)
			.notRepeatable()
			.minLevel(0)
			.region(Region.SEMOS_CITY)
			.questGiverNpc("Hayunn Naratha");

		quest.history()
			.whenNpcWasMet("I have talked to Hayunn Naratha.")
			.whenQuestWasRejected("I do not want to make Hayunn drunk.")
			.whenQuestWasAccepted("I promised to buy him a beer from Margaret in Semos Tavern.")
			.whenTaskWasCompleted("I have a bottle of beer.")
			.whenQuestWasCompleted("I gave the beer to Hayunn. He paid me 20 gold coins and I got some experience.");

		// TODO: new QuestCompletedCondition(OTHER_QUEST_SLOT)),
		quest.offer()
			.respondToRequest("My mouth is dry, but I can't be seen to abandon this teaching room! Could you bring me some #beer from the #tavern?")
			.respondToUnrepeatableRequest("Thanks all the same, but I don't want to get too heavily into drinking; I'm still on duty, you know! I'll need my wits about me if a student shows up...")
			.respondToAccept("Thanks! I'll be right here, waiting. And guarding, of course.")
			.respondToReject("Oh, well forget it then. I guess I'll just hope for it to start raining, and then stand with my mouth open.")
			.rejectionKarmaPenalty(5.0)
			.remind("Hey, I'm still waiting for that beer, remember? Anyway, what can I do for you?")
			.respondTo("tavern").saying("If you don't know where the inn is, you could ask old Monogenes; he's good with directions. Are you going to help?")
			.respondTo("beer").saying("A bottle of cool beer from #Margaret will be more than enough. So, will you do it?")
			.respondTo("Margaret").saying("Margaret is the pretty maid in the tavern, of course! Quite a looker, too... heh. Will you go for me?");

		quest.task()
			.requestItem(1, "beer");

		quest.complete()
			.greet("Hey! Is that beer for me?")
			.respondToReject("Drat! You remembered that I asked you for one, right? I could really use it right now.")
			.respondToAccept("*glug glug* Ah! That hit the spot. Let me know if you need anything, ok?")
			.rewardWith(new EquipItemAction("money", 20))
			.rewardWith(new IncreaseXPAction(50))
			.rewardWith(new IncreaseKarmaAction(10));

		quest.setBaseHOFScore(HOFScore.EASY);

		return quest;
	}


	public boolean removeFromWorld() {
		final boolean res = ResetSpeakerNPC.reload(new RetiredAdventurerNPC(), "Hayunn Naratha");
		// reload other associated quests
		SingletonRepository.getStendhalQuestSystem().reloadQuestSlots("meet_hayunn");
		return res;
	}
}
