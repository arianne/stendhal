/***************************************************************************
 *                 (C) Copyright 2003-2013 - Stendhal team                 *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.item.behavior;

import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.NPC;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPObject;

/**
 * A UseBehavior that can a public and private messages.
 */
public class MessagingUseBehavior implements UseBehavior {
	private static final Logger LOGGER = Logger.getLogger(MessagingUseBehavior.class);

	private final String publicMessage;
	private final String privateMessage;

	/**
	 * Create a new MessagingUseBehavior with specified public and private
	 * messages. Either of those can be <code>null</code>, but a warning is
	 * logged if neither is specified.
	 *
	 * @param publicMessage message send as public text
	 * @param privateMessage message sent only to the using player
	 */
	private MessagingUseBehavior(String publicMessage, String privateMessage) {
		this.publicMessage = publicMessage;
		this.privateMessage = privateMessage;
		if ((publicMessage == null) && (privateMessage == null)) {
			LOGGER.warn("MessagingUseBehavior with no messages");
		}
	}

	/**
	 * Create a new MessagingUseBehavior. This constructor is meant for the
	 * item XML loader.
	 *
	 * @param params map of parameters. The values of "public" and "private"
	 * 	should contain the wanted public and private messages respectively
	 */
	public MessagingUseBehavior(Map<String, String> params) {
		this(params.get("public"), params.get("private"));
	}

	@Override
	public boolean use(RPEntity user, Item item) {
		RPObject base = item.getBaseContainer();

		if ((base instanceof Entity) && user.nextTo((Entity) base)) {
			sendMessages(user);
			return true;
		}
		int amount = item.getQuantity();
		sendPrivateMessage(user, Grammar.ThatThose(amount) + " "
				+ Grammar.plnoun(amount, item.getName()) + " "
				+ Grammar.isare(amount) + " too far away.");

		return false;
	}

	/**
	 * Send the messages.
	 *
	 * @param user entity using the item
	 */
	private void sendMessages(RPEntity user) {
		if (publicMessage != null) {
			if (user instanceof Player) {
				Player player = (Player) user;
				player.put("text", publicMessage);
				SingletonRepository.getRuleProcessor().removePlayerText(player);
			} else if (user instanceof NPC) {
				((NPC) user).say(publicMessage);
			}
		}
		if (privateMessage != null) {
			sendPrivateMessage(user, privateMessage);
		}
		user.notifyWorldAboutChanges();
	}

	/**
	 * Send the private message. This is only sent if the recipient is a player.
	 *
	 * @param user using entity
	 * @param message message text
	 */
	private void sendPrivateMessage(RPEntity user, String message) {
		if (user instanceof Player) {
			Player player = (Player) user;
			player.sendPrivateText(message);
		}
	}
}
