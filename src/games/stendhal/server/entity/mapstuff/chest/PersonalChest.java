package games.stendhal.server.entity.mapstuff.chest;

import games.stendhal.server.entity.RPEntity;
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
	 * Open the chest for an attending user.
	 *
	 * @param user
	 *            The attending user.
	 */
	public void open(RPEntity user) {
		user.sendPrivateText("Banks is closed because of bug abusing and will not open until after the Christmas.");
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
}
