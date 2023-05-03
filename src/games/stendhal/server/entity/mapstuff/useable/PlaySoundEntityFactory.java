/***************************************************************************
 *                    Copyright © 2010-2023 - Stendhal                     *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.mapstuff.useable;

import games.stendhal.server.core.config.factory.ConfigurableFactoryContext;
import games.stendhal.server.entity.mapstuff.sound.LoopedAmbientSoundSource;
import games.stendhal.server.entity.mapstuff.sound.SoundSourceFactory;

/**
 * A factory for PeriodicAmbientSoundSource.
 */
public class PlaySoundEntityFactory extends SoundSourceFactory {

	/**
	 * Create an PlaySoundEntity.
	 *
	 * @param ctx
	 *            Configuration context.
	 *
	 * @return An PlaySoundEntity.
	 *
	 * @throws IllegalArgumentException
	 *             If there is a problem with the attributes.
	 * @see LoopedAmbientSoundSource
	 */
	@Override
	public PlaySoundEntity create(final ConfigurableFactoryContext ctx) {
		PlaySoundEntity source;

		source = new PlaySoundEntity(getSound(ctx), getRadius(ctx), getVolume(ctx));

		return source;
	}

}
