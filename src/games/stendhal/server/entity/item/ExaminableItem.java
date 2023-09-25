
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.item;

import java.util.Map;

import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.events.ExamineEvent;

public class ExaminableItem extends StackableItem {

	public ExaminableItem(StackableItem item) {
		super(item);
		put("menu", "Look closely|use");
	}

	public ExaminableItem(String name, String clazz, String subclass, Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	@Override
	public boolean onUsed(RPEntity user) {

		String[] infostring = getInfoString().split("\t");
		String image = infostring[0];
		String title = infostring[1];
		String caption = infostring[2];

		user.addEvent(new ExamineEvent(image, title, caption));
		user.notifyWorldAboutChanges();
		return false;
	}

}
