/*
 * @(#) games/stendhal/client/entity/Sheep2DView.java
 *
 * $Id$
 */

package games.stendhal.client.entity;

//
//

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import marauroa.common.game.RPObject;

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
		String idea= sheep.getIdea();


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
	 * Populate named animations.
	 *
	 * @param	map		The map to populate.
	 * @param	object		The entity to load animations for.
	 * @param	width		The image width in tile units.
	 * @param	height		The image height in tile units.
	 */
	@Override
	public void buildAnimations(Map<String, Sprite []> map, final RPObject object, double width, double height) {
		SpriteStore store = SpriteStore.get();


		Sprite tiles = getAnimationSprite(object);

		map.put("move_up",
			store.getAnimatedSprite(tiles, 0, 3, width, height));

		map.put("move_right",
			store.getAnimatedSprite(tiles, 1, 3, width, height));

		map.put("move_down",
			store.getAnimatedSprite(tiles, 2, 3, width, height));

		map.put("move_left",
			store.getAnimatedSprite(tiles, 3, 3, width, height));

		map.put("big_move_up",
			store.getAnimatedSprite(tiles, 4, 3, width, height));

		map.put("big_move_right",
			store.getAnimatedSprite(tiles, 5, 3, width, height));

		map.put("big_move_down",
			store.getAnimatedSprite(tiles, 6, 3, width, height));

		map.put("big_move_left",
			store.getAnimatedSprite(tiles, 7, 3, width, height));
	}


	//
	// AnimatedEntity2DView
	//

	/**
	 * Populate named animations.
	 *
	 * @param	map		The map to populate.
	 * @param	object		The entity to load animations for.
	 */
	@Override
	public void buildAnimations(Map<String, Sprite []> map, final RPObject object) {
		buildAnimations(map, object, 1.0, 1.0);
	}


	//
	// Entity2DView
	//

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
	 * Draw the entity.
	 *
	 * @param	screen		The screen to drawn on.
	 */
	@Override
	public void draw(final GameScreen screen) {
		super.draw(screen);

		if (ideaSprite != null) {
			Rectangle2D rect = sheep.getArea();
			double sx = rect.getMaxX();
			double sy = rect.getY();
			screen.draw(ideaSprite, sx - 0.25, sy - 0.25);
		}
	}


	/**
	 * Update representation.
	 */
	@Override
	public void update() {
		ideaSprite = getIdeaSprite();
	}
}
