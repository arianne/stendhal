/*
 * @(#) src/games/stendhal/server/entity/area/CreatureProtectionAreaFactory.java
 *
 * $Id$
 */

package games.stendhal.server.entity.mapstuff.sound;

import games.stendhal.server.core.config.factory.ConfigurableFactory;
import games.stendhal.server.core.config.factory.ConfigurableFactoryContext;

/**
 * A factory for SoundSources.
 */
public abstract class SoundSourceFactory implements ConfigurableFactory {


	/**
	 * gets the sound name
	 *
	 * @param ctx ConfigurableFactoryContext
	 * @return name of sound
	 */
	protected String getSound(ConfigurableFactoryContext ctx) {
		return ctx.getRequiredString("sound");
	}

	/**
	 * gets the radius, default to a value large enough to cover the complete zone
	 *
	 * @param ctx ConfigurableFactoryContext
	 * @return radius
	 */
	protected int getRadius(ConfigurableFactoryContext ctx) {
		// default to a value that is larger than all known zones
		return ctx.getInt("radius", 10000);
	}

	/**
	 * gets the volume, defaulting to 100%
	 *
	 * @param ctx ConfigurableFactoryContext
	 * @return volume
	 */
	protected int getVolume(ConfigurableFactoryContext ctx) {
		// default to 100%
		return ctx.getInt("volume", 100);
	}

}
