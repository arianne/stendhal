/*
 * @(#) src/games/stendhal/server/entity/area/CreatureProtectionAreaFactory.java
 *
 * $Id$
 */

package games.stendhal.server.entity.mapstuff.sound;

import games.stendhal.server.core.config.factory.ConfigurableFactoryContext;

/**
 * A factory for PeriodicAmbientSoundSource.
 */
public class PeriodicAmbientSoundSourceFactory extends SoundSourceFactory {
	/**
	 * gets the minimum interval, defaulting to one minute
	 *
	 * @param ctx ConfigurableFactoryContext
	 * @return minInterval
	 */
	private int getMinInterval(ConfigurableFactoryContext ctx) {
		// default to 1 minute
		return ctx.getInt("min", 60);
	}

	/**
	 * gets the maximum interval, defaulting to 5 minutes
	 *
	 * @param ctx ConfigurableFactoryContext
	 * @return maxInterval
	 */
	private int getMaxInterval(ConfigurableFactoryContext ctx) {
		// default to 5 minutes
		return ctx.getInt("max", 5 * 60);
	}

	/**
	 * Create an PeriodicAmbientSoundSource.
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
	public PeriodicAmbientSoundSource create(final ConfigurableFactoryContext ctx) {
		PeriodicAmbientSoundSource source;

		source = new PeriodicAmbientSoundSource(getSound(ctx), getRadius(ctx), getVolume(ctx), getMinInterval(ctx), getMaxInterval(ctx));

		return source;
	}
}
