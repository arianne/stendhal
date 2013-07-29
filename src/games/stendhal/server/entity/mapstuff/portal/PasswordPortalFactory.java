package games.stendhal.server.entity.mapstuff.portal;

import games.stendhal.server.core.config.factory.ConfigurableFactory;
import games.stendhal.server.core.config.factory.ConfigurableFactoryContext;

/**
 * A factory for <code>PasswordPortal</code> objects.
 */
public class PasswordPortalFactory implements ConfigurableFactory {
    
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
        final PasswordPortal portal = new PasswordPortal();

        final String password = getPassword(ctx);
        final String message = getRejectedMessage(ctx);

        if (password != null) {
            portal.setPassword(password);
        }
        if (message != null) {
            portal.setRejectedMessage(message);
        }

        return portal;
    }
    
    protected String getPassword(final ConfigurableFactoryContext ctx) {
        return ctx.getString("password", null);
    }

    protected String getRejectedMessage(final ConfigurableFactoryContext ctx) {
        return ctx.getString("rejected", null);
    }
}
