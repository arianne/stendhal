package games.stendhal.server.actions.equip;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Stackable;
import games.stendhal.server.entity.player.Player;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.server.game.RPWorld;

/**
 * this encapsulates the equip/drop destination
 */
class DestinationObject {
	private static Logger logger = Logger.getLogger(DestinationObject.class);


	/** true when this object is valid */
	private boolean valid;

	/** optional, parent item */
	private Entity parent;

	/** optional, slot */
	String slot;

	/** x coordinate when dropped on ground */
	private int x;

	/** y coordinate when dropped on ground */
	private int y;

	/** interprets the given action */
	public DestinationObject(RPAction action, Player player) {
		valid = false;
		// droppped into another item
		if (action.has(EquipActionConsts.TARGET_OBJECT) && action.has(EquipActionConsts.TARGET_SLOT)) {
			// get base item and slot
			parent = EquipUtil.getEntityFromId(player, action.getInt(EquipActionConsts.TARGET_OBJECT));

			// check slot
			if (parent == null) {
				logger.warn("cannot find target entity for action " + action);
				// Not valid...
				return;
			}

			slot = action.get(EquipActionConsts.TARGET_SLOT);

			// is the container a player and not the current one?
			if ((parent instanceof Player) && !parent.getID().equals(player.getID())) {
				logger.warn("trying to drop an item into another players inventory");
				// trying to drop an item into another players inventory
				return;
			}

			// check slot
			if (!parent.hasSlot(slot)) {
				logger.warn("Parent don't have slot: " + action);
				return;
			}
			// ok, action is valid
			valid = true;
			return;
		}

		// dropped to the ground
		if (action.has(EquipActionConsts.GROUND_X) && action.has(EquipActionConsts.GROUND_Y)) {
			x = action.getInt(EquipActionConsts.GROUND_X);
			y = action.getInt(EquipActionConsts.GROUND_Y);
			valid = true;
		}

		// not valid
	}

	/** checks if it is possible to add the entity to the world */
	public boolean preCheck(Entity entity, RPWorld world) {
		StendhalRPZone zone = (StendhalRPZone) world.getRPZone(entity.getID());

		if (parent != null) {
			RPSlot rpslot = parent.getSlot(slot);
			if (rpslot.isFull()) {
				// RPSlot bag=parent.getSlot("bag");
				//              
				// if(!bag.isFull())
				// {
				// // We change the entity
				// RPObject object=rpslot.getFirst();
				//
				// System.out.println (object);
				// System.out.println ("BEFORE SLOT: "+rpslot);
				// System.out.println ("BEFORE BAG: "+bag);
				//    
				// rpslot.remove(object.getID());
				// bag.assignValidID(object);
				// bag.add(object);
				//
				// System.out.println ("AFTER SLOT: "+rpslot);
				// System.out.println ("AFTER BAG: "+bag);
				//    
				// return true;
				// }
				//
				//
				boolean isStackable = false;
				// is the entity stackable
				if (entity instanceof Stackable) {
					Stackable stackEntity = (Stackable) entity;
					// now check if it can be stacked on top of another item
					Iterator<RPObject> it = rpslot.iterator();
					while (it.hasNext()) {
						RPObject object = it.next();
						if (object instanceof Stackable) {
							// found another stackable
							Stackable other = (Stackable) object;
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
					return false;
				}
			}

			// check if someone tried to put an item into itself (maybe
			// through
			// various levels of indirection)
			if (rpslot.hasAsParent(entity.getID())) {
				logger.warn("tried to put item " + entity.getID() + " into itself, equip rejected");
				return false;
			}

			// not very accurate...the containment level of this slot
			int depth = rpslot.getContainedDepth();

			// count items in source item (if it is an container)
			for (RPSlot sourceSlot : entity.slots()) {
				depth += sourceSlot.getNumberOfContainedItems();
			}

			// check the maximum level of contained elements
			if ((entity.slots().size() > 0) && (depth > EquipActionConsts.MAX_CONTAINED_DEPTH)) {
				logger.warn("maximum contained depth (is: " + depth + " max: " + EquipActionConsts.MAX_CONTAINED_DEPTH
				        + ") reached, equip rejected");
				return false;
			}
		} else {
			logger.warn("entity: " + entity + " zone: " + zone);
			// check if the destination is free
			if ((zone != null) && zone.simpleCollides(entity, x, y)) {
				logger.warn("object " + entity + " collides with " + x + "x" + y);
				return false;
			}
			// and in reach
			if (entity.has("x") && entity.has("y") && (entity.squaredDistance(x, y) > 8 * 8)) {
				logger.warn("object " + entity + " is too far away from " + x + "x" + y);
				return false;
			}

		}

		return true;
	}

	/** returns true when this DestinationObject is valid */
	public boolean isValid() {
		return valid;
	}

	/**
	 * returns true when this entity and the other is within the given
	 * distance
	 */
	public boolean checkDistance(Entity other, double distance) {
		if (parent != null) {
			return (other.nextTo(parent, distance));
		}

		// should be dropped to the ground
		return (other.nextTo(x, y, distance));
	}

	/**
	 * add the entity to the world (specified by the action during
	 * constuction). Note that you should call isValid(), preCheck(..) and
	 * checkDistance(..) before adding an item to the world
	 * 
	 * @return true when the item is added, false otherwise
	 */
	public boolean addToWorld(Entity entity, RPWorld world, Player player) {
		if (parent != null) {
			// drop the entity into a slot
			if (parent.getID().equals(entity.getID())) {
				logger.warn("tried to put an item into itself");
				// tried to add the item to itself
				return false;
			}

			RPSlot rpslot = parent.getSlot(slot);

			// check if the item can be merged with one already in the slot
			if (entity instanceof Stackable) {
				Stackable stackEntity = (Stackable) entity;
				// find a stackable item of the same type
				Iterator<RPObject> it = rpslot.iterator();
				while (it.hasNext()) {
					RPObject object = it.next();
					if (object instanceof Stackable) {
						// found another stackable
						Stackable other = (Stackable) object;
						if (other.isStackable(stackEntity)) {
							// other is the same type...merge them
							other.add(stackEntity);
							entity = null; // do not process the entity
							// further
							break;
						}
					}
				}
			}

			// entity still there?
			if (entity != null) {
				// yep, so it is stacked. simplay add it
				rpslot.assignValidID(entity);
				rpslot.add(entity);
			}

			world.modify(parent);
		} else {
			// drop the entity to the ground. Do this always in the players
			// zone
			StendhalRPZone zone = (StendhalRPZone) world.getRPZone(player.getID());
			logger.warn("adding " + entity.get("name") + " to " + zone);

			// HACK: Avoid a problem on database
			if (entity.has("#db_id")) {
				entity.remove("#db_id");
			}

			entity.setX(x);
			entity.setY(y);
			logger.warn("entity set to " + x + "x" + y);

			zone.assignRPObjectID(entity);
			logger.warn("entity has valid id: " + entity.getID());

			// FIXME: This should add the item to the zone.
			zone.add(entity);
		}
		return true;
	}

	/**
	 * returns true when the rpobject is one of the classes in
	 * <i>validClasses</i>
	 */
	public boolean checkClass(List<Class> validClasses) {
		if (parent != null) {
			return EquipUtil.isCorrectClass(validClasses, parent);
		}
		return true;
	}

	String getSlot() {
		return slot;
	}

}
