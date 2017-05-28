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
package games.stendhal.server.entity.mapstuff.sound;

import games.stendhal.common.constants.SoundLayer;
import games.stendhal.server.entity.PassiveEntity;
import marauroa.common.game.Definition.DefinitionClass;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;

/**
 * Plays a sound and music in a loop.
 *
 * @author hendrik
 */
public class LoopedSoundSource extends PassiveEntity {
	private static final String RPCLASS_NAME = "looped_sound_source";


	/**
	 * Create an ambient sound area.
	 */
	public LoopedSoundSource() {
		setRPClass(RPCLASS_NAME);
		put("type", RPCLASS_NAME);
	}


	/**
	 * Create an ambient sound area.
	 *
	 * @param sound sound name
	 * @param radius
	 * @param volume
	 * @param layer
	 */
	public LoopedSoundSource(String sound, int radius, int volume, SoundLayer layer) {
		setRPClass(RPCLASS_NAME);
		put("type", RPCLASS_NAME);
		put("sound", sound);
		put("radius", radius);
		put("volume", volume);
		put("layer", layer.ordinal());
	}


	/**
	 * generates the RPClass
	 */
	public static void generateRPClass() {
		final RPClass rpclass = new RPClass(RPCLASS_NAME);
		rpclass.isA("area");
		rpclass.addAttribute("sound", Type.STRING);
		rpclass.addAttribute("radius", Type.INT);
		rpclass.addAttribute("volume", Type.BYTE);
		rpclass.add(DefinitionClass.ATTRIBUTE, "layer", Type.BYTE);
	}

}
