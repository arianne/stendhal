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

import java.util.LinkedList;
import java.util.List;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.impl.BuyerBehaviour;
import games.stendhal.server.entity.npc.behaviour.impl.MerchantBehaviour;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;
import marauroa.common.Pair;

public class MerchantsRegister {

	/** The singleton instance. */
	private static MerchantsRegister instance;

	private final List<Pair<String, BuyerBehaviour>> buyers;
	private final List<Pair<String, SellerBehaviour>> sellers;


	/**
	 * Singleton access method.
	 *
	 * @return
	 *     The static instance.
	 */
	public static MerchantsRegister get() {
		if (instance == null) {
			instance = new MerchantsRegister();
		}

		return instance;
	}

	protected MerchantsRegister() {
		instance = this;
		buyers  = new LinkedList<Pair<String, BuyerBehaviour>>();
		sellers  = new LinkedList<Pair<String, SellerBehaviour>>();
	}

	/**
	 * Adds an NPC to the NPCList. Does nothing if an NPC with the same name
	 * already exists. This makes sure that each NPC can be uniquely identified
	 * by his/her name.
	 *
	 * @param npc
	 *            The NPC that should be added
	 * @param behaviour
	 *            The MerchantBehaviour of that NPC
	 */
	public void add(final SpeakerNPC npc, final MerchantBehaviour behaviour) {
		final String npcName = npc.getName();

		npc.put("job_merchant", 0);

		if (behaviour instanceof BuyerBehaviour) {
			Pair<String, BuyerBehaviour> pair = new Pair<String, BuyerBehaviour>(npcName, (BuyerBehaviour) behaviour);
			buyers.add(pair);
		}
		else {
			Pair<String, SellerBehaviour> pair = new Pair<String, SellerBehaviour>(npcName, (SellerBehaviour) behaviour);
			sellers.add(pair);
		}
	}

	public List<Pair<String, BuyerBehaviour>> getBuyers() {
		return buyers;
	}

	public List<Pair<String, SellerBehaviour>> getSellers() {
		return sellers;
	}

	/**
	 * Retrieves list of NPC names registered as buyers.
	 *
	 * @return
	 *     Buyers names.
	 */
	public List<String> getBuyersNames() {
		final List<String> names = new LinkedList<>();

		for (final Pair<String, BuyerBehaviour> p: buyers) {
			names.add(p.first());
		}

		return names;
	}

	/**
	 * Retrieves list of NPC names registered as sellers.
	 *
	 * @return
	 *     Sellers names.
	 */
	public List<String> getSellersNames() {
		final List<String> names = new LinkedList<>();

		for (final Pair<String, SellerBehaviour> p: sellers) {
			names.add(p.first());
		}

		return names;
	}
}
