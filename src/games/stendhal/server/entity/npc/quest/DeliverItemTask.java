/***************************************************************************
 *                   (C) Copyright 2003-2023 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.npc.quest;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.common.Rand;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.SetQuestToTimeStampAction;
import games.stendhal.server.entity.npc.condition.OutfitCompatibleWithClothesCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.player.Player;


public class DeliverItemTask extends QuestTaskBuilder {
	private final static Outfit UNIFORM = new Outfit(null, Integer.valueOf(990), null, null, null, null, null, null, null);

	private static final String QUEST_SLOT = "pizza_delivery";

	private Map<String, DeliverItemOrder> orders = new HashMap<>();

	/*
	@Override
	public List<String> getHistory(final Player player) {
		final String questState = player.getQuest(QUEST_SLOT);
		res.add("I met Leander and agreed to help with pizza delivery.");
		if (!"done".equals(questState)) {
			final String[] questData = questState.split(";");
			final String customerName = questData[0];
			final CustomerData customerData = customerDB.get(customerName);
			res.add("Leander gave me a " + customerData.flavor + " for " + customerName + ".");
			res.add("Leander told me: \"" + customerData.npcDescription + "\"");
			if (!isDeliveryTooLate(player)) {
				res.add("If I hurry I might still get there with the pizza hot.");
			} else {
				res.add("The pizza has already gone cold.");
			}
		}
		return res;
	}
*/


	/**
	 * Get a list of customers appropriate for a player
	 *
	 * @param player the player doing the quest
	 * @return list of customer data
	 */
	private List<String> getAllowedCustomers(Player player) {
		List<String> allowed = new LinkedList<String>();
		int level = player.getLevel();
		for (Map.Entry<String, DeliverItemOrder> entry : orders.entrySet()) {
			if (level >= entry.getValue().getLevel()) {
				allowed.add(entry.getKey());
			}
		}
		return allowed;
	}

	/**
	 * Checks whether the player has failed to fulfil his current delivery job
	 * in time.
	 *
	 * @param player
	 *            The player.
	 * @return true if the player is too late. false if the player still has
	 *         time, or if he doesn't have a delivery to do currently.
	 */
	boolean isDeliveryTooLate(final Player player) {
		if (player.hasQuest(QUEST_SLOT) && !player.isQuestCompleted(QUEST_SLOT)) {
			final String[] questData = player.getQuest(QUEST_SLOT).split(";");
			final String customerName = questData[0];
			final DeliverItemOrder customerData = orders.get(customerName);
			final long bakeTime = Long.parseLong(questData[1]);
			final long expectedTimeOfDelivery = bakeTime
				+ (long) 60 * 1000 * customerData.getExpectedMinutes();
			if (System.currentTimeMillis() > expectedTimeOfDelivery) {
				return true;
			}
		}
		return false;

	}

	/** Takes away the player's uniform, if the he is wearing it.
	 * @param player to remove uniform from*/
	void putOffUniform(final Player player) {
		if (UNIFORM.isPartOf(player.getOutfit())) {
			player.returnToOriginalOutfit();
		}
	}

	private void prepareBaker() {
		SpeakerNPC npc = NPCList.get().get("Leander");
		for (final String name : orders.keySet()) {
			final DeliverItemOrder data = orders.get(name);
			npc.addReply(name, data.getNpcDescription());
		}
	}

	@Override
	void simulate(QuestSimulator simulator) {
		// TODO Auto-generated method stub
		
	}

	@Override
	ChatAction buildStartQuestAction(String questSlot) {
		prepareBaker(); // TODO
		return new ChatAction() {
			@Override
			public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
				final String name = Rand.rand(getAllowedCustomers(player));
				final DeliverItemOrder data = orders.get(name);

				final Item pizza = SingletonRepository.getEntityManager().getItem("pizza");
				pizza.setInfoString(data.getFlavor());
				pizza.setDescription("You see a " + data.getFlavor() + ".");
				pizza.setBoundTo(name);

				if (player.equipToInventoryOnly(pizza)) {
					npc.say("You must bring this "
						+ data.getFlavor()
						+ " to "
						+ Grammar.quoteHash("#" + name)
						+ " within "
						+ Grammar.quantityplnoun(data.getExpectedMinutes(), "minute", "one")
						+ ". Say \"pizza\" so that "
						+ name
						+ " knows that I sent you. Oh, and please wear this uniform on your way and don't drop this " + data.getFlavor() + " on the ground! Our customers want it fresh.");
					player.setOutfit(UNIFORM, true);
					player.setQuest(QUEST_SLOT, 0, name);
					new SetQuestToTimeStampAction(questSlot, 1).fire(player, null, npc);
				} else {
					npc.say("Come back when you have space to carry the pizza!");
				}
			}
		};
	}

	@Override
	ChatCondition buildQuestPreCondition(String questSlot) {
		return new OutfitCompatibleWithClothesCondition();
	}


	@Override
	ChatAction buildRejectQuestAction(String questSlot) {
		return new ChatAction() {
			@Override
			public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
				putOffUniform(player);
			}
		};
	}

	@Override
	ChatAction buildRemindQuestAction(String questSlot) {
		return new ChatAction() {
			@Override
			public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					final String[] questData = player.getQuest(QUEST_SLOT)
							.split(";");
					final String customerName = questData[0];
					if (customerName.equals("rejected")) {
						buildStartQuestAction(questSlot).fire(player, sentence, npc);
						return;
					}
					if (isDeliveryTooLate(player)) {
						// If the player still carries any pizza due for an NPC,
						// take it away because the baker is angry,
						// and because the player probably won't
						// deliver it anymore anyway.
						for (final Item pizza : player.getAllEquipped("pizza")) {
							if (pizza.getInfoString()!=null) {
								player.drop(pizza);
							}
						}
						npc.say("I see you failed to deliver the pizza to "
							+ customerName
							+ " in time. Are you sure you will be more reliable this time?");
						npc.setCurrentState(ConversationStates.QUEST_OFFERED);
					} else {
						npc.say("You still have to deliver a pizza to "
								+ customerName + ", and hurry!");
						npc.setCurrentState(ConversationStates.ATTENDING);
					}
			}
		};
	}

	@Override
	ChatCondition buildQuestCompletedCondition(String questSlot) {
		return new QuestCompletedCondition(questSlot);
	}

	@Override
	ChatAction buildQuestCompleteAction(String questSlot) {
		// TODO Auto-generated method stub
		return null;
	}

	public DeliverItemOrder order() {
		return new DeliverItemOrder(this);
	}

	Map<String, DeliverItemOrder> getOrders() {
		return this.orders;
	}

	String getItemName() {
		return "pizza";
	}
}
