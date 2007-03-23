/*
 * @(#) src/games/stendhal/server/entity/portal/KeyDestroyingDoorFactory.java
 *
 * $Id$
 */

package games.stendhal.server.entity.portal;

//
//

import games.stendhal.common.ConfigurableFactory;
import games.stendhal.common.ConfigurableFactoryContext;

/**
 * A factory for <code>KeyDestroyingDoor</code> objects.
 */
public class KeyDestroyingDoorFactory extends LockedDoorFactory {

	//
	// ConfigurableFactory
	//

	/**
	 * Create a locked door.
	 *
	 * @param	ctx		Configuration context.
	 *
	 * @return	A KeyDestroyingDoor.
	 *
	 * @throws	IllegalArgumentException
	 *				If there is a problem with the
	 *				attributes. The exception message
	 *				should be a value sutable for
	 *				meaningful user interpretation.
	 *
	 * @see		KeyDestroyingDoor
	 */
	@Override
	public Object create(ConfigurableFactoryContext ctx) throws IllegalArgumentException {
		return new KeyDestroyingDoor(getKey(ctx), getClass(ctx), getDirection(ctx));
	}

	//
	//

	public static void main(String[] args) throws Exception {
		ConfigurableFactory factory;
		java.util.Map<String, String> attrs;
		ConfigurableFactoryContext ctx;

		factory = games.stendhal.common.ConfigurableFactoryHelper
		        .getFactory("games.stendhal.server.entity.portal.KeyDestroyingDoor");
		//		factory = games.stendhal.common.ConfigurableFactoryHelper.getFactory("games.stendhal.server.entity.portal.OneWayPortalDestination");

		attrs = new java.util.HashMap<String, String>();
		attrs.put("key", "magical_flute");
		attrs.put("class", "music");
		attrs.put("direction", "left");

		ctx = new games.stendhal.common.ConfigurableFactoryContextImpl(attrs);

		// Object's toString() blows up because it requires world
		System.out.println("door: " + factory.create(ctx).getClass());
	}
}
