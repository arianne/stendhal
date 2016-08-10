/***************************************************************************
 *                    (C) Copyright 2003-2016 - Stendhal                   *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.mapstuff.sign;

import games.stendhal.common.constants.Actions;
import games.stendhal.server.core.events.UseListener;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.events.ExamineEvent;

/**
 * A sign (or transparent area) which is placed on the ground and can be looked at closely.
 */
public class PopupImage extends Sign implements UseListener {
	private String image;
	private final String title;
	private final String caption;

	/**
	 * Creates a new PopupImage
	 *
	 * @param image the image to display
	 * @param title the title
	 * @param caption text to display along the image
	 */
	public PopupImage(final String image, final String title, final String caption) {
		put(Actions.ACTION, Actions.LOOK_CLOSELY);
		this.image = image;
		if (!image.startsWith("http://") && !image.startsWith("https://")) {
			this.image = "examine/" + image;
		}
		this.title = title;
		this.caption = caption;
	}

	@Override
	public boolean onUsed(RPEntity user) {
		user.addEvent(new ExamineEvent(image, title, caption));
		user.notifyWorldAboutChanges();
		return true;
	}

}
