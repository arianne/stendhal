/*
 * @(#) src/games/stendhal/server/entity/EntityFactoryHelper.java
 *
 * $Id$
 */

package games.stendhal.server.entity;

//
//

import games.stendhal.common.ConfigurableFactory;
import games.stendhal.common.ConfigurableFactoryContextImpl;
import games.stendhal.common.ConfigurableFactoryHelper;

import java.util.Map;

/**
 * A utility class for creating entities using ConfigurableFactory.
 */
public class EntityFactoryHelper {
	/**
	 * Create an entity using a [logical] class name, and apply optional
	 * attribute values.
	 *
	 * @param	className	A base class name to load.
	 * @param	parameters	A collection of factory parameters.
	 * @param	attributes	A collection of entity attributes,
	 *				or <code>null</code>.
	 *
	 * @return	A new entity, or <code>null</code> if allowed by
	 *		the factory type.
	 *
	 * @throws	IllegalArgumentException
	 *				If there is a problem with the
	 *				attributes. The exception message
	 *				should be a value sutable for
	 *				meaningful user interpretation.
	 *
	 * @see-also	ConfigurableFactory
	 */
	public static Entity create(String className, Map<String, String> parameters, Map<String, String> attributes) throws IllegalArgumentException {
		ConfigurableFactory	factory;
		Object			obj;
		Entity			entity;


		if((factory = ConfigurableFactoryHelper.getFactory(className)) == null) {
			return null;
		}

		obj = factory.create(new ConfigurableFactoryContextImpl(parameters));

		if(!(obj instanceof Entity)) {
			throw new IllegalArgumentException(
				obj.getClass().getName()
					+ " is not an instance of Entity");
		}

		entity = (Entity) obj;


		/*
		 * Apply optional attributes
		 */
		if(attributes != null) {
			for(String name : attributes.keySet()) {
				try {
					entity.put(name, attributes.get(name));
				} catch(Exception ex) {
					throw new IllegalArgumentException(
						"Unable to set attribute '"
						+ name + "' on "
						+ entity.getClass().getName());
				}
			}
		}


		return entity;
	}
}
