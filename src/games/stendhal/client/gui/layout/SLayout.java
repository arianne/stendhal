package games.stendhal.client.gui.layout;

/**
 * Layout constraints.
 * <p>
 * Use <code>constraints(SLayout ... c)</code> of the appropriate
 * layout manager to create a constraints object.
 */
public enum SLayout {
	/** The component should expand in the direction of the layout */
	EXPAND_AXIAL,
	/** The component should expand perpendicular to the direction of the layout */
	EXPAND_PERPENDICULAR,
	/** The component should expand in horizontal direction */
	EXPAND_X,
	/** The component should expand in vertical direction */
	EXPAND_Y;
}
