/*
 * @(#) games/stendhal/client/entity/Sheep2DView.java
 *
 * $Id$
 */

package games.stendhal.client.entity;

//
//

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.Map;


import games.stendhal.client.AnimatedSprite;
import games.stendhal.client.GameScreen;
import games.stendhal.client.Sprite;
import games.stendhal.client.SpriteStore;

/**
 * The 2D view of a sheep.
 */
public class Sheep2DView extends RPEntity2DView {
	/**
	 * Sprite representing eating.
	 */
	private static Sprite eatSprite;

	/**
	 * Sprite representing hunger.
	 */
	private static Sprite foodSprite;

	/**
	 * Sprite representing walking.
	 */
	private static Sprite walkSprite;

	/**
	 * Sprite representing following
	 */
	private static Sprite followSprite;


	/**
	 * The entity this view is for.
	 */
	private Sheep	sheep;

	private Sprite	ideaSprite;


	static {
		SpriteStore st = SpriteStore.get();

		eatSprite = st.getSprite("data/sprites/ideas/eat.png");
		foodSprite = st.getSprite("data/sprites/ideas/food.png");
		walkSprite = st.getSprite("data/sprites/ideas/walk.png");
		followSprite = st.getSprite("data/sprites/ideas/follow.png");
	}


	/**
	 * Create a 2D view of a sheep.
	 *
	 * @param	sheep		The entity to render.
	 */
	public Sheep2DView(final Sheep sheep) {
		super(sheep);

		this.sheep = sheep;
		ideaSprite = null;
	}


	//
	// Sheep2DView
	//

	/**
	 * Get the approriete idea sprite.
	 *
	 *
	 */
	protected Sprite getIdeaSprite() {
		String idea = sheep.getIdea();


		if(idea == null) {
			return null;
		}

		if ("eat".equals(idea)) {
			return eatSprite;
		} else if ("food".equals(idea)) {
			return foodSprite;
		} else if ("walk".equals(idea)) {
			return walkSprite;
		} else if ("follow".equals(idea)) {
			return followSprite;
		} else {
			return null;
		}
	}


	//
	// RPEntity2DView
	//

	/**
	 * Populate named state sprites.
	 *
	 * @param	map		The map to populate.
	 * @param	width		The image width in tile units.
	 * @param	height		The image height in tile units.
	 */
	@Override
	protected void buildSprites(Map<Object, AnimatedSprite> map, double width, double height) {
		Sprite tiles = getAnimationSprite();

		SpriteStore store = SpriteStore.get();

		map.put(ActiveEntity.STATE_UP,
			store.getAnimatedSprite(tiles, 0, 3, width, height, 100L, false));

		map.put(ActiveEntity.STATE_RIGHT,
			store.getAnimatedSprite(tiles, 1, 3, width, height, 100L, false));

		map.put(ActiveEntity.STATE_DOWN,
			store.getAnimatedSprite(tiles, 2, 3, width, height, 100L, false));

		map.put(ActiveEntity.STATE_LEFT,
			store.getAnimatedSprite(tiles, 3, 3, width, height, 100L, false));

		map.put(Sheep.STATE_BIG_UP,
			store.getAnimatedSprite(tiles, 4, 3, width, height, 100L, false));

		map.put(Sheep.STATE_BIG_RIGHT,
			store.getAnimatedSprite(tiles, 5, 3, width, height, 100L, false));

		map.put(Sheep.STATE_BIG_DOWN,
			store.getAnimatedSprite(tiles, 6, 3, width, height, 100L, false));

		map.put(Sheep.STATE_BIG_LEFT,
			store.getAnimatedSprite(tiles, 7, 3, width, height, 100L, false));
	}


	//
	// AnimatedStateEntity2DView
	//

	/**
	 * Populate named state sprites.
	 *
	 * @param	map		The map to populate.
	 */
	@Override
	protected void buildSprites(Map<Object, AnimatedSprite> map) {
		buildSprites(map, 1.0, 1.0);
	}


	//
	// Entity2DView
	//

	/**
	 * Draw the entity.
	 *
	 * @param	screen		The screen to drawn on.
	 */
	@Override
	protected void draw(final GameScreen screen, Graphics2D g2d, int x, int y, int width, int height) {
		super.draw(screen, g2d, x, y, width, height);

		if (ideaSprite != null) {
			Rectangle2D rect = sheep.getArea();
			double sx = rect.getMaxX();
			double sy = rect.getY();
			screen.draw(ideaSprite, sx - 0.25, sy - 0.25);
		}
	}


	/**
	 * Get the 2D area that is drawn in.
	 *
	 * @return	The 2D area this draws in.
	 */
	@Override
	public Rectangle2D getDrawnArea() {
		return new Rectangle.Double(getX(), getY(), 1.0, 1.0);
	}


	//
	// <EntityView>
	//

	/**
	 * Update representation.
	 */
	@Override
	public void update() {
		super.update();

		ideaSprite = getIdeaSprite();
	}
}
