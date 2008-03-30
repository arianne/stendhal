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

//	/**
//	 * The content pane.
//	 */
//	protected StyledJPanel contentPane;

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

//	/**
//	 * Do simulated dialog layout.
//	 */
//	public void pack() {
//		Dimension cSize;
//		int width;
//
//		if (content != null) {
//			cSize = content.getPreferredSize();
//		} else {
//			cSize = new Dimension(0, 0);
//		}
//
//		if (content != null) {
//			content.setBounds(0, tbSize.height, width, cSize.height);
//
//			content.validate();
//		}
//
//		if (isMinimized()) {
//			dialog.setBounds(0, 0, width, tbSize.height);
//			contentPane.setBounds(0, 0, width, tbSize.height);
//		} else {
//			dialog.setSize(width, tbSize.height + cSize.height);
//
//			contentPane.setSize(width, tbSize.height + cSize.height);
//		}
//	}

//	/**
//	 * Get the actual dialog.
//	 * 
//	 * @return The dialog.
//	 */
//	public Container getDialog() {
//		return dialog;
//	}

	/**
	 * Set the content component. For now, if the content wishes to resize the
	 * dialog, it should set a client property named <code>size-change</code>
	 * on itself.
	 * 
	 * @param content
	 *            A component to implement the content.
	 */
	public void setContent(Component content) {
//		if (this.content != null) {
////			this.content.removePropertyChangeListener(sizeChangeListener);
//
//			contentPane.remove(this.content);
//		}
//
//		this.content = content;
//
//		if (content instanceof JComponent) {
//			contentPane.add(content);
//		}

//		content.addPropertyChangeListener("size-change", sizeChangeListener);

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

//	/**
//	 * Handle content resize required property change.
//	 */
//	protected class ContentSizeChangeCB implements PropertyChangeListener {
//
//		//
//		// PropertyChangeListener
//		//
//
//		public void propertyChange(PropertyChangeEvent ev) {
//			pack();
//		}
//	}

}
