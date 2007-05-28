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

import games.stendhal.client.GameScreen;
import games.stendhal.client.Sprite;
import games.stendhal.client.sprite.SpriteStore;
import games.stendhal.common.Direction;

/**
 * The 2D view of a sheep.
 */
public class Sheep2DView extends RPEntity2DView {
	/**
	 * The weight that a sheep becomes fat (big).
	 */
	protected static final int	BIG_WEIGHT	= 60;

	/**
	 * The down facing big state.
	 */
	protected static final String	STATE_BIG_DOWN	= "big:move_down";

	/**
	 * The up facing big state.
	 */
	protected static final String	STATE_BIG_UP	= "big:move_up";

	/**
	 * The left facing big state.
	 */
	protected static final String	STATE_BIG_LEFT	= "big:move_left";

	/**
	 * The right facing big state.
	 */
	protected static final String	STATE_BIG_RIGHT	= "big:move_right";


	/**
	 * The entity this view is for.
	 */
	private Sheep	sheep;

	/**
	 * The current idea sprite.
	 */
	private Sprite	ideaSprite;

	/**
	 * The idea property changed.
	 */
	protected boolean	ideaChanged;


	/**
	 * Create a 2D view of a sheep.
	 *
	 * @param	sheep		The entity to render.
	 */
	public Sheep2DView(final Sheep sheep) {
		super(sheep);

		this.sheep = sheep;
		ideaSprite = null;
		ideaChanged = false;
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

		return SpriteStore.get().getSprite("data/sprites/ideas/" + idea + ".png");
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
	protected void buildSprites(final Map<Object, Sprite> map, final double width, final double height) {
		Sprite tiles = getAnimationSprite();

		SpriteStore store = SpriteStore.get();

		map.put(STATE_UP,
			store.getAnimatedSprite(tiles, 0, 3, width, height, 100L, false));

		map.put(STATE_RIGHT,
			store.getAnimatedSprite(tiles, 1, 3, width, height, 100L, false));

		map.put(STATE_DOWN,
			store.getAnimatedSprite(tiles, 2, 3, width, height, 100L, false));

		map.put(STATE_LEFT,
			store.getAnimatedSprite(tiles, 3, 3, width, height, 100L, false));

		map.put(STATE_BIG_UP,
			store.getAnimatedSprite(tiles, 4, 3, width, height, 100L, false));

		map.put(STATE_BIG_RIGHT,
			store.getAnimatedSprite(tiles, 5, 3, width, height, 100L, false));

		map.put(STATE_BIG_DOWN,
			store.getAnimatedSprite(tiles, 6, 3, width, height, 100L, false));

		map.put(STATE_BIG_LEFT,
			store.getAnimatedSprite(tiles, 7, 3, width, height, 100L, false));
	}


	//
	// ActiveEntity2DView
	//

	/**
	 * Get the appropriete named state for a direction.
	 *
	 * @param	direction	The direction.
	 *
	 * @return	A named state.
	 */
	@Override
	protected String getDirectionState(final Direction direction) {
		if(sheep.getWeight() < BIG_WEIGHT) {
			return super.getDirectionState(direction);
		}

		switch (direction) {
			case LEFT:
				return STATE_BIG_LEFT;

			case RIGHT:
				return STATE_BIG_RIGHT;

			case UP:
				return STATE_BIG_UP;

			case DOWN:
				return STATE_BIG_DOWN;

			default:
				return STATE_BIG_DOWN;
		}
	}


	//
	// StateEntity2DView
	//

	/**
	 * Populate named state sprites.
	 *
	 * @param	map		The map to populate.
	 */
	@Override
	protected void buildSprites(final Map<Object, Sprite> map) {
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
	protected void draw(final GameScreen screen, final Graphics2D g2d, final int x, final int y, final int width, final int height) {
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


	/**
	 * Update representation.
	 */
	@Override
	public void update() {
		super.update();

		if(ideaChanged) {
			ideaSprite = getIdeaSprite();
			ideaChanged = false;
		}
	}


	//
	// EntityChangeListener
	//

	/**
	 * An entity was changed.
	 *
	 * @param	entity		The entity that was changed.
	 * @param	property	The property identifier.
	 */
	@Override
	public void entityChanged(final Entity entity, final Object property)
	{
		super.entityChanged(entity, property);

		if(property == Sheep.PROP_IDEA) {
			ideaChanged = true;
		} else if(property == Sheep.PROP_WEIGHT) {
			stateChanged = true;
		}
	}
}
