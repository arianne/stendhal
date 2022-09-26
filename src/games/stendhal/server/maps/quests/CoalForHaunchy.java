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
import games.stendhal.server.entity.npc.action.EquipRandomAmountOfItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.quest.BringItemTask;
import games.stendhal.server.entity.npc.quest.QuestBuilder;
import games.stendhal.server.entity.npc.quest.QuestManuscript;
import games.stendhal.server.maps.Region;

/**
 * QUEST: Coal for Haunchy
 *
 * PARTICIPANTS:
 * <ul>
 * <li>Haunchy Meatoch, the BBQ grillmaster on the Ados market</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li>Haunchy Meatoch asks you to fetch coal for his BBQ</li>
 * <li>Find some coal in Semos Mine or buy some from other players</li>
 * <li>Take the coal to Haunchy</li>
 * <li>Haunchy gives you a tasty reward</li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li>Karma +25 in all</li>
 * <li>XP +200 in all</li>
 * <li>Some grilled steaks, random between 1 and 4.</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li>You can repeat it each 2 days.</li>
 * </ul>
 *
 * @author Vanessa Julius and storyteller
 */
public class CoalForHaunchy implements QuestManuscript {

	@Override
	public QuestBuilder<?> story() {
		QuestBuilder<BringItemTask> quest = new QuestBuilder<>(new BringItemTask());

		quest.info()
			.name("Coal for Haunchy")
			.description("Haunchy Meatoch is afraid of his BBQ grillfire. Will his coal last till the steaks are ready or will he need some more?")
			.internalName("coal_for_haunchy")
			.repeatableAfterMinutes(2 * 24 * 60)
			.minLevel(0)
			.region(Region.ADOS_CITY)
			.questGiverNpc("Haunchy Meatoch");

		quest.history()
			.whenNpcWasMet("Haunchy Meatoch welcomed me to the Ados market.")
			.whenQuestWasRejected("He asked me to fetch him some pieces of coal but I don't have time to collect some.")
			.whenQuestWasAccepted("The BBQ grill-heat is low and I promised Haunchy to help him out with 25 pieces of coal.")
			.whenTaskWasCompleted("I found 25 pieces of coal for the Haunchy and think he will be happy.")
			.whenQuestWasCompleted("Haunchy Meatoch was really happy when I gave him the coal, he has enough for now. He gave me some of the best steaks which I ever ate!")
			.whenQuestCanBeRepeated("But I'd bet his amount is low again and needs more. Maybe I'll get more grilled tasty steaks.");


		quest.offer()
			.respondToRequest("I cannot use wood for this huge BBQ. To keep the heat I need some really old stone coal but there isn't much left. The problem is, that I can't fetch it myself because my steaks would burn then so I have to stay here. Can you bring me 25 pieces of #coal for my BBQ please?")
			.respondToUnrepeatableRequest("The coal amount behind my counter is still high enough. I will not need more for some time.")
			.respondToRepeatedRequest("The last coal you brought me is mostly gone again. Will you bring me some more?")
			.respondToAccept("Thank you! I'll be sure to give you a nice and tasty reward.")
			.respondTo("coal").saying("Coal isn't easy to find. You normally can find it somewhere in the ground but perhaps you are lucky and find some in the old Semos Mine tunnels... Will you help me?")
			.respondToReject("Oh, never mind. I thought you love BBQs like I do. Bye then.")
			.rejectionKarmaPenalty(10.0)
			.remind("Luckily my BBQ is still going. But please hurry up to bring me 25 coal as you promised.");

		NPCList.get().get("Haunchy Meatoch").addReply("coal", "Sometime you could do me a #favour ...");

		quest.task()
			.requestItem(25, "coal");

		quest.complete()
			.greet("Ah, I see, you have enough coal to keep my BBQ on! Is it for me?")
			.respondToReject("Well then, hopefully someone else will help me before my BBQ goes out.")
			.respondToAccept(null)
			.rewardWith(new IncreaseXPAction(200))
			.rewardWith(new IncreaseKarmaAction(20))
			.rewardWith(new EquipRandomAmountOfItemAction("grilled steak", 1, 4, 1,
					"Thank you! Take [this_these] [number_item] from my grill!"));

		return quest;
	}

}
