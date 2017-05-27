/***************************************************************************
 *                 (C) Copyright 2003-2015 - Faiumoni e.V.                 *
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

import org.apache.log4j.Logger;

import games.stendhal.client.GameScreen;
import games.stendhal.client.entity.Entity;
import games.stendhal.client.gui.BlackenScreenEffect;
import games.stendhal.client.gui.EffectLayer;
import games.stendhal.client.gui.LightningEffect;

/**
 * An event that tells the client to display a visual effect that affects the
 * entire game screen.
 */
class GlobalVisualEffectEvent extends Event<Entity> {
	private static final Logger LOGGER = Logger.getLogger(GlobalVisualEffectEvent.class);

	@Override
	public void execute() {
		EffectLayer effect = null;
		int duration = event.getInt("duration");
		String name = event.get("effect_name");
		switch (name) {
		case "blacken":
			effect = new BlackenScreenEffect(duration);
			break;
		case "lightning":
			effect = new LightningEffect(duration, event.getInt("strength"));
			break;
		default:
			LOGGER.error("Unknown effect name: " + name);
			return;
		}
		GameScreen.get().addEffect(effect);
	}
}
