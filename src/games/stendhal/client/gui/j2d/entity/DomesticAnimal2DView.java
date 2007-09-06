/*
 * @(#) games/stendhal/client/gui/j2d/entity/DomesticAnimal2DView.java
 *
 * $Id$
 */

package games.stendhal.client.gui.j2d.entity;

//
//

import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.Entity;
import games.stendhal.client.entity.DomesticAnimal;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;
import games.stendhal.common.Direction;

import java.awt.Graphics2D;
import java.util.Map;

import marauroa.common.game.RPAction;

/**
 * The 2D view of a domestic animal.
 */
public abstract class DomesticAnimal2DView extends RPEntity2DView {
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
	private DomesticAnimal	animal;

	/**
	 * The current idea sprite.
	 */
	private Sprite	ideaSprite;

	/**
	 * The idea property changed.
	 */
	protected boolean	ideaChanged;


	/**
	 * Create a 2D view of a animal.
	 *
	 * @param	animal		The entity to render.
	 */
	public DomesticAnimal2DView(final DomesticAnimal animal) {
		super(animal);

		this.animal = animal;
		ideaSprite = null;
		ideaChanged = false;
	}


	//
	// DomesticAnimal2DView
	//

	/**
	 * Get the weight at which the animal becomes big.
	 *
	 * @return	A weight.
	 */
	protected abstract int getBigWeight();


	/**
	 * Get the approriete idea sprite.
	 *
	 * @return	The sprite representing the current idea, or null.
	 */
	protected Sprite getIdeaSprite() {
		String idea = animal.getIdea();

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
	 * @param	tiles		The master sprite.
	 * @param	width		The image width in tile units.
	 * @param	height		The image height in tile units.
	 */
	@Override
	protected void buildSprites(final Map<Object, Sprite> map, final Sprite tiles, final double width, final double height) {
		SpriteStore store = SpriteStore.get();

		map.put(STATE_UP,
			store.getAnimatedSprite(tiles, 0, 3, width, height, 100, false));

		map.put(STATE_RIGHT,
			store.getAnimatedSprite(tiles, 1, 3, width, height, 100, false));

		map.put(STATE_DOWN,
			store.getAnimatedSprite(tiles, 2, 3, width, height, 100, false));

		map.put(STATE_LEFT,
			store.getAnimatedSprite(tiles, 3, 3, width, height, 100, false));

		map.put(STATE_BIG_UP,
			store.getAnimatedSprite(tiles, 4, 3, width, height, 100, false));

		map.put(STATE_BIG_RIGHT,
			store.getAnimatedSprite(tiles, 5, 3, width, height, 100, false));

		map.put(STATE_BIG_DOWN,
			store.getAnimatedSprite(tiles, 6, 3, width, height, 100, false));

		map.put(STATE_BIG_LEFT,
			store.getAnimatedSprite(tiles, 7, 3, width, height, 100, false));
	}


	/**
	 * Get the number of tiles in the Y axis of the base sprite.
	 *
	 * @return	The number of tiles.
	 */
	@Override
	protected int getTilesY() {
		return 8;
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
		if(animal.getWeight() < getBigWeight()) {
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
	// Entity2DView
	//

	/**
	 * Draw the entity.
	 *
	 * @param	g2d		The graphics to drawn on.
	 */
	@Override
	protected void draw(final Graphics2D g2d, final int x, final int y, final int width, final int height) {
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

		if(property == DomesticAnimal.PROP_IDEA) {
			ideaChanged = true;
		} else if(property == DomesticAnimal.PROP_WEIGHT) {
			stateChanged = true;
		}
	}


	//
	// EntityView
	//

	/**
	 * Perform an action.
	 *
	 * @param	at		The action.
	 */
	@Override
	public void onAction(final ActionType at) {
		switch (at) {
			case OWN:
				RPAction rpaction = new RPAction();

				rpaction.put("type", at.toString());
				rpaction.put("target", animal.getID().getObjectID());

				at.send(rpaction);
				break;

			default:
				super.onAction(at);
				break;
		}
	}
}
