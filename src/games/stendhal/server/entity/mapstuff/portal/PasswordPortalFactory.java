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

        final String requiredPassword = getStringValue(ctx, "password");
        final String acceptedMessage = getStringValue(ctx, "accepted");
        final String rejectedMessage = getStringValue(ctx, "rejected");
        final int listeningRadius = getIntValue(ctx, "radius");

        if (requiredPassword != null) {
            portal.setPassword(requiredPassword);
        }
        if (acceptedMessage != null) {
            portal.setAcceptedMessage(acceptedMessage);
        }
        if (rejectedMessage != null) {
            portal.setRejectedMessage(rejectedMessage);
        }
        if (listeningRadius >= 0) {
            portal.setListeningRadius(listeningRadius);
        }

        return portal;
    }

    protected String getStringValue(final ConfigurableFactoryContext ctx, final String key) {
        return ctx.getString(key, null);
    }

    protected int getIntValue(final ConfigurableFactoryContext ctx, final String key) {
        return ctx.getInt(key, -1);
    }
}
