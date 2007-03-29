package games.stendhal.server.actions.equip;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.EquipListener;

import java.util.List;

import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.server.game.RPWorld;

import org.apache.log4j.Logger;

/**
 * this encapsulates the equip/drop source
 */
class SourceObject {
	private static Logger logger = Logger.getLogger(SourceObject.class);

	/** the item */
	private Entity base;

	/** optional, parent item */
	private Entity parent;

	/** optional, slot */
	private String slot;

	/** interprets the given action */
	public SourceObject(RPAction action, Player player) {
		// base item must be there
		if (!action.has(EquipActionConsts.BASE_ITEM)) {
			logger.warn("action does not have a base item. action: " + action);
			return;
		}

		// get base item
		RPObject.ID baseItemId = new RPObject.ID(action.getInt(EquipActionConsts.BASE_ITEM), player.getID().getZoneID());
		// is the item in a container?
		if (action.has(EquipActionConsts.BASE_OBJECT)) {
			// yes, contained

			// remove zone from id (contained items does not have a zone)
			baseItemId = new RPObject.ID(baseItemId.getObjectID(), "");

			parent = EquipUtil.getEntityFromId(player, action.getInt(EquipActionConsts.BASE_OBJECT));

			if (parent == null) {
				// Object doesn't exist.
				return;
			}

			// is the container a player and not the current one?
			if ((parent instanceof Player) && !parent.getID().equals(player.getID())) {
				// trying to remove an item from another player
				return;
			}

			slot = action.get(EquipActionConsts.BASE_SLOT);

			RPSlot baseSlot = parent.getSlot(slot);

			if (!baseSlot.has(baseItemId)) {
				logger.warn("Base item(" + parent + ") doesn't containt item(" + baseItemId + ") on given slot("
				        + slot + ")");
				return;
			}

			base = (Entity) baseSlot.get(baseItemId);
		} else {
			// item is not contained
			if (StendhalRPWorld.get().has(baseItemId)) {
				base = (Entity) StendhalRPWorld.get().get(baseItemId);
			}
		}
	}
	
	String getSlot() {
		return slot;
	}

	/** moves this entity to the destination */
	public boolean moveTo(DestinationObject dest, Player player) {
		StendhalRPWorld world = StendhalRPWorld.get();

		if ((!(base instanceof EquipListener)) || (!((EquipListener) base).canBeEquippedIn(dest.slot))) {
			// give some feedback
			player.sendPrivateText("You can't carry this " + base.getName() + " on your " + dest.slot);
			logger.warn("tried to equip an entity into disallowed slot: " + base.getClass() + "; equip rejected");
			return false;
		}

		if (!dest.isValid() || !dest.preCheck(base, world)) {
			logger.warn("moveto not possible: " + dest.isValid() + "\t" + dest.preCheck(base, world));
			return false;
		}

		removeFromWorld(world);
		logger.debug("item removed");
		dest.addToWorld(base, world, player);
		logger.debug("item readded");

		return true;
	}

	/** returns true when this SourceObject is valid */
	public boolean isValid() {
		if (base != null) {
			return true;
		}
		logger.debug("source is not valid, base == null");
		return false;
	}

	/**
	 * returns true when this entity and the other is within the given
	 * distance
	 */
	public boolean checkDistance(Entity other, double distance) {
		Entity checker = (parent != null) ? parent : base;
		if (other.nextTo(checker, distance)) {
			return true;
		}
		logger.debug("distance check failed " + other.squaredDistance(checker));
		return false;
	}

	/**
	 * removes the entity from the world and returns it (so it may nbe added
	 * again)
	 */
	public Entity removeFromWorld(RPWorld world) {
		if (parent == null) {
			world.remove(base.getID());
		} else {
			parent.getSlot(slot).remove(base.getID());
			world.modify(parent);
		}
		return base;
	}

	/**
	 * returns true when the rpobject is one of the classes in
	 * <i>validClasses</i>
	 */
	public boolean checkClass(List<Class> validClasses) {
		if (parent != null) {
			if (!EquipUtil.isCorrectClass(validClasses, parent)) {
				logger.debug("parent is the wrong class " + parent.getClass().getName());
				return false;
			}
		}
		return true;
	}

	/**
	 * gets the entity that should be equiped
	 *
	 * @return entity
	 */
	public Entity getEntity() {
		return base;
	}
}

