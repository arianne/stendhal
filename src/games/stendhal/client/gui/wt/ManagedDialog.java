/*
 * @(#) src/games/stendhal/client/gui/wt/ManagedDialog.java
 *
 * $Id$
 */

package games.stendhal.client.gui.wt;

//
//

import games.stendhal.client.gui.ManagedWindow;
import games.stendhal.client.gui.wt.core.WtCloseListener;
import games.stendhal.client.gui.wt.core.WtWindowManager;

import java.awt.Frame;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.WindowConstants;

/**
 * A base dialog that implements ManagedWindow.
 * 
 * This saves it's coordinates relative to it's owner window to be compatible
 * with existing saved interior-window positions (and to try and prevent WM
 * decor shifting).
 * 
 */
public abstract class ManagedDialog implements ManagedWindow {

	/**
	 * The actual dialog.
	 */
	protected JDialog dialog;

	/**
	 * The window name.
	 */
	protected String name;

	/**
	 * The owner window.
	 */
	protected Frame owner;

	/**
	 * Listeners interesting in close notifications.
	 */
	protected List<WtCloseListener> closeListeners;

	/**
	 * Create a managed dialog window.
	 * 
	 * @param owner
	 *            The owner window.
	 * @param name
	 *            The logical name.
	 * @param title
	 *            The dialog window title.
	 */
	public ManagedDialog(final Frame owner, final String name, final String title) {
		JComponent content;

		this.owner = owner;
		this.name = name;

		closeListeners = new LinkedList<WtCloseListener>();

		dialog = new JDialog(owner, title);
		dialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		dialog.addComponentListener(new DialogStateHandler());

		content = createContent();

		content.addPropertyChangeListener("size-change",
				new ContentSizeChangeCB());

		dialog.setContentPane(content);
		dialog.pack();

		WtWindowManager.getInstance().formatWindow(this);
	}

	//
	// ManagedDialog
	//

	/**
	 * Create the content component. For now, if the content wishes to resize
	 * the dialog, it should set a client property named
	 * <code>size-change</code> on itself.
	 * 
	 * @return A component to implement the content.
	 */
	protected abstract JComponent createContent();

	/**
	 * Get the actual dialog.
	 * 
	 * @return The dialog.
	 */
	public JDialog getDialog() {
		return dialog;
	}

	/**
	 * Called when the window's visible state changes.
	 */
	protected void visibilityChanged() {
		/*
		 * Update saved state
		 */
		WtWindowManager.getInstance().setVisible(this, isVisible());

		/*
		 * Notify close listeners
		 */
		if (!isVisible()) {
			fireCloseListeners();
		}
	}

	/**
	 * Call all registered close listeners.
	 */
	protected void fireCloseListeners() {
		WtCloseListener[] listeners;

		listeners = closeListeners.toArray(new WtCloseListener[closeListeners.size()]);

		for (final WtCloseListener l : listeners) {
			l.onClose(getName());
		}
	}

	/**
	 * Register a close listener.
	 * 
	 * @param listener
	 *            A close listener.
	 */
	public void registerCloseListener(final WtCloseListener listener) {
		closeListeners.add(listener);
	}

	/**
	 * Unregister a close listener.
	 * 
	 * @param listener
	 *            A close listener.
	 */
	public void removeCloseListener(final WtCloseListener listener) {
		closeListeners.remove(listener);
	}

	/**
	 * Called when the window's position changes.
	 */
	protected void windowMoved() {
		/*
		 * Update saved state
		 */
		WtWindowManager.getInstance().moveTo(this, getX(), getY());
	}

	//
	// ManagedWindow
	//

	/**
	 * Get the managed window name.
	 * 
	 * @return The logical window name (not title).
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get X coordinate of the window.
	 * 
	 * @return A value suitable for passing to <code>moveTo()</code>.
	 */
	public int getX() {
		return (dialog.getX() - owner.getX());
	}

	/**
	 * Get Y coordinate of the window.
	 * 
	 * @return A value suitable for passing to <code>moveTo()</code>.
	 */
	public int getY() {
		return (dialog.getY() - owner.getY());
	}

	/**
	 * Determine if the window is minimized.
	 * 
	 * @return <code>true</code> if the window is minimized.
	 */
	public boolean isMinimized() {
		return false;
	}

	/**
	 * Determine if the window is visible.
	 * 
	 * @return <code>true</code> if the window is visible.
	 */
	public boolean isVisible() {
		return dialog.isVisible();
	}

	/**
	 * Move to a location. This may be subject to internal representation, and
	 * should only use what was passed from <code>getX()</code> and
	 * <code>getY()</code>.
	 * 
	 * @param x
	 *            The X coordinate
	 * @param y
	 *            The Y coordinate
	 * 
	 * @return <code>true</code> if the move was allowed.
	 */
	public boolean moveTo(final int x, final int y) {
		/*
		 * TODO Perhaps we should require some of it to remain on the
		 * screen (incase it was saved while in hi-res, then run in low-res) XXX
		 */
		dialog.setLocation(x + owner.getX(), y + owner.getY());
		return true;
	}

	/**
	 * Set the window as minimized.
	 * 
	 * @param minimized
	 *            Whether the window should be minimized.
	 */
	public void setMinimized(final boolean minimized) {
		// No-op
	}

	/**
	 * Set the window as visible (or hidden).
	 * 
	 * @param visible
	 *            Whether the window should be visible.
	 */
	public void setVisible(final boolean visible) {
		dialog.setVisible(visible);
	}

	//
	//

	/**
	 * Handle dialog state events.
	 */
	private class DialogStateHandler extends ComponentAdapter {

		//
		// ComponentListener
		//

		/**
		 * Dialog was made invisible.
		 * 
		 * @param ev
		 *            The event.
		 */
		@Override
		public void componentHidden(final ComponentEvent ev) {
			// System.err.println("componentHidden() - ev = " + ev);
			// System.err.println("componentHidden() - dialog = " +
			// getDialog());
			visibilityChanged();
		}

		/**
		 * Dialog was moved.
		 * 
		 * @param ev
		 *            The event.
		 */
		@Override
		public void componentMoved(final ComponentEvent ev) {
			// System.err.println("componentMoved() - ev = " + ev);
			// System.err.println("componentMoved() - dialog = " + getDialog());
			windowMoved();
		}

		/**
		 * Dialog was made visible.
		 * 
		 * @param ev
		 *            The event.
		 */
		@Override
		public void componentShown(final ComponentEvent ev) {
			// System.err.println("componentShown() - ev = " + ev);
			// System.err.println("componentShown() - dialog.insets = " +
			// getDialog().getInsets());
			visibilityChanged();
		}
	}

	/**
	 * Handle content resize require property change.
	 */
	private class ContentSizeChangeCB implements PropertyChangeListener {

		//
		// PropertyChangeListener
		//

		public void propertyChange(final PropertyChangeEvent ev) {
			getDialog().pack();
		}
	}
}
