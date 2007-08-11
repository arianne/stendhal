package games.stendhal.server.entity;

import games.stendhal.server.config.factory.ConfigurableFactory;
import games.stendhal.server.config.factory.ConfigurableFactoryContext;

/**
 * A factory for <code>Fire</code> objects.
 */
public class FireFactory implements ConfigurableFactory {

	public Object create(ConfigurableFactoryContext ctx) throws IllegalArgumentException {
		return new Fire(ctx.getRequiredInt("width"), ctx.getRequiredInt("height"));
	}
}
