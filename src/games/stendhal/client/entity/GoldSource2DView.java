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
public class GoldSource2DView extends AnimatedLoopEntity2DView {
	private static final int FRAME_COUNT = 32;


	/**
	 * Create a 2D view of a gold source.
	 *
	 * @param	goldSource	The entity to render.
	 */
	public GoldSource2DView(final GoldSource goldSource) {
		super(goldSource, FRAME_COUNT);
	}
}
