/*
 * @(#) games/stendhal/client/entity/Fire2DView.java
 *
 * $Id$
 */

package games.stendhal.client.entity;

//
//

/**
 * The 2D view of a fire.
 */
public class Fire2DView extends AnimatedLoopEntity2DView {
	private static final int FRAME_COUNT = 2;


	/**
	 * Create a 2D view of fire.
	 *
	 * @param	fire		The entity to render.
	 */
	public Fire2DView(final Fire fire) {
		super(fire, FRAME_COUNT);
	}
}
