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
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.quest.KillCreaturesTask;
import games.stendhal.server.entity.npc.quest.QuestBuilder;
import games.stendhal.server.entity.npc.quest.QuestManuscript;
import games.stendhal.server.maps.Region;

/**
 * QUEST: Kill Gnomes
 * <p>
 * PARTICIPANTS:
 * <ul>
 * <li> Jenny, by the mill in Semos Plains
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li> Gnomes have been stealing carrots so Jenny asks you to kill some.
 * <li> You go kill the Gnomes in the gnome village and you get the reward from Jenny
 * </ul>
 * <p>
 * REWARD:
 * <ul>
 * <li> 3 potions
 * <li> 100 XP
 * <li> No karma (deliberately. Killing gnomes is mean!)
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li> after 7 days.
 * </ul>
 */

public class KillGnomes implements QuestManuscript {

	@Override
	public QuestBuilder<?> story() {
		QuestBuilder<KillCreaturesTask> quest = new QuestBuilder<>(new KillCreaturesTask());

		quest.info()
			.name("Kill Gnomes")
			.description("Jenny isn't happy that gnomes keep stealing her carrots.")
			.internalName("kill_gnomes")
			.repeatableAfterMinutes(MathHelper.MINUTES_IN_ONE_WEEK)
			.minLevel(10)
			.region(Region.SEMOS_SURROUNDS)
			.questGiverNpc("Jenny");

		quest.history()
			.whenNpcWasMet("I have met Jenny at the windmill northeast of Semos.")
			.whenQuestWasRejected("I am not going to murder cute gnomes.")
			.whenQuestWasAccepted("I agreed to kill some gnomes, especially the leader ones, to teach them all a lesson.")
			.whenTaskWasCompleted("The gnomes are now staying away from Jenny's carrots. Yeah!")
			.whenQuestWasCompleted("Jenny gave me some potions to thank me.")
			.whenQuestCanBeRepeated("But this was a long time ago. I should check in with Jenny to make sure the gnomes still remember the lesson.");

		quest.offer()
			.respondToRequest("Some gnomes have been stealing carrots from the farms North of Semos. "
					+ "They need to be taught a lesson, will you help?")
			.respondToUnrepeatableRequest("The gnomes haven't made any trouble since you last taught them a lesson.")
			.respondToRepeatedRequest("Those pesky gnomes are stealing carrots again. I think they need another lesson. Will you help?")
			.respondToAccept("Excellent. You'll find the gnomes camped out, north west of Semos. Make sure you kill some of the ringleaders, too, at least one infantryman and one cavalryman.")
			.respondToReject("You're right, perhaps it is cruel to slaughter gnomes who only stole a carrot or so. "
					+ "Maybe the farms should just increase their security. ")
			// no karma penalty for rejecting the quest because killing gnomes is evil
			.rejectionKarmaPenalty(0)
			.remind("You need to teach those pesky gnomes a lesson, by killing some as an example! "
					+ "Make sure you get the leaders, too, at least one infantryman and one cavalryman.");

		quest.task()
			.requestKill(1, "gnome")
			.requestKill(1, "infantry gnome")
			.requestKill(1, "cavalryman gnome");

		quest.complete()
			.greet("I see you have killed the gnomes as I asked. I hope they will stay away from the carrots for a while! "
					+ "Please take these potions as a reward.")
			.rewardWith(new IncreaseXPAction(100))
			.rewardWith(new EquipItemAction("potion", 3));

		return quest;
	}

}
