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

import java.util.List;

import games.stendhal.common.MathHelper;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rp.StendhalQuestSystem;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.quest.DeliverItemQuestBuilder;
import games.stendhal.server.entity.npc.quest.QuestHistoryResult;
import games.stendhal.server.entity.npc.quest.QuestManuscript;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;
import games.stendhal.server.maps.quests.houses.HouseBuyingMain;
import games.stendhal.server.maps.semos.bakery.ChefNPC;
import games.stendhal.server.util.QuestUtils;
import games.stendhal.server.util.ResetSpeakerNPC;

/**
 * QUEST: Pizza Delivery
 * <p>
 * PARTICIPANTS:
 * <ul>
 * <li> Leander (the baker in Semos)
 * <li> NPC's all over the world (as customers)
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li> Leander gives you a pizza and tells you who ordered it, and how much
 * time you have to deliver.
 * <li> As a gimmick, you get a pizza delivery uniform.
 * <li> You walk to the customer and say "pizza".
 * <li> The customer takes the pizza. If you were fast enough, you get a tip.
 * <li> You put on your original clothes automatically.
 * </ul>
 * REWARD:
 * <ul>
 * <li> XP (Amount varies depending on customer. You only get half of the XP if
 * the pizza has become cold.)
 * <li> some karma if delivered on time (5)
 * <li> gold coins (As a tip, if you were fast enough; amount varies depending
 * on customer.)
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li> As many as wanted, but you can't get a new task while you still have the
 * chance to do the current delivery on time.
 * </ul>
 */
public class PizzaDelivery implements QuestManuscript {

	private static final String questSlot = "pizza_delivery";


	@Override
	public DeliverItemQuestBuilder story() {
		DeliverItemQuestBuilder quest = new DeliverItemQuestBuilder();


		quest.info()
			.name("Pizza Delivery")
			.description("Leander's pizza business is doing so well that he now recruits delivery boys and girls.")
			.internalName(questSlot)
			.repeatableAfterMinutes(0)
			.minLevel(0)
			.region(Region.SEMOS_CITY)
			.questGiverNpc("Leander");


		quest.history()
			.whenNpcWasMet("I met Leander, the baker of Semos.")
			.whenQuestWasRejected("He asked me to deliver pizza but I rejected his request.")
			.whenQuestWasAccepted("I agreed to help with pizza delivery")
			.whenItemWasGiven("Leander gave me a [flavor] for [customerName].")
			.whenToldAboutCustomer("Leander told me: \"[customerDescription]\"")
			.whenInTime("If I hurry, I might still get there, with the pizza hot.")
			.whenOutOfTime("The pizza has already gone cold.")
			.whenQuestWasCompleted("I delivered the last pizza Leander gave to me.")
			.whenQuestCanBeRepeated("But I'd bet, Leander has more orders.")
			.addResult(new HotDeliveryResult());


		quest.offer()
			.respondToRequest("I need you to quickly deliver a hot pizza. If you're fast enough, you might get quite a nice tip. So, will you do it?")
			.respondIfUnableToWearUniform("Sorry, you can't wear our pizza delivery uniform looking like that. If you get changed, you can ask about the #task again.")
			.respondToUnrepeatableRequest("Thank you very much for your help. I don't have any other orders at this time.")
			.respondToAccept("You must bring this [flavor] to [customerName] within [time]. Say \"pizza\" so that [customerName] knows that I sent you. Oh, and please wear this uniform on your way.")
			.respondToReject("Too bad. I hope my daughter #Sally will soon come back from her camp to help me with the deliveries.")
			.remind("You still have to deliver a pizza to [customerName], and hurry!")
			.respondIfLastQuestFailed("I see you failed to deliver the pizza to [customerName] in time. Are you sure you will be more reliable this time?")
			.respondIfInventoryIsFull("Come back when you have space to carry the pizza!");


		quest.task()
			.itemName("pizza")
			.itemDescription("You see a [flavor].")
			.outfitUniform(new Outfit(null, Integer.valueOf(990), null, null, null, null, null, null, null));

		// Don't add Sally here, as it would conflict with Leander telling
		// about his daughter.
		quest.task().order()
			.customerNpc("Balduin")
			.customerDescription("Balduin is a hermit who's living on a mountain between Semos and Ados. It's called Ados Rock. Walk east from here.")
			.itemDescription("Pizza Prosciutto")
			// Tested by mort: 6:30 min, with killing some orcs.
			.minutesToDeliver(7)
			// Quite high because you can't do much senseful on top of the hill and must walk down again.
			.tipOnFastDelivery(200)
			.xpReward(300)
			.respondToFastDelivery("Thanks! I wonder how you managed to bring it up here so fast. Take these [tip] pieces of gold as a tip, I can't spend it up here anyway!")
			.respondToSlowDelivery("Brrr. This [flavor] is no longer hot. Well, thanks for the effort anyway.")
			.playerMinLevel(10);

		quest.task().order()
			.customerNpc("Cyk")
			.customerDescription("Cyk is currently on holiday on Athor Island. You'll easily recognize him by his blue hair. Go South East to find Athor ferry.")
			.itemDescription("Pizza Hawaii")
			// You need about 6 min to Eliza, up to 12 min to wait for the
			// ferry, 5 min for the crossing, and 0.5 min from the docks to
			// the beach, so you need a bit of luck for this one.
			.minutesToDeliver(20)
			.tipOnFastDelivery(300)
			.xpReward(500)
			.respondToFastDelivery("Wow, I never believed you would really deliver this half over the world! Here, take these [flavor] bucks!")
			.respondToSlowDelivery("It has become cold, but what do I expect when I order a pizza from a bakery so far away... So thanks anyway.")
			.playerMinLevel(20);

		quest.task().order()
			.customerNpc("Eliza")
			.customerDescription("Eliza works for the Athor Island ferry service. You'll find her at the docks south of the Ados swamps.")
			.itemDescription("Pizza del Mare")
			// minutes to deliver. Tested by mort: 6 min, ignoring slow animals and not
			// walking through the swamps.
			.minutesToDeliver(7)
			.tipOnFastDelivery(170)
			.xpReward(300)
			.respondToFastDelivery("Incredible! It's still hot! Here, buy something nice from these [tip] pieces of gold!")
			.respondToSlowDelivery("What a pity. It has become cold. Nevertheless, thank you!")
			.playerMinLevel(20);

		quest.task().order()
			.customerNpc("Fidorea")
			.customerDescription("Fidorea lives in Ados city. She is a makeup artist. You'll need to walk east from here.")
			.itemDescription("Pizza Napoli")
			// Tested by mort: about 6 min, outrunning all enemies.
			.minutesToDeliver(7)
			.tipOnFastDelivery(150)
			.xpReward(200)
			.respondToFastDelivery("Thanks a lot! You're a born pizza deliverer. You can have these [tip] pieces of gold as a tip!")
			.respondToSlowDelivery("Bummer. Cold pizza.")
			.playerMinLevel(15);

		quest.task().order()
			.customerNpc("Haizen")
			.customerDescription("Haizen is a magician who lives in a hut near the road to Ados. You'll need to walk east and north from here.")
			.itemDescription("Pizza Diavolo")
			// minutes to deliver. Tested by kymara: exactly 3 min.
			.minutesToDeliver(4)
			.tipOnFastDelivery(80)
			.xpReward(150)
			.respondToFastDelivery("Ah, my [flavor]! And it's fresh out of the oven! Take these [tip] coins as a tip!")
			.respondToSlowDelivery("I hope next time I order a pizza it's still hot.")
			.playerMinLevel(10);

		quest.task().order()
			.customerNpc("Jenny")
			.customerDescription("Jenny owns a mill in the plains north and a little east of Semos.")
			.itemDescription("Pizza Margherita")
			// Tested by mort: can be done in 1:15 min, with no real danger.
			.minutesToDeliver(2)
			.tipOnFastDelivery(20)
			.xpReward(50)
			.respondToFastDelivery("Ah, you brought my [flavor]! Very nice of you! Here, take [tip] coins as a tip!")
			.respondToSlowDelivery("It's a shame. Your pizza service can't deliver a hot pizza although the bakery is just around the corner.")
			.playerMinLevel(2);

		quest.task().order()
			.customerNpc("Jynath")
			.customerDescription("Jynath is a witch who lives in a small house south of Or'ril castle. You'll need to walk south west from Semos, all the way through the forest, then follow the path west till you see her hut.")
			.itemDescription("Pizza Funghi")
			// Tested by mort: 5:30 min, leaving the slow monsters on the way behind.
			.minutesToDeliver(6)
			.tipOnFastDelivery(140)
			.xpReward(200)
			.respondToFastDelivery("Oh, I didn't expect you so early. Great! Usually I don't give tips, but for you I'll make an exception. Here are [tip] pieces of gold.")
			.respondToSlowDelivery("Too bad... I will have to use an extra strong spell to get this pizza hot again.")
			.playerMinLevel(5);

		quest.task().order()
			.customerNpc("Katinka")
			.customerDescription("Katinka takes care of the animals at the Ados Wildlife Refuge. That's north east of here, on the way to Ados city.")
			.itemDescription("Pizza Vegetale")
			// Tested by kymara in 3:25 min, leaving behind the orcs.
			.minutesToDeliver(4)
			.tipOnFastDelivery(100)
			.xpReward(200)
			.respondToFastDelivery("Yay! My [flavor]! Here, you can have [tip] pieces of gold as a tip!")
			.respondToSlowDelivery("Eek. I hate cold pizza. I think I'll feed it to the animals.")
			.playerMinLevel(10);

		quest.task().order()
			.customerNpc("Marcus")
			.customerDescription("Marcus is a guard in the Semos jail. That is due west from here, beyond Semos village.")
			.itemDescription("Pizza Tonno")
			// Tested by kymara: takes longer than before due to fence in village
			.minutesToDeliver(3)
			// A bit higher than Jenny because you can't do anything
			// else in the jail and need to walk out again.
			.tipOnFastDelivery(25)
			.xpReward(100)
			.respondToFastDelivery("Ah, my [flavor]! Here's your tip: [tip] pieces of gold.")
			.respondToSlowDelivery("Finally! Why did that take so long?")
			.playerMinLevel(2);

		quest.task().order()
			.customerNpc("Nishiya")
			.customerDescription("Nishiya sells sheep. You'll find him west of here, in Semos village.")
			.itemDescription("Pizza Pasta")
			// Tested by mort: easy to do in less than 1 min.
			.minutesToDeliver(1)
			.tipOnFastDelivery(10)
			.xpReward(25)
			.respondToFastDelivery("Thank you! That was fast. Here, take [tip] pieces of gold as a tip!")
			.respondToSlowDelivery("Too bad. It has become cold. Thank you anyway.")
			.playerMinLevel(0);

		quest.task().order()
			.customerNpc("Ouchit")
			.customerDescription("Ouchit is a weapons trader. He has currently rented a room upstairs in Semos tavern, just around the corner.")
			.itemDescription("Pizza Quattro Stagioni")
			// Tested by mort: can be done in 45 sec with no danger.
			.minutesToDeliver(1)
			.tipOnFastDelivery(10)
			.xpReward(25)
			.respondToFastDelivery("Thank you! It's nice to have a pizza service right around the corner. Here, you can have [tip] coins!")
			.respondToSlowDelivery("I should have rather picked it up myself at the bakery, that would have been faster.")
			.playerMinLevel(0);

		quest.task().order()
			.customerNpc("Ramon")
			.customerDescription("Ramon works as a blackjack dealer on the ferry to Athor Island. The ferry terminal is south east from here - it's a long way!")
			.itemDescription("Pizza Bolognese")
			// You need about 6 mins to Eliza, and once you board the ferry,
			// about 15 sec to deliver. If you have bad luck, you need to
			// wait up to 12 mins for the ferry to arrive at the mainland, so
			// you need a bit of luck for this one.
			.minutesToDeliver(14)
			.tipOnFastDelivery(250)
			.xpReward(400)
			.respondToFastDelivery("Thank you so much! Finally I get something better than the terrible food that Laura cooks. Take these [tip] pieces of gold as a tip!")
			.respondToSlowDelivery("Too bad. It is cold. And I had hoped to get something better than that galley food.")
			.playerMinLevel(20);

		quest.task().order()
			.customerNpc("Tor'Koom")
			.customerDescription("Tor'Koom is an orc who lives in the dungeon below this town, Semos. Sheep are his favorite food. He lives at the 4th level below the ground. Be careful!")
			.itemDescription("Pizza Pecora")
			// Tested by kymara:
			// done in about 8 min, with lots of monsters getting in your way.
			.minutesToDeliver(9)
			.tipOnFastDelivery(170)
			.xpReward(300)
			.respondToFastDelivery("Yummy [flavor]! Here, take [tip] moneys!")
			.respondToSlowDelivery("Grrr. Pizza cold. You walking slow like sheep.")
			.playerMinLevel(15);

		quest.task().order()
			.customerNpc("Martin Farmer")
			.customerDescription("Martin Farmer is holidaying in Ados City. You'll need to walk east from here.")
			.itemDescription("Pizza Fiorentina")
			// Time for Fidorea was 7, so 8 should be ok for martin
			.minutesToDeliver(8)
			.tipOnFastDelivery(160)
			.xpReward(220)
			.respondToFastDelivery("Ooooh, I loove fresh hot pizza, thanks. take this [tip] money...!")
			.respondToSlowDelivery("Hmpf.. a cold pizza.. ok.. I will take it. But hurry up next time.")
			.playerMinLevel(10);

		quest.complete()
			.respondToItemWithoutQuest("Eek! This pizza is all dirty! Did you find it on the ground?")
			.respondToItemForOtherNPC("No, thanks. I like [flavor] better.")
			.respondToMissingItem("A pizza? Where?")
			.npcStatusEffect("pizza");

		// completions count is stored in 3rd index of quest slot
		quest.setCompletionsIndexes(2);

		return quest;
	}


	// TODO: Remove program logic from manuscript classes
	public boolean removeFromWorld() {
		boolean res = ResetSpeakerNPC.reload(new ChefNPC(), "Leander")
			&& ResetSpeakerNPC.reload(new games.stendhal.server.maps.ados.rock.WeaponsCollectorNPC(), "Balduin")
			&& ResetSpeakerNPC.reload(new games.stendhal.server.maps.ados.coast.FerryConveyerNPC(), "Eliza")
			&& ResetSpeakerNPC.reload(new games.stendhal.server.maps.ados.city.MakeupArtistNPC(), "Fidorea")
			&& ResetSpeakerNPC.reload(new games.stendhal.server.maps.ados.magician_house.WizardNPC(), "Haizen")
			&& ResetSpeakerNPC.reload(new games.stendhal.server.maps.semos.plains.MillerNPC(), "Jenny")
			&& ResetSpeakerNPC.reload(new games.stendhal.server.maps.orril.magician_house.WitchNPC(), "Jynath")
			&& ResetSpeakerNPC.reload(new games.stendhal.server.maps.ados.outside.AnimalKeeperNPC(), "Katinka")
			&& ResetSpeakerNPC.reload(new games.stendhal.server.maps.semos.jail.GuardNPC(), "Marcus")
			&& ResetSpeakerNPC.reload(new games.stendhal.server.maps.semos.village.SheepSellerNPC(), "Nishiya")
			&& ResetSpeakerNPC.reload(new games.stendhal.server.maps.semos.tavern.BowAndArrowSellerNPC(), "Ouchit")
			&& ResetSpeakerNPC.reload(new games.stendhal.server.maps.semos.dungeon.SheepBuyerNPC(), "Tor'Koom")
			&& ResetSpeakerNPC.reload(new games.stendhal.server.maps.ados.wall.HolidayingManNPC(), "Martin Farmer");
		final StendhalQuestSystem quests = SingletonRepository.getStendhalQuestSystem();
		// reload other quests associated with NPCs
		quests.reloadQuestSlots(
			// Balduin
			"weapons_collector", "weapons_collector2", "ultimate_collector",
			// Fidorea
			QuestUtils.evaluateQuestSlotName("paper_chase_20[year]"),
			// Haizen
			"maze",
			// Jenny
			"kill_gnomes",
			// Jynath
			"ceryl_book",
			// Katinka
			"zoo_food",
			// Nishiya
			"sheep_growing",
			// Ouchit
			"bows_ouchit"
		);
		final NPCList npcs = SingletonRepository.getNPCList();
		// Cyk & Ramon are not loaded via zone configurators
		SpeakerNPC npc = npcs.get("Ramon");
		StendhalRPZone npczone;
		if (npc != null) {
			npczone = npc.getZone();
			if (npczone != null) {
				npczone.remove(npc);
				res = res && npcs.get("Ramon") == null;
				quests.reloadQuestSlots("blackjack");
			}
		}
		npc = npcs.get("Cyk");
		if (npc != null) {
			npczone = npc.getZone();
			if (npczone != null) {
				npczone.remove(npc);
				res = res && npcs.get("Cyk") == null;
				new HouseBuyingMain().createAthorNPC(npczone);
			}
		}
		return res;
	}

	/**
	 * Adds number of hot deliveries to quest history.
	 */
	// TODO: Remove program logic from manuscript classes
	private class HotDeliveryResult implements QuestHistoryResult {
		@Override
		public void apply(Player player, List<String> res) {
			final int count = MathHelper.parseIntDefault(player.getQuest(questSlot, 3), 0);
			if (count > 0) {
				res.add("I have delivered " + count + " hot " + Grammar.plnoun(count, "pizza") + ".");
			}
		}
	}
}
