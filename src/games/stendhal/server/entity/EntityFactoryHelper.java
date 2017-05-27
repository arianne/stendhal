/*
 * @(#) src/games/stendhal/server/entity/EntityFactoryHelper.java
 *
 * $Id$
 */

package games.stendhal.server.entity;

import java.util.Map;
import java.util.Map.Entry;

//
//

import games.stendhal.server.core.config.factory.ConfigurableFactory;
import games.stendhal.server.core.config.factory.ConfigurableFactoryContext;
import games.stendhal.server.core.config.factory.ConfigurableFactoryHelper;

/**
 * A utility class for creating entities using ConfigurableFactory.
 */
public class EntityFactoryHelper {
	/**
	 * Create an entity using a [logical] class name, and apply optional
	 * attribute values.
	 *
	 * @param className
	 *            A base class name to load.
	 * @param parameters
	 *            A collection of factory parameters.
	 * @param attributes
	 *            A collection of entity attributes, or <code>null</code>.
	 *
	 * @return A new entity, or <code>null</code> if allowed by the factory
	 *         type.
	 *
	 * @throws IllegalArgumentException
	 *             If there is a problem with the attributes. The exception
	 *             message should be a value suitable for meaningful user
	 *             interpretation.
	 *
	 * @see ConfigurableFactory
	 */
	public static Entity create(final String className,
			final Map<String, String> parameters, final Map<String, String> attributes) {

		final ConfigurableFactory factory = ConfigurableFactoryHelper.getFactory(className);
		if (factory == null) {
			return null;
		}

		final Object obj = factory.create(new ConfigurableFactoryContext(parameters));

		if (!(obj instanceof Entity)) {
			throw new IllegalArgumentException(obj.getClass().getName()
					+ " is not an instance of Entity");
		}

		final Entity entity = (Entity) obj;

		/*
		 * Apply optional attributes
		 */
		if (attributes != null) {
			for (Entry<String, String> entry : attributes.entrySet()) {
				try {
					entity.put(entry.getKey(), entry.getValue());
				} catch (final Exception ex) {
					throw new IllegalArgumentException(
							"Unable to set attribute '" + entry.getKey() + "' on "
									+ entity.getClass().getName(), ex);
				}
			}

			/*
			 * Sync the internal state
			 */
			if (!attributes.isEmpty()) {
				entity.update();
			}
		}

		return entity;
	}
}
