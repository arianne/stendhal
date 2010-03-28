/*
 * @(#) games/stendhal/client/entity/EntityView.java
 *
 * $Id$
 */

package games.stendhal.client.gui.j2d.entity;

import games.stendhal.client.IGameScreen;
import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.gui.styled.cursor.StendhalCursor;

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
	public String[] getActions();

	/**
	 * Get the view's entity.
	 * 
	 * @return The view's entity.
	 */
	public IEntity getEntity();

	/**
	 * Determine if this entity can be moved (e.g. via dragging).
	 * 
	 * @return <code>true</code> if the entity is movable.
	 */
	public boolean isMovable();

	/**
	 * Perform the default action.
	 */
	public void onAction();

	/**
	 * Perform an action.
	 * 
	 * @param at
	 *            The action.
	 */
	public void onAction(ActionType at);

	/**
	 * Release any view resources. This view should not be used after this is
	 * called.
	 * @param gameScreen 
	 * 			 The gameScreen to paint on.
	 */
	public void release(IGameScreen gameScreen);

	public Rectangle getArea();
	
	public void draw(final Graphics2D g2d);

	public int getZIndex();

	public void drawTop(Graphics2D g2d);

	public void setContained(boolean b);

	public void initialize(IEntity entity);

	/**
	 * is this entity interactive so that the player can click or move it?
	 * 
	 * @return true if the player can interact with it, false otherwise.
	 */
	public boolean isInteractive();

	/**
	 * gets the mouse cursor image to use for this entity
	 *
	 * @return StendhalCursor
	 */
	public StendhalCursor getCursor();
}
