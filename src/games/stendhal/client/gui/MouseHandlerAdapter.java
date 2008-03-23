package games.stendhal.client.gui;

import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * MouseHandlerAdapter maps Swing mouse click notifications to
 * more specific function calls.
 *
 * @author Martin Fuchs
 */
public class MouseHandlerAdapter extends MouseAdapter {

	private static boolean osChecked = false;
	private static boolean osIsMacOS;

	private static boolean isOnMac() {
		if (!osChecked) {
    		// support for ctrl + click for Mac OS X intensifly@gmx.com
			osIsMacOS = System.getProperty("os.name").toLowerCase().contains("os x");
			osChecked = true;
		}

	    return osIsMacOS;
    }

	@Override
	public void mouseClicked(MouseEvent e) {
		boolean rightMouseButton;

		if (e.getButton() == MouseEvent.BUTTON3) {
			rightMouseButton = true;
		} else if (isOnMac() && (e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0) {
			rightMouseButton = true;
		} else {
			rightMouseButton = false;
		}

		if (rightMouseButton) {
    		if (e.getClickCount() >= 2) {
    	    	onRDoubleClick(e);
    		} else {
    			onRightClick(e);
    		}
		} else {
			if (e.getClickCount() >= 2) {
    	    	onLDoubleClick(e);
    		} else {
    	    	onLeftClick(e);
    		}
		}
	}

	/**
	 * The left mouse button has been clicked.
	 * @param e
	 */
	protected void onLeftClick(MouseEvent e) {
	}

	/**
	 * The leftmouse button has been double-clicked.
	 * @param e
	 */
	protected void onLDoubleClick(MouseEvent e) {
	}

	/**
	 * The right mouse button has been clicked.
	 * @param e
	 */
	protected void onRightClick(MouseEvent e) {
	}

	/**
	 * The right mouse button has been double-clicked.
	 * @param e
	 */
	protected void onRDoubleClick(MouseEvent e) {
	}


	@Override
	public void mousePressed(MouseEvent e) {
		if (e.isPopupTrigger()) {
			onPopup(e);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger()) {
			onPopup(e);
		}
	}

	/**
	 * Trigger a popup menu.
	 * @param e
	 */
	protected void onPopup(MouseEvent e) {
    }

}
