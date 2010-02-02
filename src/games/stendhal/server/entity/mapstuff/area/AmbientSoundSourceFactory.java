/*
 * @(#) src/games/stendhal/server/entity/area/CreatureProtectionAreaFactory.java
 *
 * $Id$
 */

package games.stendhal.server.entity.mapstuff.area;

import games.stendhal.server.core.config.factory.ConfigurableFactory;
import games.stendhal.server.core.config.factory.ConfigurableFactoryContext;

/**
 * A factory for AmbientSoundSource.
 */
public class AmbientSoundSourceFactory implements ConfigurableFactory {


	/**
	 * gets the sound name
	 *
	 * @param ctx ConfigurableFactoryContext
	 * @return name of sound
	 */
	private String getSound(ConfigurableFactoryContext ctx) {
		return ctx.getRequiredString("sound");
	}

	/**
	 * gets the radius, default to a value large enough to cover the complete zone
	 *
	 * @param ctx ConfigurableFactoryContext
	 * @return radius
	 */
	private int getRadius(ConfigurableFactoryContext ctx) {
		// default to a value that is larger than all known zones
		return ctx.getInt("radius", 10000);
	}

	/**
	 * gets the volume, defaulting to 100%
	 *
	 * @param ctx ConfigurableFactoryContext
	 * @return volume
	 */
	private int getVolume(ConfigurableFactoryContext ctx) {
		// default to 100%
		return ctx.getInt("volume", 100);
	}

	/**
	 * Create an AmbientSoundSource.
	 * 
	 * @param ctx
	 *            Configuration context.
	 * 
	 * @return An AmbientSoundSource.
	 * 
	 * @throws IllegalArgumentException
	 *             If there is a problem with the attributes.
	 * @see AmbientSoundSource
	 */
	public AmbientSoundSource create(final ConfigurableFactoryContext ctx) {
		AmbientSoundSource source;

		source = new AmbientSoundSource(getSound(ctx), getRadius(ctx), getVolume(ctx));

		return source;
	}

}
