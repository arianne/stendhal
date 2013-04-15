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
package games.stendhal.client.events;

import games.stendhal.client.entity.Creature;
import games.stendhal.client.entity.RPEntity;
import games.stendhal.client.gui.wt.core.WtWindowManager;


/**
 * public chat and creature (text) noise.
 *
 * @author hendrik
 */
public class PublicTextEvent extends Event<RPEntity> {

	/**
	 * executes the event
	 */
	@Override
	public void execute() {
		boolean showCreatureSpeech = Boolean.parseBoolean(WtWindowManager.getInstance().getProperty("gamescreen.creaturespeech", "true"));
		System.out.println(showCreatureSpeech);
		if (entity instanceof Creature) {
			if (showCreatureSpeech) {
				entity.onTalk(event.get("text"));
			}
		}
		else {
			entity.onTalk(event.get("text"));
		}
	}

}
