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
	private Sheep	entity;

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
	 * @param	entity		The entity to render.
	 */
	public Sheep2DView(final Sheep entity) {
		super(entity);

		this.entity = entity;
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
		String idea;


		if((idea = entity.getIdea()) == null) {
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
			Rectangle2D rect = entity.getArea();
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
