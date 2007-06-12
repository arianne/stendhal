/*
 * @(#) games/stendhal/client/entity/GoldSource2DView.java
 *
 * $Id$
 */

package games.stendhal.client.entity;

//
//

/**
 * The 2D view of a gold source.
 */
public class WellSource2DView extends AnimatedLoopEntity2DView {
	private static final int FRAME_COUNT = 32;


	/**
	 * Create a 2D view of a well source.
	 *
	 * @param	wellSource	The entity to render.
	 */
	public WellSource2DView(final WellSource wellSource) {
		super(wellSource, FRAME_COUNT);
	}
}
