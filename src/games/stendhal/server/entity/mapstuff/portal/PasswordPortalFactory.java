package games.stendhal.server.entity.mapstuff.portal;

import games.stendhal.server.core.config.factory.ConfigurableFactory;
import games.stendhal.server.core.config.factory.ConfigurableFactoryContext;

/**
 * A factory for <code>PasswordPortal</code> objects.
 */
abstract class PasswordPortalFactory implements
		ConfigurableFactory {
	//
	// PasswordPortalFactory
	//

	/**
	 * Create the password portal implementation.
	 * 
	 * @param ctx
	 *            The configuration context.
	 * 
	 * @return The portal.
	 * 
	 * @throws IllegalArgumentException
	 *             If the class attribute is missing.
	 */
	protected abstract PasswordPortal createPortal(
			ConfigurableFactoryContext ctx);

	/**
	 * Extract the rejected message from a context.
	 * 
	 * @param ctx
	 *            The configuration context.
	 * @return The rejected message, or <code>null</code> if none.
	 * @throws IllegalArgumentException
	 *             If the class attribute is missing.
	 */
	protected String getRejectedMessage(final ConfigurableFactoryContext ctx) {
		return ctx.getString("rejected", null);
	}
	
	/**
	 * Extract the password from context.
	 */
	protected String getPassword(final ConfigurableFactoryContext ctx) {
	    return ctx.getString("password", null);
	}

	//
	// ConfigurableFactory
	//

	/**
	 * Create a password portal.
	 * 
	 * @param ctx
	 *            Configuration context.
	 * 
	 * @return A PasswordPortal.
	 * 
	 * @throws IllegalArgumentException
	 *             If there is a problem with the attributes. The exception
	 *             message should be a value suitable for meaningful user
	 *             interpretation.
	 * 
	 * @see PasswordPortal
	 */
	@Override
	public Object create(final ConfigurableFactoryContext ctx) {
	    System.out.println("\nAttempting to create password portal.\n");
		final PasswordPortal portal = createPortal(ctx);

		final String message = getRejectedMessage(ctx);
		final String password = getPassword(ctx);

		if (message != null) {
			portal.setRejectedMessage(message);
		}
		if (password != null) {
		    portal.setPassword(password);
		}

		return portal;
	}
}
