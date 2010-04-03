/* $Id$ */
/***************************************************************************
 *                    (C) Copyright 2003-2010 - Stendhal                   *
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
	private String title;
	private String alt;

	/**
	 * Creates a new PopupImage
	 *
	 * @param image the image to display
	 * @param title the title
	 * @param alt alternative text incase the image cannot be displayed
	 */
	public PopupImage(final String image, final String title, final String alt) {
		put(Actions.ACTION, Actions.LOOK_CLOSELY);
		this.image = image;
		this.title = title;
		this.alt = alt;
	}

	public boolean onUsed(RPEntity user) {
		user.addEvent(new ExamineEvent("examine/" + image, title, alt));
		return true;
	}

}
