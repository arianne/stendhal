/*
 * @(#) games/stendhal/client/entity/ActiveEntity2DView.java
 *
 * $Id$
 */

package games.stendhal.client.entity;

/**
 * The 2D view of an animated entity.
 */
public abstract class ActiveEntity2DView extends AnimatedStateEntity2DView {
	/**
	 * The active entity.
	 */
	private ActiveEntity	activeEntity;


	/**
	 * Create a 2D view of an entity.
	 *
	 * @param	activeEntity	The entity to render.
	 */
	public ActiveEntity2DView(final ActiveEntity activeEntity) {
		super(activeEntity);

		this.activeEntity = activeEntity;
	}


	//
	// Entity2DView
	//

	/**
	 * Determine if this view is currently animatable.
	 *
	 * @return	<code>true</code> if animating enabled.
	 */
	@Override
	protected boolean isAnimating() {
		return !activeEntity.stopped();
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
	public void entityChanged(Entity entity, Object property)
	{
		super.entityChanged(entity, property);

		if(property == ActiveEntity.PROP_SPEED) {
			animatedChanged = true;
		}
	}
}
