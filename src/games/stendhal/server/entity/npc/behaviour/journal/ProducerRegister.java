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
package games.stendhal.server.entity.npc.behaviour.journal;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.behaviour.impl.MultiProducerBehaviour;
import games.stendhal.server.entity.npc.behaviour.impl.ProducerBehaviour;
import games.stendhal.server.entity.player.Player;
import marauroa.common.Pair;

public class ProducerRegister {

	/** The singleton instance. */
	private static ProducerRegister instance;

	private final List<Pair<String, ProducerBehaviour>> producers;
	private final List<Pair<String, MultiProducerBehaviour>> multiproducers;


	/**
	 * Singleton access method.
	 *
	 * @return
	 *     The static instance.
	 */
	public static ProducerRegister get() {
		if (instance == null) {
			instance = new ProducerRegister();
		}

		return instance;
	}

	protected ProducerRegister() {
		instance = this;
		producers  = new LinkedList<Pair<String, ProducerBehaviour>>();
		multiproducers  = new LinkedList<Pair<String, MultiProducerBehaviour>>();
	}

	/**
	 * Adds an NPC to the NPCList. Does nothing if an NPC with the same name
	 * already exists. This makes sure that each NPC can be uniquely identified
	 * by his/her name.
	 *
	 * @param npcName
	 *            The NPC that should be added
	 * @param behaviour
	 *            The ProducerBehaviour of that NPC
	 */
	public void add(final String npcName, final ProducerBehaviour behaviour) {
		// insert lower case names ?
		// final String name = npcName.toLowerCase();
		Pair<String, ProducerBehaviour> pair = new Pair<String, ProducerBehaviour>(npcName, behaviour);
		producers.add(pair);
	}

	public void add(final String npcName, final MultiProducerBehaviour behaviour) {
		// insert lower case names ?
		// final String name = npcName.toLowerCase();
		Pair<String, MultiProducerBehaviour> pair = new Pair<String, MultiProducerBehaviour>(npcName, behaviour);
		multiproducers.add(pair);
	}

	public List<Pair<String, ProducerBehaviour>> getProducers() {
		return producers;
	}

	public List<Pair<String, MultiProducerBehaviour>> getMultiProducers() {
		return multiproducers;
	}

	public String listWorkingProducers(final Player player) {
		final StringBuilder sb = new StringBuilder("");

		// Open orders - do not state if ready to collect or not, yet
		for (final Pair<String, ProducerBehaviour> producer : producers) {
			String npcName = producer.first();
			ProducerBehaviour behaviour = producer.second();
			String questSlot =  behaviour.getQuestSlot();
			String activity =  behaviour.getProductionActivity();
			String product =  behaviour.getProductName();
			if (player.hasQuest(questSlot) && !player.isQuestCompleted(questSlot)) {
				int amount = behaviour.getNumberOfProductItems(player);
				if (behaviour.isOrderReady(player)) {
					// put all completed orders first - player wants to collect these!
					sb.insert(0,"\n" + npcName + " has finished " + Grammar.gerundForm(activity)
							+ " your " + Grammar.plnoun(amount,product) + ".");
				} else {
					String timeleft = behaviour.getApproximateRemainingTime(player);
					// put all ongoing orders last
					sb.append("\n" + npcName + " is " + Grammar.gerundForm(activity)
							+ " " + Grammar.quantityplnoun(amount, product, "a") + " and will be ready in " + timeleft + ".");
				}
			}
		}
		for (final Pair<String, MultiProducerBehaviour> producer : multiproducers) {
			String npcName = producer.first();
			MultiProducerBehaviour behaviour = producer.second();
			String questSlot =  behaviour.getQuestSlot();
			String activity =  behaviour.getProductionActivity();
            // Retrieve all production details from the questSlot
			if (player.hasQuest(questSlot) && !player.isQuestCompleted(questSlot)) {
                final String orderString = player.getQuest(questSlot);
                final String[] order = orderString.split(";");
                //final long orderTime = Long.parseLong(order[2]);
                final int amount = Integer.parseInt(order[0]);
                final String product = order[1];
				if (behaviour.isOrderReady(player)) {
					// put all completed orders first - player wants to collect these!
					sb.insert(0,"\n" + npcName + " has finished " + Grammar.gerundForm(activity)
							+ " your " + Grammar.plnoun(amount,product) + ".");
				} else {
					String timeleft = behaviour.getApproximateRemainingTime(player);
					// put all ongoing orders last
					sb.append("\n" + npcName + " is " + Grammar.gerundForm(activity)
							+ " " + Grammar.quantityplnoun(amount, product, "a") + " and will be ready in " + timeleft + ".");
				}
			}
		}
		if (!"".equals(sb.toString())) {
			sb.insert(0,"\r\nOrders: ");
		} else {
			sb.append("You have no ongoing or uncollected orders.");
		}
		return sb.toString();
	}

	/**
	 * gets description of the production
	 *
	 * @param player player to get the description for
	 * @param npcName name of quest
	 * @return details
	 */
	public String getProductionDescription(final Player player, final String npcName) {
		for (final Pair<String, ProducerBehaviour> producer : producers) {
			if(npcName.equals(producer.first())) {
				ProducerBehaviour behaviour = producer.second();
				String activity =  behaviour.getProductionActivity();
				String product =  behaviour.getProductName();
				return npcName + " " + Grammar.plural(activity) + " " + Grammar.plural(product) + ".";
			}
		}
		for (final Pair<String, MultiProducerBehaviour> producer : multiproducers) {
			if(npcName.equals(producer.first())) {
				MultiProducerBehaviour behaviour = producer.second();
				String activity =  behaviour.getProductionActivity();
				HashSet<String> products =  behaviour.getProductsNames();
				return npcName + " " + Grammar.plural(activity) + " " + Grammar.enumerateCollection(products) + ".";
			}
		}
		return "";
	}

	/**
	 * gets description of the produced item
	 *
	 * Note: if more than one NPC makes the item, just the details of the first NPC in the list who makes it are returned
	 *
	 * @param itemName produced item
	 * @return details about the produced item
	 */
	public String getProducedItemDetails(final String itemName) {
		for (final Pair<String, ProducerBehaviour> producer : producers) {
			ProducerBehaviour behaviour = producer.second();
			String product =  behaviour.getProductName();

			if(itemName.equals(product)) {
				String npcName = producer.first();
				String activity =  behaviour.getProductionActivity();
				String resources = behaviour.getRequiredResourceNames(1);
				return  npcName + " " + Grammar.plural(activity) + " " + Grammar.plural(product) + ", which need " + resources + " each.";
			}
		}
		for (final Pair<String, MultiProducerBehaviour> producer : multiproducers) {
			MultiProducerBehaviour behaviour = producer.second();
			for (String product : behaviour.getProductsNames()) {
				if(itemName.equals(product)) {
                    String npcName = producer.first();
                    String activity =  behaviour.getProductionActivity();
                    String resources = behaviour.getRequiredResourceNames(product, 1);
                    return npcName + " " + Grammar.plural(activity) + " " + Grammar.plural(product) + ", which need " + resources + " each.";
                }
			}
		}
		return "";
	}

	/**
	 * gets names of all items produced, which are of the given item class (i.e. food, drink)
	 *
	 * @param clazz Item class to check
	 * @return list of item names
	 */
	public List<String> getProducedItemNames(final String clazz) {
		List<String> res = new LinkedList<String>();
		for (final Pair<String, ProducerBehaviour> producer : producers) {
			final ProducerBehaviour behaviour = producer.second();
			final String product =  behaviour.getProductName();
			final Item item = SingletonRepository.getEntityManager().getItem(product);
			if (item.isOfClass(clazz)) {
				res.add(product);
			}
		}
		for (final Pair<String, MultiProducerBehaviour> producer : multiproducers) {
			final MultiProducerBehaviour behaviour = producer.second();
			for (String product : behaviour.getProductsNames()) {
				final Item item = SingletonRepository.getEntityManager().getItem(product);
                if (item.isOfClass(clazz)) {
                    res.add(product);
                }
			}
		}
		return res;
	}

	/**
     * gets the names of all the NPCs to whom the player has asked to produce something
     *
	 * @param player player to get the details for
	 * @return NPC names
     */
	public List<String> getWorkingProducerNames(final Player player) {
		List<String> res = new LinkedList<String>();
		for (final Pair<String, ProducerBehaviour> producer : producers) {
			String npcName = producer.first();
			ProducerBehaviour behaviour = producer.second();
			String questSlot =  behaviour.getQuestSlot();
			if (player.hasQuest(questSlot) && !player.isQuestCompleted(questSlot)) {
				if (behaviour.isOrderReady(player)) {
					// put all completed orders first - player wants to collect these!
					res.add(0, npcName);
				} else {
					res.add(npcName);
				}
			}
		}
		for (final Pair<String, MultiProducerBehaviour> producer : multiproducers) {
			String npcName = producer.first();
			MultiProducerBehaviour behaviour = producer.second();
			String questSlot =  behaviour.getQuestSlot();
			if (player.hasQuest(questSlot) && !player.isQuestCompleted(questSlot)) {
				if (behaviour.isOrderReady(player)) {
					// put all completed orders first - player wants to collect these!
					res.add(0, npcName);
				} else {
					res.add(npcName);
				}
			}
		}
		return res;
	}

	/**
	 * gets details on the progress of the production
	 *
	 * @param player player to get the details for
	 * @param npcName name of quest
	 * @return details
	 */
	public List<String> getProductionDetails(final Player player, final String npcName) {
		List<String> res = new LinkedList<String>();
		for (final Pair<String, ProducerBehaviour> producer : producers) {
			if(npcName.equals(producer.first())) {
				ProducerBehaviour behaviour = producer.second();
				String questSlot =  behaviour.getQuestSlot();
				String activity =  behaviour.getProductionActivity();
				String product =  behaviour.getProductName();
				if (player.hasQuest(questSlot) && !player.isQuestCompleted(questSlot)) {
					int amount = behaviour.getNumberOfProductItems(player);
					if (behaviour.isOrderReady(player)) {
						// put all completed orders first - player wants to collect these!
						res.add(npcName + " has finished " + Grammar.gerundForm(activity)
							+ " your " + Grammar.plnoun(amount,product) + ".");
					} else {
						String timeleft = behaviour.getApproximateRemainingTime(player);
						// put all ongoing orders last
						res.add("\n" + npcName + " is " + Grammar.gerundForm(activity)
							+ " " + Grammar.quantityplnoun(amount, product, "a") + " and will be ready in " + timeleft + ".");
					}
				}
			}
		}
		for (final Pair<String, MultiProducerBehaviour> producer : multiproducers) {
			if(npcName.equals(producer.first())) {
                MultiProducerBehaviour behaviour = producer.second();
                String questSlot =  behaviour.getQuestSlot();
                String activity =  behaviour.getProductionActivity();
                // Retrieve all production details from the questSlot
                if (player.hasQuest(questSlot) && !player.isQuestCompleted(questSlot)) {
                    final String orderString = player.getQuest(questSlot);
                    final String[] order = orderString.split(";");
                    //final long orderTime = Long.parseLong(order[2]);
                    final int amount = Integer.parseInt(order[0]);
                    final String product = order[1];
                    if (behaviour.isOrderReady(player)) {
                        // put all completed orders first - player wants to collect these!
                        res.add(npcName + " has finished " + Grammar.gerundForm(activity)
                            + " your " + Grammar.plnoun(amount,product) + ".");
                    } else {
                        String timeleft = behaviour.getApproximateRemainingTime(player);
                        // put all ongoing orders last
						res.add("\n" + npcName + " is " + Grammar.gerundForm(activity)
							+ " " + Grammar.quantityplnoun(amount, product, "a") + " and will be ready in " + timeleft + ".");
                    }
                }
			}
		}
		return res;
	}

}
