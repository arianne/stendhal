/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.slot;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.OwnedItem;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPSlot;
import marauroa.common.game.SlotOwner;
import marauroa.server.db.DBTransaction;
import marauroa.server.db.TransactionPool;
import marauroa.server.game.db.CharacterDAO;
import marauroa.server.game.db.DAORegister;


/**
 * Slot for managing item correspondence between players.
 *
 * FIXME:
 * - might be better to use two class properties "inbox"/"outbox"
 * - should not be able to send item to player if their inbox is already occupied
 * - might be better to use slot(s) not attached to a specific player
 */
public class Mailbox extends PlayerSlot {

	private static final Logger logger = Logger.getLogger(Mailbox.class);

	public static enum MailboxResult {
		CONTENTS_EMPTY,
		CANNOT_CARRY,
		INVALID_ITEM,
		OWNED_ITEM,
		RECEIVER_NOT_FOUND,
		RECEIVER_ACCEPTED,
		RECEIVER_DENIED,
		ABORTER_ACCEPTED,
		ABORTER_DENIED,
		SQL_ERROR
	}

	/** Name of player meant to receive contents. */
	private String target;


	public Mailbox() {
		super("mailbox");
	}

	/**
	 * Retrieves targeted receiver.
	 *
	 * @return
	 *   Name of player to receive item.
	 */
	public String getTarget() {
		return target;
	}

	@Override
	public int getCapacity() {
		return 1;
	}

	/**
	 * Configures player meant to received contents.
	 *
	 * @param target
	 *   Player name.
	 * @param item
	 *   Item to add to slot.
	 * @return
	 *   Mailbox action result.
	 * @throws SQLException
	 */
	public MailboxResult requestSend(final String target, final Item item) {
		if (item == null) {
			return MailboxResult.INVALID_ITEM;
		}
		if (item.isBound() || (item instanceof OwnedItem && ((OwnedItem) item).hasOwner())) {
			return MailboxResult.OWNED_ITEM;
		}
		final DBTransaction transaction = TransactionPool.get().beginWork();
		final CharacterDAO characterDAO = DAORegister.get().get(CharacterDAO.class);
		String accountName;
		try {
			accountName = characterDAO.getAccountName(transaction, target);
			if (accountName == null) {
				return MailboxResult.RECEIVER_NOT_FOUND;
			}
			this.target = accountName;
			// NOTE: does this also remove it from the source slot?
			add(item);
			return MailboxResult.RECEIVER_ACCEPTED;
		} catch (final SQLException e) {
			logger.error("Error occurred when looking up account for character \"" + target + "\"", e);
		}
		return MailboxResult.SQL_ERROR;
	}

	/**
	 * Requests retrieval of contents.
	 *
	 * @param player
	 *   Player making request.
	 * @return
	 *   Mailbox action result.
	 */
	public MailboxResult requestReceive(final Player player) {
		if (target == null) {
			logger.warn("Target receiver not assigned for " + this);
			return MailboxResult.RECEIVER_DENIED;
		}
		final String name = player.getName();
		if (!name.equals(target)) {
			logger.warn("Requested contents of " + this + " for player " + name);
			return MailboxResult.RECEIVER_DENIED;
		}
		final Item item = (Item) getFirst();
		if (item == null) {
			logger.warn("Requested contents of empty " + this);
			return MailboxResult.CONTENTS_EMPTY;
		}
		final RPSlot slot = player.getSlotToEquip(item);
		if (slot == null) {
			return MailboxResult.CANNOT_CARRY;
		}
		// NOTE: does this also remove it from the mailbox slot?
		slot.add(item);
		if (getFirst() != null) {
			logger.warn("Contents received but contents not empty in " + this);
		}
		// reset receiver
		target = null;
		return MailboxResult.RECEIVER_ACCEPTED;
	}

	/**
	 * Aborts request to send item and returns to sender.
	 *
	 * @param player
	 *   Player requesting abort.
	 * @return
	 *   Mailbox action result.
	 */
	public MailboxResult requestAbort(final Player player) {
		final String name = player.getName();
		final SlotOwner owner = getOwner();
		if (!name.equals(owner.get("name"))) {
			return MailboxResult.ABORTER_DENIED;
		}
		final Item item = (Item) getFirst();
		if (item == null) {
			logger.warn("Requested contents of empty " + this);
			return MailboxResult.CONTENTS_EMPTY;
		}
		final RPSlot slot = player.getSlotToEquip(item);
		if (slot == null) {
			return MailboxResult.CANNOT_CARRY;
		}
		// NOTE: does this also remove it from the mailbox slot?
		slot.add(item);
		if (getFirst() != null) {
			logger.warn("Contents received but contents not empty in " + this);
		}
		// reset receiver
		target = null;
		return MailboxResult.ABORTER_ACCEPTED;
	}
}
