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
package games.stendhal.server.entity.npc.action;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

import games.stendhal.common.NotificationType;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;

/**
 * Sends the message as a private text
 * Optional notification type when used with NPCs
 */
@Dev(category=Category.CHAT, label="Message")
public class SendPrivateMessageAction implements ChatAction {

	private final String text;
	private final NotificationType type;

	/**
	 * Creates a new SendPrivateMessageAction.
	 *
	 * @param text text to send
	 */
	public SendPrivateMessageAction(String text) {
		this.text = checkNotNull(text);
		this.type = NotificationType.PRIVMSG;
	}

	/**
	 * Creates a new SendPrivateMessageAction - does NOT work with portals
	 *
	 * @param text text to send
	 *
	 * @param type type of message
	 */
	@Dev
	public SendPrivateMessageAction(@Dev(defaultValue="SERVER") final NotificationType type, final String text) {
		this.text = checkNotNull(text);
		this.type = type;
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		player.sendPrivateText(type, text);
	}

	@Override
	public String toString() {
		// would need to send the type toString also to include the type here
		return "Send Private Message<" + type + ", " + text + ">";
	}

	@Override
	public int hashCode() {
		return 5449 * (text.hashCode() + 5471 * (type == null ? 0 : type.hashCode()));
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof SendPrivateMessageAction)) {
			return false;
		}
		SendPrivateMessageAction other = (SendPrivateMessageAction) obj;
		return text.equals(other.text)
			&& Objects.equal(type, other.type);
	}
}
