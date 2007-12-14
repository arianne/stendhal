/*
 * @(#) src/games/stendhal/client/events/EntityHolderListener.java
 *
 * $Id$
 */

package games.stendhal.client.events;

//
//

import games.stendhal.client.entity.Entity;

/**
 * A listener of entity changes in a holder (slot or link).
 */
public interface EntityHolderListener {
	/**
	 * An entity was added.
	 * 
	 * @param parent
	 *            The parent entity (if known).
	 * @param name
	 *            The name of the holder.
	 * @param entity
	 *            The entity.
	 */
	void entityAdded(Entity parent, String name, Entity entity);

	/**
	 * An entity was removed.
	 * 
	 * @param parent
	 *            The parent entity (if known).
	 * @param name
	 *            The name of the holder.
	 * @param entity
	 *            The entity.
	 */
	void entityRemoved(Entity parent, String name, Entity entity);
}
