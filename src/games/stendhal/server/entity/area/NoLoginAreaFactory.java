package games.stendhal.server.entity.area;

import games.stendhal.server.config.factory.ConfigurableFactory;
import games.stendhal.server.config.factory.ConfigurableFactoryContext;

/**
 * A base factory for <code>NoLoginArea</code> objects.
 */
public class NoLoginAreaFactory implements ConfigurableFactory {
	
	/**
	 * Extract the width from context
	 *
	 * @param	ctx		The configuration context.
	 *
	 * @return	The width, 1 if unspecified.
	 *
	 * @throws	IllegalArgumentException If the attribute is invalid.
	 */
	protected int getWidth(ConfigurableFactoryContext ctx) throws IllegalArgumentException {
		String s;

		if ((s = ctx.getAttribute("width")) == null) {
			return 1;
		}

		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException("Invalid 'width' attribute: " + s);
		}
	}

	/**
	 * Extract the height from context
	 *
	 * @param	ctx		The configuration context.
	 *
	 * @return	The height, 1 if unspecified.
	 *
	 * @throws	IllegalArgumentException If the attribute is invalid.
	 */
	protected int getHeight(ConfigurableFactoryContext ctx) throws IllegalArgumentException {
		String s;

		if ((s = ctx.getAttribute("height")) == null) {
			return 1;
		}

		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException("Invalid 'height' attribute: " + s);
		}
	}


	protected int getNewX(ConfigurableFactoryContext ctx) throws IllegalArgumentException {
		String s;
		if ((s = ctx.getAttribute("new-x")) == null) {
			throw new IllegalArgumentException("Required attribute 'new-x' missing");
		}
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException("Invalid 'damage' attribute: " + s);
		}
	}

	protected int getNewY(ConfigurableFactoryContext ctx) throws IllegalArgumentException {
		String s;
		if ((s = ctx.getAttribute("new-y")) == null) {
			throw new IllegalArgumentException("Required attribute 'new-x' missing");
		}
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException("Invalid 'damage' attribute: " + s);
		}
	}


	public NoLoginArea create(ConfigurableFactoryContext ctx) throws IllegalArgumentException {
		return new NoLoginArea(getWidth(ctx), getHeight(ctx), getNewX(ctx), getNewY(ctx));
	}
}
