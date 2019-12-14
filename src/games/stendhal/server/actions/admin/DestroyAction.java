/***************************************************************************
 *                   (C) Copyright 2003-2016 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.actions.admin;

import static games.stendhal.common.constants.Actions.NAME;
import static games.stendhal.common.constants.Actions.TARGET;

import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.ItemLogger;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.Blood;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.SlotActivatedItem;
import games.stendhal.server.entity.mapstuff.portal.Portal;
import games.stendhal.server.entity.mapstuff.spawner.FlowerGrower;
import games.stendhal.server.entity.npc.PassiveNPC;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

class DestroyAction extends AdministrationAction {
	public static void register() {
		CommandCenter.register("destroy", new DestroyAction(), 700);

	}

	@Override
	public void perform(final Player player, final RPAction action) {

		final Entity inspected = getTarget(player, action);

		if (inspected == null) {
			final String text = "Entity not found";

			player.sendPrivateText(text);
			return;
		}

		String clazz = inspected.getRPClass().getName();
		String name = "";

		if (inspected.has(NAME)) {
			name = inspected.get(NAME);
		}

		if (inspected.isContained()) {
			RPObject slot = inspected.getContainer();

			new GameEvent(player.getName(), "removed", name + " " + clazz, slot.getID().toString(), Integer.toString(inspected.getX()), Integer.toString(inspected.getY())).raise();
			// items should be added to itemlog as well, to help tracing problems
			if (inspected instanceof Item) {
				//				String slotName = null;
				//				if (inspected.getContainerSlot() != null) {
				//					slotName = inspected.getContainerSlot().getName();
				//				}
				//				String quantity = inspected.get("quantity");
				//				if (quantity == null) {
				//					quantity = "1";
				//				}
				new ItemLogger().destroy(player, inspected.getContainerSlot(), inspected, "admin");
			}

			// disable effects of slot activated items
			if (inspected instanceof SlotActivatedItem) {
				((SlotActivatedItem) inspected).onUnequipped();
			}

			String slotname = inspected.getContainerSlot().getName();
			int objectID = inspected.getID().getObjectID();
			if (null != inspected.getContainerSlot().remove(inspected.getID())) {
				if (slot instanceof Entity) {
					((Entity) slot).notifyWorldAboutChanges();
				}
				player.sendPrivateText("Removed contained " + name + " " + clazz + " with ID " + objectID + " from " + slotname);
			} else {
				player.sendPrivateText("could not remove contained " + inspected + " " + clazz + " with ID " + objectID + " from " + slotname);

				// re-enable effects of slot activated item in case removal failed
				if (inspected instanceof SlotActivatedItem) {
					((SlotActivatedItem) inspected).onEquipped(player, slotname);
				}
			}
		} else {
			if (inspected instanceof Player) {
				final String text = "You can't remove players";
				player.sendPrivateText(text);
				return;
			}

			if (inspected instanceof PassiveNPC) {
				final String text = "You can't remove PassiveNPCs";
				player.sendPrivateText(text);
				return;
			}

			if (inspected instanceof Portal) {
				final String text = "You can't remove portals. Try blocking it with a few of /script AdminSign.class.";
				player.sendPrivateText(text);
				return;
			}

			final StendhalRPZone zone = inspected.getZone();

			if (inspected instanceof RPEntity) {
				if (inspected instanceof Creature) {
					// *destroyed creatures should not drop items
					((Creature) inspected).clearDropItemList();
				}
				((RPEntity) inspected).onDead(player);
			} else if ((inspected instanceof Item) || (inspected instanceof FlowerGrower) || (inspected instanceof Blood) || (inspected instanceof Corpse)) {
				// items should be added to itemlog as well, to help tracing problems
				if (inspected instanceof Item) {
					String quantity = inspected.get("quantity");
					if (quantity == null) {
						quantity = "1";
					}
					new ItemLogger().destroy(player, null, inspected, "admin");
				}
				zone.remove(inspected);
			} else {
				player.sendPrivateText("You can't remove this type of entity");
				return;
			}

			if (inspected instanceof TurnListener) {
				TurnListener listener = (TurnListener) inspected;
				TurnNotifier.get().dontNotify(listener);
			}

			new GameEvent(player.getName(), "removed",  name + " " + clazz, zone.getName(), Integer.toString(inspected.getX()), Integer.toString(inspected.getY())).raise();

			player.sendPrivateText("Removed " + name + " " + clazz + " with ID " + action.get(TARGET));
		}
	}
}
