/***************************************************************************
 *                    Copyright © 2003-2023 - Arianne                      *
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
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.quest.BringItemTask;
import games.stendhal.server.entity.npc.quest.QuestBuilder;
import games.stendhal.server.entity.npc.quest.QuestManuscript;
import games.stendhal.server.maps.Region;


/**
 * QUEST: Unicorn Horns for Zelan
 *
 * PARTICIPANTS:
 * <ul>
 * <li>Zelan</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li>Zelan needs 10 unicorn horns.</li>
 * <li>Bring the unicorn horns to him for a reward.</li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li>50000 xp</li>
 * <li>5 karma</li>
 * <li>3 soup</li>
 * <li>20000 money</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li>never</li>
 * </ul>
 */
public class UnicornHornsForZelan implements QuestManuscript {

	@Override
	public QuestBuilder<?> story() {
		QuestBuilder<BringItemTask> quest = new QuestBuilder<>(new BringItemTask());

		final String npcName = "Zelan";
		final int quantity = 10;
		final String itemName = "unicorn horn";
		final String plItemName = itemName + "s";
		final int rewardSoup = 3;
		final int rewardMoney = 20000;

		quest.info()
			.name("Unicorn Horns for Zelan")
			.internalName("unicorn_horns_for_zelan")
			.description("Zelan needs help gathering unicorn horns.")
			.region(Region.ATLANTIS)
			.questGiverNpc(npcName)
			.notRepeatable();

		quest.history()
			.whenNpcWasMet(npcName + " asked me to get " + quantity
					+ " " + plItemName + ".")
			.whenQuestWasRejected("I do not want to help " + npcName + ".")
			.whenQuestWasAccepted("I have not found what I am looking for yet.")
			.whenTaskWasCompleted("I have what " + npcName + " asked for.")
			.whenQuestWasCompleted("I found " + quantity
					+ " " + plItemName + " for " + npcName + ".");

		quest.offer()
			.respondToRequest("Hello! I'm in need of some unicorn horns to"
					+ " make some daggers. It is really dangerous in the woods"
					+ " surrounding Atlantis. If you are a brave sort I could"
					+ " really use some help gathering unicorn horns. Will you"
					+ " help me?")
			.respondToUnrepeatableRequest("Thanks, but I don't need any more"
					+ " help.")
			.respondToAccept("Great! Be careful out there lots of large"
					+ " monsters, and those centaurs are really nasty.")
			.respondToReject("Thats ok, I will find someone else to help me.")
			.rejectionKarmaPenalty(0)
			//~ .remind("I have already asked you to get " + quantity
					//~ + " " + plItemName + ". Are you #done?");
			.remind("I asked you to bring me " + quantity + " " + plItemName
					+ ".");

		quest.task()
			.requestItem(quantity, itemName);

		quest.complete()
			.greet("Did you find the " + plItemName + "?")
			.respondToReject("I asked you to bring me " + quantity + " "
					+ plItemName + ".")
			.respondToAccept("Thanks a bunch! As a reward I will give you "
					+ rewardSoup + " soups and " + rewardMoney + " money.")
			.rewardWith(new IncreaseXPAction(50000))
			.rewardWith(new IncreaseKarmaAction(5.0))
			.rewardWith(new EquipItemAction("soup", rewardSoup))
			.rewardWith(new EquipItemAction("money", rewardMoney));

		return quest;
	}
}
