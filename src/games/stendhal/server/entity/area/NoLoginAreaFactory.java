package games.stendhal.server.entity.area;

import games.stendhal.server.config.factory.ConfigurableFactory;
import games.stendhal.server.config.factory.ConfigurableFactoryContext;

/**
 * A base factory for <code>NoLoginArea</code> objects.
 */
public class NoLoginAreaFactory implements ConfigurableFactory {

	/**
	 * Extract the height from context
	 *
	 * @param	ctx		The configuration context.
	 * @return	The height, 1 if unspecified.
	 * @throws	IllegalArgumentException If the attribute is invalid.
	 */
	protected int getHeight(ConfigurableFactoryContext ctx) {
		return ctx.getInt("height", 1);
	}

	/**
	 * Extract the width from context
	 *
	 * @param	ctx		The configuration context.
	 * @return	The width, 1 if unspecified.
	 * @throws	IllegalArgumentException If the attribute is invalid.
	 */
	protected int getWidth(ConfigurableFactoryContext ctx) {
		return ctx.getInt("width", 1);
	}


	protected int getNewX(ConfigurableFactoryContext ctx) {
		return ctx.getRequiredInt("new-x");
	}

	protected int getNewY(ConfigurableFactoryContext ctx) {
		return ctx.getRequiredInt("new-y");
	}


	public NoLoginArea create(ConfigurableFactoryContext ctx) {
		return new NoLoginArea(getWidth(ctx), getHeight(ctx), getNewX(ctx), getNewY(ctx));
	}
}
