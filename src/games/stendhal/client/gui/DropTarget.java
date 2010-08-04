package games.stendhal.client.gui;

import java.awt.Point;

import games.stendhal.client.entity.IEntity;

public interface DropTarget {
	/**
	 * Drop an entity at a given location. Called when dragging ends.
	 * 
	 * @param entity dropped entity
	 * @param point location within the DropTarget
	 */
	void dropEntity(IEntity entity, Point point);
}
