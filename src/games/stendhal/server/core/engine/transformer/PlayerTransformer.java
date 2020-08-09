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
package games.stendhal.server.core.engine.transformer;

import static games.stendhal.common.constants.Actions.AWAY;
import static games.stendhal.common.constants.Actions.GRUMPY;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.common.Debug;
import games.stendhal.common.FeatureList;
import games.stendhal.common.Version;
import games.stendhal.server.core.engine.ItemLogger;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TutorialNotifier;
import games.stendhal.server.core.rp.StendhalQuestSystem;
import games.stendhal.server.core.rp.StendhalRPAction;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.creature.DomesticAnimal;
import games.stendhal.server.entity.creature.Pet;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.SlotActivatedItem;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.player.UpdateConverter;
import games.stendhal.server.entity.slot.BankSlot;
import games.stendhal.server.entity.slot.Banks;
import games.stendhal.server.entity.slot.PlayerKeyringSlot;
import games.stendhal.server.entity.slot.PlayerMoneyPouchSlot;
import games.stendhal.server.entity.slot.PlayerSlot;
import games.stendhal.server.entity.slot.PlayerTradeSlot;
import games.stendhal.server.entity.spell.Spell;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

public class PlayerTransformer implements Transformer {

	@Override
	public RPObject transform(final RPObject object) {
		return create(object);
	}

	/** these items should be unbound.*/
	private static final List<String> ITEMS_TO_UNBIND = Arrays.asList("marked scroll");

	/** these items should be deleted for non admins */
	private static final List<String> ITEMS_FOR_ADMINS = Arrays.asList("rod of the gm", "master key");

	public Player create(final RPObject object) {

		removeVolatile(object);

		// add attributes and slots
		UpdateConverter.updatePlayerRPObject(object);

		final Player player = new Player(object);
		player.stop();
		player.stopAttack();

		loadItemsIntoSlots(player);
		loadSpellsIntoSlots(player);
		player.cancelTradeInternally(null);

		// buddy handling with maps
		if(player.hasBuddies()) {
			for(String buddyName : player.getBuddies()) {
				final Player buddy = SingletonRepository.getRuleProcessor().getPlayer(buddyName);
				if ((buddy != null) && !buddy.isGhost()) {
					player.setBuddyOnlineStatus(buddyName, true);
				} else {
					player.setBuddyOnlineStatus(buddyName, false);
				}
			}
		}

		convertOldfeaturesList(player);

		player.updateItemAtkDef();
		StendhalQuestSystem.updatePlayerQuests(player);

		UpdateConverter.updateQuests(player);
		// Should be at least after converting the features list, as this
		// depends on checking the keyring feature.

		if (System.getProperty("stendhal.container") != null) {
			UpdateConverter.updateKeyring(player);
		}

		// update player with 'outfit_ext' attribute
		if (!player.has("outfit_ext")) {
			player.put("outfit_ext", new Outfit(player.get("outfit")).toString());
		}

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
				"legs", "feet", "finger", "cloak", "back", "belt", "keyring",
				/*"portfolio",*/ "trade", "pouch" };

		try {
			for (final String slotName : slotsItems) {
				if (!player.hasSlot(slotName)) {
					continue;
				}
				final RPSlot slot = player.getSlot(slotName);
				final PlayerSlot newSlot;
				if (slotName.equals("keyring")) {
					newSlot = new PlayerKeyringSlot(slotName);
				/*
				} else if (slotName.equals("portfolio")) {
					newSlot = new PlayerPortfolioSlot(slotName);
				*/
				} else if (slotName.equals("trade")) {
					newSlot = new PlayerTradeSlot(slotName);
				} else if (slotName.equals("pouch")) {
					newSlot = new PlayerMoneyPouchSlot(slotName);
				} else {
					newSlot = new PlayerSlot(slotName);
				}
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

	/**
	 * Transforms the RPObjects in the spells slots to the real spell objects
	 *
	 * @param player
	 */
	private void loadSpellsIntoSlots(Player player) {
		// load spells
		// use list of slot names to make code easily extendable in case spell can be put into
		// different slots
		final List<String> slotsSpells = Arrays.asList("spells");
		for(String slotName : slotsSpells) {
			RPSlot slot = player.getSlot(slotName);
			List<RPObject> objects = new LinkedList<RPObject>();
			// collect objects from slot before clearing and transforming
			for (final RPObject objectInSlot : slot) {
				objects.add(objectInSlot);
			}
			// clear the slot
			slot.clear();
			SpellTransformer transformer = new SpellTransformer();
			//transform rpobjects in slot to spell
			for(RPObject o : objects) {
				Spell s = (Spell) transformer.transform(o);
				//only add to slot if transforming was successful
				if(s != null) {
					slot.add(s);
				}
			}
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
	public static void placePlayerIntoWorldOnLogin(final RPObject object, final Player player) {
		StendhalRPZone zone = null;

		String zoneName = System.getProperty("stendhal.forcezone");
		if (zoneName != null) {
			zone = SingletonRepository.getRPWorld().getZone(zoneName);
			zone.placeObjectAtEntryPoint(player);
			return;
		}

		try {
			if (object.has("zoneid") && object.has("x") && object.has("y")) {
				if (Version.checkCompatibility(object.get("release"),Debug.VERSION)) {
					zone = SingletonRepository.getRPWorld().getZone(object.get("zoneid"));
				} else {
					if (player.getLevel() >= 2) {
						TutorialNotifier.newrelease(player);
					}
				}
				player.put("release", Debug.VERSION);
			}
		} catch (final RuntimeException e) {
			// If placing the player at its last position
			// fails, we reset to default zone
			logger.warn("Cannot place player at its last position. Using default", e);
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

	public static void placeSheepAndPetIntoWorld(final Player player) {
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

		ItemTransformer transformer = new ItemTransformer();
		for (final RPObject rpobject : objects) {
			try {
				// remove admin items the player does not deserve
				if (ITEMS_FOR_ADMINS.contains(rpobject.get("name"))
						&& (!player.has("adminlevel") || player.getInt("adminlevel") < 1000)) {
					logger.warn("removed admin item " + rpobject.get("name") + " from player " + player.getName());
					new ItemLogger().destroyOnLogin(player, slot, rpobject);

					continue;
				}

				Item item = transformer.transform(rpobject);

				// log removed items
				if (item == null) {
					int quantity = 1;
					if (rpobject.has("quantity")) {
						quantity = rpobject.getInt("quantity");
					}

					logger.warn("Cannot restore " + quantity + " " + rpobject.get("name")
							+ " on login of " + player.getName()
							+ " because this item"
							+ " was removed from items.xml");
					new ItemLogger().destroyOnLogin(player, slot, rpobject);

					continue;
				}

				boundOldItemsToPlayer(player, item);

				newSlot.add(item);

				/* Check if item has attributes that can be activated by a slot.
				 *
				 * XXX: Perhaps onEquipped() should be run for all items when
				 *      player is created.
				 */
				if (item instanceof SlotActivatedItem) {
					item.onEquipped(player, newSlot.getName());
				}
			} catch (final Exception e) {
				logger.error("Error adding " + rpobject + " to player slot" + slot,
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

		item.autobind(player.getName());
	}


	public static final String DEFAULT_ENTRY_ZONE = "int_semos_guard_house";
	public static final String RESET_ENTRY_ZONE = "int_semos_townhall";

	/**
	 * Low level players have a different start zone.
	 *
	 * @param player Player
	 * @return name of start zone
	 */
	private static String getDefaultZoneForPlayer(final Player player) {
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
	protected static boolean placeAnimalIntoWorld(final DomesticAnimal animal,
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
