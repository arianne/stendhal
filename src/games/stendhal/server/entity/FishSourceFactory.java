package games.stendhal.server.entity;

import games.stendhal.server.config.factory.ConfigurableFactory;
import games.stendhal.server.config.factory.ConfigurableFactoryContext;

/**
 * A factory for <code>FishSource</code> objects.
 */
public class FishSourceFactory implements ConfigurableFactory {

	/**
	 * Extract the species name from a context.
	 * 
	 * @param ctx The configuration context.
	 * @return The species name.
	 * @throws IllegalArgumentException If the attribute is invalid.
	 */
	protected String getSpecies(ConfigurableFactoryContext ctx)	throws IllegalArgumentException {
		return ctx.getRequiredString("species");
	}

	//
	// ConfigurableFactory
	//

	/**
	 * Create a personal fish source.
	 * 
	 * @param ctx
	 *            Configuration context.
	 * 
	 * @return A FishSource.
	 * 
	 * @throws IllegalArgumentException
	 *             If there is a problem with the attributes. The exception
	 *             message should be a value sutable for meaningful user
	 *             interpretation.
	 * 
	 * @see FishSource
	 */
	public Object create(ConfigurableFactoryContext ctx)
			throws IllegalArgumentException {
		return new FishSource(getSpecies(ctx));
	}
}
