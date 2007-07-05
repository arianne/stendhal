/*
 * @(#) games/stendhal/client/entity/EntityView.java
 *
 * $Id$
 */

package games.stendhal.client.entity;

//
//

/**
 * The view of an entity.
 */
public interface EntityView {
	/**
	 * Get the view's entity.
	 *
	 * @return	The view's entity.
	 */
	public Entity getEntity();


	/**
	 * Determine if this entity can be moved (e.g. via dragging).
	 *
	 * @return	<code>true</code> if the entity is movable.
	 */
	public boolean isMovable();


	/**
	 * Perform the default action.
	 */
	public void onAction();


	/**
	 * Perform an action.
	 *
	 * @param	at		The action.
	 * @param	params		The parameters.
	 */
	public void onAction(ActionType at);


	/**
	 * Release any view resources. This view should not be used after
	 * this is called.
	 */
	public void release();
}
