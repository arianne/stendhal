package games.stendhal.client.entity;

import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

import java.util.Map;


public class BossCreature2DView extends Creature2DView {
	public BossCreature2DView(final BossCreature creature) {
		super(creature);
	}


	//
	// Creature2DView
	//

	/*
	 * Populate named state sprites.
	 *
	 * This only has a single frame for left and right direction.
	 *
	 * @param	map		The map to populate.
	 */
	@Override
	protected void buildSprites(final Map<Object, Sprite> map) {
		Sprite tiles=getAnimationSprite();
		SpriteStore store = SpriteStore.get();

		Sprite right = store.getSprite(tiles, 0, 0, getWidth(), getHeight());
		Sprite left = store.getSprite(tiles, 0, 1, getWidth(), getHeight());

		map.put(STATE_RIGHT, right);
		map.put(STATE_LEFT, left);
		map.put(STATE_UP, right);
		map.put(STATE_DOWN, left);
	}
}
