/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
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

import games.stendhal.common.Grammar;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.quests.logic.BringListOfItemsQuest;
import games.stendhal.server.maps.quests.logic.BringListOfItemsQuestLogic;

import java.util.Arrays;
import java.util.List;

/**
 * QUEST: The Weapons Collector
 * <p>
 * PARTICIPANTS:
 * <ul>
 * <li> Balduin, a hermit living on a mountain between Semos and Ados
 * </ul>
 * <p>
 * STEPS:
 * <ul>
 * <li> Balduin asks you for some weapons.
 * <li> You get one of the weapons somehow, e.g. by killing a monster.
 * <li> You bring the weapon up the mountain and give it to Balduin.
 * <li> Repeat until Balduin received all weapons. (Of course you can bring up
 * several weapons at the same time.)
 * <li> Balduin gives you an ice sword in exchange.
 * </ul>
 * <p>
 * REWARD:
 * <ul>
 * <li> ice sword
 * <li> 5000 XP
 * </ul>
 * <p>
 * REPETITIONS:
 * <li> None.
 */
public class WeaponsCollector extends AbstractQuest implements
		BringListOfItemsQuest {

	private static final List<String> neededWeapons = Arrays.asList("bardiche",
			"battle axe", "broadsword", "flail", "halberd", "katana",
			"golden mace", "scimitar", "scythe", "war hammer");

	private static final String QUEST_SLOT = "weapons_collector";



	@Override
	public void addToWorld() {
		super.addToWorld();
		fillQuestInfo(
				"Weapon Collector",
				"Balduin, the hermit who is living on Ados rock, has an exciting challenge for you.",
				true);
		setupAbstractQuest();
	}

	private void setupAbstractQuest() {
		final BringListOfItemsQuest concreteQuest = this;
		final BringListOfItemsQuestLogic bringItems = new BringListOfItemsQuestLogic(concreteQuest);
		bringItems.addToWorld();
	}

	public SpeakerNPC getNPC() {
		return npcs.get("Balduin");
	}

	public List<String> getNeededItems() {
		return neededWeapons;
	}

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	public List<String> getTriggerPhraseToEnumerateMissingItems() {
		return Arrays.asList("collection");
	}

	public List<String> getAdditionalTriggerPhraseForQuest() {
		return ConversationPhrases.EMPTY;
	}

	public double getKarmaDiffForQuestResponse() {
		return 0;
	}

	public String welcomeBeforeStartingQuest() {
		return "Greetings. I am Balduin. Are you interested in weapons? "
				+ "I certainly am, I have been collecting them since I was "
				+ "young. Maybe you can do a little #task for me.";
	}

	public String welcomeDuringActiveQuest() {
		return "Welcome back. I hope you have come to help me with my #collection.";
	}

	public String welcomeAfterQuestIsCompleted() {
		return "Welcome! Thanks again for completing my collection.";
	}

	public boolean shouldWelcomeAfterQuestIsCompleted() {
		// because of WeaponsCollector2
		return false; 
	}

	public String respondToQuest() {
		return "Although I have collected weapons for such a long time, I "
				+ "still don't have everything I want. I need "
				+ "help to complete my #collection.";
	}

	public String respondToQuestAfterItHasAlreadyBeenCompleted() {
		return "My collection is now complete! Thanks again.";
	}

	public String respondToQuestAcception() {
		return "If you help me to complete my collection, I will give you "
				+ "something very interesting and useful in exchange. Bye";
	}

	public String respondToQuestRefusal() {
		return "Well, maybe someone else will happen by and help me. Bye";
	}

	public String askForMissingItems(final List<String> missingItems) {
		return "There " + Grammar.isare(missingItems.size()) + " "
				+ Grammar.quantityplnoun(missingItems.size(), "weapon", "a")
				+ " still missing from my collection: "
				+ Grammar.enumerateCollection(missingItems)
				+ ". Do you have anything of that nature with you?";
	}
	public String firstAskForMissingItems(final List<String> missingItems) {
		return "There " + Grammar.isare(missingItems.size()) + " "
				+ Grammar.quantityplnoun(missingItems.size(), "weapon", "a")
				+ " missing from my collection: "
				+ Grammar.enumerateCollection(missingItems)
				+ ". Will you bring them to me?";
	}


	public String respondToPlayerSayingHeHasNoItems(final List<String> missingItems) {
		return "Let me know as soon as you find "
				+ Grammar.itthem(missingItems.size()) + ". Farewell.";
	}

	public String askForItemsAfterPlayerSaidHeHasItems() {
		return "What is it that you found?";
	}

	public String respondToItemBrought() {
		return "Thank you very much! Do you have anything else for me?";
	}

	public String respondToLastItemBrought() {
		return "At last, my collection is complete! Thank you very much; "
				+ "here, take this #'ice sword' in exchange!";
	}

	public void rewardPlayer(final Player player) {
		final Item iceSword = SingletonRepository.getEntityManager().getItem("ice sword");
		iceSword.setBoundTo(player.getName());
		player.equipOrPutOnGround(iceSword);
		player.addXP(5000);
	}

	public String respondToOfferOfNotExistingItem(final String itemName) {
		return "I may be old, but I'm not senile, and you clearly don't have "
				+ Grammar.a_noun(itemName)
				+ ". What do you really have for me?";
	}

	public String respondToOfferOfNotMissingItem() {
		return "I already have that one. Do you have any other weapon for me?";
	}

	public String respondToOfferOfNotNeededItem() {
		return "Oh, that is not an interesting weapon";
	}

	@Override
	public String getName() {
		return "WeaponsCollector";
	}
	
	// it can be a long quest so they can always start it before they can necessarily finish all
	@Override
	public int getMinLevel() {
		return 30;
	}
}
