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

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.ExamineEvent;

/**
 * Opens an examine window on the client showing an image
 *
 * @author hendrik
 */
@Dev(category=Category.CHAT, label="Image")
public class ExamineChatAction implements ChatAction {
	private String image;
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
		this.image = checkNotNull(image);
		if (!image.startsWith("http://") && !image.startsWith("https://")) {
			this.image = "examine/" + image;
		}
		this.title = checkNotNull(title);
		this.caption = checkNotNull(caption);
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
		player.addEvent(new ExamineEvent(image, title, caption));
		player.notifyWorldAboutChanges();
	}

	@Override
	public String toString() {
		return "ExamineChatAction <" + image + ">";
	}

	@Override
	public int hashCode() {
		return 5189 * (image.hashCode() + 5197 * (title.hashCode() + 5209 * caption.hashCode()));
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof ExamineChatAction)) {
			return false;
		}
		ExamineChatAction other = (ExamineChatAction) obj;
		return image.equals(other.image)
			&& title.equals(other.title)
			&& caption.equals(other.caption);
	}

}
