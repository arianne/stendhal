/*
 * @(#) games/stendhal/client/entity/FishSource2DView.java
 *
 * $Id$
 */

package games.stendhal.client.entity;

//
//

/**
 * The 2D view of a fish source.
 */
public class FishSource2DView extends AnimatedLoopEntity2DView {
	private static final int FRAME_COUNT = 32;


	/**
	 * Create a 2D view of food.
	 *
	 * @param	fishSource	The entity to render.
	 */
	public FishSource2DView(final FishSource fishSource) {
		super(fishSource, FRAME_COUNT);
	}
}
