package games.stendhal.server.entity.mapstuff.chest;

import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


import org.apache.log4j.Logger;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * A PersonalChest is a Chest that can be used by everyone, but shows different
 * contents depending on the player who is currently using it. Thus, a player
 * can put in items into this chest and be sure that nobody else will be able to
 * take them out.
 * <p>
 * Caution: each PersonalChest must be placed in such a way that only one player
 * can stand next to it at a time, to prevent other players from stealing while
 * the owner is looking at his items. TODO: fix this.
 */
public class PersonalChest extends Chest {
	private static Logger logger = Logger.getLogger(PersonalChest.class);

	/**
	 * The default bank slot name.
	 */
	public static final String DEFAULT_BANK = "bank";

	private RPEntity attending;

	private String bankName;

	/**
	 * Create a personal chest using the default bank slot.
	 */
	public PersonalChest() {
		this(DEFAULT_BANK);
	}

	/**
	 * Create a personal chest using a specific bank slot.
	 *
	 * @param bankName
	 *            The name of the bank slot.
	 */
	public PersonalChest(String bankName) {
		this.bankName = bankName;
		attending = null;
	}

	/**
	 * Copies an item
	 *
	 * TODO: Move this to Item.copy() to hide impl (and eventually remove
	 * reflection).
	 *
	 * @param item
	 *            item to copy
	 * @return copy
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private RPObject cloneItem(RPObject item) throws NoSuchMethodException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		Class< ? > clazz = item.getClass();
		Constructor<?> ctor = clazz.getConstructor(clazz);
		Item clone = (Item) ctor.newInstance(item);
		return clone;
	}

	/**
	 * Get the slot that holds items for this chest.
	 *
	 * @return A per-player/per-bank slot.
	 */
	protected RPSlot getBankSlot() {
		/*
		 * It's assumed attending != null when called
		 */
		return attending.getSlot(bankName);
	}

	/**
	 * Sync the slot contents.
	 *
	 * @return <code>true</code> if it should be called again.
	 */
	protected boolean syncContent() {
		if (attending != null) {
			/* Can be replaced when we add Equip event */
			/* Mirror chest content into player's bank slot */
			RPSlot bank = getBankSlot();
			bank.clear();

			for (RPObject item : getSlot("content")) {
				bank.addPreservingId(item);
			}

			RPSlot content = getSlot("content");
			content.clear();

			// Verify the user is next to the chest
			if ((getZone() == attending.getZone()) && nextTo(attending)) {
				// A hack to allow client update correctly the
				// chest...
				// by clearing the chest and copying the items
				// back to it from the player's bank slot
				for (RPObject item : getBankSlot()) {
					try {
						content.addPreservingId(cloneItem(item));
					} catch (Exception e) {
						logger.error("Cannot clone item " + item, e);
					}
				}

				return true;
			} else {
				// If player is not next to depot, clean it.
				close();
				notifyWorldAboutChanges();
			}
		}

		return false;
	}

	/**
	 * Open the chest for an attending user.
	 *
	 * @param user
	 *            The attending user.
	 */
	public void open(RPEntity user) {
		attending = user;

		TurnNotifier.get().notifyInTurns(0, new SyncContent());

		RPSlot content = getSlot("content");
		content.clear();

		for (RPObject item : getBankSlot()) {
			try {
				content.addPreservingId(cloneItem(item));
			} catch (Exception e) {
				logger.error("Cannot clone item " + item, e);
			}
		}

		super.open();
	}

	/**
	 * Close the chest.
	 */
	@Override
	public void close() {
		super.close();

		attending = null;
	}

	/**
	 * Don't let this be called directly for personal chests.
	 */
	@Override
	public void open() {
		throw new RuntimeException("User context required to open");
	}

	@Override
	public boolean onUsed(RPEntity user) {
		if (user.nextTo(this)) {
			if (isOpen()) {
				close();
			} else {
				open(user);
			}

			notifyWorldAboutChanges();
			return true;
		}

		return false;
	}

	//
	//

	/**
	 * A listener for syncing the slot contents.
	 */
	protected class SyncContent implements TurnListener {
		/**
		 * This method is called when the turn number is reached. NOTE: The
		 * <em>message</em> parameter is deprecated.
		 *
		 * @param currentTurn
		 *            The current turn number.
		 */
		public void onTurnReached(int currentTurn) {
			if (syncContent()) {
				TurnNotifier.get().notifyInTurns(0, this);
			}
		}
	}
}
