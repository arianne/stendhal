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
package games.stendhal.server.core.rp.equipping.builder;

import static games.stendhal.common.constants.Actions.BASEITEM;
import static games.stendhal.common.constants.Actions.X;
import games.stendhal.common.EquipActionConsts;
import games.stendhal.server.core.rp.equipping.EquipmentActionData;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

/**
 * builds an EquipmentActionData object based on an action
 *
 * @author hendrik
 */
public class EquipmentActionBuilder {
	private final EquipmentActionData data;
	private final Player player;
	private final RPAction action;

	/**
	 * creates a new EquipmentActionBuilder
	 *
	 * @param player player
	 * @param action RPAction to build from
	 */
	public EquipmentActionBuilder(Player player, RPAction action) {
		data = new EquipmentActionData();
		this.player = player;
		this.action = action;
	}

	/**
	 * gets the built EquipmentActionData
	 *
	 * @return data
	 */
	public EquipmentActionData getData() {
		return data;
	}

	/**
	 * build
	 */
	public void build() {
		data.setPlayer(player);
		if (action.has("quantity")) {
			data.setQuantity(action.getInt("quantity"));
		}

		buildSource();
		buildTarget();

		// TODO: /drop, NPC quests, NPC sells, NPC buyes, player trade, Harold
		// TODO: check that the client sent the request for this zone
		// TODO: get name from sourceItems
	}

	private void buildSource() {
		if (action.has(EquipActionConsts.SOURCE_PATH)) {
			new BuildSourceFromPath().build(data, player, action);
		} else if (action.has(EquipActionConsts.BASE_OBJECT)) {
			new BuildSourceFromOldActionFormat().build(data, player, action);
		} else if (action.has(BASEITEM)) {
			new BuildSourceFromGround().build(data, player, action);
		} else {
			data.setErrorMessage("");
		}
	}

	private void buildTarget() {
		if (action.has(EquipActionConsts.TARGET_PATH)) {
			new BuildTargetFromPath().build(data, player, action);
		} else if (action.has(X)) {
			new BuildTargetFromGround().build(data, player, action);
		} else if (action.has(EquipActionConsts.TARGET_OBJECT)) {
			new BuildTargetFromOldActionFormat().build(data, player, action);
		} else {
			data.setErrorMessage("");
		}
	}

}
