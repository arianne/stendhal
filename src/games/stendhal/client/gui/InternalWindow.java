/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.gui.layout.SBoxLayout;
import games.stendhal.client.gui.styled.Style;
import games.stendhal.client.gui.styled.StyleUtil;
import games.stendhal.common.constants.SoundLayer;

/**
 * A window like panel component with a title bar and optional close and
 * minimize buttons.
 */
public class InternalWindow extends JPanel implements ComponentPaintCache.Cacheable {
	/**
	 * serial version uid.
	 */
	private static final long serialVersionUID = 7086677981083580331L;
	private static final int TITLEBAR_HEIGHT = 13;
	/** Space between titlebar components and before the title. */
	private static final int TITLEBAR_PADDING = 2;

	private static Icon closeIcon, minimizeIcon;

	// Draw the buttons over static background to avoid overhead from constantly
	// painting translucent
	static {
		createIcons();
	}

	private final TitleBar titleBar;
	final JButton minimizeButton;
	final JButton closeButton;
	/** Title text label. */
	final JLabel titleLabel;

	private JComponent content;
	private boolean hideOnClose = false;

	private String openSound = "gui-window-fold";
	private String minimizeSound = "gui-window-fold";
	private String closeSound = "click-1";

	private final List<CloseListener> closeListeners = new LinkedList<CloseListener>();

	private final ComponentPaintCache cache;

	/**
	 * Create a new InternalWindow.
	 *
	 * @param title title text
	 */
	InternalWindow(String title) {
		setLayout(new BorderLayout());

		titleBar = new TitleBar();
		add(titleBar, BorderLayout.NORTH);
		titleBar.setLayout(new SBoxLayout(SBoxLayout.HORIZONTAL, TITLEBAR_PADDING));
		titleLabel = new JLabel();
		setTitle(title);
		// Squeeze the label if there's not enough space
		titleLabel.setMinimumSize(new Dimension(0, 0));
		titleBar.add(titleLabel);
		SBoxLayout.addSpring(titleBar);

		// Add the close and minimize buttons
		minimizeButton = new JButton(minimizeIcon);
		minimizeButton.setMargin(new Insets(0, 0, 0, 0));
		minimizeButton.setBorder(BorderFactory.createEmptyBorder());
		minimizeButton.setFocusable(false);
		minimizeButton.addActionListener(new MinimizeListener());
		titleBar.add(minimizeButton);

		closeButton = new JButton(closeIcon);
		closeButton.setMargin(new Insets(0, 0, 0, 0));
		closeButton.setBorder(BorderFactory.createEmptyBorder());
		closeButton.setFocusable(false);
		closeButton.addActionListener(new CloseActionListener());
		titleBar.add(closeButton);

		cache = new ComponentPaintCache(this);
	}

	/**
	 * Add a close listener to the window. All the listeners will be notified
	 * when this window is closed.
	 *
	 * @param listener new listener
	 */
	public void addCloseListener(CloseListener listener) {
		closeListeners.add(listener);
	}

	/**
	 * Set the content of the window. The minimum width of the title bar
	 * is determined at this stage, so usually you should defer setting
	 * the window contents until the content component has been fully
	 * constructed.
	 *
	 * @param content window content
	 */
	public void setContent(JComponent content) {
		if (this.content != null) {
			remove(this.content);
		}
		add(content, BorderLayout.CENTER);
		this.content = content;
		/*
		 * Keep the preferred width even if the content is minimized
		 */
		titleBar.setPreferredWidth(content.getPreferredSize().width);
	}

	/**
	 * Make the window closeable by the user by showing or hiding the close
	 * button.
	 *
	 * @param closeable <code>true</code> if the window should have a close
	 * 	button, otherwise <code>false</code>
	 */
	public void setCloseable(boolean closeable) {
		closeButton.setVisible(closeable);
	}

	/**
	 * Set closing policy. Normally windows delete themselves on close, but
	 * setting the policy to hiding just makes it invisible.
	 *
	 * @param hide if <code>true</code>, the window will hide itself when the
	 * 	user closes it, otherwise the window is deleted on close
	 */
	public void setHideOnClose(boolean hide) {
		hideOnClose = hide;
	}

	/**
	 * Make the window minimizable by the user by showing or hiding the
	 * minimize button.
	 *
	 * @param minimizable <code>true</code> if the window should have a minimize
	 * 	button, otherwise <code>false</code>
	 */
	public void setMinimizable(boolean minimizable) {
		minimizeButton.setVisible(minimizable);
	}

	/**
	 * Get the minimization status of the window.
	 *
	 * @return <code>true</code> if the window is minimized, <code>false</code>
	 * otherwise
	 */
	public boolean isMinimized() {
		return !content.isVisible();
	}

	/**
	 * Set the minimization status of the window.
	 *
	 * @param minimized <code>true</code> to minimize the window,
	 * 	<code>false</code> to restore it
	 */
	public void setMinimized(boolean minimized) {
		// Cosmetics. Hide the borders of the title bar while the window is
		// minimized
		if (minimized) {
			titleBar.hideBorder();
		} else {
			titleBar.restoreBorder();
		}
		content.setVisible(!minimized);
	}

	/**
	 * Get the title bar component.
	 * @return title bar
	 */
	protected JComponent getTitlebar() {
		return titleBar;
	}

	/**
	 * Set the window title.
	 *
	 * @param title title text
	 */
	public final void setTitle(String title) {
		/*
		 * Work around JLabel drawing ellipsis when it's short of space. To
		 * avoid titles like "cor..." for corpses. Html labels do not get the
		 * ellipsis.
		 *
		 * Use no-break space to avoid the title overflowing to the
		 * next line when using html.
		 */
		title = title.replaceAll(" +", "&nbsp;");
		title = "<html>" + title + "</html>";
		titleLabel.setText(title);
	}

	/**
	 * Close the window. Either deletes or hides it, according to the policy
	 * set with {@link #setHideOnClose}.
	 */
	public void close() {
		if (hideOnClose) {
			setVisible(false);
		} else {
			Container parent = InternalWindow.this.getParent();
			if (parent != null) {
				parent.remove(InternalWindow.this);
				parent.validate();
				parent.repaint();
			}
		}
		// notify listeners
		for (CloseListener listener : closeListeners) {
			listener.windowClosed(this);
		}
	}

	/**
	 * Play a user interface sound.
	 *
	 * @param sound the sound to be played
	 */
	private void playSound(String sound) {
		ClientSingletonRepository.getSound().getGroup(SoundLayer.USER_INTERFACE.groupName).play(sound, 0, null, null, false, true);
	}

	/**
	 * Create the close and minimize icons.
	 */
	private static void createIcons() {
		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		Image bg = createIconBackground(gc);

		// copy bg for drawing
		Image image = gc.createCompatibleImage(TITLEBAR_HEIGHT, TITLEBAR_HEIGHT, Transparency.OPAQUE);
		Graphics g = image.getGraphics();
		g.drawImage(bg, 0, 0, null);
		closeIcon = createCloseIcon(image);

		// now we can draw over the background image
		minimizeIcon = createMinimizeIcon(bg);
	}

	/**
	 * Create image background for the title bar buttons. Tries using the
	 * style of the theme if available.
	 *
	 * @param gc graphics configuration for creating the image
	 *
	 * @return image background image
	 */
	private static Image createIconBackground(GraphicsConfiguration gc) {
		BufferedImage image = gc.createCompatibleImage(TITLEBAR_HEIGHT, TITLEBAR_HEIGHT, Transparency.OPAQUE);
		Graphics2D g = image.createGraphics();
		/*
		 * Use proper style if defined. If someone's using a different theme we
		 * don't have a nice background, but the client should not crash anyway.
		 * Those get just a white background.
		 */
		Style style = StyleUtil.getStyle();
		if (style != null) {
			style.getBackground().draw(g, 0, 0);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
		}
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, TITLEBAR_HEIGHT, TITLEBAR_HEIGHT);
		g.dispose();

		return image;
	}

	/**
	 * Draw the close button icon.
	 *
	 * @param image background image. The button image is drawn over this,
	 * 	without making a local copy first
	 * @return icon for close button
	 */
	private static Icon createCloseIcon(Image image) {
		Graphics g = image.getGraphics();

		g.setColor(Color.BLACK);
		/* \\\ */
		g.drawLine(1, 2, TITLEBAR_HEIGHT - 3, TITLEBAR_HEIGHT - 2);
		g.drawLine(1, 1, TITLEBAR_HEIGHT - 2, TITLEBAR_HEIGHT - 2);
		g.drawLine(2, 1, TITLEBAR_HEIGHT - 2, TITLEBAR_HEIGHT - 3);

		/* /// */
		g.drawLine(TITLEBAR_HEIGHT - 3, 1, 1, TITLEBAR_HEIGHT - 3);
		g.drawLine(TITLEBAR_HEIGHT - 2, 1, 1, TITLEBAR_HEIGHT - 2);
		g.drawLine(TITLEBAR_HEIGHT - 2, 2, 2, TITLEBAR_HEIGHT - 2);

		g.dispose();
		return new ImageIcon(image);
	}

	/**
	 * Draw the minimize button icon.
	 *
	 * @param image background image. The button image is drawn over this,
	 * 	without making a local copy first
	 * @return icon for minimize button
	 */
	private static Icon createMinimizeIcon(Image image) {
		Graphics g = image.getGraphics();

		g.setColor(Color.BLACK);
		g.fillRect(1, TITLEBAR_HEIGHT - 3, TITLEBAR_HEIGHT - 2, 2);

		return new ImageIcon(image);
	}

	/* ********************************************************************
	 * Speed up the drawing by caching the image of the window. This is done
	 * because repeatedly drawing the borders makes a noticeable performance
	 * hit.
	 */
	@Override
	public void paint(Graphics g) {
		cache.paintComponent(g);
		paintChildren(g);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

	@Override
	public void paintBorder(Graphics g) {
		super.paintBorder(g);
	}

	@Override
	public void paintChildren(Graphics g) {
		super.paintChildren(g);
	}

	/**
	 * Listener interface for window close events.
	 */
	public interface CloseListener {
		/**
		 * Called when the window is closed.
		 *
		 * @param window the closed window
		 */
		void windowClosed(InternalWindow window);
	}

	/**
	 * Handle close button.
	 */
	private class CloseActionListener implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent ev) {
			close();
			playSound(closeSound);
		}
	}

	/**
	 * Handle minimization button.
	 */
	private class MinimizeListener implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent ev) {
			setMinimized(!isMinimized());
			if (isMinimized()) {
				playSound(openSound);
			} else {
				playSound(minimizeSound);
			}
		}
	}

	/**
	 * A JPanel that draws only the lower part of the border.
	 */
	private static class TitleBar extends JPanel implements ComponentPaintCache.Cacheable {
		/**
		 * serial version uid.
		 */
		private static final long serialVersionUID = -6859560118307192124L;
		/** Original, unmodified insets. */
		private Insets insets;
		private final Border border;
		private final ComponentPaintCache cache;
		/**
		 * Width of the window content. The title bar should not request a
		 * larger width.
		 */
		private int preferredWidth = -1;

		/**
		 * Create a TitleBar.
		 */
		TitleBar() {
			/*
			 * Compensate with negative empty border the borders that are not
			 * drawn anyway. Left and right borders are useful as padding so
			 * they are kept.
			 */
			insets = getInsets();
			border = BorderFactory.createCompoundBorder(getBorder(),
					BorderFactory.createEmptyBorder(-insets.top, 0, 0, 0));
			setBorder(border);
			cache = new ComponentPaintCache(this);
		}

		/**
		 * Hide the special border of the title bar.
		 */
		void hideBorder() {
			/*
			 * Create an empty border that corresponds exactly to the normal
			 * borders.
			 */
			Insets insets = getInsets();
			setBorder(BorderFactory.createEmptyBorder(insets.top, insets.left, insets.bottom, insets.right));
		}

		/**
		 * Restore the special border of the title bar.
		 */
		void restoreBorder() {
			setBorder(border);
		}

		/**
		 * Set what the title should report as its preferred width.
		 *
		 * @param width preferred width
		 */
		void setPreferredWidth(int width) {
			preferredWidth = width;
		}

		@Override
		public Dimension getPreferredSize() {
			Dimension tmp = super.getPreferredSize();
			if (preferredWidth != -1) {
				tmp.width = preferredWidth;
			}

			return tmp;
		}

		@Override
		public void paintBorder(Graphics g) {
			Graphics graphics = g.create();
			graphics.clipRect(0, getHeight() - insets.bottom, getWidth(), getHeight());
			/*
			 * Adjust the width, so that the drawn border does not get corners
			 * that may look ugly
			 */
			getBorder().paintBorder(this, graphics, -insets.left, 0,
					getWidth() + insets.left + insets.right, getHeight());
			graphics.dispose();
		}

		@Override
		public void paint(Graphics g) {
			cache.paintComponent(g);
			paintChildren(g);
		}

		// *** for cached painting ***

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
		}

		@Override
		public void paintChildren(Graphics g) {
			super.paintChildren(g);
		}
	}
}
