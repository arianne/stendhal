/*
 * @(#) games/stendhal/client/entity/Chest2DView.java
 *
 * $Id$
 */

package games.stendhal.client.entity;

//
//

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import marauroa.common.game.RPAction;

import games.stendhal.client.gui.wt.EntityContainer;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

/**
 * The 2D view of a chest.
 */
public class Chest2DView extends StateEntity2DView implements Inspectable {
	/*
	 * The closed state.
	 */
	protected final static String	STATE_CLOSED	= "close";

	/*
	 * The open state.
	 */
	protected final static String	STATE_OPEN	= "open";

	/**
	 * The chest entity.
	 */
	protected Chest		chest;

	/**
	 * The chest model open value changed.
	 */
	protected boolean	openChanged;

	/**
	 * The slot content inspector.
	 */
	private Inspector	inspector;

	/**
	 * Whether the user requested to open this chest.
	 */
	private boolean		requestOpen;

	/**
	 * The current content inspector.
	 */
	private EntityContainer	wtEntityContainer;


	/**
	 * Create a 2D view of a chest.
	 *
	 * @param	chest		The entity to render.
	 */
	public Chest2DView(final Chest chest) {
		super(chest);

		this.chest = chest;
		openChanged = false;
		requestOpen = false;
	}


	//
	// Inspectable
	//

	/**
	 * Set the content inspector for this entity.
	 *
	 * @param	inspector	The inspector.
	 */
	public void setInspector(final Inspector inspector) {
		this.inspector = inspector;
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
		SpriteStore store = SpriteStore.get();

		Sprite tiles = store.getSprite(translate(entity.getType()));

		map.put(STATE_CLOSED, store.getSprite(tiles, 0, 0, 1.0, 1.0));
		map.put(STATE_OPEN, store.getSprite(tiles, 0, 1, 1.0, 1.0));
	}


	/**
	 * Get the current entity state.
	 *
	 * @return	The current state.
	 */
	@Override
	protected Object getState() {
		return chest.isOpen() ? STATE_OPEN : STATE_CLOSED;
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
		return 5000;
	}


	/**
	 * Handle updates.
	 */
	@Override
	protected void update() {
		super.update();

		if(openChanged) {
			if (chest.isOpen()) {
				// we're wanted to open this?
				if (requestOpen) {
					wtEntityContainer = inspector.inspectMe(chest, chest.getContent(), wtEntityContainer);
				}
			} else {

				if(wtEntityContainer != null) {
					wtEntityContainer.destroy();
					wtEntityContainer = null;
				}
			}

			requestOpen = false;
			openChanged = false;
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

		if(property == Chest.PROP_OPEN) {
			stateChanged = true;
			openChanged = true;
		}
	}


	//
	// EntityView
	//

	/**
	 * Perform an action.
	 *
	 * @param	at		The action.
	 * @param	params		The parameters.
	 */
	@Override
	public void onAction(final ActionType at, final String... params) {
		switch (at) {
			case INSPECT:
				wtEntityContainer = inspector.inspectMe(chest, chest.getContent(), wtEntityContainer);
				break;

			case OPEN:
				if (!chest.isOpen()) {
					// If it was closed, open it and inspect it...
					requestOpen = true;
				}

				/* no break */

			case CLOSE:
				RPAction rpaction = new RPAction();

				rpaction.put("type", at.toString());
				rpaction.put("target", chest.getID().getObjectID());

				at.send(rpaction);
				break;

			default:
				super.onAction(at, params);
				break;
		}
	}


	/**
	 * Release any view resources. This view should not be used after
	 * this is called.
	 */
	@Override
	public void release() {
		if (wtEntityContainer != null) {
			wtEntityContainer.destroy();
			wtEntityContainer = null;
		}

		super.release();
	}
}
