/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2005 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
/*
 * Panel.java
 * Created on 16. Oktober 2005, 10:55
 */
package games.stendhal.client.gui.wt.core;

import games.stendhal.client.IGameScreen;
import games.stendhal.client.gui.ManagedWindow;
import games.stendhal.client.soundreview.SoundMaster;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;
import games.stendhal.common.Debug;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPopupMenu;

import org.apache.log4j.Logger;

/**
 * Base class for all kinds of panels/windows/buttons.
 * <p>
 * Panels
 * <ul>
 * <li>can have other panels as children</li>
 * <li>can have a border and a title bar. If they have a title bar they
 * <ul>
 * <li>can be moved</li>
 * <li>can be minimized (reduces the panel to the title bar)</li>
 * <li>can be closed</li>
 * </ul>
 * </li>
 * </ul>
 * <b>Note:</b> This class is not thread safe.
 * 
 * 
 * See http://www.grsites.com/ for the textures
 * 
 * @author mtotz
 */
public class WtPanel implements ManagedWindow, WtDraggable {

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(WtPanel.class);

	/** size of the titlebar. */
	private static final int TITLEBAR_SIZE = 14;

	/** size of the titlebar font. */
	private static final int TITLEBAR_FONT_SIZE = 12;

	/** thickness of the frame. */
	private static final int FRAME_SIZE = 3;

	/** panel has a title bar. */
	private boolean titleBar;

	/** text of the title. */
	private String titleText;

	/** panel has a frame. */
	private boolean frame;

	/** is the frame embossed? */
	private boolean frameEmbossed;

	/** panel is movable. */
	private boolean movable;

	/** panel can be resized. */
	private boolean resizeable;

	/** panel can be minimized. */
	private boolean minimizeable;

	/** true when the panel is minimized. */
	private boolean minimized;

	/** true when the panel can be closed. */
	private boolean closeable;

	/** x-position relative to its parent. */
	private int x;

	/** y-position relative to its parent. */
	private int y;

	/** the point where we were before the drag started. */
	private Point dragPosition;

	/** width of the panel inclusive frames and title bar. */
	private int width;

	/** height of the panel inclusive frames and title bar. */
	private int height;

	/** name of the panel. */
	private String name;

	/** all children of this panel. */
	private LinkedList<WtPanel> children;

	/** the parent of this panel. */
	private WtPanel parent;

	/** List of registered CloseListener. */
	protected List<WtCloseListener> closeListeners;

	/** List of registered ClickListener. */
	protected List<WtClickListener> clickListeners;

	/** changes the titlebar/frame image. */
	private BufferedImage cachedImage;

	/** true when the window is already closed. */
	private boolean closed;

	/** make it transparent. */
	private float transparency;

	// ///////////////
	// Debug stuff //
	// ///////////////

	/** current texture. */
	private int texture;

	/** list of textures . */
	private final List<Sprite> textureSprites;

	protected IGameScreen gameScreen;

	/**
	 * Creates a new panel. The panel is not movable or resizeable and has no
	 * title bar or frame;
	 * 
	 * @param name
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param gameScreen 
	 * 			 The gameScreen to paint on.
	 */
	public WtPanel(final String name, final int x, final int y, final int width, final int height,
			final IGameScreen gameScreen) {
		this.name = name;
		this.titleText = name;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.children = new LinkedList<WtPanel>();
		this.titleBar = false;
		this.frame = false;
		this.movable = false;
		this.resizeable = false;
		this.closeable = true;
		this.closed = false;
		this.texture = 0;
		this.textureSprites = new ArrayList<Sprite>();
		this.closeListeners = new ArrayList<WtCloseListener>();
		this.clickListeners = new ArrayList<WtClickListener>();
		this.transparency = 1.0f;
		this.gameScreen = gameScreen;
		if (useWindowManager()) {
			WtWindowManager.getInstance().formatWindow(this);
		}

		// get texture sprite
		final SpriteStore st = SpriteStore.get();

		textureSprites.add(st.getSprite("data/gui/panelwood003.jpg"));
		if (Debug.CYCLE_PANEL_TEXTURES) {
			textureSprites.add(st.getSprite("data/gui/panelwood006.jpg"));
			textureSprites.add(st.getSprite("data/gui/panelwood032.gif"));
			textureSprites.add(st.getSprite("data/gui/panelwood119.jpg"));
			textureSprites.add(st.getSprite("data/gui/paneldrock009.jpg"));
			textureSprites.add(st.getSprite("data/gui/paneldrock048.jpg"));
			textureSprites.add(st.getSprite("data/gui/panelmetal003.gif"));
		}
	}

	/**
	 * Adds a CloseListener to this panel. All registered closelistener are
	 * notified before the panel is closed
	 * 
	 * @param listener
	 */
	public void registerCloseListener(final WtCloseListener listener) {
		closeListeners.add(listener);
	}

	/**
	 * removes a (registered) closelistener.
	 * 
	 * @param listener
	 */
	public void removeCloseListener(final WtCloseListener listener) {
		closeListeners.remove(listener);
	}

	/**
	 * Adds a ClickListener to this panel. All registered ClickListener are
	 * notified when user clicks on the panel. Note that not all panels must
	 * support/notify this type listener. The default panel for example ignores
	 * all click events.
	 * 
	 * @param listener
	 */
	public void registerClickListener(final WtClickListener listener) {
		clickListeners.add(listener);
	}

	/**
	 * removes a (registered) ClickListener.
	 * 
	 * @param listener
	 */
	public void removeClickListener(final WtClickListener listener) {
		clickListeners.remove(listener);
	}

	/**
	 * Override this if you want to save your window positions in the
	 * WindowManager. Default is not to use the WindowManager.
	 * 
	 * @return false
	 */
	protected boolean useWindowManager() {
		return false;
	}

	/** @return x-position of the panel (relative to its parent). */
	public int getX() {
		return x;
	}

	/** @return y-position of the panel (relative to its parent). */
	public int getY() {
		return y;
	}

	/** @return width of the panel. */
	public int getWidth() {
		return width;
	}

	/** @return height of the panel. */
	public int getHeight() {
		return height;
	}

	/** @return width of the client area. */
	protected int getClientWidth() {
		if (frame) {
			return width - FRAME_SIZE * 2;
		} else {
			return width;
		}
	}

	/** @return height of the panel. */
	protected int getClientHeight() {
		int clientHeight = height;
		if (frame) {
			clientHeight -= FRAME_SIZE * 2;
		}

		if (titleBar) {
			clientHeight -= TITLEBAR_SIZE;
		}

		return clientHeight;
	}

	/** @return x-pos of the client area. */
	protected int getClientX() {
		if (frame) {
			return FRAME_SIZE;
		} else {
			return 0;
		}
	}

	/** @return y-pos of the client area. */
	protected int getClientY() {
		int clienty;
		if (frame) {
			clienty = FRAME_SIZE;
		} else {
			clienty = 0;
		}

		if (titleBar) {
			clienty += TITLEBAR_SIZE;
		}

		return clienty;
	}

	/**
	 * Moves the panel by dx pixels to the right and dy pixels down.
	 * 
	 * @param dx
	 *            amount of pixels to move rights ( < 0 is allowed, will move
	 *            left)
	 * @param dy
	 *            amount of pixels to move down ( < 0 is allowed, will move up)
	 * @return true when the operation is allowed (panel is moved) or false if
	 *         not (panel is not moved)
	 */
	public boolean move(final int dx, final int dy) {
		return moveTo(x + dx, y + dy);
	}

	/**
	 * Moves the panel to the given position.
	 * 
	 * @param x
	 *            x-coordinate
	 * @param y
	 *            y-coordinate
	 * @return true when the operation is allowed (panel is moved) or false if
	 *         not (panel is not moved)
	 */
	public boolean moveTo(final int x, final int y) {
		return moveTo(x, y, false);
	}

	/**
	 * Moves the panel to the given position.
	 * 
	 * @param x
	 *            x-coordinate
	 * @param y
	 *            y-coordinate
	 * @param checkHeight
	 *            when true the height must be completely inside the parent
	 * @return true when the operation is allowed (panel is moved) or false if
	 *         not (panel is not moved)
	 */
	public boolean moveTo(final int x, final int y, final boolean checkHeight) {
		this.x = x;
		this.y = y;

		// check if we are inside the bounds of our parent
		if (x < 0) {
			this.x = 0;
		}

		if (hasParent() && (parent.getWidth() - width < x)) {
			this.x = parent.getWidth() - width;
		}

		if (y < 0) {
			this.y = 0;
		}

		if (checkHeight && hasParent()
				&& (parent.getHeight() - getHeight() < y)) {
			this.y = parent.getHeight() - getHeight();
		} else {
			int heightTemp = 0;
			if (hasTitleBar()) {
				heightTemp += TITLEBAR_SIZE;
			}

			if (hasFrame()) {
				heightTemp += FRAME_SIZE;
			}

			if (hasParent() && (parent.getHeight() - heightTemp < y)) {
				this.y = parent.getHeight() - heightTemp;
			}
		}

		// tell the windowmanager we're moved (if we use it)
		if (useWindowManager()) {
			WtWindowManager.getInstance().moveTo(this, this.x, this.y);
		}

		return true;
	}

	/**
	 * Sets the name.
	 * 
	 * @param name
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/** @return the name. */
	public String getName() {
		return name;
	}

	/** @return whether the panel has a title bar. */
	public boolean hasTitleBar() {
		return titleBar;
	}

	/**
	 * Enables/disables the title bar.
	 * 
	 * @param titleBar
	 */
	public void setTitleBar(final boolean titleBar) {
		this.titleBar = titleBar;
		// refresh cached panel image
		cachedImage = null;
	}

	/**
	 * Sets the text in the titlebar.
	 * 
	 * @param text
	 */
	public void setTitletext(final String text) {
		if (!this.titleText.equals(text)) {
			this.titleText = text;
			// refresh cached panel image
			cachedImage = null;
		}
	}

	/** @return whether the panel has a frame. */
	public boolean hasFrame() {
		return frame;
	}

	/**
	 * Enables/disables the frame.
	 * 
	 * @param frame
	 */
	public void setFrame(final boolean frame) {
		this.frame = frame;
		// refresh cached panel image
		cachedImage = null;
	}

	/** @return whether the panel is movable . */
	public boolean isMovable() {
		return movable;
	}

	/**
	 * Sets the embossed-state of then frame.
	 * 
	 * @param emboss
	 */
	public void setEmboss(final boolean emboss) {
		this.frameEmbossed = emboss;
		cachedImage = null;
	}

	/** @return whether the panels frame is embossed. */
	public boolean isEmbossed() {
		return frameEmbossed;
	}

	/**
	 * enables/disables moving the panel. Note: the panel must have a title bar
	 * to be movable
	 * 
	 * @param movable
	 */
	public void setMovable(final boolean movable) {
		this.movable = movable;
	}

	/** @return whether the panel can be resized. */
	public boolean isResizeable() {
		return resizeable;
	}

	/**
	 * enables/disables resizing the panel. Note: the panel must have a frame
	 * 
	 * @param resizeable
	 */
	public void setResizeable(final boolean resizeable) {
		this.resizeable = resizeable;
	}

	/** @return whether the panel can be minmized. */
	public boolean isMinimizeable() {
		return minimizeable;
	}

	/**
	 * enables/disables minimizing the panel. Note: the panel must have a title
	 * bar
	 * 
	 * @param minimizeable
	 */
	public void setMinimizeable(final boolean minimizeable) {
		this.minimizeable = minimizeable;
	}

	/** @return whether the panel is minimized. */
	public boolean isMinimized() {
		return minimized;
	}

	/**
	 * Sets the minimized state.
	 * 
	 * @param minimized
	 */
	public void setMinimized(final boolean minimized) {
		this.minimized = minimized;
		// refresh cached panel image
		cachedImage = null;

		// tell the windowmanager we're changed (if we use it)
		if (useWindowManager()) {
			WtWindowManager.getInstance().setMinimized(this, minimized);
		}
	}

	/** @return whether the panel can be closed. */
	public boolean isCloseable() {
		return closeable;
	}

	/**
	 * enables/disables closing the panel. Note: the panel must have a title bar
	 * 
	 * @param closeable
	 */
	public void setCloseable(final boolean closeable) {
		this.closeable = closeable;
	}

	/** @return the parent of the panel. */
	public WtPanel getParent() {
		return parent;
	}

	/**
	 * Sets the parent of the panel. Do not use this function if you don't know
	 * exactly what you're doing.
	 * 
	 * @param parent
	 */
	protected void setParent(final WtPanel parent) {
		this.parent = parent;
	}

	/** @return whether the panel has a parent. */
	public boolean hasParent() {
		return (parent != null);
	}

	/**
	 * Adds a child-panel to this panel.
	 * 
	 * @param panel
	 */
	public synchronized void addChild(final WtPanel panel) {
		if (panel.hasParent()) {
			logger.error("Panel " + panel.name + " cannot be added to " + name
					+ " because it already is a child of " + panel.parent.name);
			return;
		}
		final LinkedList<WtPanel> newChildren = new LinkedList<WtPanel>(children);
		newChildren.addFirst(panel);
		this.children = newChildren;
		panel.parent = this;
	}

	/**
	 * Removes a child-panel from this panel.
	 * 
	 * @param panel
	 */
	public synchronized void removeChild(final WtPanel panel) {
		final LinkedList<WtPanel> newChildren = new LinkedList<WtPanel>(children);
		newChildren.remove(panel);
		this.children = newChildren;
		// be sure to remove ourself from the other panel
		panel.parent = null;
	}

	/** @return true when the window is scheduled to be closed. */
	public boolean isClosed() {
		return closed;
	}

	/** Tells this panel (and all subpanels) to close. 
	 * @param gameScreen 
	 * 			 The gameScreen to paint on.
	 */
	public void close(final IGameScreen gameScreen) {
		if (isCloseable()) {
			setVisible(false);
		}
	}

	/**
	 * Destroy the panel.
	 * @param gameScreen 
	 * 			 The gameScreen to paint on.
	 */
	public void destroy(final IGameScreen gameScreen) {
		setVisible(false);

		// destroy/remove all children
		final Iterator<WtPanel> iter = children.iterator();

		while (iter.hasNext()) {
			final WtPanel child = iter.next();

			child.destroy(gameScreen);
			iter.remove();
		}

		parent = null;
		closeListeners.clear();
		clickListeners.clear();
	}

	/**
	 * Scans all children and remove those without our parenting.
	 */
	protected void checkDisowned() {
		// remove un/reparented children
		final Iterator<WtPanel> iter = children.iterator();

		while (iter.hasNext()) {
			final WtPanel child = iter.next();

			if (child.getParent() != this) {
				iter.remove();
			}
		}
	}

	/**
	 * Determine if the window is visible.
	 * 
	 * @return <code>true</code> if the window is visible.
	 */
	public boolean isVisible() {
		return !closed;
	}

	/**
	 * Set the window as visible (or hidden). This does not check if closing is
	 * allowed.
	 * 
	 * @param visible
	 *            Whether the window should be visible.
	 */
	public void setVisible(final boolean visible) {
		/*
		 * No change?
		 */
		if (visible != closed) {
			return;
		}

		/*
		 * Apply to children
		 */
		for (final WtPanel child : children) {
			child.setVisible(visible);
		}

		closed = !visible;

		if (closed) {
			// inform all listeners we're closed
			final WtCloseListener[] listeners = closeListeners
					.toArray(new WtCloseListener[closeListeners.size()]);

			for (final WtCloseListener listener : listeners) {
				listener.onClose(name);
			}
		}

		// tell the windowmanager we're changed (if we use it)
		if (useWindowManager()) {
			WtWindowManager.getInstance().setVisible(this, visible);
		}
	}

	/**
	 * notifies all registered clicklisteners that this panel has been clicked .
	 * 
	 * @param name
	 * @param point
	 */
	protected void notifyClickListeners(final String name, final Point point) {
		final WtClickListener[] listeners = clickListeners
				.toArray(new WtClickListener[clickListeners.size()]);

		for (final WtClickListener listener : listeners) {
			listener.onClick(name, point, gameScreen);
		}
	}

	/**
	 * @return an unmodifiable list this panels children. 
	 */
	protected List<WtPanel> getChildren() {
		return Collections.unmodifiableList(children);
	}

	/**
	 * Resizes the panel so that the client area has the given width and height.
	 * 
	 * @param width
	 *            width of client area
	 * @param height
	 *            height of client area
	 */
	public void resizeToFitClientArea(final int width, final int height) {
		this.width = width;
		this.height = height;

		// adjust size to include the frame
		if (frame) {
			this.width += FRAME_SIZE * 2;
			this.height += FRAME_SIZE * 2;
		}

		// adjust size to include the title bar
		if (titleBar) {
			this.height += TITLEBAR_SIZE;
		}
		// refresh cached panel image
		cachedImage = null;
	}

	/**
	 * creates the image background as an image.
	 * 
	 * @param g
	 * @return the image background
	 */
	private BufferedImage recreatePanelImage(final Graphics g) {
		int localHeight = this.height;

		final GraphicsConfiguration gc = GraphicsEnvironment
				.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getDefaultConfiguration();
		final BufferedImage tempImage = gc.createCompatibleImage(width, height,
				Transparency.TRANSLUCENT);
		Graphics panelGraphics = tempImage.createGraphics();

		// if this frame is minimized, reduce frame to enclose the title bar
		// only
		if (isMinimized()) {
			localHeight = TITLEBAR_SIZE + FRAME_SIZE * 2;
		}

		// get texture sprite
		final Sprite woodTexture = textureSprites.get(texture);

		final int repeatx = width / woodTexture.getWidth() + 1;
		final int repeaty = height / woodTexture.getHeight() + 1;

		for (int xTemp = 0; xTemp < repeatx; xTemp++) {
			for (int yTemp = 0; yTemp < repeaty; yTemp++) {
				woodTexture.draw(panelGraphics, xTemp * woodTexture.getWidth(),
						yTemp * woodTexture.getHeight());
			}
		}

		final Color darkColor = new Color(0.0f, 0.0f, 0.0f, 0.5f);
		final Color lightColor = new Color(1.0f, 1.0f, 1.0f, 0.5f);

		// draw frame
		if (frame) {
			for (int i = 0; i < FRAME_SIZE; i++) {
				if (frameEmbossed) {
					panelGraphics.setColor(darkColor);
				} else {
					panelGraphics.setColor(lightColor);
				}
				panelGraphics.drawLine(i, i, width - i - 2, i);
				panelGraphics.drawLine(i, i, i, localHeight - i - 2);

				if (frameEmbossed) {
					panelGraphics.setColor(lightColor);
					
				} else {
					panelGraphics.setColor(darkColor);
					
				}
				panelGraphics.drawLine(width - i - 1, i, width - i - 1,
						localHeight - i - 1);
				panelGraphics.drawLine(i, localHeight - i - 1, width - i - 1,
						localHeight - i - 1);
			}
			// update clipping to exclude the frame
			panelGraphics = panelGraphics.create(FRAME_SIZE, FRAME_SIZE, width
					- (FRAME_SIZE * 2), localHeight - (FRAME_SIZE * 2));
		}

		// draw title bar
		if (titleBar) {
			if (isMinimizeable()) {
				// minimize button
				panelGraphics.setColor(lightColor);
				final Rectangle rect = getMiminizeButton();
				panelGraphics.fillRect(rect.x - FRAME_SIZE,
						rect.y - FRAME_SIZE, rect.width, rect.height);

				panelGraphics.setColor(Color.BLACK);
				panelGraphics.fillRect(rect.x - FRAME_SIZE + 1, rect.y
						- FRAME_SIZE + rect.height - 3, rect.width - 2, 2);
			}

			if (isCloseable()) {
				// minimize button
				panelGraphics.setColor(lightColor);
				final Rectangle rect = getCloseButton();
				panelGraphics.fillRect(rect.x - FRAME_SIZE,
						rect.y - FRAME_SIZE, rect.width, rect.height);

				panelGraphics.setColor(Color.BLACK);
				panelGraphics.drawLine(rect.x - FRAME_SIZE + 1, rect.y
						- FRAME_SIZE + 1, rect.x - FRAME_SIZE + rect.width - 2,
						rect.y - FRAME_SIZE + rect.height - 2);
				panelGraphics.drawLine(rect.x - FRAME_SIZE + 2, rect.y
						- FRAME_SIZE + 1, rect.x - FRAME_SIZE + rect.width - 2,
						rect.y - FRAME_SIZE + rect.height - 3);
				panelGraphics.drawLine(rect.x - FRAME_SIZE + 1, rect.y
						- FRAME_SIZE + 2, rect.x - FRAME_SIZE + rect.width - 3,
						rect.y - FRAME_SIZE + rect.height - 2);
				panelGraphics.drawLine(rect.x - FRAME_SIZE + rect.width - 2,
						rect.y - FRAME_SIZE + 1, rect.x - FRAME_SIZE + 1,
						rect.y - FRAME_SIZE + rect.height - 2);
				panelGraphics.drawLine(
						rect.x - FRAME_SIZE + rect.width - 2 - 1, rect.y
								- FRAME_SIZE + 1, rect.x - FRAME_SIZE + 1,
						rect.y - FRAME_SIZE + rect.height - 2 - 1);
				panelGraphics.drawLine(rect.x - FRAME_SIZE + rect.width - 2,
						rect.y - FRAME_SIZE + 1 + 1, rect.x - FRAME_SIZE + 1
								+ 1, rect.y - FRAME_SIZE + rect.height - 2);
			}

			// the dark line under the title bar
			panelGraphics.setColor(darkColor);
			panelGraphics.drawLine(0, TITLEBAR_SIZE, width - (FRAME_SIZE * 2),
					TITLEBAR_SIZE);
			panelGraphics.drawLine(0, TITLEBAR_SIZE + 1, width
					- (FRAME_SIZE * 2), TITLEBAR_SIZE + 1);

			// panels title text
			panelGraphics.setColor(new Color(0.8f, 0.8f, 0.8f, 1.0f));
			final Font font = panelGraphics.getFont();
			panelGraphics.setFont(font
					.deriveFont(Font.BOLD, TITLEBAR_FONT_SIZE));
			panelGraphics.drawString(titleText, 3, TITLEBAR_FONT_SIZE);

			// update clipping
			panelGraphics = panelGraphics.create(0, TITLEBAR_SIZE + 2, width
					- (FRAME_SIZE * 2), height - (FRAME_SIZE * 2)
					- TITLEBAR_SIZE - 2);
		}

		final BufferedImage image = gc.createCompatibleImage(width, localHeight);
		image.createGraphics().drawImage(tempImage, 0, 0, null);

		return image;
	}

	/**
	 * draws the panel into the graphics object.
	 * 
	 * @param g
	 *            graphics where to render to
	 * @param gameScreen
	 */
	public void draw(Graphics2D g, final IGameScreen gameScreen) {
		// are we closed? then don't draw anything
		if (isClosed()) {
			return;
		}

		/*
		 * Remove un/reparented children
		 */
		checkDisowned();

		// only draw something when we have a title bar or a frame
		if (frame || titleBar) {
			BufferedImage image = cachedImage;
			// (re)create the image if it does not exist
			if (image == null) {
				image = recreatePanelImage(g);
				cachedImage = image;
			}
			if (transparency < 0.99) {
				g.setComposite(AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, transparency));
			}

			g.drawImage(image, 0, 0, null);
		}


		if (frame) {
			g = (Graphics2D) g.create(FRAME_SIZE, FRAME_SIZE, width
					- (FRAME_SIZE * 2), height - (FRAME_SIZE * 2));
		}

		if (titleBar) {
			g = (Graphics2D) g.create(0, TITLEBAR_SIZE + 2, width
					- (FRAME_SIZE * 2), height - (FRAME_SIZE * 2)
					- TITLEBAR_SIZE - 2);
		}

		if (!minimized) {
			drawContent(g, gameScreen);
		}
	}

	/**
	 * Draw the panel contents. This is only called while open and not
	 * minimized.
	 * 
	 * @param g
	 *            The graphics context to draw with.
	 * @param gameScreen
	 */
	protected void drawContent(final Graphics2D g, final IGameScreen gameScreen) {
		drawChildren(g, gameScreen);
	}

	/**
	 * panels draw themselves.
	 * 
	 * @param g
	 */
	public void drawDragged(final Graphics g) {
	}

	/**
	 * draws all children.
	 * 
	 * @param g
	 *            Graphics object clipped to the client region.
	 * @param gameScreen
	 */
	protected void drawChildren(final Graphics2D g, final IGameScreen gameScreen) {
		int i = children.size();

		while (i-- != 0) {
			final WtPanel child = children.get(i);

			// get correct clipped graphics
			final Graphics2D cg = (Graphics2D) g.create(child.getX(), child.getY(),
					child.getWidth(), child.getHeight());

			child.draw(cg, gameScreen);

			cg.dispose();
		}
	}

	/**
	 * Checks if the Point p is inside the Panel. Note that the coordinates are
	 * local to the parent, not local to this Panel.
	 * 
	 * @param p
	 *            point to check (in parents coordinate space)
	 * @return true when the point is in this panel, false otherwise
	 */
	public boolean isHit(final Point p) {
		return isHit(p.x, p.y);
	}

	/**
	 * Checks if the point is inside the Panel. Note that the coordinates are
	 * local to the parent, not local to this Panel.
	 * 
	 * @param x
	 *            x-coordinate to check (in parents coordinate space)
	 * @param y
	 *            y-coordinate to check (in parents coordinate space)
	 * @return true when the point is in this panel, false otherwise
	 */
	public boolean isHit(final int x, final int y) {
		if (isClosed()) {
			return false;
		}

		int heightTemp = this.height;
		final int widthTemp = this.width;

		if (isMinimized()) {
			if (frame) {
				heightTemp = TITLEBAR_SIZE + (FRAME_SIZE * 2);
			} else {
				heightTemp = TITLEBAR_SIZE;
			}
		}

		if ((x < this.x) || (y < this.y) || (x > this.x + widthTemp)
				|| (y > this.y + heightTemp)) {
			return false;
		}
		return true;
	}

	/**
	 * @param x
	 * @param y
	 * @return true if the point is in the title.
	 */
	private boolean hitTitle(final int x, final int y) {
		// do we have a title
		if (!titleBar) {
			return false;
		}

		//
		if ((x < FRAME_SIZE) || (y < FRAME_SIZE) || (x > width - FRAME_SIZE)
				|| (y > FRAME_SIZE + TITLEBAR_SIZE)) {
			return false;
		}

		return true;
	}

	/**
	 * @param p
	 * @return a object for dragging which is at the position p or null.
	 */
	protected WtDraggable getDragged(final Point p) {
		return getDragged(p.x, p.y);
	}

	/**
	 * @param x
	 * @param y
	 * @return a object for dragging which is at the position (x,y) or null.
	 */
	protected WtDraggable getDragged(final int x, final int y) {
		if (isClosed()) {
			return null;
		}

		// if the user drags our titlebar we return ourself
		if (hitTitle(x, y)) {
			return this;
		}

		// translate point to client coordinates
		int transposedX = x - getClientX();
		int transposedY = y - getClientY();

		// check all children
		for (final WtPanel panel : children) {
			// only if the point is inside the child
			if (panel.isHit(transposedX, transposedY)) {
				final WtDraggable draggedObject = panel.getDragged(transposedX - panel.getX(),
						transposedY - panel.getY());

				// did we get an object
				if (draggedObject != null) {
					// activate the panel
					focus(panel);
					return draggedObject;
				}

				// this child don't want to be dragged. Ignore all children
				// below
				// this one
				return null;
			}
		}

		// no more dragging allowed
		return null;
	}

	/**
	 * Checks if there is a droptarget direct under the position (x,y).
	 * 
	 * @param x
	 *            x-coordinate in client space
	 * @param y
	 *            y-coordinate in client space
	 * @param droppedObject
	 *            the dropped object
	 * @return true when this panel or a child panel is a droptarget and has
	 *         received the object, false when there is no droptarget found
	 */
	protected boolean checkDropped(final int x, final int y, final WtDraggable droppedObject) {
		// translate point to client coordinates
		int transposedX = x - getClientX();
		int transposedY = y - getClientY();

		// now ask each child
		for (final WtPanel panel : children) {
			// only if the point is inside the child
			if (panel.isHit(transposedX, transposedY)) {
				// the child checks itself
				if (panel.checkDropped(transposedX - panel.getX(), transposedY - panel.getY(),
						droppedObject)) {
					return true;
				}
			}
		}

		// are we ourself a drop target
		if (this instanceof WtDropTarget) {
			final WtDropTarget target = (WtDropTarget) this;
			return target.onDrop(transposedX, transposedY, droppedObject);
		}

		// no drop target found
		return false;
	}

	/** @return the rectangle for the minimize button. */
	private Rectangle getMiminizeButton() {
		return new Rectangle(width - (TITLEBAR_SIZE * 2) - FRAME_SIZE,
				FRAME_SIZE + 1, TITLEBAR_SIZE - 2, TITLEBAR_SIZE - 2);
	}

	/**
	 * @param x
	 * @param y
	 * @return true when the point (x,y) is inside the minimize button.
	 */
	private boolean hitMinimizeButton(final int x, final int y) {
		return getMiminizeButton().contains(x, y);
	}

	/** @return the rectangle for the close button. */
	private Rectangle getCloseButton() {
		return new Rectangle(width - TITLEBAR_SIZE - FRAME_SIZE,
				FRAME_SIZE + 1, TITLEBAR_SIZE - 2, TITLEBAR_SIZE - 2);
	}

	/**
	 * @param x
	 * @param y
	 * @return true when the point (x,y) is inside the close button.
	 */
	private boolean hitCloseButton(final int x, final int y) {
		return getCloseButton().contains(x, y);
	}

	/**
	 * callback for a mouse click. returns true when the click has been
	 * processed
	 * 
	 * @param p
	 * @return true if click was processed
	 */
	public synchronized boolean onMouseClick(final Point p) {
		if (isClosed()) {
			return false;
		}

		// check if the minimize button has been clicked
		if (titleBar && minimizeable && hitMinimizeButton(p.x, p.y)) {
			// change minimized state
			final boolean state = !isMinimized();

			setMinimized(state);
			clickSound(state);

			return true;
		}

		// check if the close button has been clicked
		if (titleBar && closeable && hitCloseButton(p.x, p.y)) {
			// close the window
			close(gameScreen);
			return true;
		}

		if (Debug.CYCLE_PANEL_TEXTURES && hitTitle(p.x, p.y)) {
			texture = (texture + 1) % textureSprites.size();
			cachedImage = null;
		}

		// translate point to client coordinates
		final Point p2 = p.getLocation();
		p2.translate(-getClientX(), -getClientY());

		// be sure to inform all children of the mouse click
		for (final WtPanel panel : children) {
			// only if the point is inside the child
			if (panel.isHit(p2.x, p2.y)) {
				focus(panel);
				final Point point = p2.getLocation();
				point.translate(-panel.getX(), -panel.getY());
				// click the child
				return panel.onMouseClick(point);
			}
		}

		// click not processed
		return false;
	}

	private void clickSound(final boolean state) {
		if (state) {
			playOpenSound();
		} else {
			playCloseSound();
		}
	}

	private void playCloseSound() {
		SoundMaster.play("click-10.wav");
	}

	protected void playOpenSound() {
		//TODO: use polymorphism
		if (name.equals("bag")) {
			SoundMaster.play("click-8.wav");
		}  else if (name.equals("chest")) {
			SoundMaster.play("click-5.wav");
		}
	}

	/**
	 * callback for a doubleclick.
	 * 
	 * @param p
	 * 
	 * @return true if click was processed
	 */
	public synchronized boolean onMouseDoubleClick(final Point p) {
		if (isClosed()) {
			return false;
		}

		// translate point to client coordinates
		final Point p2 = p.getLocation();
		p2.translate(-getClientX(), -getClientY());

		// be sure to inform all children of the mouse click
		for (final WtPanel panel : children) {
			// only if the point is inside the child
			if (panel.isHit(p2.x, p2.y)) {
				final Point point = p2.getLocation();
				point.translate(-panel.getX(), -panel.getY());
				// right-click the child
				panel.onMouseDoubleClick(point);

				// the doubleclick hit the window, bail out
				return true;
			}
		}
		// not processed
		return false;
	}

	/**
	 * the right mouse button has been clicked (callback). *
	 * 
	 * @param p
	 * 
	 * @return true if click was processed
	 */
	public synchronized boolean onMouseRightClick(final Point p) {
		if (isClosed()) {
			return false;
		}
		if (isMinimized()) {
			return false;
		}
		// translate point to client coordinates
		final Point p2 = p.getLocation();
		p2.translate(-getClientX(), -getClientY());

		// be sure to inform all children of the mouse click
		for (final WtPanel panel : children) {
			// only if the point is inside the child
			if (panel.isHit(p2.x, p2.y)) {
				final Point point = p2.getLocation();
				point.translate(-panel.getX(), -panel.getY());
				// right-click the child
				panel.onMouseRightClick(point);
				// the rightclick hit the window, bail out
				return true;
			}
		}
		// not processed
		return false;
	}

	public boolean dragStarted(final IGameScreen gameScreen) {
		if (isClosed()) {
			return false;
		}

		dragPosition = new Point(x, y);
		return true;
	}

	/**
	 * ignored.
	 * 
	 * @param p
	 * @return false
	 */
	public boolean dragFinished(final Point p, final IGameScreen gameScreen) {
		return false;
	}

	/**
	 * move the frame to the requested position .
	 * 
	 * @param p
	 * @return true if done
	 */
	public boolean dragMoved(final Point p) {
		return moveTo(dragPosition.x + p.x, dragPosition.y + p.y);
	}

	/**
	 * moves the child panel on top of all others.
	 * 
	 * @param child
	 */
	private void focus(final WtPanel child) {
		if (!children.remove(child)) {
			return;
		}

		children.addFirst(child);
	}

	/**
	 * Sets the context menu. It is closed automatically one the user clicks.
	 * outside of it. Note: This implementation forwards the context menu to its
	 * parent (if it has one) until someone feels responsible to handle it. This
	 * is most times the very root of the window hierarchy.
	 * 
	 * @param contextMenu
	 */
	public void setContextMenu(final JPopupMenu contextMenu) {
		if (parent != null) {
			// moves the context-menu to match the position of this panel
			parent.setContextMenu(contextMenu);
		}
	}

	public void setTransparency(final float t) {
		this.transparency = t;
	}

	@Override
	public String toString() {
		return super.toString() + ": " + name + " at " + x + "x" + y + " size:"
				+ width + "x" + height;
	}
}
