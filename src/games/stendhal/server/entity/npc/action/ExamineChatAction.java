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

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.ExamineEvent;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Opens an examine window on the client showing an image
 *
 * @author hendrik
 */
@Dev(category=Category.CHAT, label="Image")
public class ExamineChatAction implements ChatAction {
	private final String image;
	private final String title;
	private final String caption;

	/**
	 * Creates a new ExamineChatAction
	 *
	 * @param image the image to display
	 * @param title the title
	 * @param caption text to display along the image
	 */
	public ExamineChatAction(final String image, final String title, final String caption) {
		this.image = image;
		this.title = title;
		this.caption = caption;
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
		player.addEvent(new ExamineEvent("examine/" + image, title, caption));
		player.notifyWorldAboutChanges();
	}

	@Override
	public String toString() {
		return "ExamineChatAction <" + image + ">";
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				ExamineChatAction.class);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

}
