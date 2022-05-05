/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
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

import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.quest.BringItemTask;
import games.stendhal.server.entity.npc.quest.QuestBuilder;
import games.stendhal.server.entity.npc.quest.QuestManuscript;
import games.stendhal.server.maps.Region;

/**
 * QUEST: Armor for Dagobert
 *
 * PARTICIPANTS:
 * <ul>
 * <li>Dagobert, the consultant at the bank of Semos</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li>Dagobert asks you to find a leather cuirass.</li>
 * <li>You get a leather cuirass, e.g. by killing a cyclops.</li>
 * <li>Dagobert sees your leather cuirass and asks for it and then thanks you.</li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li>50 XP</li>
 * <li>80 gold</li>
 * <li>Karma: 10</li>
 * <li>Access to vault</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li>None</li>
 * </ul>
 */
public class ArmorForDagobert implements QuestManuscript {

	@Override
	public QuestBuilder<?> story() {
		QuestBuilder<BringItemTask> quest = new QuestBuilder<>(new BringItemTask());

		quest.info()
			.name("Armor for Dagobert")
			.description("Dagobert, the consultant at the bank of Semos, needs protection.")
			.internalName("armor_dagobert")
			.notRepeatable()
			.minLevel(0)
			.region(Region.SEMOS_CITY)
			.questGiverNpc("Dagobert");

		quest.history()
			.whenNpcWasMet("I have met Dagobert. He is the consultant at the bank in Semos.")
			.whenQuestWasRejected("He asked me to find a leather cuirass but I rejected his request.")
			.whenQuestWasAccepted("I promised to find a leather cuirass for him because he has been robbed.")
			.whenTaskWasCompleted("I found a leather cuirass and will take it to Dagobert.")
			.whenQuestWasCompleted("I took the leather cuirass to Dagobert. As a little thank you, he will allow me to use a private vault.");

		quest.offer()
			.respondToRequest("I'm so afraid of being robbed. I don't have any protection. Do you think you can help me?")
			.respondToUnrepeatableRequest("Thank you very much for the armor, but I don't have any other task for you.")
			.respondToAccept("Once I had a nice #'leather cuirass', but it was destroyed during the last robbery. If you find a new one, I'll give you a reward.")
			.respondToReject("Well, then I guess I'll just duck and cover.")
			.remind("Luckily I haven't been robbed while you were away. I would be glad to receive a leather cuirass. Anyway, how can I #help you?");

		SpeakerNPC npc = NPCList.get().get("Dagobert");
		npc.addReply(Arrays.asList("leather cuirass", "leather", "cuirass"), "A leather cuirass is the traditional cyclops armor. Some cyclopes are living in the dungeon deep under the city.");

		quest.task()
			.requestItem(1, "leather cuirass")
			.alternativeItem(1, "pauldroned leather cuirass");

		quest.complete()
			.greet("Excuse me, please! I have noticed the leather cuirass you're carrying. Is it for me?")
			.respondToReject("Well then, I hope you find another one which you can give to me before I get robbed again.")
			.respondToAccept("Oh, I am so thankful! Here is some gold I found ... ehm ... somewhere. Now that you have proven yourself a trusted customer, you may have access to your own private banking #vault any time you like.")
			.rewardWith(new EquipItemAction("money", 80))
			.rewardWith(new IncreaseXPAction(50))
			.rewardWith(new IncreaseKarmaAction(10));

		return quest;
	}

}
