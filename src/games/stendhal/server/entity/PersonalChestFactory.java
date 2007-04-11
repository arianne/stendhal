// $Id$

package games.stendhal.server.entity;

import games.stendhal.common.ConfigurableFactory;
import games.stendhal.common.ConfigurableFactoryContext;
import games.stendhal.server.entity.slot.Banks;

/**
 * A factory for <code>PersonalChest</code> objects.
 */
public class PersonalChestFactory implements ConfigurableFactory {

	//
	// PersonalChestFactory
	//

	/**
	 * Extract the slot name from a context.
	 *
	 * @param	ctx		The configuration context.
	 * @return	The bank enum
	 * @throws	IllegalArgumentException if the attribute is invalid.
	 */
	protected Banks getSlot(ConfigurableFactoryContext ctx) throws IllegalArgumentException {
		if (ctx.getAttribute("bank") == null) {
			throw new IllegalArgumentException("Missing parameter bank");
		}
		return Banks.valueOf(ctx.getAttribute("bank"));
	}

	public Object create(ConfigurableFactoryContext ctx) throws IllegalArgumentException {
		return new PersonalChest(getSlot(ctx));
	}
}
