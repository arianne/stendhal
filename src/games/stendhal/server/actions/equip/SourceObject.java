package games.stendhal.server.actions.equip;

import games.stendhal.server.ItemLogger;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.events.EquipListener;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.Stackable;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.slot.EntitySlot;

import java.util.List;

import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

import org.apache.log4j.Logger;

/**
 * this encapsulates the equip/drop source
 */
class SourceObject extends MoveableObject {
	private static Logger logger = Logger.getLogger(SourceObject.class);
	/** the item */
	private Item item;

	/** optional, parent item */
	private Entity parent;

	private int quantity;

	/** interprets the given action */
	// TODO: split this method into parts (and move the checks out of the
	// constructor)
	public SourceObject(RPAction action, Player player) {
		super(player);

		// base item must be there
		if (!action.has(EquipActionConsts.BASE_ITEM)) {
			logger.warn("action does not have a base item. action: " + action);
			return;
		}

		// get base item
		RPObject.ID baseItemId = new RPObject.ID(
				action.getInt(EquipActionConsts.BASE_ITEM),
				player.getID().getZoneID());
		// is the item in a container?
		if (action.has(EquipActionConsts.BASE_OBJECT)) {
			// yes, contained

			// remove zone from id (contained items does not have a zone)
			baseItemId = new RPObject.ID(baseItemId.getObjectID(), "");

			parent = EquipUtil.getEntityFromId(player,
					action.getInt(EquipActionConsts.BASE_OBJECT));

			if (parent == null) {
				// Object doesn't exist.
				return;
			}

			// TODO: Check that this code is not required because this check is
			// done in PlayerSlot
			// is the container a player and not the current one?
			if ((parent instanceof Player)
					&& !parent.getID().equals(player.getID())) {
				// trying to remove an item from another player
				return;
			}

			slot = action.get(EquipActionConsts.BASE_SLOT);

			// check that this slots exists
			if (!parent.hasSlot(slot)) {
				player.sendPrivateText("Source " + slot + " does not exist");
				logger.error(player.getName()
						+ " tried to use non existing slot " + slot + " of "
						+ parent + " as source. player zone: "
						+ player.getZone() + " object zone: "
						+ parent.getZone());
				return;
			}

			RPSlot baseSlot = parent.getSlot(slot);

			if (!baseSlot.has(baseItemId)) {
				logger.warn("Base item(" + parent + ") doesn't containt item("
						+ baseItemId + ") on given slot(" + slot + ")");
				player.sendPrivateText("There is no such item in the " + slot
						+ " of " + parent.getDescriptionName(true));
				return;
			}

			if (!(baseSlot instanceof EntitySlot)
					|| (!((EntitySlot) baseSlot).isReachableForTakingThingsOutOfBy(player))) {
				logger.warn("Unreachable slot");
				player.sendPrivateText("The " + slot + " of "
						+ parent.getDescriptionName(true) + " is too far away.");
				return;
			}

			Entity entity = (Entity) baseSlot.get(baseItemId);
			if (!(entity instanceof Item)) {
				player.sendPrivateText("Oh, that "
						+ entity.getDescriptionName(true)
						+ " is not an item and can therefor not be equipped");
				return;
			}
			item = (Item) entity;
		} else {
			// item is not contained
			if (StendhalRPWorld.get().has(baseItemId)) {
				Entity entity = (Entity) StendhalRPWorld.get().get(baseItemId);
				if (!(entity instanceof Item)) {
					return;
				}
				item = (Item) entity;
			}

			if ((item != null) && isItemBelowOtherPlayer()) {
				item = null;
			}
		}

		if ((item instanceof Stackable)
				&& action.has(EquipActionConsts.QUANTITY)) {
			int entityQuantity = ((Stackable) item).getQuantity();

			quantity = action.getInt(EquipActionConsts.QUANTITY);
			if ((entityQuantity < 1) || (quantity < 1)
					|| (quantity >= entityQuantity)) {
				quantity = 0; // quantity == 0 performs a regular move
				// of the entire item
			}
		}
	}

	/** moves this entity to the destination */
	public boolean moveTo(DestinationObject dest, Player player) {
		if (!((EquipListener) item).canBeEquippedIn(dest.slot)) {
			// give some feedback
			player.sendPrivateText("You can't carry this " + item.getTitle()
					+ " on your " + dest.slot);
			logger.warn("tried to equip an entity into disallowed slot: "
					+ item.getClass() + "; equip rejected");
			return false;
		}

		if (!dest.isValid() || !dest.preCheck(item, player)) {
			logger.warn("moveto not possible: " + dest.isValid() + "\t"
					+ dest.preCheck(item, player));
			return false;
		}

		String[] srcInfo = getLogInfo();
		Item entity = removeFromWorld(player);
		logger.debug("item removed");
		dest.addToWorld(entity, player);
		logger.debug("item readded");
	
		ItemLogger.equipAction(player, entity, srcInfo, dest.getLogInfo());

		return true;
	}

	/** returns true when this SourceObject is valid */
	@Override
	public boolean isValid() {
		return (item != null);
	}

	/**
	 * returns true when this entity and the other is within the given distance
	 */
	@Override
	public boolean checkDistance(Entity other, double distance) {
		Entity checker = (parent != null) ? parent : item;
		if (other.nextTo(checker, distance)) {
			return true;
		}
		logger.debug("distance check failed " + other.squaredDistance(checker));
		player.sendPrivateText("You cannot reach that far");
		return false;
	}

	/**
	 * removes the entity from the world and returns it (so it may nbe added
	 * again). In case of splitted StackableItem the only item is reduced and a
	 * new StackableItem with the splitted off amount is returned.
	 * 
	 * @return Entity to place somewhere else in the world
	 */
	public Item removeFromWorld(Player player) {
		if (quantity != 0) {
			StackableItem newItem = ((StackableItem) item).splitOff(quantity);
			ItemLogger.splitOff(player, item, newItem, quantity);
			return newItem;
		} else {
			item.removeFromWorld();
			return item;
		}
	}

	/**
	 * returns true when the rpobject is one of the classes in <i>validClasses</i>
	 */
	public boolean checkClass(List<Class<?>> validClasses) {
		if (parent != null) {
			if (!EquipUtil.isCorrectClass(validClasses, parent)) {
				logger.debug("parent is the wrong class "
						+ parent.getClass().getName());
				return false;
			}
		}
		return true;
	}

	/**
	 * gets the entity that should be equipped
	 * 
	 * @return entity
	 */
	public Entity getEntity() {
		return item;
	}

	public int getQuantity() {
		int temp = quantity;
		if (quantity == 0) {
			// everything
			temp = 1;
			if (item instanceof StackableItem) {
				temp = ((StackableItem) item).getQuantity();
			}

		}
		return temp;
	}

	/**
	 * Checks whether the item is below <b>another</b> player.
	 * 
	 * @return true, if it cannot be take; false otherwise
	 */
	private boolean isItemBelowOtherPlayer() {
		// prevent taking of items which are below other players
		List<Player> players = player.getZone().getPlayers();
		for (Player otherPlayer : players) {
			if (player.equals(otherPlayer)) {
				continue;
			}
			if (otherPlayer.getArea().intersects(item.getArea())) {
				player.sendPrivateText("You cannot take items which are below other players");
				return true;
			}
		}
		return false;
	}

	@Override
    public String[] getLogInfo() {
	    String[] res = new String[3];
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
	    	res[1] = item.getZone().getName();
	    	res[2] = item.getX() + " " + item.getY();
	    }
	    return res;
    }
}
