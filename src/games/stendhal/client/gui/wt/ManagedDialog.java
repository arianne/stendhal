/*
 * @(#) src/games/stendhal/client/gui/wt/ManagedDialog.java
 *
 * $Id$
 */

package games.stendhal.client.gui.wt;

//
//

import java.awt.Frame;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.LinkedList;
import javax.swing.JComponent;
import javax.swing.JDialog;

import games.stendhal.client.gui.ManagedWindow;
import games.stendhal.client.gui.wt.core.WtCloseListener;
import games.stendhal.client.gui.wt.core.WtWindowManager;

/**
 * A base dialog that implements ManagedWindow.
 *
 *
 */
public abstract class ManagedDialog implements ManagedWindow {
	protected JDialog		dialog;
	protected String		name;
	protected Frame			owner;
	protected List<WtCloseListener>	closeListeners;


	public ManagedDialog(Frame owner, String name, String title) {
		JComponent	content;


		this.owner = owner;
		this.name = name;

		closeListeners = new LinkedList<WtCloseListener>();

		dialog = new JDialog(owner, title);
		dialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		dialog.addComponentListener(new CL());

		content = createContent();

		content.addPropertyChangeListener(
			"size-change", new ContentSizeChangeCB());

		dialog.setContentPane(content);
		dialog.pack();

		WtWindowManager.getInstance().formatWindow(this);
	}


	//
	// ManagedDialog
	//

	/**
	 * Create the content component.
	 *
	 *
	 */
	protected abstract JComponent createContent();


	/**
	 * Called when the window's minimize state changes.
	 */
	protected void minimizeChanged() {
		WtWindowManager.getInstance().setMinimized(
			this, isMinimized());

		if(isMinimized())
			fireCloseListeners();
	}


	protected void fireCloseListeners() {
		WtCloseListener []	listeners;


		listeners = (WtCloseListener []) closeListeners.toArray(
			new WtCloseListener[closeListeners.size()]);

		for(WtCloseListener l : listeners)
			l.onClose(getName());
	}


	public void registerCloseListener(WtCloseListener listener) {
		closeListeners.add(listener);
	}


	public void removeCloseListener(WtCloseListener listener) {
		closeListeners.remove(listener);
	}


	/**
	 * Called when the window's position changes.
	 */
	protected void windowMoved() {
		WtWindowManager.getInstance().moveTo(this, getX(), getY());
	}


	//
	// ManagedWindow
	//

	/**
	 * Get the managed window name.
	 *
	 *
	 */
	public String getName()
	{
		return name;
	}


	/**
	 * Get X coordinate of the window.
	 *
	 * @return	A value sutable for passing to <code>moveTo()</code>.
	 */
	public int getX()
	{
		return (dialog.getX() - owner.getX());
	}


	/**
	 * Get Y coordinate of the window.
	 *
	 * @return	A value sutable for passing to <code>moveTo()</code>.
	 */
	public int getY()
	{
		return (dialog.getY() - owner.getY());
	}


	/**
	 * Determin if the window is visible.
	 *
	 * @return	<code>true</code> if the window is visible.
	 */
	public boolean isMinimized()
	{
		return !dialog.isVisible();
	}


	/**
	 * Move to a location. This may be subject to internal representation,
	 * and should only use what was passed from <code>getX()</code> and
	 * <code>getY()</code>.
	 *
	 * @param	x		The X coordinate
	 * @param	y		The Y coordinate
	 *
	 * @return	<code>true</code> if the move was allowed.
	 */
	public boolean moveTo(int x, int y)
	{
		dialog.setLocation(x + owner.getX(), y + owner.getY());
		return true;
	}


	/**
	 * Set the window as hidden (or visible).
	 *
	 * @param	minimized	Whether the window should be hidden.
	 */
	public void setMinimized(boolean minimized)
	{
		dialog.setVisible(!minimized);
	}

	//
	//

	protected class CL extends ComponentAdapter {

		//
		// ComponentListener
		//

		public void componentHidden(ComponentEvent ev) {
			minimizeChanged();
		}


		public void componentMoved(ComponentEvent ev) {
					windowMoved();
		}


		public void componentShown(ComponentEvent ev) {
			minimizeChanged();
		}
	}


	protected class ContentSizeChangeCB implements PropertyChangeListener {

		//
		// PropertyChangeListener
		//

		public void propertyChange(PropertyChangeEvent ev) {
System.err.println("->> pack()");
			dialog.pack();
		}
	}
}
