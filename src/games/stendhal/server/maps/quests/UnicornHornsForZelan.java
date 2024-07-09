/***************************************************************************
 *                    Copyright © 2003-2024 - Arianne                      *
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
import games.stendhal.server.entity.npc.quest.BringItemQuestBuilder;
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
 * <li>karma</li>
 * <li>3 soup</li>
 * <li>20000 money</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li>3 days</li>
 * </ul>
 */
public class UnicornHornsForZelan implements QuestManuscript {

	@Override
	public BringItemQuestBuilder story() {
		BringItemQuestBuilder quest = new BringItemQuestBuilder();

		quest.info()
			.name("Unicorn Horns for Zelan")
			.internalName("unicorn_horns_for_zelan")
			.description("Zelan needs help gathering unicorn horns.")
			.region(Region.ATLANTIS)
			.questGiverNpc("Zelan")
			// 3 days
			.repeatableAfterMinutes(60 * 24 * 3);

		quest.history()
			.whenNpcWasMet("Zelan asked me to get 10 unicorn horns.")
			.whenQuestWasRejected("I do not want to help Zelan.")
			.whenQuestWasAccepted("I have agreed to help.")
			.whenTaskWasCompleted("I found enough unicorn horns.")
			.whenQuestWasCompleted("Zelan  is now able to make daggers.")
			.whenQuestCanBeRepeated("I should ask Zelan if he wants more help.")
			.whenCompletionsShown("I have helped Zelan [count] [time].");

		quest.offer()
			.respondToRequest("Hello! I'm in need of some unicorn horns"
					+ " to make some daggers. It is really dangerous in the woods"
					+ " surrounding Atlantis. If you are a brave sort I could"
					+ " really use some help gathering unicorn horns."
					+ " Will you help me?")
			.respondToAccept("Great! I need 10 unicorn horns. Be careful, out there are lots of large"
					+ " monsters, and those centaurs are really nasty.")
			.respondToReject("Thats ok, I will find someone else to help me.")
			.rejectionKarmaPenalty(10.0)
			.remind("I asked you to bring me 10 unicorn horns.")
			.respondToUnrepeatableRequest("Thanks, but I don't need any more help yet.")
			.respondToRepeatedRequest("I want to make more daggers. I could"
					+ " really use your help again. Would you gather more"
					+ " unicorn horns for me?");

		quest.task()
			.requestItem(10, "unicorn horn");

		quest.complete()
			.greet("Did you find the unicorn horns?")
			.respondToReject("I asked you to bring me 10 unicorn horns.")
			.respondToAccept("Thanks a bunch! As a reward I will give you"
					+ " 3 soups and 20000 money.")
			.rewardWith(new IncreaseXPAction(50000))
			.rewardWith(new IncreaseKarmaAction(30.0))
			.rewardWith(new EquipItemAction("soup", 3))
			.rewardWith(new EquipItemAction("money", 20000));

		return quest;
	}
}
