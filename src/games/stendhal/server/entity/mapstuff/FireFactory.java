package games.stendhal.server.entity.mapstuff;

import games.stendhal.server.core.config.factory.ConfigurableFactory;
import games.stendhal.server.core.config.factory.ConfigurableFactoryContext;

/**
 * A factory for <code>Fire</code> objects.
 */
public class FireFactory implements ConfigurableFactory {

	public Object create(final ConfigurableFactoryContext ctx) {
		return new Fire(ctx.getRequiredInt("width"),
				ctx.getRequiredInt("height"));
	}
}
