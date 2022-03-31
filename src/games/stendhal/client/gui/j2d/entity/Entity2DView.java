/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2022 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui.j2d.entity;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

//
//

import games.stendhal.client.IGameScreen;
import games.stendhal.client.stendhal;
import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.EntityChangeListener;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.ImageEventProperty;
import games.stendhal.client.entity.Inspector;
import games.stendhal.client.entity.RPEntity;
import games.stendhal.client.entity.User;
import games.stendhal.client.gui.j2DClient;
import games.stendhal.client.gui.j2d.ImageEffect;
import games.stendhal.client.gui.j2d.entity.helpers.HorizontalAlignment;
import games.stendhal.client.gui.j2d.entity.helpers.VerticalAlignment;
import games.stendhal.client.gui.styled.cursor.StendhalCursor;
import games.stendhal.client.sprite.AnimatedSprite;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;
import marauroa.common.game.RPObject;

/**
 * The 2D view of an entity.
 *
 * @param <T> type of entity
 */
public abstract class Entity2DView<T extends IEntity> implements EntityView<T> {
	/**
	 * The entity this view is for.
	 */
	protected T entity;

	/**
	 * The entity drawing composite.
	 */
	private Composite entityComposite;

	/**
	 * Model values affecting animation.
	 */
	protected volatile boolean animatedChanged;

	/**
	 * The position value changed.
	 */
	private volatile boolean positionChanged;

	/**
	 * Model values affecting visual representation changed.
	 */
	protected volatile boolean representationChanged;

	/**
	 * The visibility value changed.
	 */
	protected volatile boolean visibilityChanged;

	/**
	 * The screen X coordinate.
	 */
	private int x;

	/**
	 * The X alignment offset.
	 */
	private int xoffset;

	/**
	 * The screen Y coordinate.
	 */
	private int y;

	/**
	 * The Y alignment offset.
	 */
	private int yoffset;

	/**
	 * The entity image (or current one at least).
	 */
	private Sprite sprite;

	/**
	 * Whether this view is contained.
	 */
	private boolean contained;
	private HorizontalAlignment xAlign = HorizontalAlignment.CENTER;
	private VerticalAlignment yAlign = VerticalAlignment.MIDDLE;

	/**
	 * Some model value changed.
	 */
	private volatile boolean changed;
	/** Additional sprites attached to the view. */
	private Collection<AttachedSprite> attachedSprites;

	/**
	 * Flag for detecting that the view has been released <code>true</code> if
	 * the view has been released, <code>false</code> otherwise.
	 */
	private volatile boolean released = false;
	/**
	 * Listener for entity changes. Forwards the changes to the the EntityView.
	 * The purpose is that extending classes get a chance to process the changes
	 * before the {@link #changed} flag is toggled.
	 */
	private final UpdateListener updateListener = new UpdateListener();
	/**
	 * The area rectangle. Reused because it's otherwise one of the most
	 * allocated objects.
	 */
	private final Rectangle area = new Rectangle();

	/** determines if sprite animation cycles while idle */
	private boolean activeIdle = false;

	@Override
	public void initialize(final T entity) {
		if (entity == null) {
			throw new IllegalArgumentException("entity must not be null");
		}
		if (this.entity != null) {
			this.entity.removeChangeListener(updateListener);
		}
		this.entity = entity;

		x = 0;
		y = 0;
		xoffset = 0;
		yoffset = 0;

		entityComposite = AlphaComposite.SrcOver;
		contained = false;
		animatedChanged = false;
		changed = true;
		positionChanged = true;
		visibilityChanged = true;
		representationChanged = true;

		entity.addChangeListener(updateListener);

		if (entity instanceof RPEntity) {
			final RPObject obj = ((RPEntity) entity).getRPObject();
			if (obj.has("active_idle")) {
				activeIdle = true;
			}
		}
	}

	//
	// Entity2DView
	//

	/**
	 * Handle entity changes.
	 */
	@Override
	public void applyChanges() {
		if (changed) {
			changed = false;
			update();
		}
	}

	/**
	 * Build a list of entity specific actions. <strong>NOTE: The first entry
	 * should be the default.</strong>
	 *
	 * @param list
	 *            The list to populate.
	 */
	protected void buildActions(final List<String> list) {
		if (entity.getRPObject().has("menu")) {
			list.add(entity.getRPObject().get("menu"));
		}
		list.add(ActionType.LOOK.getRepresentation());
	}

	/**
	 * Rebuild the representation using the base entity.
	 *
	 * @param entity the eEntity to build the representation for
	 */
	protected void buildRepresentation(T entity) {
		setSprite(SpriteStore.get().getSprite(translate(entity.getType())));
		calculateOffset(entity, getWidth(), getHeight());
	}

	/**
	 * Set the alignment of the sprite.
	 *
	 * @param xAlign horizontal position
	 * @param yAlign vertical position
	 */
	void setSpriteAlignment(HorizontalAlignment xAlign, VerticalAlignment yAlign) {
		this.xAlign = xAlign;
		this.yAlign = yAlign;
	}

	/**
	 * Calculate sprite image offset for the entity.
	 *
	 * @param entity entity
	 * @param swidth
	 *            The sprite width (in pixels).
	 * @param sheight
	 *            The sprite height (in pixels).
	 */
	protected void calculateOffset(T entity, final int swidth, final int sheight) {
		final Rectangle2D area = entity.getArea();

		calculateOffset(swidth, sheight, (int) (IGameScreen.SIZE_UNIT_PIXELS * area.getWidth()),
				(int) (IGameScreen.SIZE_UNIT_PIXELS * area.getHeight()));
	}

	/**
	 * Calculate sprite image offset The result depends on the alignment
	 * specified with
	 * {@link #setSpriteAlignment(HorizontalAlignment, VerticalAlignment)}. The
	 * default if centered in both directions.
	 *
	 * @param swidth
	 *            The sprite width (in pixels).
	 * @param sheight
	 *            The sprite height (in pixels).
	 * @param ewidth
	 *            The entity width (in pixels).
	 * @param eheight
	 *            The entity height (in pixels).
	 */
	private void calculateOffset(final int swidth, final int sheight,
			final int ewidth, final int eheight) {
		switch (xAlign) {
		case LEFT:
			xoffset = 0;
			break;
		case RIGHT:
			xoffset = ewidth - swidth;
			break;
		default:
			xoffset = (ewidth - swidth) / 2;
		}
		switch (yAlign) {
		case TOP:
			yoffset = 0;
			break;
		case BOTTOM:
			yoffset = eheight - sheight;
			break;
		default:
			yoffset = (eheight - sheight) / 2;
		}
	}

	/**
	 * Mark this as changed. This will force the <code>update()</code> method to
	 * be called.
	 */
	void markChanged() {
		changed = true;
	}

	/**
	 * Attach a sprite to the view. These are drawn on top of the main view
	 * sprite.
	 *
	 * @param sprite
	 * @param xAlign alignment in horizontal direction
	 * @param yAlign alignment in vertical direction
	 * @param xOffset x coordinate offset that is used <b>in addition</b> to
	 * 	the alignment information
	 * @param yOffset y coordinate offset that is used <b>in addition</b> to
	 * 	the alignment information
	 */
	public void attachSprite(Sprite sprite, HorizontalAlignment xAlign,
			VerticalAlignment yAlign, int xOffset, int yOffset) {
		int x = xOffset;
		switch (xAlign) {
		case LEFT:
			break;
		case RIGHT:
			x += getWidth() - sprite.getWidth();
			break;
		case CENTER:
			x += (getWidth() - sprite.getWidth()) / 2;
			break;
		}

		int y = yOffset;
		switch (yAlign) {
		case TOP:
			break;
		case MIDDLE:
			y += (getHeight() - sprite.getHeight()) / 2;
			break;
		case BOTTOM:
			y += getHeight() - sprite.getHeight();
			break;
		}

		synchronized (this) {
			if (attachedSprites == null) {
				attachedSprites = new ConcurrentLinkedQueue<AttachedSprite>();
			}
		}
		attachedSprites.add(new AttachedSprite(sprite, x, y));
	}

	/**
	 * Detach a sprite that has been previously attached to the view.
	 *
	 * @param sprite sprite to be detached
	 */
	public void detachSprite(Sprite sprite) {
		Collection<AttachedSprite> sprites = attachedSprites;
		if (sprites != null) {
			Iterator<AttachedSprite> it = sprites.iterator();
			while (it.hasNext()) {
				AttachedSprite as = it.next();
				if (as.sprite == sprite) {
					it.remove();
					break;
				}
			}
		}
	}

	/**
	 * Draw the entity.
	 *
	 * @param g2d
	 *            The graphics to drawn on.
	 */
	@Override
	public void draw(final Graphics2D g2d) {
		applyChanges();

		final Rectangle r = getDrawingArea();

		if (isContained()) {
			r.setLocation(0, 0);
		} else {
			if (!isOnScreen(g2d, r)) {
				return;
			}
		}

		final Composite oldComposite = g2d.getComposite();

		try {
			g2d.setComposite(entityComposite);
			draw(g2d, r.x, r.y, r.width, r.height);
		} finally {
			g2d.setComposite(oldComposite);
		}
	}

	private boolean isOnScreen(Graphics2D g2d, Rectangle r) {
		Rectangle clip = g2d.getClipBounds();
		return ((clip == null) || r.intersects(g2d.getClipBounds()));
	}

	/**
	 * Draw the entity.
	 *
	 * @param g2d
	 *            The graphics context.
	 * @param x
	 *            The drawn X coordinate.
	 * @param y
	 *            The drawn Y coordinate.
	 * @param width
	 *            The drawn entity width.
	 * @param height
	 *            The drawn entity height.
	 */
	protected void draw(final Graphics2D g2d, final int x, final int y,
			final int width, final int height) {
		drawEntity(g2d, x, y, width, height);

		if (stendhal.SHOW_COLLISION_DETECTION) {
			g2d.setColor(Color.blue);
			g2d.drawRect(x, y, width, height);

			g2d.setColor(Color.green);
			g2d.draw(entity.getArea());
		}

		drawAttachedSprites(g2d, x, y);
	}

	/**
	 * Draw all attached sprites.
	 *
	 * @param g2d graphics
	 * @param x x position of the view
	 * @param y y position of the view
	 */
	private void drawAttachedSprites(Graphics2D g2d, int x, int y) {
		Collection<AttachedSprite> sprites = attachedSprites;
		if (sprites != null) {
			for (AttachedSprite sprite : sprites) {
				sprite.draw(g2d, x, y);
			}
		}
	}

	/**
	 * Draw the base entity part.
	 *
	 * @param g2d
	 *            The graphics context.
	 * @param x
	 *            The drawn X coordinate.
	 * @param y
	 *            The drawn Y coordinate.
	 * @param width
	 *            The drawn entity width.
	 * @param height
	 *            The drawn entity height.
	 */
	protected void drawEntity(final Graphics2D g2d, final int x, final int y,
			final int width, final int height) {
		getSprite().draw(g2d, x, y);
	}

	/**
	 * Draw the top layer parts of an entity. This will be on down after all
	 * other game layers are rendered.
	 *
	 * @param g2d
	 *            The graphics to drawn on.
	 */
	@Override
	public void drawTop(final Graphics2D g2d) {
		final Rectangle r = getArea();

		if (isContained()) {
			r.setLocation(0, 0);
		} else {
			if (!isOnScreen(g2d, r)) {
				return;
			}
		}

		final Composite oldComposite = g2d.getComposite();

		try {
			g2d.setComposite(entityComposite);
			drawTop(g2d, r.x, r.y, r.width, r.height);
		} finally {
			g2d.setComposite(oldComposite);
		}
	}

	/**
	 * Draw the entity.
	 *
	 * @param g2d
	 *            The graphics context.
	 * @param x
	 *            The drawn X coordinate.
	 * @param y
	 *            The drawn Y coordinate.
	 * @param width
	 *            The drawn entity width.
	 * @param height
	 *            The drawn entity height.
	 */
	protected void drawTop(final Graphics2D g2d, final int x, final int y,
			final int width, final int height) {
	}

	/**
	 * Get the screen area this is drawn in. NOTE: This only covers the area for
	 * the main sprite.
	 *
	 * @return The area this draws in.
	 */
	@Override
	public Rectangle getArea() {
		area.setBounds(getX() + getXOffset(), getY() + getYOffset(), getWidth(), getHeight());
		return area;
	}

	/**
	 * Get the drawn area used by the entity. Used for checking if the entity
	 * should be drawn. By default the same as getArea(), but extending classes
	 * can override it to return a different area if they need it.
	 *
	 * @return The area this draws in.
	 */
	protected Rectangle getDrawingArea() {
		return getArea();
	}

	/**
	 * Get the class resource sub-path. The is the base sprite image name,
	 * relative to <code>translate()</code>.
	 *
	 * @return The resource path.
	 */
	protected String getClassResourcePath() {
		String rpath = entity.getEntityClass();

		if (rpath != null) {
			final String subclass = entity.getEntitySubclass();

			if (subclass != null) {
				rpath += "/" + subclass;
			}
		}

		return rpath;
	}

	/**
	 * Get the drawing composite.
	 *
	 * @return The drawing composite.
	 */
	protected AlphaComposite getComposite() {
		final int visibility = getVisibility();
		if (visibility >= 100) {
			return AlphaComposite.SrcOver;
		} else if (visibility <= 0) {
			return AlphaComposite.Dst;
		} else {
			return AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
					visibility / 100.0f);
		}
	}

	/**
	 * Get the height.
	 *
	 * @return The height (in pixels).
	 */
	public int getHeight() {
		if (sprite != null) {
			return sprite.getHeight();
		}
		return IGameScreen.SIZE_UNIT_PIXELS;
	}

	/**
	 * Get the sprite image for this entity.
	 *
	 * @return The image representation.
	 */
	public Sprite getSprite() {
		return sprite;
	}

	/**
	 * Get the entity's visibility.
	 *
	 * @return The visibility value (0-100).
	 */
	protected int getVisibility() {
		return entity.getVisibility();
	}

	/**
	 * Get the width.
	 *
	 * @return The width (in pixels).
	 */
	public int getWidth() {
		if (sprite != null) {
			return sprite.getWidth();
		}
		return IGameScreen.SIZE_UNIT_PIXELS;
	}

	/**
	 * Get the entity's X coordinate.
	 *
	 * @return The X coordinate (in pixels).
	 */
	protected int getX() {
		return x;
	}

	/**
	 * Get the X offset alignment adjustment.
	 *
	 * @return The X offset (in pixels).
	 */
	protected int getXOffset() {
		return xoffset;
	}

	/**
	 * Get the entity's Y coordinate.
	 *
	 * @return The Y coordinate (in pixels).
	 */
	protected int getY() {
		return y;
	}

	/**
	 * Get the Y offset alignment adjustment.
	 *
	 * @return The Y offset (in pixels).
	 */
	protected int getYOffset() {
		return yoffset;
	}

	/**
	 * Determines on top of which other entities this entity should be drawn.
	 * Entities with a high Z index will be drawn on top of ones with a lower Z
	 * index.
	 *
	 * Also, players can only interact with the topmost entity.
	 *
	 * @return The drawing index.
	 */
	@Override
	public int getZIndex() {
		return 10000;
	}

	/**
	 * Determine if this view is currently animatable.
	 *
	 * @return <code>true</code> if animating enabled.
	 */
	protected boolean isAnimating() {
		// Allow sprites to animate by default
		return true;
	}

	/**
	 * Determine if this view is contained, and should render in a compressed
	 * (it's defined) area without clipping anything important.
	 *
	 * @return <code>true</code> if contained.
	 */
	public boolean isContained() {
		return contained;
	}

	/**
	 * Reorder the actions list (if needed). Please use as last resort.
	 *
	 * @param list
	 *            The list to reorder.
	 */
	protected void reorderActions(final List<String> list) {
	}

	/**
	 * Set the sprite's animation state (if applicable).
	 *
	 * @param sprite
	 *            The sprite.
	 */
	private void setAnimation(final Sprite sprite) {
		if (sprite instanceof AnimatedSprite) {
			final AnimatedSprite asprite = (AnimatedSprite) sprite;

			if (isAnimating() || activeIdle) {
				asprite.start();
			} else {
				asprite.stop();
				if (this instanceof ActiveEntity2DView) {
					// Use index 1 to show active entities as standing
					asprite.reset(1);
				} else {
					asprite.reset(0);
				}
			}
		}
	}

	/**
	 * Set whether this view is contained, and should render in a compressed
	 * (it's defined) area without clipping anything important.
	 *
	 * @param contained
	 *            <code>true</code> if contained.
	 */
	@Override
	public void setContained(final boolean contained) {
		this.contained = contained;
	}

	/**
	 * Set the content inspector for this entity (if needed).
	 *
	 * @param inspector
	 *            The inspector.
	 */
	@Override
	public void setInspector(final Inspector inspector) {
	}

	@Override
	public void setVisibleScreenArea(Rectangle area) {
	}

	/**
	 * Set the sprite.
	 *
	 * @param sprite
	 *            The sprite.
	 */
	protected void setSprite(final Sprite sprite) {
		setAnimation(sprite);
		animatedChanged = false;

		this.sprite = sprite;
	}

	/**
	 * Translate a resource name into it's sprite image path.
	 *
	 * @param name
	 *            The resource name.
	 *
	 * @return The full resource name.
	 */
	protected String translate(final String name) {
		return "data/sprites/" + name + ".png";
	}

	/**
	 * Handle updates.
	 */
	protected void update() {
		T entity = this.entity;
		if (entity == null) {
			return;
		}
		/*
		 * The flags are reseted *before* reading the relevant data to ensure
		 * that we get up to date data from the game loop thread. (So that if
		 * the data changes during we are reading it, the flags will force
		 * reread at the next draw).
		 */
		if (representationChanged) {
			representationChanged = false;
			buildRepresentation(entity);
		}

		if (positionChanged) {
			positionChanged = false;
			x = (int) (IGameScreen.SIZE_UNIT_PIXELS * entity.getX());
			y = (int) (IGameScreen.SIZE_UNIT_PIXELS * entity.getY());
		}

		if (visibilityChanged) {
			visibilityChanged = false;
			entityComposite = getComposite();
		}

		if (animatedChanged) {
			animatedChanged = false;
			setAnimation(getSprite());
		}
	}

	/**
	 * A property of the entity changed.
	 *
	 * @param property
	 *            The property identifier.
	 */
	void entityChanged(final Object property) {
		if (property == IEntity.PROP_ANIMATED) {
			animatedChanged = true;
		} else if (property == IEntity.PROP_POSITION) {
			positionChanged = true;
		} else if (property == IEntity.PROP_VISIBILITY) {
			visibilityChanged = true;
		} else if (property instanceof ImageEventProperty) {
			new ImageEffect(this, ((ImageEventProperty) property).getImageName());
		}
	}

	//
	// EntityView
	//

	/**
	 * Get the list of actions.
	 *
	 * @return The list of actions.
	 */
	@Override
	public final String[] getActions() {
		final List<String> list = new ArrayList<String>();

		buildActions(list);

		/*
		 * Special admin options
		 */
		if (User.isAdmin()) {
			list.add(ActionType.ADMIN_INSPECT.getRepresentation());
			list.add(ActionType.ADMIN_DESTROY.getRepresentation());
			if (!this.isContained()) {
				list.add(ActionType.ADMIN_ALTER.getRepresentation());
			}
		}

		reorderActions(list);

		return list.toArray(new String[list.size()]);
	}

	/**
	 * Get the view's entity.
	 *
	 * @return The view's entity.
	 */
	@Override
	public T getEntity() {
		return entity;
	}

	/**
	 * Determine if this entity can be moved (e.g. via dragging).
	 *
	 * @return <code>true</code> if the entity is movable.
	 */
	@Override
	public boolean isMovable() {
		return false;
	}

	/**
	 * Perform the default action.
	 */
	@Override
	public void onAction() {
		onAction(ActionType.LOOK);
	}


	/**
	 * Perform the default action unless it is not safe.
	 *
	 * @return <code>true</code> if the action was performed, <code>false</code> if nothing was done
	 */
	@Override
	public boolean onHarmlessAction() {
		onAction();
		return true;
	}

	/**
	 * Perform an action.
	 *
	 * @param at
	 *            The action.
	 */
	@Override
	public void onAction(final ActionType at) {
		IEntity entity = this.entity;
		// return prematurely if view has already been released
		if (isReleased()) {
			Logger.getLogger(Entity2DView.class).debug(
					"View " + this + " already released - action not processed: " + at);
			return;
		}

		final int id = entity.getID().getObjectID();

		switch (at) {
		case LOOK:
		case ADMIN_INSPECT:
		case ADMIN_DESTROY:
		case MARK_ALL:
		case USE:
			at.send(at.fillTargetInfo(entity));
			break;

		case ADMIN_ALTER:
			j2DClient.get().setChatLine("/alter #" + id + " ");
			break;

		default:
			Logger.getLogger(Entity2DView.class).error(
					"Unknown action not processed: " + at);
			break;
		}
	}

	/**
	 * is this entity interactive so that the player can click or move it?
	 *
	 * @return true if the player can interact with it, false otherwise.
	 */
	@Override
	public boolean isInteractive() {
		return true;
	}

	/**
	 * Release any view resources. This view should not be used after this is
	 * called.
	 */
	@Override
	public void release() {
		entity.removeChangeListener(updateListener);
		released = true;
	}

	/**
	 * Check if the view has been released. Usually a released view should not
	 * be used anymore, but in certain situations it may be preferable to send
	 * an action for a deleted entity to the server anyway. That can happen for
	 * example with items in bag, where a new view gets created after an item
	 * has changed, but the new view represents the same item stack as the old
	 * one.
	 *
	 * @return <code>true</code> if the view has been released,
	 * 	<code>false</code> otherwise
	 */
	protected boolean isReleased() {
		return released;
	}

	/**
	 * gets the mouse cursor image to use for this entity.
	 *
	 * @return StendhalCursor
	 */
	@Override
	public StendhalCursor getCursor() {
		String cursorName = entity.getCursor();
		return StendhalCursor.valueOf(cursorName, StendhalCursor.UNKNOWN);
	}

	/**
	 * Container for sprites attached to the view.
	 */
	private static class AttachedSprite {
		/** Attached sprite. */
		final Sprite sprite;
		/** x offset compared to the EntityView's location. */
		int xOffset;
		/** y offset compared to the EntityView's location. */
		int yOffset;

		/**
		 * Create a new AttachedSprite.
		 *
		 * @param sprite
		 * @param x x position relative to the EntityView
		 * @param y y position relative to the EntityView
		 */
		AttachedSprite(Sprite sprite, int x, int y) {
			this.sprite = sprite;
			this.xOffset = x;
			this.yOffset = y;
		}

		/**
		 * Draw the sprite at the EntityView's location.
		 *
		 * @param g
		 * @param x x coordinate of the view
		 * @param y y coordinate of the view
		 */
		void draw(Graphics2D g, int x, int y) {
			sprite.draw(g, x + xOffset, y + yOffset);
		}
	}

	/**
	 * Helper for monitoring entity changes.
	 */
	private class UpdateListener implements EntityChangeListener<T> {
		@Override
		public void entityChanged(T entity, Object property) {
			// In this order, to ensure the changed flag is toggled only after
			// all the changes have been made.
			Entity2DView.this.entityChanged(property);
			markChanged();
		}
	}
}
