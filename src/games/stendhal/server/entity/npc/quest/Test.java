package games.stendhal.server.entity.npc.quest;

import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.maps.Region;

public class Test {

	public void test() {
		QuestBuilder<KillCreaturesTask> quest = new QuestBuilder<>(new KillCreaturesTask());

		quest.info()
			.name("Clean the Storage Space")
			.description("Eonna is too scared to go into her underground storage space, as it is filled with rats and snakes.")
			.internalName("CleanStorageSpace")
			.repeatable(false)
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
			.respondToRepeatedRequest("Thanks again! I think it's still clear down there.")
			.respondToAccept("Oh, thank you! I'll wait up here, and if any try to escape I'll hit them with the broom!")
			.respondToReject("*sigh* Oh well, maybe someone else will be my hero...")
			.respondTo("basement", "storage space").saying("Yes, it's just down the stairs, over there. A whole bunch of nasty-looking rats; I think I saw a snake as well! You should be careful... still want to help me?")
			.remind("Don't you remember promising to clean out the rats from my #basement?");

		// TODO: npc.addReplay("basement", "Down the stairs, like I said. Please get rid of all those rats, and see if you can find the snake as well!");

		quest.task()
			.requestKill(1, "rat")
			.requestKill(1, "caverat")
			.requestKill(1, "snake");

		quest.complete()
			.greet("A hero at last! Thank you!")
			.rewardWith(new IncreaseKarmaAction(5.0))
			.rewardWith(new IncreaseXPAction(100));

		quest.simulate();
	}

	public void test2() {
		QuestBuilder<BringItemTask> quest = new QuestBuilder<>(new BringItemTask());

		quest.info()
			.name("Armor for Dagobert")
			.description("Dagobert, the consultant at the bank of Semos, needs protection.")
			.internalName("ArmorForDagobert")
			.repeatable(false)
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
			.respondToRepeatedRequest("Thank you very much for the armor, but I don't have any other task for you.")
			.respondToAccept("Once I had a nice #'leather cuirass', but it was destroyed during the last robbery. If you find a new one, I'll give you a reward.")
			.respondToReject("Well, then I guess I'll just duck and cover.")
			.remind("Luckily I haven't been robbed while you were away. I would be glad to receive a leather cuirass. Anyway, how can I #help you?");

		// npc.addReply(Arrays.toList("leather cuirass", "leather", "cuirass"), "A leather cuirass is the traditional cyclops armor. Some cyclopes are living in the dungeon deep under the city.");

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
	
		quest.simulate();
	}

	public static void main(String[] args) {
		Test test = new Test();
		test.test();
		System.out.println();
		System.out.println("-----------------------------");
		System.out.println();
		test.test2();
	}
}
