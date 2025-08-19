/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.actions;

import org.apache.log4j.Logger;

import games.stendhal.server.entity.npc.MerchantNPC;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.shop.ShopType;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.ShopInventoryEvent;
import marauroa.common.game.RPAction;


/**
 * Action for requesting an NPC's shop inventory.
 */
public class ShopInventoryAction implements ActionListener {

	private static Logger logger = Logger.getLogger(ShopInventoryAction.class);


	@Override
	public void onAction(final Player player, final RPAction action) {
		if (!action.has("npc")) {
			logger.error("NPC name must be specified");
			return;
		}
		if (!action.has("type")) {
			logger.error("Shop type must be specified");
			return;
		}

		final String typeName = action.get("type");
		final ShopType type = ShopType.fromString(typeName);
		if (type == null) {
			logger.error("Unrecognized shop type \"" + typeName + "\"");
			return;
		}

		final String npcName = action.get("npc");
		final SpeakerNPC npc = NPCList.get().get(npcName);
		if (npc == null || !(npc instanceof MerchantNPC)) {
			logger.error("Unrecognized merchant NPC \"" + npcName + "\"");
			return;
		}

		player.addEvent(new ShopInventoryEvent((MerchantNPC) npc, type));
		player.notifyWorldAboutChanges();
	}
}
