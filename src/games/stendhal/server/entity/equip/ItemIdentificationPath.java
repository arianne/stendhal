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
package games.stendhal.server.entity.equip;

import games.stendhal.common.EquipActionConsts;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.slot.EntitySlot;
import games.stendhal.server.entity.slot.GroundSlot;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

import org.apache.log4j.Logger;

/**
 * Identifies an item in a slot, in a nested slot or on the ground.
 *
 * <p><b>Important:</b> There is no validation if the item/slot may be accessed.
 *
 * @author hendrik
 */
public class ItemIdentificationPath {
	private static Logger logger = Logger.getLogger(ItemIdentificationPath.class);

	private StendhalRPZone zone;
	private EntitySlot slot;
	private Item item;
	private int quantity = -1;


	/**
	 * creates a new ItemIdentificationPath
	 *
	 * @param action RPAction which has baseitem, baseitem+baseslot+baseobject or itempath
	 */
	public ItemIdentificationPath(RPAction action) {
		this(action, true);
	}

	/**
	 * creates a new ItemIdentificationPath
	 *
	 * @param source true, if the RPAction is for the base/source, false if it is for the target
	 * @param action RPAction which has baseitem, baseitem+baseslot+baseobject or itempath, if source=true. targetobject+targetslot or x+y if source=false
	 */
	public ItemIdentificationPath(RPAction action, boolean source) {
		String zoneName = action.get("zoneid");
		zone = SingletonRepository.getRPWorld().getZone(zoneName);
		if (source) {
			fillBySource(action);
		} else {
			fillByTarget(action);
		}
	}

	/**
	 * gets the Slot
	 *
	 * @return EntitySlot
	 */
	public EntitySlot getSlot() {
		return slot;
	}

	/**
	 * gets the item
	 *
	 * @return Item, or <code>null</code>, if not specified (target slot)
	 */
	public Item getItem() {
		return item;
	}

	/**
	 * gets the desired quantity
	 *
	 * @return quantity, or <code>-1/<code> if not specified
	 */
	public int getQuantity() {
		return quantity;
	}

	private void fillBySource(RPAction action) {
		// TODO
	}

	private void fillByTarget(RPAction action) {
		if (action.has(EquipActionConsts.TARGET_OBJECT)
				&& action.has(EquipActionConsts.TARGET_SLOT)) {

			fillBySlotTarget(action);

		} else if (action.has(EquipActionConsts.GROUND_X) && action.has(EquipActionConsts.GROUND_Y)) {
			// dropped to the ground
			fillByGroundTarget(action);

		} else {
			// TODO: zone, (objectid, slot)*, itemid
			logger.warn("Target identification missing in " + action);
		}
	}

	private void fillBySlotTarget(RPAction action) {
		// get base entity
		int objectId = action.getInt(EquipActionConsts.TARGET_OBJECT);
		RPObject.ID id = new RPObject.ID(objectId, zone.getID());
		Entity parent = (Entity) zone.get(id);
		if (parent == null) {
			logger.warn("cannot find target entity for action " + action);
			return;
		}

		// get slot
		String slotName = action.get(EquipActionConsts.TARGET_SLOT);
		if (!parent.hasSlot(slotName)) {
			logger.warn("Parent doesn't have slot: " + action);
			return;
		}
		slot = (EntitySlot) parent.getSlot(slotName);
	}

	/**
	 * fill by coordinates on the ground
	 *
	 * @param action RPAction
	 */
	private void fillByGroundTarget(RPAction action) {
		int x = action.getInt(EquipActionConsts.GROUND_X);
		int y = action.getInt(EquipActionConsts.GROUND_Y);
		slot = new GroundSlot(zone, x, y);
	}
}
