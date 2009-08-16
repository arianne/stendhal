package games.stendhal.server.entity.mapstuff.chest;

import games.stendhal.common.Grammar;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.slot.ChestSlot;

import java.util.LinkedList;
import java.util.List;

import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

import org.apache.log4j.Logger;

/**
 * A Chest whose contents are stored by the zone.
 * 
 * @author kymara
 */
public class StoredChest extends Chest {
	private static Logger logger = Logger.getLogger(StoredChest.class);
	private ChestListener chestListener;

	/**
	 * Creates a new StoredChest.
	 * 
	 */
	public StoredChest() {
		super();
		store();
	}

	@Override
	public void open() {
		chestListener = new ChestListener();
		SingletonRepository.getTurnNotifier().notifyInTurns(0, chestListener);
		super.open();
	}

	@Override
	public void close() {
		super.close();
		StendhalRPZone zone = this.getZone();
		if (zone != null) {
			zone.storeToDatabase();
		}
	}

	/**
	 * Creates a StoredChest based on an existing RPObject. This is just for
	 * loading a chest from the database, use the other constructors.
	 * 
	 * @param rpobject
	 */
	public StoredChest(final RPObject rpobject) {
		super(rpobject);
		loadSlotContent();
		store();
	}

	private void loadSlotContent() {
		if (hasSlot("content")) {
			final RPSlot slot = getSlot("content");
			final List<RPObject> objects = new LinkedList<RPObject>();
			for (final RPObject objectInSlot : slot) {
				objects.add(objectInSlot);
			}
			slot.clear();
			removeSlot("content");

			final RPSlot newSlot = new ChestSlot(this);
			addSlot(newSlot);
			// does this code look familiar to you? well it might, it's from
			// PlayerRPClass. :*
			for (final RPObject item : objects) {
				try {
					// We simply ignore corpses...
					if (item.get("type").equals("item")) {
						// TODO: what about items whose names have changed?
						// the update converter is in player :(
						final String name = item.get("name");
						final Item entity = SingletonRepository
								.getEntityManager().getItem(name);

						// log removed items
						if (entity == null) {
							int quantity = 1;
							if (item.has("quantity")) {
								quantity = item.getInt("quantity");
							}
							logger.warn("Cannot restore " + quantity + " "
									+ name + " of stored chest "
									+ " because this item"
									+ " was removed from items.xml");
							continue;
						}

						entity.setID(item.getID());

						if (item.has("persistent")
								&& (item.getInt("persistent") == 1)) {
							/*
							 * Keep [new] rpclass
							 */
							final RPClass rpclass = entity.getRPClass();
							entity.fill(item);
							entity.setRPClass(rpclass);

							// If we've updated the item name we don't want
							// persistent reverting it
							entity.put("name", name);
						}

						if (entity instanceof StackableItem) {
							int quantity = 1;
							if (item.has("quantity")) {
								quantity = item.getInt("quantity");
							} else {
								logger.warn("Adding quantity=1 to " + item
									+ ". Most likely cause is that this item was not stackable in the past");
							}
							((StackableItem) entity).setQuantity(quantity);

							if (quantity <= 0) {
								logger.warn("Ignoring item " + name
									+ " on restore of stored chest"
									+ " because this item has an invalid quantity: "
									+ quantity);
								continue;
							}
						}

						// make sure saved individual information is restored
						final String[] individualAttributes = { "infostring", "description", "bound", "undroppableondeath" };
						for (final String attribute : individualAttributes) {
							if (item.has(attribute)) {
								entity.put(attribute, item.get(attribute));
							}
						}

						if (item.has("logid")) {
							entity.put("logid", item.get("logid"));
						}
						newSlot.add(entity);
					} else {
						logger.warn("Non-item object found in stored chest: " + item);
					}
				} catch (final Exception e) {
					logger.error("Error adding " + item + " to stored chest slot", e);
				}
			}
		}
	}

	@Override
	public String getDescriptionName(final boolean definite) {
		return Grammar.article_noun("chest in " + this.getZone().getName(), definite);
	}

	/**
	 * Checks if it should close the chest
	 * 
	 * @return <code>true</code> if it should be called again.
	 */
	protected boolean chestCloser() {

		if (getZone().getPlayers().size() > 0) {
			// do nothing - people are still in the zone
				return true;
		} else {
			// the zone is empty, close the chest
				close();
				notifyWorldAboutChanges();
		}
		return false;
	}

	/**
	 * A listener for closing the chest
	 */

	protected class ChestListener implements TurnListener {
		/**
		 * This method is called when the turn number is reached.
		 * 
		 * @param currentTurn
		 *            The current turn number.
		 */
		public void onTurnReached(final int currentTurn) {
			if (chestCloser()) {
				SingletonRepository.getTurnNotifier().notifyInTurns(0, this);
			}
		}
		
		
	}

	@Override
	public void onRemoved(final StendhalRPZone zone) {
		SingletonRepository.getTurnNotifier().dontNotify(chestListener);

		super.onRemoved(zone);
	}
}
