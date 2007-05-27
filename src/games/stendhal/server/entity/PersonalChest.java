package games.stendhal.server.entity;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.IRPZone;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

import org.apache.log4j.Logger;

/**
 * A PersonalChest is a Chest that can be used by everyone, but shows
 * different contents depending on the player who is currently using
 * it. Thus, a player can put in items into this chest and be sure that
 * nobody else will be able to take them out.
 * 
 * Caution: each PersonalChest must be placed in such a way that only one
 * player can stand next to it at a time, to prevent other players from
 * stealing while the owner is looking at his items.
 * TODO: fix this.
 */
public class PersonalChest extends Chest {
	private static Logger logger = Logger.getLogger(PersonalChest.class);

	/**
	 * The default bank slot name.
	 */
	public static final String DEFAULT_BANK = "bank";

	private Player attending;

	private IRPZone zone;

	private String bankName;

	/**
	 * Create a personal chest using the default bank slot.
	 */
	public PersonalChest() throws AttributeNotFoundException {
		this(DEFAULT_BANK);
	}

	/**
	 * Create a personal chest using a specific bank slot.
	 *
	 * @param	bankName	The name of the bank slot.
	 */
	public PersonalChest(String bankName) throws AttributeNotFoundException {
		this.bankName = bankName;

		attending = null;

		TurnListener turnListener = new TurnListener() {

			public void onTurnReached(int currentTurn, String message) {
				if (attending != null) {
					/* Can be replaced when we add Equip event */
					/* Mirror chest content into player's bank slot */
					RPSlot bank = getBankSlot();
					bank.clear();

					for (RPObject item : getSlot("content")) {
						bank.add(item);
					}

					RPSlot content = getSlot("content");
					content.clear();

					// if the player is next to the chest (and still logged in)
					if (nextTo(attending) && zone.has(attending.getID())) {
						// A hack to allow client update correctly the chest...
						// by clearing the chest and copying the items back to it
						// from the player's bank slot
						for (RPObject item : getBankSlot()) {
							try {
								content.add(cloneItem(item));
							} catch (Exception e) {
								logger.error("Cannot clone item " + item, e);
							}
						}

					} else {

						// If player is not next to depot, clean it.
						content.clear();
						close();
						PersonalChest.this.notifyWorldAboutChanges();

						attending = null;
					}
				}
				TurnNotifier.get().notifyInTurns(0, this);
			}
		};
		TurnNotifier.get().notifyInTurns(0, turnListener);
	}


	/**
	 * Copies an item
	 * 
	 * 
	 * @param item item to copy
	 * @return copy
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private RPObject cloneItem(RPObject item) throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		Class clazz = item.getClass();
		Constructor ctor = clazz.getConstructor(clazz);
		Item clone = (Item) ctor.newInstance(item);
		return clone;
	}

	/**
	 * Get the slot that holds items for this chest.
	 *
	 * @return	A per-player/per-bank slot.
	 */
	protected RPSlot getBankSlot() {
		/*
		 * It's assumed attending != null when called
		 */
		return attending.getSlot(bankName);
	}

	@Override
	public void onUsed(RPEntity user) {
		Player player = (Player) user;

		zone = StendhalRPWorld.get().getRPZone(player.getID());

		if (player.nextTo(this)) {
			if (isOpen()) {
				close();
			} else {
				attending = player;

				RPSlot content = getSlot("content");
				content.clear();

				for (RPObject item : getBankSlot()) {
					content.add(item);
				}

				open();
			}
			notifyWorldAboutChanges();
		}
	}
}
