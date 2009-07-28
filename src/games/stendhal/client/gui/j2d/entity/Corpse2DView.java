/*
 * @(#) games/stendhal/client/gui/j2d/entity/Corpse2DView.java
 *
 * $Id$
 */

package games.stendhal.client.gui.j2d.entity;

//
//

import games.stendhal.client.GameScreen;
import games.stendhal.client.IGameScreen;
import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.Corpse;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.Inspector;
import games.stendhal.client.gui.wt.EntityContainer;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

import java.util.List;

/**
 * The 2D view of a corpse.
 */
class Corpse2DView extends Entity2DView {


	/**
	 * The corpse height.
	 */
	private int height;

	/**
	 * The corpse width.
	 */
	private int width;

	/**
	 * The slot content inspector.
	 */
	private Inspector inspector;

	/**
	 * The current content inspector.
	 */
	private EntityContainer wtEntityContainer;

	/**
	 * Create a 2D view of an entity.
	 * 
	 * @param corpse
	 *            The entity to render.
	 */
	public Corpse2DView() {
		

		height = IGameScreen.SIZE_UNIT_PIXELS;
		width = IGameScreen.SIZE_UNIT_PIXELS;
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
		list.add(ActionType.INSPECT.getRepresentation());

		super.buildActions(list);
	}

	/**
	 * Build the visual representation of this entity.
	 */
	@Override
	protected void buildRepresentation(final IGameScreen gameScreen) {
		final String imageName = entity.getRPObject().get("image");
		final Sprite sprite = SpriteStore.get().getSprite(translate("corpse/"  + imageName));

		width = sprite.getWidth();
		height = sprite.getHeight();

		setSprite(sprite);

		calculateOffset(width, height, gameScreen);
	}

	/**
	 * Get the height.
	 * 
	 * @return The height (in pixels).
	 */
	@Override
	public int getHeight() {
		return height;
	}

	/**
	 * Get the width.
	 * 
	 * @return The width (in pixels).
	 */
	@Override
	public int getWidth() {
		return width;
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
		return 5500;
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

		if (property == IEntity.PROP_CLASS) {
			representationChanged = true;
		}
	}

	//
	// EntityView
	//

	/**
	 * Determine if this entity can be moved (e.g. via dragging).
	 * 
	 * @return <code>true</code> if the entity is movable.
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
		onAction(ActionType.INSPECT);
	}

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
			wtEntityContainer = inspector.inspectMe(entity,
					((Corpse) entity).getContent(), wtEntityContainer, 2, 2, GameScreen
							.get());
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
}
