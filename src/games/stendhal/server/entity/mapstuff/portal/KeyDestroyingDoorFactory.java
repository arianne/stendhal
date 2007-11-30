/*
 * @(#) src/games/stendhal/server/entity/portal/KeyDestroyingDoorFactory.java
 *
 * $Id$
 */

package games.stendhal.server.entity.mapstuff.portal;

import games.stendhal.server.config.factory.ConfigurableFactoryContext;

/**
 * A factory for <code>KeyDestroyingDoor</code> objects.
 */
public class KeyDestroyingDoorFactory extends LockedDoorFactory {


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
	 *				should be a value suitable for
	 *				meaningful user interpretation.
	 *
	 * @see		KeyDestroyingDoor
	 */
	@Override
	public Object create(ConfigurableFactoryContext ctx) {
		return new KeyDestroyingDoor(getKey(ctx), getClass(ctx));
	}


}
