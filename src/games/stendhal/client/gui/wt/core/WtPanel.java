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

import games.stendhal.client.gui.ManagedWindow;
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

	/** size of the titlebar */
	private static final int TITLEBAR_SIZE = 14;

	/** size of the titlebar font */
	private static final int TITLEBAR_FONT_SIZE = 12;

	/** thickness of the frame */
	private static final int FRAME_SIZE = 3;

	/** panel has a title bar */
	private boolean titleBar;

	/** text of the title */
	private String titleText;

	/** panel has a frame */
	private boolean frame;

	/** is the frame embossed? */
	private boolean frameEmbossed;

	/** panel is movable */
	private boolean movable;

	/** panel can be resized */
	private boolean resizeable;

	/** panel can be minimized */
	private boolean minimizeable;

	/** true when the panel is minimized */
	private boolean minimized;

	/** true when the panel is closeable */
	private boolean closeable;

	/** x-position relative to its parent */
	private int x;

	/** y-position relative to its parent */
	private int y;

	/** the point where we were before the drag started */
	private Point dragPosition;

	/** width of the panel inclusive frames and title bar */
	private int width;

	/** height of the panel inclusive frames and title bar */
	private int height;

	/** name of the panel */
	private String name;

	/** all children of this panel. */
	private LinkedList<WtPanel> children;

	/** the parent of this panel */
	private WtPanel parent;

	/** List of registered CloseListener */
	protected List<WtCloseListener> closeListeners;

	/** List of registered ClickListener */
	protected List<WtClickListener> clickListeners;

	/** changes the titlebar/frame image */
	private BufferedImage cachedImage;

	/** true when the window is already closed */
	private boolean closed;

	/** make it transparent */
	private float transparency;

	// ///////////////
	// Debug stuff //
	// ///////////////

	/** current texture */
	private int texture;

	/** list of textures */
	private List<Sprite> textureSprites;

	/**
	 * Creates a new panel. The panel is not movable or resizeable and has no
	 * title bar or frame;
	 */
	public WtPanel(String name, int x, int y, int width, int height) {
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

		if (useWindowManager()) {
			WtWindowManager.getInstance().formatWindow(this);
		}

		// get texture sprite
		SpriteStore st = SpriteStore.get();

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
	 */
	public void registerCloseListener(WtCloseListener listener) {
		closeListeners.add(listener);
	}

	/** removes a (registered) closelistener */
	public void removeCloseListener(WtCloseListener listener) {
		closeListeners.remove(listener);
	}

	/**
	 * Adds a ClickListener to this panel. All registered ClickListener are
	 * notified when user clicks on the panel. Note that not all panels must
	 * support/notify this type listener. The default panel for example ignores
	 * all click events.
	 */
	public void registerClickListener(WtClickListener listener) {
		clickListeners.add(listener);
	}

	/** removes a (registered) ClickListener */
	public void removeClickListener(WtClickListener listener) {
		clickListeners.remove(listener);
	}

	/**
	 * Override this if you want to save your window positions in the
	 * WindowManager. Default is not to use the WindowManager.
	 */
	protected boolean useWindowManager() {
		return false;
	}

	/** returns x-position of the panel (relative to its parent) */
	public int getX() {
		return x;
	}

	/** returns y-position of the panel (relative to its parent) */
	public int getY() {
		return y;
	}

	/** returns width of the panel */
	public int getWidth() {
		return width;
	}

	/** returns height of the panel */
	public int getHeight() {
		return height;
	}

	/** returns width of the client area */
	protected int getClientWidth() {
		return (frame ? width - FRAME_SIZE * 2 : width);
	}

	/** returns height of the panel */
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

	/** returns x-pos of the client area */
	protected int getClientX() {
		return (frame ? FRAME_SIZE : 0);
	}

	/** returns y-pos of the client area */
	protected int getClientY() {
		int clienty = (frame ? FRAME_SIZE : 0);

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
	public boolean move(int dx, int dy) {
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
	public boolean moveTo(int x, int y) {
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
	public boolean moveTo(int x, int y, boolean checkHeight) {
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

	/** sets the name */
	public void setName(String name) {
		this.name = name;
	}

	/** returns the name */
	public String getName() {
		return name;
	}

	/** returns wether the panel has a title bar */
	public boolean hasTitleBar() {
		return titleBar;
	}

	/** enables/disables the title bar */
	public void setTitleBar(boolean titleBar) {
		this.titleBar = titleBar;
		// refresh cached panel image
		cachedImage = null;
	}

	/** sets the text in the titlebar */
	public void setTitletext(String text) {
		if (!this.titleText.equals(text)) {
			this.titleText = text;
			// refresh cached panel image
			cachedImage = null;
		}
	}

	/** returns wether the panel has a frame */
	public boolean hasFrame() {
		return frame;
	}

	/** enables/disables the frame */
	public void setFrame(boolean frame) {
		this.frame = frame;
		// refresh cached panel image
		cachedImage = null;
	}

	/** returns wether the panel is movable */
	public boolean isMovable() {
		return movable;
	}

	/** sets the embossed-state of then frame */
	public void setEmboss(boolean emboss) {
		this.frameEmbossed = emboss;
		cachedImage = null;
	}

	/** returns wether the panels frame is embossed */
	public boolean isEmbossed() {
		return frameEmbossed;
	}

	/**
	 * enables/disables moving the panel. Note: the panel must have a title bar
	 * to be movable
	 */
	public void setMovable(boolean movable) {
		this.movable = movable;
	}

	/** returns wether the panel is resizeable */
	public boolean isResizeable() {
		return resizeable;
	}

	/** enables/disables resizing the panel. Note: the panel must have a frame */
	public void setResizeable(boolean resizeable) {
		this.resizeable = resizeable;
	}

	/** returns wether the panel is minimizeable */
	public boolean isMinimizeable() {
		return minimizeable;
	}

	/**
	 * enables/disables minimizing the panel. Note: the panel must have a title
	 * bar
	 */
	public void setMinimizeable(boolean minimizeable) {
		this.minimizeable = minimizeable;
	}

	/** returns whether the panel is minimized */
	public boolean isMinimized() {
		return minimized;
	}

	/** sets the minimized state */
	public void setMinimized(boolean minimized) {
		this.minimized = minimized;
		// refresh cached panel image
		cachedImage = null;

		// tell the windowmanager we're changed (if we use it)
		if (useWindowManager()) {
			WtWindowManager.getInstance().setMinimized(this, minimized);
		}
	}

	/** returns wether the panel is closeable */
	public boolean isCloseable() {
		return closeable;
	}

	/**
	 * enables/disables closing the panel. Note: the panel must have a title bar
	 */
	public void setCloseable(boolean closeable) {
		this.closeable = closeable;
	}

	/** returns the parent of the panel */
	public WtPanel getParent() {
		return parent;
	}

	/**
	 * Sets the parent of the panel. Do not use this function if you don't know
	 * exactly what you're doing.
	 */
	protected void setParent(WtPanel parent) {
		this.parent = parent;
	}

	/** returns wether the panel has a parent */
	public boolean hasParent() {
		return (parent != null);
	}

	/** adds a child-panel to this panel */
	public synchronized void addChild(WtPanel panel) {
		if (panel.hasParent()) {
			logger.error("Panel " + panel.name + " cannot be added to " + name
					+ " because it already is a child of " + panel.parent.name);
			return;
		}
		LinkedList<WtPanel> newChildren = new LinkedList<WtPanel>(children);
		newChildren.addFirst(panel);
		this.children = newChildren;
		panel.parent = this;
	}

	/** removes a child-panel from this panel */
	public synchronized void removeChild(WtPanel panel) {
		LinkedList<WtPanel> newChildren = new LinkedList<WtPanel>(children);
		newChildren.remove(panel);
		this.children = newChildren;
		// be sure to remove ourself from the other panel
		panel.parent = null;
	}

	/** returns true when the window is scheduled to be closed. */
	public boolean isClosed() {
		return closed;
	}

	/** tells this panel (and all subpanels) to close */
	public void close() {
		if (isCloseable()) {
			setVisible(false);
		}
	}

	/**
	 * Destroy the panel.
	 */
	public void destroy() {
		setVisible(false);

		// destroy/remove all children
		Iterator<WtPanel> iter = children.iterator();

		while (iter.hasNext()) {
			WtPanel child = iter.next();

			child.destroy();
			iter.remove();
		}

		parent = null;
		closeListeners.clear();
		clickListeners.clear();
	}

	/**
	 * Scan all children and remove those without our parenting
	 */
	protected void checkDisowned() {
		// remove un/reparented children
		Iterator<WtPanel> iter = children.iterator();

		while (iter.hasNext()) {
			WtPanel child = iter.next();

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
	public void setVisible(boolean visible) {
		/*
		 * No change?
		 */
		if (visible != closed) {
			return;
		}

		/*
		 * Apply to children
		 */
		for (WtPanel child : children) {
			child.setVisible(visible);
		}

		closed = !visible;

		if (closed) {
			// inform all listeners we're closed
			WtCloseListener[] listeners = closeListeners.toArray(new WtCloseListener[closeListeners.size()]);

			for (WtCloseListener listener : listeners) {
				listener.onClose(name);
			}
		}

		// tell the windowmanager we're changed (if we use it)
		if (useWindowManager()) {
			WtWindowManager.getInstance().setVisible(this, visible);
		}
	}

	/** notifies all registered clicklisteners that this panel has been clicked */
	protected void notifyClickListeners(String name, Point point) {
		WtClickListener[] listeners = clickListeners.toArray(new WtClickListener[clickListeners.size()]);

		for (WtClickListener listener : listeners) {
			listener.onClick(name, point);
		}
	}

	/**
	 * returns an unmodifiable list this panels children. TODO: cache this
	 */
	protected List<WtPanel> getChildren() {
		return Collections.unmodifiableList(children);
	}

	/**
	 * resizes the panel so that the client area has the given width and height
	 * 
	 * @param width
	 *            width of client area
	 * @param height
	 *            height of client area
	 */
	public void resizeToFitClientArea(int width, int height) {
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

	/** creates the image background as an image */
	private BufferedImage recreatePanelImage(Graphics g) {
		int localHeight = this.height;

		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		BufferedImage tempImage = gc.createCompatibleImage(width, height,
				Transparency.TRANSLUCENT);
		Graphics panelGraphics = tempImage.createGraphics();

		// if this frame is minimized, reduce frame to enclose the title bar
		// only
		if (isMinimized()) {
			localHeight = TITLEBAR_SIZE + FRAME_SIZE * 2;
		}

		// get texture sprite
		Sprite woodTexture = textureSprites.get(texture);

		int repeatx = width / woodTexture.getWidth() + 1;
		int repeaty = height / woodTexture.getHeight() + 1;

		for (int xTemp = 0; xTemp < repeatx; xTemp++) {
			for (int yTemp = 0; yTemp < repeaty; yTemp++) {
				woodTexture.draw(panelGraphics, xTemp * woodTexture.getWidth(),
						yTemp * woodTexture.getHeight());
			}
		}

		Color darkColor = new Color(0.0f, 0.0f, 0.0f, 0.5f);
		Color lightColor = new Color(1.0f, 1.0f, 1.0f, 0.5f);

		// draw frame
		if (frame) {
			for (int i = 0; i < FRAME_SIZE; i++) {
				panelGraphics.setColor(frameEmbossed ? darkColor : lightColor);
				panelGraphics.drawLine(i, i, width - i - 2, i);
				panelGraphics.drawLine(i, i, i, localHeight - i - 2);

				panelGraphics.setColor(frameEmbossed ? lightColor : darkColor);
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
				Rectangle rect = getMiminizeButton();
				panelGraphics.fillRect(rect.x - FRAME_SIZE,
						rect.y - FRAME_SIZE, rect.width, rect.height);

				panelGraphics.setColor(Color.BLACK);
				panelGraphics.fillRect(rect.x - FRAME_SIZE + 1, rect.y
						- FRAME_SIZE + rect.height - 3, rect.width - 2, 2);
			}

			if (isCloseable()) {
				// minimize button
				panelGraphics.setColor(lightColor);
				Rectangle rect = getCloseButton();
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
			Font font = panelGraphics.getFont();
			panelGraphics.setFont(font.deriveFont(Font.BOLD, TITLEBAR_FONT_SIZE));
			panelGraphics.drawString(titleText, 3, TITLEBAR_FONT_SIZE);

			// update clipping
			panelGraphics = panelGraphics.create(0, TITLEBAR_SIZE + 2, width
					- (FRAME_SIZE * 2), height - (FRAME_SIZE * 2)
					- TITLEBAR_SIZE - 2);
		}

		BufferedImage image = gc.createCompatibleImage(width, localHeight);
		image.createGraphics().drawImage(tempImage, 0, 0, null);

		return image;
	}

	/**
	 * draws the panel into the graphics object
	 * 
	 * @param g
	 *            graphics where to render to
	 */
	public void draw(Graphics2D g) {
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
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency));
			}

			g.drawImage(image, 0, 0, null);
		}

		// TODO: Find a clean way to do g.dispose() after g.create()'s

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
			drawContent(g);
		}
	}

	/**
	 * Draw the panel contents. This is only called while open and not
	 * minimized.
	 * 
	 * @param g
	 *            The graphics context to draw with.
	 */
	protected void drawContent(Graphics2D g) {
		drawChildren(g);
	}

	/** panels draw themselves */
	public void drawDragged(Graphics g) {
	}

	/**
	 * draws all children
	 * 
	 * @param clientArea
	 *            Graphics object clipped to the client region.
	 */
	protected void drawChildren(Graphics2D g) {
		int i = children.size();

		while (i-- != 0) {
			WtPanel child = children.get(i);

			// get correct clipped graphics
			Graphics2D cg = (Graphics2D) g.create(child.getX(), child.getY(),
					child.getWidth(), child.getHeight());

			child.draw(cg);

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
	public boolean isHit(Point p) {
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
	public boolean isHit(int x, int y) {
		if (isClosed()) {
			return false;
		}

		int heightTemp = this.height;
		int widthTemp = this.width;

		if (isMinimized()) {
			heightTemp = TITLEBAR_SIZE + (frame ? FRAME_SIZE * 2 : 0);
		}

		if ((x < this.x) || (y < this.y) || (x > this.x + widthTemp)
				|| (y > this.y + heightTemp)) {
			return false;
		}
		return true;
	}

	/** return true if the point is in the title */
	private boolean hitTitle(int x, int y) {
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

	/** return a object for dragging which is at the position p or null */
	protected WtDraggable getDragged(Point p) {
		return getDragged(p.x, p.y);
	}

	/** return a object for dragging which is at the position (x,y) or null */
	protected WtDraggable getDragged(int x, int y) {
		if (isClosed()) {
			return null;
		}

		// if the user drags our titlebar we return ourself
		if (hitTitle(x, y)) {
			return this;
		}

		// translate point to client coordinates
		x -= getClientX();
		y -= getClientY();

		// check all children
		for (WtPanel panel : children) {
			// only if the point is inside the child
			if (panel.isHit(x, y)) {
				WtDraggable draggedObject = panel.getDragged(x - panel.getX(),
						y - panel.getY());

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
	 * checks if there is a droptarget direct under the position (x,y)
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
	protected boolean checkDropped(int x, int y, WtDraggable droppedObject) {
		// translate point to client coordinates
		x -= getClientX();
		y -= getClientY();

		// now ask each child
		for (WtPanel panel : children) {
			// only if the point is inside the child
			if (panel.isHit(x, y)) {
				// the child checks itself
				if (panel.checkDropped(x - panel.getX(), y - panel.getY(),
						droppedObject)) {
					return true;
				}
			}
		}

		// are we ourself a drop target
		if (this instanceof WtDropTarget) {
			WtDropTarget target = (WtDropTarget) this;
			return target.onDrop(x, y, droppedObject);
		}

		// no drop target found
		return false;
	}

	/** returns the rectangle for the minimize button */
	private Rectangle getMiminizeButton() {
		return new Rectangle(width - (TITLEBAR_SIZE * 2) - FRAME_SIZE,
				FRAME_SIZE + 1, TITLEBAR_SIZE - 2, TITLEBAR_SIZE - 2);
	}

	/** returns true when the point (x,y) is inside the minimize button */
	private boolean hitMinimizeButton(int x, int y) {
		return getMiminizeButton().contains(x, y);
	}

	/** returns the rectangle for the close button */
	private Rectangle getCloseButton() {
		return new Rectangle(width - TITLEBAR_SIZE - FRAME_SIZE,
				FRAME_SIZE + 1, TITLEBAR_SIZE - 2, TITLEBAR_SIZE - 2);
	}

	/** returns true when the point (x,y) is inside the close button */
	private boolean hitCloseButton(int x, int y) {
		return getCloseButton().contains(x, y);
	}

	/**
	 * callback for a mouse click. returns true when the click has been
	 * processed
	 */
	public synchronized boolean onMouseClick(Point p) {
		if (isClosed()) {
			return false;
		}

		// check if the minimize button has been clicked
		if (titleBar && minimizeable && hitMinimizeButton(p.x, p.y)) {
			// change minimized state
			setMinimized(!isMinimized());
			return true;
		}

		// check if the close button has been clicked
		if (titleBar && closeable && hitCloseButton(p.x, p.y)) {
			// close the window
			close();
			return true;
		}

		if (Debug.CYCLE_PANEL_TEXTURES && hitTitle(p.x, p.y)) {
			texture = (texture + 1) % textureSprites.size();
			cachedImage = null;
		}

		// translate point to client coordinates
		Point p2 = p.getLocation();
		p2.translate(-getClientX(), -getClientY());

		// be sure to inform all children of the mouse click
		for (WtPanel panel : children) {
			// only if the point is inside the child
			if (panel.isHit(p2.x, p2.y)) {
				focus(panel);
				Point point = p2.getLocation();
				point.translate(-panel.getX(), -panel.getY());
				// click the child
				return panel.onMouseClick(point);
			}
		}
		// click not processed
		return false;
	}

	/** callback for a doubleclick */
	public synchronized boolean onMouseDoubleClick(Point p) {
		if (isClosed()) {
			return false;
		}

		// translate point to client coordinates
		Point p2 = p.getLocation();
		p2.translate(-getClientX(), -getClientY());

		// be sure to inform all children of the mouse click
		for (WtPanel panel : children) {
			// only if the point is inside the child
			if (panel.isHit(p2.x, p2.y)) {
				Point point = p2.getLocation();
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

	/** the right mouse button has been clicked (callback) */
	public synchronized boolean onMouseRightClick(Point p) {
		if (isClosed()) {
			return false;
		}
		if (isMinimized()) {
			return false;
		}
		// translate point to client coordinates
		Point p2 = p.getLocation();
		p2.translate(-getClientX(), -getClientY());

		// be sure to inform all children of the mouse click
		for (WtPanel panel : children) {
			// only if the point is inside the child
			if (panel.isHit(p2.x, p2.y)) {
				Point point = p2.getLocation();
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

	/** ignored */
	public boolean dragStarted() {
		if (isClosed()) {
			return false;
		}

		dragPosition = new Point(x, y);
		return true;
	}

	/** ignored */
	public boolean dragFinished(Point p) {
		return false;
	}

	/** move the frame to the requested position */
	public boolean dragMoved(Point p) {
		return moveTo(dragPosition.x + p.x, dragPosition.y + p.y);
	}

	/** moves the child panel on top of all others */
	private void focus(WtPanel child) {
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
	 */
	public void setContextMenu(JPopupMenu contextMenu) {
		if (parent != null) {
			// moves the contex-menu to match the position of this panel
			parent.setContextMenu(contextMenu);
		}
	}

	public void setTransparency(float t) {
		this.transparency = t;
	}

	/** toString */
	@Override
	public String toString() {
		return super.toString() + ": " + name + " at " + x + "x" + y + " size:"
				+ width + "x" + height;
	}
}
