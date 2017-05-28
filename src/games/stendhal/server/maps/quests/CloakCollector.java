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
import java.util.List;

import games.stendhal.common.ItemTools;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.parser.Expression;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;
import games.stendhal.server.maps.quests.logic.BringListOfItemsQuest;
import games.stendhal.server.maps.quests.logic.BringListOfItemsQuestLogic;

/**
 * QUEST: Cloak Collector
 * <p>
 * PARTICIPANTS: - Josephine, a young woman who live in Ados/Fado
 * <p>
 * STEPS:
 * <ul>
 * <li> Josephine asks you to bring her a cloak in every colour available on
 * the mainland
 * <li> You bring cloaks to Josephine
 * <li> Repeat until Josephine
 * received all cloaks. (Of course you can bring several cloaks at the same
 * time.)
 * <li> Josephine gives you a reward
 * </ul>
 * <p>
 * REWARD:
 * <ul>
 * <li> black cloak </li>
 * <li> 10,000 XP </li>
 * <li> 5 karma (+5 for accepting, -5 for rejecting) </li>
 * </ul>
 * <p>
 * REPETITIONS: - None.
 */
public class CloakCollector extends AbstractQuest implements BringListOfItemsQuest {

	private static final List<String> NEEDED_CLOAKS = Arrays.asList("cloak",
			"elf cloak", "dwarf cloak", "blue elf cloak", "stone cloak",
			"green dragon cloak", "bone dragon cloak", "lich cloak",
			"vampire cloak", "blue dragon cloak");

	private static final String QUEST_SLOT = "cloaks_collector";

	private BringListOfItemsQuestLogic bringItems;

	@Override
	public List<String> getHistory(final Player player) {
		return bringItems.getHistory(player);
	}

	private void setupAbstractQuest() {
		final BringListOfItemsQuest concreteQuest = this;
		bringItems = new BringListOfItemsQuestLogic(concreteQuest);
		bringItems.addToWorld();
	}

	@Override
	public void addToWorld() {
		step_1();
		setupAbstractQuest();
		fillQuestInfo(
				"Cloaks Collector",
				"Josephine wants cloaks in many colours.",
				false);
	}

	private void step_1() {
		final SpeakerNPC npc = npcs.get("Josephine");

		// player asks about an individual cloak before accepting the quest
		for(final String itemName : NEEDED_CLOAKS) {
			npc.add(ConversationStates.QUEST_OFFERED, itemName, null,
				ConversationStates.QUEST_OFFERED, null,
				new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
						Expression obj = sentence.getObject(0);
						if (obj!=null && !obj.getNormalized().equals(itemName)) {
							raiser.say("I don't know " + obj.getOriginal() + ". Can you name me another cloak please?");
						} else {
							final Item item = SingletonRepository.getEntityManager().getItem(itemName);
							StringBuilder stringBuilder = new StringBuilder();
							stringBuilder.append("You haven't seen one before? Well, it's a ");

							if (item == null) {
								stringBuilder.append(itemName);
							} else {
								stringBuilder.append(ItemTools.itemNameToDisplayName(item.getItemSubclass()));
							}

							stringBuilder.append(". So, will you find them all?");
							raiser.say(stringBuilder.toString());
						}
					}

					@Override
					public String toString() {
						return "describe item";
					}
			});
		}
	}

	@Override
	public List<String> getAdditionalTriggerPhraseForQuest() {
		return Arrays.asList("clothes");
	}

	@Override
	public SpeakerNPC getNPC() {
		return npcs.get("Josephine");
	}

	@Override
	public List<String> getNeededItems() {
		return NEEDED_CLOAKS;
	}

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public List<String> getTriggerPhraseToEnumerateMissingItems() {
		return Arrays.asList("cloaks");
	}

	@Override
	public double getKarmaDiffForQuestResponse() {
		return 5.0;
	}

	@Override
	public boolean shouldWelcomeAfterQuestIsCompleted() {
		return false;
	}

	@Override
	public String welcomeBeforeStartingQuest() {
		return "Hi there, gorgeous! I can see you like my pretty dress. I just love #clothes...";
	}

	@Override
	public String welcomeDuringActiveQuest() {
		return "Hello! Did you bring any #cloaks with you?";
	}

	@Override
	public String welcomeAfterQuestIsCompleted() {
		return "Hi again, lovely. The cloaks still look great. Thanks!";
	}

	@Override
	public String respondToQuest() {
		return "At the moment I'm obsessed with #cloaks! They come in so many colours. I want all the pretty ones!";
	}

	@Override
	public String respondToQuestAcception() {
		// player.addKarma(5.0);
		return "Brilliant! I'm so excited!";
	}

	@Override
	public String respondToQuestAfterItHasAlreadyBeenCompleted() {
		return "Hi again, lovely. The cloaks still look great. Thanks!";
	}

	@Override
	public String respondToQuestRefusal() {
		// player.addKarma(-5.0);
		return "Oh ... you're not very friendly. Bye then.";
	}

	@Override
	public String askForItemsAfterPlayerSaidHeHasItems() {
		return "Great! What #cloaks did you bring?";
	}

	@Override
	public String firstAskForMissingItems(final List<String> missingItems) {
		return "I want " + Grammar.quantityplnoun(missingItems.size(), "cloak", "a")
				+ ". That's " + Grammar.enumerateCollection(missingItems)
				+ ". Will you find them?";
	}

	@Override
	public String askForMissingItems(final List<String> missingItems) {
		return "I want " + Grammar.quantityplnoun(missingItems.size(), "cloak", "a")
				+ ". That's " + Grammar.enumerateCollection(missingItems)
				+ ". Did you bring any?";
	}

	@Override
	public String respondToItemBrought() {
		return "Wow, thank you! What else did you bring?";
	}

	@Override
	public String respondToLastItemBrought() {
		return "Oh, they look so beautiful all together, thank you. Please take this black cloak in return, I don't like the colour.";
	}

	@Override
	public String respondToOfferOfNotExistingItem(final String itemName) {
		return "Oh, I'm disappointed. You don't really have " + Grammar.a_noun(itemName) + " with you.";
	}

	@Override
	public String respondToOfferOfNotMissingItem() {
		return "You've already brought that cloak to me.";
	}

	@Override
	public String respondToOfferOfNotNeededItem() {
		return "Sorry, that's not a cloak I asked you for.";
	}

	@Override
	public String respondToPlayerSayingHeHasNoItems(final List<String> missingItems) {
		return "Okay then. Come back later.";
	}

	@Override
	public void rewardPlayer(final Player player) {
		final Item blackcloak = SingletonRepository.getEntityManager().getItem("black cloak");
		blackcloak.setBoundTo(player.getName());
		player.equipOrPutOnGround(blackcloak);
		player.addKarma(5.0);
		player.addXP(10000);
	}

	@Override
	public String getName() {
		return "CloakCollector";
	}

	// You can start collecting just with a simple cloak which you can buy, but maybe not a good idea to send to Fado too early.
	@Override
	public int getMinLevel() {
		return 15;
	}

	@Override
	public String getRegion() {
		return Region.FADO_CITY;
	}

	@Override
	public String getNPCName() {
		return "Josephine";
	}
}
