package games.stendhal.server.entity;

import games.stendhal.common.ConfigurableFactory;
import games.stendhal.common.ConfigurableFactoryContext;

/**
 * A factory for <code>FishSource</code> objects.
 */
public class FishSourceFactory implements ConfigurableFactory {

	/**
	 * Extract the species name from a context.
	 *
	 * @param	ctx		The configuration context.
	 *
	 * @return	The species name.
	 *
	 * @throws	IllegalArgumentException
	 *				If the attribute is invalid.
	 */
	protected String getSpecies(ConfigurableFactoryContext ctx) throws IllegalArgumentException {
		String s;

		if ((s = ctx.getAttribute("species")) == null) {
			throw new IllegalArgumentException("Required attribute 'species' missing");
		}

		return s;
	}

	//
	// ConfigurableFactory
	//

	/**
	 * Create a personal fish source.
	 *
	 * @param	ctx		Configuration context.
	 *
	 * @return	A FishSource.
	 *
	 * @throws	IllegalArgumentException
	 *				If there is a problem with the
	 *				attributes. The exception message
	 *				should be a value sutable for
	 *				meaningful user interpretation.
	 *
	 * @see		FishSource
	 */
	public Object create(ConfigurableFactoryContext ctx) throws IllegalArgumentException {
		return new FishSource(getSpecies(ctx));
	}
}
