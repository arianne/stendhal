/***************************************************************************
 *                    (C) Copyright 2003-2023 - Stendhal                   *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.engine.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.impl.BuyerBehaviour;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;
import games.stendhal.server.entity.npc.behaviour.impl.OutfitChangerBehaviour;
import marauroa.common.Pair;
import marauroa.server.db.DBTransaction;

/**
 * database base access for the NPC dump used on the website
 *
 * @author hendrik
 */
public class StendhalNPCDAO {
	private static Logger logger = Logger.getLogger(StendhalNPCDAO.class);


	/**
	 * Dumps the properties of the specified SpeakerNPC into the prepared statement as an operation in
	 * a batch.
	 *
	 * @param stmt
	 *   PreparedStatement in batch mode.
	 * @param npc
	 *   SpeakerNPC.
	 * @param shopsInfo
	 *   What the NPC buys & sells.
	 * @throws
	 *   SQLException in case a database error is thrown.
	 */
	private void dumpNPC(PreparedStatement stmt, SpeakerNPC npc, Map<String, String> shopsInfo) throws SQLException {
		stmt.setString(1, npc.getName());
		stmt.setString(2, npc.getTitle());
		stmt.setString(3, npc.get("class"));
		stmt.setString(4, getOutfit(npc));
		stmt.setString(5, getOutfitLayer(npc));
		stmt.setInt(6, npc.getHP());
		stmt.setInt(7, npc.getBaseHP());
		stmt.setString(8, npc.getZone().getName());
		stmt.setInt(9, npc.getX());
		stmt.setInt(10, npc.getY());
		stmt.setInt(11, npc.getLevel());
		stmt.setString(12, npc.getDescription());
		stmt.setString(13, npc.getJob());
		stmt.setString(14, npc.getAlternativeImage());
		stmt.setString(15, npc.get("cloned"));
		stmt.setString(16, shopsInfo.get("buys"));
		stmt.setString(17, shopsInfo.get("sells"));
		stmt.setString(18, shopsInfo.get("sells_outfit"));
		stmt.addBatch();
	}

	/**
	 * gets the outfit code as string
	 *
	 * @param npc SpeakerNPC object
	 * @return outfit code as string or null incase there is not outfit specified
	 */
	private String getOutfit(SpeakerNPC npc) {
		String outfit = null;
		if (npc.getOutfit() != null) {
			outfit = Integer.toString(npc.getOutfit().getCode());
		}
		return outfit;
	}

	/**
	 * gets the outfit code as string
	 *
	 * @param npc SpeakerNPC object
	 * @return outfit code as string or null incase there is not outfit specified
	 */
	private String getOutfitLayer(SpeakerNPC npc) {
		String outfit = null;
		if (npc.getOutfit() != null) {
			outfit = (npc.getOutfit().getData(npc.getOutfitColors()));
		}
		return outfit;
	}

	/**
	 * dumps all NPCs
	 *
	 * @param transaction DBTransaction
	 * @throws SQLException in case of an database error
	 */
	public void dumpNPCs(DBTransaction transaction) throws SQLException {
		long start = System.currentTimeMillis();
		transaction.execute("DELETE FROM npcs", null);
		PreparedStatement stmt = transaction.prepareStatement("INSERT INTO npcs " +
			"(name, title, class, outfit, outfit_layers, hp, base_hp, zone, x, y, " +
			"level, description, job, image, cloned, buys, sells, sells_outfit) " +
			"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);", null);

		final List<Pair<String, BuyerBehaviour>> buyers =
				SingletonRepository.getMerchantsRegister().getBuyers();
		final List<Pair<String, SellerBehaviour>> sellers =
				SingletonRepository.getMerchantsRegister().getSellers();
		final List<Pair<String, OutfitChangerBehaviour>> outfitters =
				SingletonRepository.getServicersRegister().getOutfitChangers();

		for (SpeakerNPC npc : SingletonRepository.getNPCList()) {
			dumpNPC(stmt, npc, getShopsInfo(npc.getName(), buyers, sellers, outfitters));
		}
		stmt.executeBatch();
		logger.debug("Completed dumping of NPCs in " + (System.currentTimeMillis() - start) + " milliseconds.");
	}

	/**
	 * Compiles information about what the NPC buys & sells.
	 *
	 * @param npcName
	 *   NPC name.
	 * @param buyers
	 *   List of buyer NPCs.
	 * @param sellers
	 *   List of seller NPCs.
	 * @param outfitters
	 *   List of outfit seller/lender NPCs.
	 */
	private Map<String, String> getShopsInfo(final String npcName,
			final List<Pair<String, BuyerBehaviour>> buyers,
			final List<Pair<String, SellerBehaviour>> sellers,
			final List<Pair<String, OutfitChangerBehaviour>> outfitters) {
		final Map<String, String> shopsInfo = new HashMap<>();

		BuyerBehaviour buyerBehaviour = null;
		SellerBehaviour sellerBehaviour = null;
		OutfitChangerBehaviour outfitterBehaviour = null;
		for (final Pair p: buyers) {
			if (npcName.equals(p.first())) {
				buyerBehaviour = (BuyerBehaviour) p.second();
				break;
			}
		}
		for (final Pair p: sellers) {
			if (npcName.equals(p.first())) {
				sellerBehaviour = (SellerBehaviour) p.second();
				break;
			}
		}
		for (final Pair p: outfitters) {
			if (npcName.equals(p.first())) {
				outfitterBehaviour = (OutfitChangerBehaviour) p.second();
				break;
			}
		}

		String buys = "";
		String sells = "";
		String sells_outfit = "";
		if (buyerBehaviour != null) {
			for (final String itemName: buyerBehaviour.dealtItems()) {
				if (!"".equals(buys)) {
					buys += ";";
				}
				buys += itemName + ":" + String.valueOf(buyerBehaviour.getUnitPrice(itemName));
			}
		}
		if (sellerBehaviour != null) {
			for (final String itemName: sellerBehaviour.dealtItems()) {
				if (!"".equals(sells)) {
					sells += ";";
				}
				sells += itemName + ":" + String.valueOf(sellerBehaviour.getUnitPrice(itemName));
			}
		}
		if (outfitterBehaviour != null) {
			for (final String outfitName: outfitterBehaviour.dealtItems()) {
				if (!"".equals(sells_outfit)) {
					sells_outfit += ";";
				}
				sells_outfit += outfitName + ":"
						+ String.valueOf(outfitterBehaviour.getUnitPrice(outfitName));
			}
		}

		shopsInfo.put("buys", buys);
		shopsInfo.put("sells", sells);
		shopsInfo.put("sells_outfit", sells_outfit);
		return shopsInfo;
	}
}
