/*
 * @(#) games/stendhal/client/entity/EntityView.java
 *
 * $Id$
 */

package games.stendhal.client.gui.j2d.entity;

import games.stendhal.client.IGameScreen;
import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.IEntity;

import java.awt.Graphics2D;
import java.awt.Rectangle;

//
//

/**
 * The view of an entity.
 */
public interface EntityView {
	/**
	 * Get the list of actions.
	 * 
	 * @return The list of actions.
	 */
	String[] getActions();

	/**
	 * Get the view's entity.
	 * 
	 * @return The view's entity.
	 */
	IEntity getEntity();

	/**
	 * Determine if this entity can be moved (e.g. via dragging).
	 * 
	 * @return <code>true</code> if the entity is movable.
	 */
	boolean isMovable();

	/**
	 * Perform the default action.
	 */
	void onAction();

	/**
	 * Perform an action.
	 * 
	 * @param at
	 *            The action.
	 */
	void onAction(ActionType at);

	/**
	 * Release any view resources. This view should not be used after this is
	 * called.
	 * @param gameScreen 
	 * 			 The gameScreen to paint on.
	 */
	void release(IGameScreen gameScreen);

	Rectangle getArea();
	
	void draw(final Graphics2D g2d);

	int getZIndex();

	void drawTop(Graphics2D g2d);

	void setContained(boolean b);

	void initialize(IEntity entity);

	/**
	 * is this entity interactive so that the player can click or move it?
	 * 
	 * @return true if the player can interact with it, false otherwise.
	 */
	boolean isInteractive();
}
