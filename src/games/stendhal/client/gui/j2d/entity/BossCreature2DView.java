/*
 * @(#) games/stendhal/client/gui/j2d/entity/BossCreature2DView.java
 *
 * $Id$
 */

package games.stendhal.client.gui.j2d.entity;

//
//

import games.stendhal.client.entity.BossCreature;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

import java.util.Map;

/**
 * A 2D view of a boss creature. Boss creatures have 1x2 image layouts.
 */
public class BossCreature2DView extends Creature2DView {
	/**
	 * Create a boss creature.
	 * 
	 * @param creature
	 *            The creature to render.
	 */
	public BossCreature2DView(final BossCreature creature) {
		super(creature);
	}

	//
	// RPEntity2DView
	//

	/*
	 * Populate named state sprites.
	 * 
	 * This only has a single frame for left and right direction.
	 * 
	 * @param map The map to populate. @param tiles The master sprite. @param
	 * width The image width (in pixels). @param height The image height (in
	 * pixels).
	 */
	@Override
	protected void buildSprites(final Map<Object, Sprite> map,
			final Sprite tiles, final int width, final int height) {
		SpriteStore store = SpriteStore.get();

		Sprite right = store.getTile(tiles, 0, 0, width, height);
		Sprite left = store.getTile(tiles, 0, height, width, height);

		map.put(STATE_RIGHT, right);
		map.put(STATE_LEFT, left);
		map.put(STATE_UP, right);
		map.put(STATE_DOWN, left);
	}

	/**
	 * Get the number of tiles in the X axis of the base sprite.
	 * 
	 * @return The number of tiles.
	 */
	@Override
	protected int getTilesX() {
		return 1;
	}

	/**
	 * Get the number of tiles in the Y axis of the base sprite.
	 * 
	 * @return The number of tiles.
	 */
	@Override
	protected int getTilesY() {
		return 2;
	}
}
