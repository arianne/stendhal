package games.stendhal.server.entity;

import games.stendhal.common.ConfigurableFactory;
import games.stendhal.common.ConfigurableFactoryContext;

/**
 * A factory for <code>Fire</code> objects.
 */
public class FireFactory implements ConfigurableFactory {

	/**
	 * Extract the height/widthfrom a context.
	 *
	 * @param ctx  The configuration context.
	 * @param dim "height" / "width"
	 * @return height or width
	 * @throws IllegalArgumentException if the attribute is invalid.
	 */
	protected int getSize(ConfigurableFactoryContext ctx, String dim) throws IllegalArgumentException {
		String value = ctx.getAttribute(dim);

		if (value == null) {
			throw new IllegalArgumentException("Required attribute '" + dim + "' missing");
		}

		return Integer.parseInt(value);
	}

	public Object create(ConfigurableFactoryContext ctx) throws IllegalArgumentException {
		return new Fire(getSize(ctx, "width"), getSize(ctx, "height"));
	}
}
