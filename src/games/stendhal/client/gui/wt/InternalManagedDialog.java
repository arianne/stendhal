/*
 * @(#) src/games/stendhal/client/gui/wt/InternalManagedDialog.java
 *
 * $Id$
 */

package games.stendhal.client.gui.wt;

import games.stendhal.client.gui.ClientPanel;

import java.awt.Component;

/**
 * A base internal dialog in swing that implements ManagedWindow.
 * 
 */
@SuppressWarnings("serial")
public class InternalManagedDialog extends ClientPanel {

	/**
	 * The window content.
	 */
	protected Component content;

	/**
	 * Create a managed dialog window.
	 * 
	 * @param name
	 *            The logical name.
	 * @param title
	 *            The dialog window title.
	 */
	public InternalManagedDialog(String name, String title) {
		super(title, 100, 100);

		setName(name);

		pack();

//		WtWindowManager.getInstance().formatWindow(this);
	}

	//
	// InternalManagedDialog
	//

	/**
	 * Close window.
	 */
	protected void closeCB() {
		setVisible(false);
	}

	/**
	 * Toggle minimization.
	 */
	protected void minimizeCB() {
		setMinimized(!isMinimized());
	}

	/**
	 * Set the content component. For now, if the content wishes to resize the
	 * dialog, it should set a client property named <code>size-change</code>
	 * on itself.
	 * 
	 * @param content
	 *            A component to implement the content.
	 */
	public void setContent(Component content) {
		add(content);

		pack();
	}

	/**
	 * Called when the window's position changes.
	 */
	protected void windowMoved() {
		/*
		 * Update saved state
		 */
//		WtWindowManager.getInstance().moveTo(this, getX(), getY());
	}

}
