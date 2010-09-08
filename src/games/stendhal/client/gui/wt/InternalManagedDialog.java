/*
 * @(#) src/games/stendhal/client/gui/wt/InternalManagedDialog.java
 *
 * $Id$
 */

package games.stendhal.client.gui.wt;

//
//

import games.stendhal.client.gui.ManagedWindow;
import games.stendhal.client.gui.styled.Style;
import games.stendhal.client.gui.styled.WoodStyle;
import games.stendhal.client.gui.styled.cursor.CursorRepository;
import games.stendhal.client.gui.styled.cursor.StendhalCursor;
import games.stendhal.client.gui.wt.core.WtWindowManager;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * A base internal dialog in swing that implements ManagedWindow.
 * 
 */
public class InternalManagedDialog implements ManagedWindow {
	/** size of the titlebar. */
	private static final int TITLEBAR_HEIGHT = 13;
	private static CursorRepository cursorRepository = new CursorRepository();

	/**
	 * The close button.
	 */
	private JButton closeButton;

	/**
	 * The window content.
	 */
	private JComponent content;

	/**
	 * The simulated dialog.
	 */
	private Panel dialog;

	/**
	 * Start of a window drag.
	 */
	private Point dragStart;

	/**
	 * The content pane.
	 */
	private JPanel contentPane;

	/**
	 * Whether the dialog is minimized.
	 */
	private boolean minimized;

	/**
	 * The minimize button.
	 */
	private JButton minimizeButton;

	/**
	 * If the window can be moved.
	 */
	private boolean movable;

	/**
	 * The window name.
	 */
	private String name;

	private ContentSizeChangeCB sizeChangeListener;

	/**
	 * The titlebar.
	 */
	private JPanel titlebar;

	/**
	 * The title label.
	 */
	private JLabel titleLabel;

	/**
	 * Create a managed dialog window.
	 * 
	 * @param name
	 *            The logical name.
	 * @param title
	 *            The dialog window title.
	 */
	public InternalManagedDialog(final String name, final String title) {
		Style style = WoodStyle.getInstance();
		Font font;
		Color color;

		this.name = name;

		minimized = false;
		movable = true;

		/*
		 * Heavy weight to work with AWT canvas
		 */
		dialog = new Panel();
		dialog.setLayout(null);
		dialog.addMouseListener(new DialogMouseListener());

		contentPane = new JPanel();
		contentPane.setLayout(null);

		dialog.add(contentPane);

		/*
		 * Titlebar
		 */
		titlebar = new JPanel();
		titlebar.setMinimumSize(new Dimension(1, TITLEBAR_HEIGHT));
		titlebar.setLayout(new BoxLayout(titlebar, BoxLayout.X_AXIS));
		titlebar.addMouseListener(new TBDragClickCB());
		titlebar.addMouseMotionListener(new TBDragMoveCB());

		contentPane.add(titlebar);

		/*
		 * Title
		 */
		titleLabel = new JLabel(title, SwingConstants.LEFT);
		titleLabel.setOpaque(false);
		titleLabel.setBorder(BorderFactory.createEmptyBorder());
		font = style.getFont();
		if (font == null) {
			font = titleLabel.getFont();
		}

		titleLabel.setFont(font.deriveFont(Font.BOLD));
		color = style.getForeground();
		if (color != null) {
			titleLabel.setForeground(color);
		}

		titlebar.add(titleLabel);

		/*
		 * Spacing
		 */
		titlebar.add(Box.createHorizontalGlue());

		/*
		 * Minimize button
		 */
		minimizeButton = new JButton(new MinimizeIcon());
		minimizeButton.setDisabledIcon(new DisabledIcon());
		minimizeButton.setFocusable(false);
		minimizeButton.setMargin(new Insets(0, 0, 0, 0));
		minimizeButton.setBorder(BorderFactory.createEmptyBorder());
		minimizeButton.setContentAreaFilled(false);
		minimizeButton.addActionListener(new MinimizeCB());
		titlebar.add(minimizeButton);

		/*
		 * Spacer
		 */
		titlebar.add(Box.createHorizontalStrut(1));

		/*
		 * Close button
		 */
		closeButton = new JButton(new CloseIcon());
		closeButton.setDisabledIcon(new DisabledIcon());
		closeButton.setFocusable(false);
		closeButton.setMargin(new Insets(0, 0, 0, 0));
		closeButton.setBorder(BorderFactory.createEmptyBorder());
		closeButton.setContentAreaFilled(false);
		closeButton.addActionListener(new CloseCB());
		titlebar.add(closeButton);

		sizeChangeListener = new ContentSizeChangeCB();

		pack();
		WtWindowManager.getInstance().formatWindow(this);
	}

	//
	// InternalManagedDialog
	//

	/**
	 * Close window.
	 */
	private void closeCB() {
		setVisible(false);
	}

	/**
	 * Toggle minimization.
	 */
	private void minimizeCB() {
		setMinimized(!isMinimized());
	}

	/**
	 * Do simulated dialog layout.
	 */
	private void pack() {
		Dimension tbSize;
		Dimension cSize;
		int width;

		tbSize = titlebar.getPreferredSize();

		if (content != null) {
			cSize = content.getPreferredSize();
		} else {
			cSize = new Dimension(0, 0);
		}

		width = Math.max(tbSize.width, cSize.width);

		titlebar.setBounds(0, 0, width, tbSize.height);
		titlebar.validate();

		if (content != null) {
			content.setBounds(0, tbSize.height, width, cSize.height);

			content.validate();
		}

		if (isMinimized()) {
			dialog.setBounds(0, 0, width, tbSize.height);
			contentPane.setBounds(0, 0, width, tbSize.height);
		} else {
			dialog.setSize(width, tbSize.height + cSize.height);

			contentPane.setSize(width, tbSize.height + cSize.height);
		}
	}

	//
	// ManagedDialog
	//

	/**
	 * Handle titlebar clicks.
	 * 
	 * @param count
	 *            The click count.
	 */
	private void tbClicked(final int count) {
		if (count == 1) {
			/*
			 * Raise the window if possible.
			 */
			final Container parent = dialog.getParent();

			if (parent instanceof JLayeredPane) {
				((JLayeredPane) parent).moveToFront(dialog);
			}
		} else if (count == 2) {
			/*
			 * Toggle minimization
			 */
			if (isMinimizable()) {
				setMinimized(!isMinimized());
			}
		}
	}

	/**
	 * Handle begining of titlebar drag.
	 * 
	 * @param x
	 *            The X coordinate.
	 * @param y
	 *            The Y coordinate.
	 */
	private void tbDragBegin(final int x, final int y) {
		if (isMovable()) {
			dragStart = SwingUtilities.convertPoint(titlebar, x, y, dialog);
		}
	}

	/**
	 * Handle end of titlebar drag.
	 * 
	 * @param x
	 *            The X coordinate.
	 * @param y
	 *            The Y coordinate.
	 */
	private void tbDragEnd(final int x, final int y) {
		tbDragMovement(x, y);
		dragStart = null;
		windowMoved();
	}

	/**
	 * Handle titlebar drag movement.
	 * 
	 * @param x
	 *            The X coordinate.
	 * @param y
	 *            The Y coordinate.
	 */
	private void tbDragMovement(final int x, final int y) {
		Point p;
		Container parent;

		if (dragStart != null) {
			parent = dialog.getParent();

			p = SwingUtilities.convertPoint(titlebar, x, y, parent);
			p.x -= dragStart.x;
			p.y -= dragStart.y;

			/*
			 * Keep in parent window
			 */
			if (p.x < 0) {
				p.x = 0;
			} else if ((p.x + dialog.getWidth()) > parent.getWidth()) {
				p.x = parent.getWidth() - dialog.getWidth();
			}

			if (p.y < 0) {
				p.y = 0;
			} else if ((p.y + dialog.getHeight()) > parent.getHeight()) {
				p.y = parent.getHeight() - dialog.getHeight();
			}

			dialog.setLocation(p);
		}
	}

	/**
	 * Get the actual dialog.
	 * 
	 * @return The dialog.
	 */
	public Container getDialog() {
		return dialog;
	}

	/**
	 * Set the content component. For now, if the content wishes to resize the
	 * dialog, it should set a client property named <code>size-change</code>
	 * on itself.
	 * 
	 * @param content
	 *            A component to implement the content.
	 */
	public void setContent(final JComponent content) {
		if (this.content != null) {
			this.content.removePropertyChangeListener(sizeChangeListener);

			contentPane.remove(this.content);
		}

		this.content = content;

		contentPane.add(content);

		content.addPropertyChangeListener("size-change", sizeChangeListener);

		pack();
	}

	/**
	 * Called when the window's position changes.
	 */
	private void windowMoved() {
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
		return dialog.getX();
	}

	/**
	 * Get Y coordinate of the window.
	 * 
	 * @return A value suitable for passing to <code>moveTo()</code>.
	 */
	public int getY() {
		return dialog.getY();
	}

	/**
	 * Determine if the window is minimizable.
	 * 
	 * @return <code>true</code> if minimizable.
	 */
	public boolean isMinimizable() {
		return minimizeButton.isEnabled();
	}

	/**
	 * Determine if the window is minimized.
	 * 
	 * @return <code>true</code> if the window is minimized.
	 */
	public boolean isMinimized() {
		return minimized;
	}

	/**
	 * Determine if the window is movable.
	 * 
	 * @return <code>true</code> if the window is movable.
	 */
	public boolean isMovable() {
		return movable;
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
		dialog.setLocation(x, y);
		return true;
	}

	/**
	 * Set whether the window is minimizable.
	 * 
	 * @param minimizable
	 *            <code>true</code> if minimizable.
	 */
	public void setMinimizable(final boolean minimizable) {
		minimizeButton.setEnabled(minimizable);
	}

	/**
	 * Set the window as minimized.
	 * 
	 * @param minimized
	 *            Whether the window should be minimized.
	 */
	public void setMinimized(final boolean minimized) {
		int cheight;

		this.minimized = minimized;

		if (minimized) {
			if (content != null) {
				content.setVisible(false);
			}

			dialog.setSize(titlebar.getWidth(), titlebar.getHeight());

			contentPane.setSize(titlebar.getWidth(), titlebar.getHeight());
		} else {
			if (content != null) {
				content.setVisible(true);
				cheight = content.getHeight();
			} else {
				cheight = 0;
			}

			dialog.setSize(titlebar.getWidth(), titlebar.getHeight() + cheight);

			contentPane.setSize(titlebar.getWidth(), titlebar.getHeight()
					+ cheight);
		}
	}

	/**
	 * Set whether the window is movable.
	 * 
	 * @param movable
	 *            <code>true</code> if movable.
	 */
	public void setMovable(final boolean movable) {
		this.movable = movable;
	}

	/**
	 * Set the window as visible (or hidden).
	 * 
	 * @param visible
	 *            Whether the window should be visible.
	 */
	public void setVisible(final boolean visible) {
		dialog.setVisible(visible);

		/*
		 * Update saved state
		 */
		WtWindowManager.getInstance().setVisible(this, visible);
	}

	//
	//

	/**
	 * Handle content resize required property change.
	 */
	private class ContentSizeChangeCB implements PropertyChangeListener {

		//
		// PropertyChangeListener
		//

		public void propertyChange(final PropertyChangeEvent ev) {
			pack();
		}
	}

	/**
	 * Handle close button.
	 */
	private class CloseCB implements ActionListener {
		public void actionPerformed(final ActionEvent ev) {
			closeCB();
		}
	}

	/**
	 * Handle minimzation button.
	 */
	private class MinimizeCB implements ActionListener {
		public void actionPerformed(final ActionEvent ev) {
			minimizeCB();
		}
	}

	/**
	 * Disabled button icon.
	 */
	private static class DisabledIcon implements Icon {
		//
		// Icon
		//

		public int getIconHeight() {
			return TITLEBAR_HEIGHT;
		}

		public int getIconWidth() {
			return TITLEBAR_HEIGHT;
		}

		public void paintIcon(final Component c, final Graphics g, final int x, final int y) {
			// do nothing
		}
	}

	/**
	 * Close button icon.
	 */
	private static class CloseIcon implements Icon {
		//
		// Icon
		//

		public int getIconHeight() {
			return TITLEBAR_HEIGHT;
		}

		public int getIconWidth() {
			return TITLEBAR_HEIGHT;
		}

		public void paintIcon(final Component c, final Graphics g, final int x, final int y) {
			Color oldColor;
			int height;
			int width;

			oldColor = g.getColor();

			height = getIconHeight();
			width = getIconWidth();

			g.setColor(new Color(1.0f, 1.0f, 1.0f, 0.5f));
			g.fillRect(x, y, width, height);

			g.setColor(Color.BLACK);

			/*
			 * \\ \\\ \\
			 */
			g.drawLine(x + 1, y + 2, x + width - 3, y + height - 2);

			g.drawLine(x + 1, y + 1, x + width - 2, y + height - 2);

			g.drawLine(x + 2, y + 1, x + width - 2, y + height - 3);

			/*
			 * // /// //
			 */
			g.drawLine(x + width - 3, y + 1, x + 1, y + height - 3);

			g.drawLine(x + width - 2, y + 1, x + 1, y + height - 2);

			g.drawLine(x + width - 2, y + 2, x + 2, y + height - 2);

			g.setColor(oldColor);
		}
	}

	/**
	 * Minmization button icon.
	 */
	private static class MinimizeIcon implements Icon {
		//
		// Icon
		//

		public int getIconHeight() {
			return TITLEBAR_HEIGHT;
		}

		public int getIconWidth() {
			return TITLEBAR_HEIGHT;
		}

		public void paintIcon(final Component c, final Graphics g, final int x, final int y) {
			Color oldColor;
			int height;
			int width;

			oldColor = g.getColor();

			height = getIconHeight();
			width = getIconWidth();

			g.setColor(new Color(1.0f, 1.0f, 1.0f, 0.5f));
			g.fillRect(x, y, width, height);

			g.setColor(Color.BLACK);
			g.fillRect(x + 1, y + height - 3, width - 2, 2);

			g.setColor(oldColor);
		}
	}

	/**
	 * Mouse drag event handler for the titlebar.
	 */
	private class TBDragMoveCB extends MouseMotionAdapter {
		/**
		 * Handle mouse drag event.
		 * 
		 * @param ev
		 *            The mouse event.
		 */
		@Override
		public void mouseDragged(final MouseEvent ev) {
			tbDragMovement(ev.getX(), ev.getY());
		}
	}

	/**
	 * Mouse click event handler for the titlebar.
	 */
	private class TBDragClickCB extends MouseAdapter {
		/**
		 * Handle mouse pressed event.
		 * 
		 * @param ev
		 *            The mouse event.
		 */
		@Override
		public void mousePressed(final MouseEvent ev) {
			if (ev.getButton() == MouseEvent.BUTTON1) {
				tbDragBegin(ev.getX(), ev.getY());
			}
		}

		/**
		 * Handle mouse released event.
		 * 
		 * @param ev
		 *            The mouse event.
		 */
		@Override
		public void mouseReleased(final MouseEvent ev) {
			if (ev.getButton() == MouseEvent.BUTTON1) {
				tbDragEnd(ev.getX(), ev.getY());
			}
		}

		/**
		 * Handle mouse click event.
		 * 
		 * @param ev
		 *            The mouse event.
		 */
		@Override
		public void mouseClicked(final MouseEvent ev) {
			if (ev.getButton() == MouseEvent.BUTTON1) {
				tbClicked(ev.getClickCount());
			}
		}
	}

	/**
	 * Mouse enter event handler for the dialog.
	 */
	private final class DialogMouseListener extends MouseAdapter {
		/**
		 * Handle mouse entered event.
		 *
		 * @param ev
		 * The mouse event.
		 */
		@Override
		public void mouseEntered(MouseEvent ev) {
		ev.getComponent().setCursor(cursorRepository.get(StendhalCursor.NORMAL));
		}
	}
}
