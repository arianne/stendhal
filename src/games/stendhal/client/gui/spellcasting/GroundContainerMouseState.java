/**
 *
 */
package games.stendhal.client.gui.spellcasting;

import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.MouseEvent;

import games.stendhal.client.gui.GroundContainer;
import games.stendhal.client.gui.MouseHandler;
import games.stendhal.client.gui.styled.cursor.StendhalCursor;


/**
 *
 * @author madmetzger
 */
public abstract class GroundContainerMouseState extends MouseHandler {

	/**
	 * The amount to shift popup menus to have the first entry under
	 * the mouse.
	 */
	protected static final int MENU_OFFSET = 10;

	final GroundContainer ground;

	// mouse tweaks for MS windows
	boolean windowWasActiveOnMousePressed = true;
	int xOnMousePressed;
	int yOnMousePressed;

	/**
	 * <code>true</code>, when a context menu is visible, <em>or</em> it has
	 * been just closed with a click.
	 */
	boolean contextMenuFlag;
	/**
	 * <code>true</code> if the next click should be ignored (because the click
	 * was used to hide a context menu).
	 */
	boolean ignoreClick;

	public abstract void switchState();

	@Override
	public void mousePressed(MouseEvent e) {
		ignoreClick = contextMenuFlag;
		windowWasActiveOnMousePressed = (KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow() != null);
		xOnMousePressed = e.getX();
		yOnMousePressed = e.getY();
		/*
		 * The canvas does not want to keep the focus, but this way it at least
		 * will end up for the right component.
		 */
		ground.getCanvas().requestFocus();
		super.mousePressed(e);
	}

	/**
	 * Get cursor for a point.
	 *
	 * @param point
	 * @return cursor
	 */
	public abstract StendhalCursor getCursor(Point point);

	/**
	 * @param ground
	 */
	GroundContainerMouseState(GroundContainer ground) {
		super();
		this.ground = ground;
	}

}
