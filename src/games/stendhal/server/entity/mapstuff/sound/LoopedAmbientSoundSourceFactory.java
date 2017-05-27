/*
 * @(#) src/games/stendhal/server/entity/area/CreatureProtectionAreaFactory.java
 *
 * $Id$
 */

package games.stendhal.server.entity.mapstuff.sound;

import games.stendhal.server.core.config.factory.ConfigurableFactoryContext;

/**
 * A factory for a LoopedAmbientSoundSource.
 */
public class LoopedAmbientSoundSourceFactory extends SoundSourceFactory {
	/**
	 * Create an LoopedAmbientSoundSource.
	 *
	 * @param ctx
	 *            Configuration context.
	 *
	 * @return An AmbientSoundSource.
	 *
	 * @throws IllegalArgumentException
	 *             If there is a problem with the attributes.
	 * @see LoopedAmbientSoundSource
	 */
	@Override
	public LoopedAmbientSoundSource create(final ConfigurableFactoryContext ctx) {
		LoopedAmbientSoundSource source;

		source = new LoopedAmbientSoundSource(getSound(ctx), getRadius(ctx), getVolume(ctx));

		return source;
	}
}
