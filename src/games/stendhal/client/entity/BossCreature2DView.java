package games.stendhal.client.entity;

import games.stendhal.client.GameScreen;
import games.stendhal.client.Sprite;
import games.stendhal.client.SpriteStore;

import java.util.Map;


public class BossCreature2DView extends Creature2DView {
	public BossCreature2DView(BossCreature creature) {
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
	protected void buildSprites(Map<Object, Sprite> map) {
		Sprite tiles=getAnimationSprite();
		SpriteStore store = SpriteStore.get();

		int pWidth = (int) (getWidth() * GameScreen.SIZE_UNIT_PIXELS);
		int pHeight = (int) (getHeight() * GameScreen.SIZE_UNIT_PIXELS);

		Sprite right = store.getTile(tiles, 0, 0, pWidth, pHeight);
		Sprite left = store.getTile(tiles, 0, pHeight, pWidth, pHeight);

		map.put(ActiveEntity.STATE_RIGHT, right);
		map.put(ActiveEntity.STATE_LEFT, left);
		map.put(ActiveEntity.STATE_UP, right);
		map.put(ActiveEntity.STATE_DOWN, left);
	}
}
