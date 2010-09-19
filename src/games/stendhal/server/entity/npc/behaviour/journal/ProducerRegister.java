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

import games.stendhal.common.Grammar;
import games.stendhal.server.entity.npc.behaviour.impl.ProducerBehaviour;
import games.stendhal.server.entity.player.Player;

import java.util.LinkedList;
import java.util.List;

import marauroa.common.Pair;

public class ProducerRegister {
	
	private static ProducerRegister instance;
	
	private final List<Pair<String, ProducerBehaviour>> producers;
	
	public static ProducerRegister get() {
		if (instance == null) {
			new ProducerRegister();
		}
		return instance;
	}

	protected ProducerRegister() {
		instance = this;
		producers  = new LinkedList<Pair<String, ProducerBehaviour>>();
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
	
	public List<Pair<String, ProducerBehaviour>> getProducers() {
		return producers;
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

		if (!"".equals(sb.toString())) {
			sb.insert(0,"\r\nOrders: ");
		} else {
			sb.append("You have no ongoing or uncollected orders.");
		}
		return sb.toString();
	}
	
}


