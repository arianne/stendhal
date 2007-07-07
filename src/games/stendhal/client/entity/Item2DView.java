/*
 * @(#) games/stendhal/client/entity/Item2DView.java
 *
 * $Id$
 */
package games.stendhal.client.entity;

//
//

import games.stendhal.client.GameScreen;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.List;

import marauroa.common.Log4J;
import marauroa.common.Logger;
import marauroa.common.game.RPAction;

/**
 * The 2D view of an item.
 */
public class Item2DView extends Entity2DView {
	/**
	 * Log4J.
	 */
	private static final Logger logger = Log4J.getLogger(Item2DView.class);

	/**
	 * Create a 2D view of an item.
	 *
	 * @param	entity		The entity to render.
	 */
	public Item2DView(final Item item) {
		super(item);
	}


	//
	// Entity2DView
	//

	/**
	 * Build a list of entity specific actions.
	 * <strong>NOTE: The first entry should be the default.</strong>
	 *
	 * @param	list		The list to populate.
	 */
	@Override
	protected void buildActions(final List<String> list) {
		list.add(ActionType.USE.getRepresentation());

		super.buildActions(list);
	}


	/**
	 * Build the visual representation of this entity.
	 */
	@Override
	protected void buildRepresentation() {
		SpriteStore store = SpriteStore.get();
		Sprite sprite = store.getSprite(translate(getClassResourcePath()));

		/*
		 * Items are always 1x1 (they need to fit in entity slots).
		 * Extra columns are animation.
		 * Extra rows are ignored.
		 */
		int width = sprite.getWidth();

		if(width > GameScreen.SIZE_UNIT_PIXELS) {
			setSprite(store.getAnimatedSprite(sprite, 0, width / GameScreen.SIZE_UNIT_PIXELS, 1.0, 1.0, 100L, true));
		} else if(sprite.getHeight() > GameScreen.SIZE_UNIT_PIXELS) {
			setSprite(store.getSprite(sprite, 0, 0, 1.0, 1.0));
			logger.info("WARNING: Multi-row item image for: " + getClassResourcePath());
		} else {
			setSprite(sprite);
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
	 * Determines on top of which other entities this entity should be
	 * drawn. Entities with a high Z index will be drawn on top of ones
	 * with a lower Z index.
	 * 
	 * Also, players can only interact with the topmost entity.
	 * 
	 * @return	The drawing index.
	 */
	@Override
	public int getZIndex() {
		return 7000;
	}


	/**
	 * Translate a resource name into it's sprite image path.
	 *
	 * @param	name		The resource name.
	 *
	 * @return	The full resource name.
	 */
	@Override
	protected String translate(final String name) {
		return "data/sprites/items/" + name + ".png";
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

		if(property == Entity.PROP_CLASS) {
			representationChanged = true;
		}
	}


	//
	// EntityView
	//

	/**
	 * Determine if this entity can be moved (e.g. via dragging).
	 *
	 * @return	<code>true</code> if the entity is movable.
	 */
	@Override
	public boolean isMovable() {
		return true;
	}


	/**
	 * Perform the default action.
	 */
	@Override
	public void onAction() {
		onAction(ActionType.USE);
	}


	/**
	 * Perform an action.
	 *
	 * @param	at		The action.
	 */
	@Override
	public void onAction(final ActionType at) {
		switch (at) {
			case USE:
				RPAction rpaction = new RPAction();

				rpaction.put("type", at.toString());
				getEntity().fillTargetInfo(rpaction);

				at.send(rpaction);
				break;

			default:
				super.onAction(at);
				break;
		}
	}
}
