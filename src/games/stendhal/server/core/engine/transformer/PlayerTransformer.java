package games.stendhal.server.core.engine.transformer;

import static games.stendhal.common.constants.Actions.AWAY;
import static games.stendhal.common.constants.Actions.GRUMPY;
import games.stendhal.common.Debug;
import games.stendhal.common.FeatureList;
import games.stendhal.server.core.engine.ItemLogEntry;
import games.stendhal.server.core.engine.ItemLogger;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TutorialNotifier;
import games.stendhal.server.core.rp.StendhalRPAction;
import games.stendhal.server.entity.creature.DomesticAnimal;
import games.stendhal.server.entity.creature.Pet;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.player.UpdateConverter;
import games.stendhal.server.entity.slot.BankSlot;
import games.stendhal.server.entity.slot.Banks;
import games.stendhal.server.entity.slot.PlayerSlot;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

import org.apache.log4j.Logger;

public class PlayerTransformer implements Transformer {

	public RPObject transform(final RPObject object) {
		return create(object);
	}


	/** these items should be bound.*/
	private static final List<String> ITEMS_TO_BIND = Arrays.asList(
			"dungeon silver key", "lich gold key", "trophy helmet",
			"lucky charm");

	/** these items should be unbound.*/
	private static final List<String> ITEMS_TO_UNBIND = Arrays.asList("marked scroll");

	/** these items should be delete for non admins */
	private static final List<String> ITEMS_FOR_ADMINS = Arrays.asList("rod of the gm", "master key");

	public Player create(final RPObject object) {
		
		removeVolatile(object);
		
		
		// add attributes and slots
		UpdateConverter.updatePlayerRPObject(object);

		final Player player = new Player(object);
		player.stop();
		player.stopAttack();

		

		
		placePlayerIntoWorldOnLogin(object, player);
		placeSheepAndPetIntoWorld(player);

		player.notifyWorldAboutChanges();
		StendhalRPAction.transferContent(player);
		
		loadItemsIntoSlots(player);

		if (player.getSlot("!buddy").size() > 0) {
			final RPObject buddies = player.getSlot("!buddy").iterator().next();
			for (final String buddyName : buddies) {
				// TODO: Remove '_' prefix if ID is made completely virtual
				if (buddyName.charAt(0) == '_') {
					final Player buddy = SingletonRepository.getRuleProcessor().getPlayer(
							buddyName.substring(1));
					if ((buddy != null) && !buddy.isGhost()) {
						buddies.put(buddyName, 1);
					} else {
						buddies.put(buddyName, 0);
					}
				}
			}
		}

		convertOldfeaturesList(player);

		player.updateItemAtkDef();

		UpdateConverter.updateQuests(player);

		

		logger.debug("Finally player is :" + player);
		return player;
	}

	/**
	 * TODO: there is a bug in marauroa, remove this if marauroa stops storing volatile attributes.
	 * review then also which attributes should be volatile and which shouldnt.
	 * @param player
	 */
	private void removeVolatile(final RPObject player) {
		if (player.has(AWAY)) {
			player.remove(AWAY);
		}
		// remove grumpy on login to give postman a chance to deliver messages
		// (and in the hope that player is receptive now)
		if (player.has(GRUMPY)) {
			player.remove(GRUMPY);
		}
	}
	
	public void convertOldfeaturesList(final Player player) {
		if (player.has("features")) {
			logger.info("Converting features for " + player.getName() + ": "
					+ player.get("features"));

			final FeatureList features = new FeatureList();
			features.decode(player.get("features"));

			for (final String name : features) {
				player.setFeature(name, features.get(name));
			}

			player.remove("features");
		}
	}
	/**
	 * Loads the items into the slots of the player on login.
	 * 
	 * @param player
	 *            Player
	 */
	void loadItemsIntoSlots(final Player player) {

		// load items
		final String[] slotsItems = { "bag", "rhand", "lhand", "head", "armor",
				"legs", "feet", "finger", "cloak", "keyring" };

		try {
			for (final String slotName : slotsItems) {
				final RPSlot slot = player.getSlot(slotName);
				final PlayerSlot newSlot = new PlayerSlot(slotName);
				loadSlotContent(player, slot, newSlot);
			}

			for (final Banks bank : Banks.values()) {
				final RPSlot slot = player.getSlot(bank.getSlotName());
				final PlayerSlot newSlot = new BankSlot(bank);
				loadSlotContent(player, slot, newSlot);
			}
		} catch (final RuntimeException e) {
			logger.error("cannot create player", e);
		}
	}
	private static Logger logger = Logger.getLogger(PlayerTransformer.class);
	
	/**
	 * Places the player (and his/her sheep if there is one) into the world on
	 * login.
	 * 
	 * @param object
	 *            RPObject representing the player
	 * @param player
	 *            Player-object
	 */
	void placePlayerIntoWorldOnLogin(final RPObject object, final Player player) {
		StendhalRPZone zone = null;

		try {
			if (object.has("zoneid") && object.has("x") && object.has("y")) {
				if (object.get("release").equals(Debug.VERSION)) {
					zone = SingletonRepository.getRPWorld().getZone(object.get("zoneid"));
				} else {
					player.put("release", Debug.VERSION);
					if (player.getLevel() >= 2) {
						TutorialNotifier.newrelease(player);
					}
				}
			}
		} catch (final RuntimeException e) {
			// If placing the player at its last position
			// fails, we reset to default zone
			logger.warn(
					"Cannot place player at its last position. Using default",
					e);
		}

		if (zone != null) {
			/*
			 * Put the player in their zone (use placeat() for collision rules)
			 */
			if (!StendhalRPAction.placeat(zone, player, player.getX(),
					player.getY())) {
				logger.warn("Cannot place player at their last position: "
						+ player.getName());
				zone = null;
			}
		}

		if (zone == null) {
			/*
			 * Fallback to default zone
			 */
			final String defaultZoneName = getDefaultZoneForPlayer(player);
			zone = SingletonRepository.getRPWorld().getZone(defaultZoneName);

			if (zone == null) {
				logger.error("Unable to locate default zone ["
						+ defaultZoneName + "]");
				return;
			}

			zone.placeObjectAtEntryPoint(player);
		}


	}

	private void placeSheepAndPetIntoWorld(final Player player) {
		// load sheep
		final Sheep sheep = player.getPetOwner().retrieveSheep();

		if (sheep != null) {
			logger.debug("Player has a sheep");

			if (!sheep.has("base_hp")) {
				sheep.initHP(10);
			}

			if (placeAnimalIntoWorld(sheep, player)) {
				player.setSheep(sheep);
			} else {
				logger.warn("Could not place sheep: " + sheep);
				player.sendPrivateText("You can not seem to locate your "
						+ sheep.getTitle() + ".");
			}

			sheep.notifyWorldAboutChanges();
		}

		// load pet
		final Pet pet = player.getPetOwner().retrievePet();

		if (pet != null) {
			logger.debug("Player has a pet");

			if (!pet.has("base_hp")) {
				pet.initHP(200);
			}

			if (placeAnimalIntoWorld(pet, player)) {
				player.setPet(pet);
			} else {
				logger.warn("Could not place pet: " + pet);
				player.sendPrivateText("You can not seem to locate your "
						+ pet.getTitle() + ".");
			}

			pet.notifyWorldAboutChanges();
		}
	}
	
	/**
	 * Loads the items into the slots of the player on login.
	 * 
	 * @param player
	 *            Player
	 * @param slot
	 *            original slot
	 * @param newSlot
	 *            new Stendhal specific slot
	 */
	private void loadSlotContent(final Player player, final RPSlot slot,
			final PlayerSlot newSlot) {
		final List<RPObject> objects = new LinkedList<RPObject>();
		for (final RPObject objectInSlot : slot) {
			objects.add(objectInSlot);
		}
		slot.clear();
		player.removeSlot(slot.getName());
		player.addSlot(newSlot);

		for (final RPObject item : objects) {
			try {
				// We simply ignore corpses...
				if (item.get("type").equals("item")) {
					
					if (ITEMS_FOR_ADMINS.contains(item.get("name")) && (!player.has("adminlevel") || player.getInt("adminlevel") < 1000)) {
						logger.warn("removed admin item " + item.get("name") + " from player " + player.getName());
						String quantity = "1";
						if (item.has("quantity")) {
							quantity = item.get("quantity");
						}
						new ItemLogger().addItemLogEntry(new ItemLogEntry(item, player, "destroy", item.get("name"), quantity, "playertransformer", slot.getName()));
						continue;
					}

					final String name = UpdateConverter.updateItemName(item.get("name"));
					final Item entity = UpdateConverter.updateItem(name);

					// log removed items
					if (entity == null) {
						int quantity = 1;
						if (item.has("quantity")) {
							quantity = item.getInt("quantity");
						}
						logger.warn("Cannot restore " + quantity + " " + name
								+ " on login of " + player.getName()
								+ " because this item"
								+ " was removed from items.xml");
						new ItemLogger().destroyOnLogin(player, newSlot, item);
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
						
						// If we've updated the item name we don't want persistent reverting it
						entity.put("name", name);
					}

					if (entity instanceof StackableItem) {
						int quantity = 1;
						if (item.has("quantity")) {
							quantity = item.getInt("quantity");
						} else {
							logger.warn("Adding quantity=1 to "
									+ item
									+ ". Most likely cause is that this item was not stackable in the past");
						}
						((StackableItem) entity).setQuantity(quantity);

						if (quantity <= 0) {
							logger.warn("Ignoring item "
									+ name
									+ " on login of player "
									+ player.getName()
									+ " because this item has an invalid quantity: "
									+ quantity);
							continue;
						}
					}

					// make sure saved individual information is
					// restored
					final String[] individualAttributes = { "infostring",
							"description", "bound", "undroppableondeath" };
					for (final String attribute : individualAttributes) {
						if (item.has(attribute)) {
							entity.put(attribute, item.get(attribute));
						}
					}

					boundOldItemsToPlayer(player, entity);

					if (item.has("logid")) {
						entity.put("logid", item.get("logid"));
					}
					new ItemLogger().loadOnLogin(player, newSlot, entity);
					newSlot.add(entity);
				} else {
					logger.warn("Non-item object found in " + player.getName()
							+ "[" + slot.getName() + "]: " + item);
				}
			} catch (final Exception e) {
				logger.error("Error adding " + item + " to player slot" + slot,
						e);
			}
		}
	}
	/**
	 * binds special items to the player.
	 * 
	 * @param player
	 *            Player
	 * @param item
	 *            Item
	 */
	private void boundOldItemsToPlayer(final Player player, final Item item) {
		if (ITEMS_TO_UNBIND.contains(item.getName())) {
			item.setBoundTo(null);
			return;
		}

		// No special processing needed, if the item is already bound
		// and we didn't want to unbind it
		if (item.isBound()) {
			return;
		}

		if (ITEMS_TO_BIND.contains(item.getName())) {
			item.setBoundTo(player.getName());
		}

	}


	
	public static final String DEFAULT_ENTRY_ZONE = "int_semos_guard_house";
	public static final String RESET_ENTRY_ZONE = "int_semos_townhall";

	/**
	 * Low level players have a different start zone.
	 *
	 * @param player Player
	 * @return name of start zone
	 */
	private String getDefaultZoneForPlayer(final Player player) {
		if (player.getLevel() < 2) {
			return DEFAULT_ENTRY_ZONE;
		} else {
			return RESET_ENTRY_ZONE;
		}
	}

	/**
	 * Places a domestic animal in the world. If it matches it's owner's zone,
	 * then try to keep it's position.
	 * 
	 * @param animal
	 *            The domestic animal.
	 * @param player
	 *            The owner.
	 * 
	 * @return <code>true</code> if placed.
	 */
	protected boolean placeAnimalIntoWorld(final DomesticAnimal animal,
			final Player player) {
		final StendhalRPZone playerZone = player.getZone();

		/*
		 * Only add directly if required attributes are present
		 */
		if (animal.has("zoneid") && animal.has("x") && animal.has("y")) {
			final StendhalRPZone zone = SingletonRepository.getRPWorld().getZone(
					animal.get("zoneid"));

			/*
			 * Player could have been forced to change zones
			 */
			if (zone == playerZone) {
				if (StendhalRPAction.placeat(zone, animal, animal.getX(),
						animal.getY())) {
					return true;
				}
			}
		}

		return StendhalRPAction.placeat(playerZone, animal, player.getX(),
				player.getY());
	}

}
