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
package games.stendhal.server.actions.equip;

import java.awt.Rectangle;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.common.EquipActionConsts;
import games.stendhal.common.MathHelper;
import games.stendhal.common.constants.Actions;
import games.stendhal.server.core.engine.ItemLogger;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.core.pathfinder.Path;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.Stackable;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.slot.EntitySlot;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * this encapsulates the equip/drop destination.
 */
class DestinationObject extends MoveableObject {
	private static Logger logger = Logger.getLogger(DestinationObject.class);

	/** true when this object is valid. */
	private boolean valid;



	/** x coordinate when dropped on ground. */
	private int x;

	/** y coordinate when dropped on ground.*/
	private int y;

	/** interprets the given action.
	 * @param action
	 * @param player
	 */
	public DestinationObject(final RPAction action, final Player player) {
		super(player);
		valid = false;
		if (action.has(Actions.TARGET_PATH)) {
			List<String> path = action.getList(Actions.TARGET_PATH);
			Iterator<String> it = path.iterator();
			parent = EquipUtil.getEntityFromId(player, MathHelper.parseInt(it.next()));

			// check slot
			if (parent == null) {
				logger.warn("cannot find target entity for action " + action);
				// Not valid...
				return;
			}
			// is the top level parent a player and not the current one?
			if ((parent instanceof Player)
					&& !parent.getID().equals(player.getID())) {
				logger.warn("trying to drop an item into another players inventory");
				// trying to drop an item into another players inventory
				return;
			}
			// Walk the slot path
			slot = null;
			while (it.hasNext()) {
				slot = it.next();
				if (!parent.hasSlot(slot)) {
					logger.error(player.getName() + " tried to use non existing slot " + slot + " of " + parent
							+ " as destination. player zone: " + player.getZone() + " object zone: " + parent.getZone());
					return;
				}
				final RPSlot rpslot = parent.getSlot(slot);
				if (it.hasNext()) {
					final RPObject.ID itemId = new RPObject.ID(MathHelper.parseInt(it.next()), "");
					if (!rpslot.has(itemId)) {
						return;
					}
					parent = (Entity) rpslot.get(itemId);
				}
			}
			valid = slot != null;
			return;
		} else if (action.has(EquipActionConsts.TARGET_OBJECT)
				&& action.has(EquipActionConsts.TARGET_SLOT)) {
			// ** Compatibility mode **

			// get base item and slot
			parent = EquipUtil.getEntityFromId(player,
					action.getInt(EquipActionConsts.TARGET_OBJECT));

			// check slot
			if (parent == null) {
				logger.warn("cannot find target entity for action " + action);
				// Not valid...
				return;
			}

			slot = action.get(EquipActionConsts.TARGET_SLOT);

			// is the container a player and not the current one?
			if ((parent instanceof Player)
					&& !parent.getID().equals(player.getID())) {
				logger.warn("trying to drop an item into another players inventory");
				// trying to drop an item into another players inventory
				return;
			}

			// check slot
			if (!parent.hasSlot(slot)) {
				logger.warn("Parent don't have slot: " + action);
				return;
			}

			// ok, action is valid.
			valid = true;
			return;
		}

		// dropped to the ground
		if (action.has(EquipActionConsts.GROUND_X)
				&& action.has(EquipActionConsts.GROUND_Y)) {
			x = action.getInt(EquipActionConsts.GROUND_X);
			y = action.getInt(EquipActionConsts.GROUND_Y);
			valid = true;
		}

		// not valid
	}

	/** checks if it is possible to add the entity to the world.
	 * @param entity
	 * @param player
	 * @return true if can be added to the world
	 * */
	@SuppressWarnings("unchecked")
	public boolean preCheck(final Entity entity, final Player player) {
		final StendhalRPZone zone = player.getZone();

		if (parent != null) {
			final EntitySlot rpslot = parent.getEntitySlot(slot);
			// Old clients may try to move items to magic slot
			if (rpslot == null) {
				return false;
			}
			rpslot.clearErrorMessage();
			if (!rpslot.isReachableForThrowingThingsIntoBy(player)) {
				player.sendPrivateText(rpslot.getErrorMessage());
				logger.debug("Unreachable slot");
				return false;
			}

			if (rpslot.isFull()) {
				boolean isStackable = false;
				// is the entity stackable
				if (entity instanceof Stackable<?>) {
					final Stackable<?> stackEntity = (Stackable<?>) entity;
					// now check if it can be stacked on top of another item
					final Iterator<RPObject> it = rpslot.iterator();
					while (it.hasNext()) {
						final RPObject object = it.next();

						if (object instanceof Stackable<?>) {
							// found another stackable
							@SuppressWarnings("rawtypes")
							final Stackable other = (Stackable<?>) object;
							if (other.isStackable(stackEntity)) {
								// other is the same type...merge them
								isStackable = true;
							}
						}
					}
				}

				if (!isStackable) {
					// entity cannot be stacked on top of another...
					// so the equip is invalid
					player.sendPrivateText("There is no space in there.");
					return false;
				}
			}

			// check if someone tried to put an item into itself (maybe
			// through various levels of indirection)
			if (rpslot.hasAsAncestor(entity)) {
				logger.warn("tried to put item " + entity.getID()
						+ " into itself, equip rejected");
				return false;
			}

			if (entity instanceof Item) {
				Item item = (Item) entity;
				if (item.isBound() && rpslot.isTargetBoundCheckRequired()) {
					player.sendPrivateText("You cannot put this special quest reward there because it can only be used by you.");
					return false;
				}

				// check if an item that is sent to a trade slot is not damaged
				if ((item.getDeterioration() > 0) && rpslot.getName().equals("trade")) {
					player.sendPrivateText("You must not trade a damaged item with other players.");
					return false;
				}
			}

		} else {
			logger.debug("entity: " + entity + " zone: " + zone);
			// check if the destination is free
			if ((zone != null) && zone.simpleCollides(entity, x, y, entity.getWidth(), entity.getHeight())) {
				logger.warn("object " + entity + " collides with " + x + "x"
						+ y);
				player.sendPrivateText("There is no space on there.");
				return false;
			}

			// and in reach
			if (!entity.isContained()
					&& (entity.squaredDistance(x, y) > (8 * 8))) {
				logger.warn("object " + entity + " is too far away from " + x
						+ "," + y);
				player.sendPrivateText("That is too far away.");
				return false;
			}

			if (!isGamblingZoneAndIsDice(entity, player)) {
				// and there is a path there
				final List<Node> path = Path.searchPath(entity, zone,
						player.getX(), player.getY(), new Rectangle(x, y, 1, 1),
						64 /* maxDestination * maxDestination */, false);

				boolean blocked = path.isEmpty();
				if (!blocked && zone != null) {
					for (final Node node: path) {
						if (blocked) {
							break;
						}

						final List<Entity> entities = zone.getEntitiesAt(node.getX(), node.getY());
						for (final Entity e: entities) {
							if (e.has("walk_blocker")) {
								blocked = true;
								break;
							}
						}
					}
				}

				if (blocked) {
					player.sendPrivateText("There is no easy path to that place.");
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Check if the entity is dice played at the gambling table.
	 *
	 * @param entity the item
	 * @param player the player to get the zone from
	 * @return <code>true</code> if the if zone is Semos tavern and entity is
	 * 	dice
	 */
	private boolean isGamblingZoneAndIsDice(final Entity entity, final Player player) {
		final StendhalRPZone zone = player.getZone();
		return "int_semos_tavern_0".equals(zone.getName()) && ("dice").equals(entity.getTitle());
	}

	/** returns true when this DestinationObject is valid. */
	@Override
	public boolean isValid() {
		return valid;
	}

	/**
	 * returns true when this entity and the other is within the given distance.
	 */
	@Override
	public boolean checkDistance(final Entity other, final double distance) {
		if (parent != null) {
			Entity base = parent;
			RPObject obj = parent.getBaseContainer();
			if (obj instanceof Entity) {
				base = (Entity) obj;
			}
			return (other.nextTo(base, distance));
		}

		// Should be dropped to the ground. Do a proper distance calculation
		return (other.squaredDistance(x, y) < (distance * distance));
	}

	/**
	 * gets the name of the content slot used for the can equipped check
	 *
	 * @return name of slot
	 */
	public String getContentSlotName() {
		if (parent != null) {
			final EntitySlot entitySlot = parent.getEntitySlot(slot);
			// Old clients may try to move items to magic slot
			if (entitySlot != null) {
				return entitySlot.getContentSlotName();
			}
		}
		return null;
	}

	/**
	 * add the entity to the world (specified by the action during construction).
	 * Note that you should call isValid(), preCheck(..) and checkDistance(..)
	 * before adding an item to the world
	 * @param entity
	 * @param player
	 */
	public void addToWorld(Entity entity, final Player player) {
		if (parent != null) {
			// drop the entity into a slot
			final RPSlot rpslot = parent.getSlot(slot);


			// check if the item can be merged with one already in the slot
			if (entity instanceof StackableItem) {
				final StackableItem stackEntity = (StackableItem) entity;
				// find a stackable item of the same type
				final Iterator<RPObject> it = rpslot.iterator();
				while (it.hasNext()) {
					final RPObject object = it.next();
					if (object instanceof StackableItem) {
						// found another stackable
						final StackableItem other = (StackableItem) object;
						if (other.isStackable(stackEntity)) {
							new ItemLogger().merge(player, stackEntity, other);

							// other is the same type...merge them
							other.add(stackEntity);
							entity = null;
							// do not process the entity further
							break;
						}
					}
				}
			}

			// entity still there?
			if (entity != null) {
				// Set position to 0,0 (relative to container)
				entity.setPosition(0, 0);

				// yep, so it is not stacked. simply add it
				rpslot.add(entity);

				/* XXX sjtsp - experiment.
				 *   - maybe should call this on player, instead of item?
				 */
				((Item) entity).onEquipped(player, rpslot.getName());
			}
			SingletonRepository.getRPWorld().modify(parent.getBaseContainer());
		} else {
			// drop the entity to the ground. Do this always in the player's
			// zone.
			final StendhalRPZone zone = player.getZone();
			logger.debug("adding " + entity.get("name") + " to " + zone);

			// HACK: Avoid a problem on database
			if (entity.has("#db_id")) {
				entity.remove("#db_id");
			}

			entity.setPosition(x, y);
			logger.debug("entity set to " + x + ", " + y);

			zone.add(entity, player);
			logger.debug("entity has valid id: " + entity.getID());
		}
	}

	@Override
	public String[] getLogInfo() {
		final String[] res = new String[3];
		if (parent != null) {
			res[0] = "slot";
			if (parent.has("name")) {
				res[1] = parent.get("name");
			} else {
				res[1] = parent.getDescriptionName(false);
			}
			res[2] = slot;
		} else {
			res[0] = "ground";
			res[1] = player.getZone().getName();
			res[2] = x + " " + y;
		}
		return res;
	}
}
