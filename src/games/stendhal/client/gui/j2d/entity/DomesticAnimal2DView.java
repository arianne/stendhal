/*
 * @(#) games/stendhal/client/gui/j2d/entity/DomesticAnimal2DView.java
 *
 * $Id$
 */

package games.stendhal.client.gui.j2d.entity;

//
//

import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.ActiveEntity;
import games.stendhal.client.entity.DomesticAnimal;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.gui.styled.cursor.StendhalCursor;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;
import games.stendhal.common.Direction;

import java.awt.Graphics2D;
import java.util.Map;


/**
 * The 2D view of a domestic animal.
 */
abstract class DomesticAnimal2DView extends RPEntity2DView {
	/**
	 * The down facing big state.
	 */
	protected static final String STATE_BIG_DOWN = "big:move_down";

	/**
	 * The up facing big state.
	 */
	protected static final String STATE_BIG_UP = "big:move_up";

	/**
	 * The left facing big state.
	 */
	protected static final String STATE_BIG_LEFT = "big:move_left";

	/**
	 * The right facing big state.
	 */
	protected static final String STATE_BIG_RIGHT = "big:move_right";

	/**
	 * The idea property changed.
	 */
	protected boolean ideaChanged;
	
	/**
	 * The current idea sprite.
	 */
	private Sprite ideaSprite;



	/**
	 * Create a 2D view of a animal.
	 * 
	 * @param animal
	 *            The entity to render.
	 */
	public DomesticAnimal2DView() {

		ideaSprite = null;
		ideaChanged = false;
	}

	//
	// StateEntity
	//

	@Override
	protected Sprite getSprite(final Object state) {
		if (((DomesticAnimal) entity).getWeight() < getBigWeight()) {
			return super.getSprite(state);
		}
		switch (((ActiveEntity) entity).getDirection()) {
		case LEFT:
			return sprites.get(STATE_BIG_LEFT);

		case RIGHT:
			return sprites.get(STATE_BIG_RIGHT);

		case UP:
			return sprites.get(STATE_BIG_UP);

		case DOWN:
			return sprites.get(STATE_BIG_DOWN);

		default:
			return sprites.get(STATE_BIG_DOWN);
		}
	}

	//
	// DomesticAnimal2DView
	//

	/**
	 * Get the weight at which the animal becomes big.
	 * 
	 * @return A weight.
	 */
	protected abstract int getBigWeight();

	/**
	 * Get the approriete idea sprite.
	 * 
	 * @return The sprite representing the current idea, or null.
	 */
	protected Sprite getIdeaSprite() {
		final String idea = ((DomesticAnimal) entity).getIdea();

		if (idea == null) {
			return null;
		}

		return SpriteStore.get().getSprite(
				"data/sprites/ideas/" + idea + ".png");
	}

	//
	// RPEntity2DView
	//

	/**
	 * Populate named state sprites.
	 * 
	 * @param map
	 *            The map to populate.
	 * @param tiles
	 *            The master sprite.
	 * @param width
	 *            The image width (in pixels).
	 * @param height
	 *            The image height (in pixels).
	 */
	@Override
	protected void buildSprites(final Map<Object, Sprite> map,
			final Sprite tiles, final int width, final int height) {
		int y = 0;
		map.put(Direction.UP, createWalkSprite(tiles, y, width, height));

		y += height;
		map.put(Direction.RIGHT, createWalkSprite(tiles, y, width, height));

		y += height;
		map.put(Direction.DOWN, createWalkSprite(tiles, y, width, height));

		y += height;
		map.put(Direction.LEFT, createWalkSprite(tiles, y, width, height));

		y += height;
		map.put(STATE_BIG_UP, createWalkSprite(tiles, y, width, height));

		y += height;
		map.put(STATE_BIG_RIGHT, createWalkSprite(tiles, y, width, height));

		y += height;
		map.put(STATE_BIG_DOWN, createWalkSprite(tiles, y, width, height));

		y += height;
		map.put(STATE_BIG_LEFT, createWalkSprite(tiles, y, width, height));
	}

	/**
	 * Get the full directional animation tile set for this entity.
	 * 
	 * @return A tile sprite containing all animation images.
	 */
	@Override
	protected Sprite getAnimationSprite() {
		return SpriteStore.get().getSprite(translate(entity.getType()));
	}

	/**
	 * Get the number of tiles in the Y axis of the base sprite.
	 * 
	 * @return The number of tiles.
	 */
	@Override
	protected int getTilesY() {
		return 8;
	}

	//
	// ActiveEntity2DView
	//

	//
	// Entity2DView
	//

	/**
	 * Draw the entity.
	 * 
	 * @param g2d
	 *            The graphics to drawn on.
	 */
	@Override
	protected void draw(final Graphics2D g2d, final int x, final int y,
			final int width, final int height) {
		super.draw(g2d, x, y, width, height);

		if (ideaSprite != null) {
			ideaSprite.draw(g2d, x + width - 8, y - 8);
		}
	}

	/**
	 * Handle updates.
	 */
	@Override
	protected void update() {
		super.update();

		if (ideaChanged) {
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
	 * @param entity
	 *            The entity that was changed.
	 * @param property
	 *            The property identifier.
	 */
	@Override
	public void entityChanged(final IEntity entity, final Object property) {
		super.entityChanged(entity, property);

		if (property == DomesticAnimal.PROP_IDEA) {
			ideaChanged = true;
		} else if (property == DomesticAnimal.PROP_WEIGHT) {
			proceedChangedState();
		}
	}

	//
	// EntityView
	//

	/**
	 * Perform an action.
	 * 
	 * @param at
	 *            The action.
	 */
	@Override
	public void onAction(final ActionType at) {
		switch (at) {
		case OWN:
			at.send(at.fillTargetInfo(entity.getRPObject()));
			break;

		default:
			super.onAction(at);
			break;
		}
	}


	/**
	 * gets the mouse cursor image to use for this entity
	 *
	 * @return StendhalCursor
	 */
	@Override
	public StendhalCursor getCursor() {
		return StendhalCursor.LOOK;
	}
}
