/*
 * @(#) src/games/stendhal/server/entity/portal/AccessCheckingPortalFactory.java
 *
 * $Id$
 */

package games.stendhal.server.entity.mapstuff.portal;

//
//

import games.stendhal.server.core.config.factory.ConfigurableFactory;
import games.stendhal.server.core.config.factory.ConfigurableFactoryContext;

/**
 * A factory for <code>AccessCheckingPortal</code> objects.
 */
abstract class AccessCheckingPortalFactory implements
		ConfigurableFactory {
    
	//
	// AccessCheckingPortalFactory
	//

	/**
	 * Create the access portal implementation.
	 * 
	 * @param ctx
	 *            The configuration context.
	 * 
	 * @return The portal.
	 * 
	 * @throws IllegalArgumentException
	 *             If the class attribute is missing.
	 */
	protected abstract AccessCheckingPortal createPortal(
			ConfigurableFactoryContext ctx);

	//
	// ConfigurableFactory
	//

	/**
	 * Create a keyed portal.
	 * 
	 * @param ctx
	 *            Configuration context.
	 * 
	 * @return A KeyedPortal.
	 * 
	 * @throws IllegalArgumentException
	 *             If there is a problem with the attributes. The exception
	 *             message should be a value suitable for meaningful user
	 *             interpretation.
	 * 
	 * @see KeyedPortal
	 */
	@Override
	public Object create(final ConfigurableFactoryContext ctx) {
		final AccessCheckingPortal portal = createPortal(ctx);
		
		final boolean instantAction = ctx.getBoolean("instantAction", false);
		final String passwordAcceptedMessage = getStringValue(ctx, "passwordAcceptedMessage");
		final String passwordRejectedMessage = getStringValue(ctx, "passwordRejectedMessage");
		final String rejectedMessage = getStringValue(ctx, "rejected");
		final String requiredPassword = getStringValue(ctx, "password");
		final int listeningRadius = getIntValue(ctx, "radius");
		
		if (instantAction) {
		    portal.setInstantAction(instantAction);
		}
		if (passwordAcceptedMessage != null) {
		    portal.setPasswordAcceptedMessage(passwordAcceptedMessage);
		}
		if (passwordRejectedMessage != null) {
		    portal.setPasswordRejectedMessage(passwordRejectedMessage);
		}
		if (rejectedMessage != null) {
			portal.setRejectedMessage(rejectedMessage);
		}
		if (requiredPassword != null) {
		    portal.setRequiredPassword(requiredPassword);
		}
		if (listeningRadius > 0) {
		    portal.setListeningRadius(listeningRadius);
		}

		return portal;
	}
	
    /**
     * Extract string value from a context.
     * 
     * @param ctx
     *            The configuration context.
     * @param key
     *            The key to search for.
     * @return
     *            The string value of the key, or <code>null</code> if none.
     * @throws IllegalArgumentException
     *             If the class attribute is missing.
     */
    protected String getStringValue(final ConfigurableFactoryContext ctx, final String key) {
        return ctx.getString(key, null);
    }
    
    /**
     * Extract integer value from a context.
     * 
     * @param ctx
     *            The configuration context.
     * @param key
     *            The key to search for.
     * @return
     *            The integer value of the key, or <code>null</code> if none.
     * @throws IllegalArgumentException
     *             If the class attribute is missing.
     */
    protected int getIntValue(final ConfigurableFactoryContext ctx, final String key) {
        return ctx.getInt(key, -1);
    }
}
