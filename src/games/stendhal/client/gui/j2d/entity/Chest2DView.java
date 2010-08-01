/*
 * @(#) games/stendhal/client/gui/j2d/entity/Chest2DView.java
 *
 * $Id$
 */

package games.stendhal.client.gui.j2d.entity;

//
//

import games.stendhal.client.GameScreen;
import games.stendhal.client.IGameScreen;
import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.Chest;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.Inspector;
import games.stendhal.client.gui.styled.cursor.StendhalCursor;
import games.stendhal.client.gui.wt.EntityContainer;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

import java.util.List;
import java.util.Map;

/**
 * The 2D view of a chest.
 */
class Chest2DView extends StateEntity2DView {
	/*
	 * The closed state.
	 */
	protected static final String STATE_CLOSED = "close";

	/*
	 * The open state.
	 */
	protected static final String STATE_OPEN = "open";

	/**
	 * The chest model open value changed.
	 */
	protected boolean openChanged;

	/**
	 * The slot content inspector.
	 */
	private Inspector inspector;

	/**
	 * Whether the user requested to open this chest.
	 */
	private boolean requestOpen;

	/**
	 * The current content inspector.
	 */
	private EntityContainer wtEntityContainer;

	/**
	 * Create a 2D view of a chest.
	 * 
	 * @param chest
	 *            The entity to render.
	 */
	public Chest2DView() {
		
		openChanged = false;
		requestOpen = false;
	}

	//
	// StateEntity2DView
	//

	/**
	 * Populate named state sprites.
	 * 
	 * @param map
	 *            The map to populate.
	 */
	@Override
	protected void buildSprites(final Map<Object, Sprite> map) {
		final SpriteStore store = SpriteStore.get();
		final Sprite tiles = store.getSprite(translate(entity.getType()));

		map.put(STATE_CLOSED, store.getTile(tiles, 0, 0,
				IGameScreen.SIZE_UNIT_PIXELS, IGameScreen.SIZE_UNIT_PIXELS));
		map.put(STATE_OPEN, store.getTile(tiles, 0,
				IGameScreen.SIZE_UNIT_PIXELS, IGameScreen.SIZE_UNIT_PIXELS,
				IGameScreen.SIZE_UNIT_PIXELS));
	}

	/**
	 * Get the current entity state.
	 * 
	 * @return The current state.
	 */
	@Override
	protected Object getState() {
		if (((Chest) entity).isOpen()) {
			return STATE_OPEN;
		} else {
			return STATE_CLOSED;
		}
	}

	//
	// Entity2DView
	//

	/**
	 * Build a list of entity specific actions. <strong>NOTE: The first entry
	 * should be the default.</strong>
	 * 
	 * @param list
	 *            The list to populate.
	 */
	@Override
	protected void buildActions(final List<String> list) {
		super.buildActions(list);

		Chest chest = (Chest) entity;
		if (chest != null && chest.isOpen()) {
			list.add(ActionType.INSPECT.getRepresentation());
			list.add(ActionType.CLOSE.getRepresentation());
		} else {
			list.add(ActionType.OPEN.getRepresentation());
		}
	}

	/**
	 * Determines on top of which other entities this entity should be drawn.
	 * Entities with a high Z index will be drawn on top of ones with a lower Z
	 * index.
	 * 
	 * Also, players can only interact with the topmost entity.
	 * 
	 * @return The drawing index.
	 */
	@Override
	public int getZIndex() {
		return 5000;
	}

	/**
	 * Set the content inspector for this entity.
	 * 
	 * @param inspector
	 *            The inspector.
	 */
	@Override
	public void setInspector(final Inspector inspector) {
		this.inspector = inspector;
	}

	/**
	 * Handle updates.
	 */
	@Override
	protected void update() {
		super.update();

		if (openChanged) {
			if (((Chest) entity).isOpen()) {
				// we're wanted to open this?
				if (requestOpen) {
					wtEntityContainer = inspector.inspectMe(entity, ((Chest) entity)
							.getContent(), wtEntityContainer, 5, 6, GameScreen.get());
				}
			} else {
				if (wtEntityContainer != null) {
					wtEntityContainer.destroy(GameScreen.get());
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
	 * @param entity
	 *            The entity that was changed.
	 * @param property
	 *            The property identifier.
	 */
	@Override
	public void entityChanged(final IEntity entity, final Object property) {
		super.entityChanged(entity, property);

		if (property == Chest.PROP_OPEN) {
			proceedChangedState();
			openChanged = true;
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
		case INSPECT:

			wtEntityContainer = inspector.inspectMe(entity, ((Chest) entity).getContent(),
					wtEntityContainer, 5, 6, GameScreen.get());
			break;

		case OPEN:
			if (!((Chest) entity).isOpen()) {
				// If it was closed, open it and inspect it...
				requestOpen = true;
			}

			/* no break */

		case CLOSE:
			
			at.send(at.fillTargetInfo(entity.getRPObject()));
			break;

		default:
			super.onAction(at);
			break;
		}
	}

	/**
	 * Release any view resources. This view should not be used after this is
	 * called.
	 * 
	 * @param gameScreen
	 */
	@Override
	public void release(final IGameScreen gameScreen) {
		if (wtEntityContainer != null) {
			wtEntityContainer.destroy(gameScreen);
			wtEntityContainer = null;
		}

		super.release(gameScreen);
	}


	@Override
	public StendhalCursor getCursor() {
		// TODO: use empty detection like in Corpse2DView, but not for bank chests
		//because they are always empty when closed
		return StendhalCursor.BAG;
	}
}
