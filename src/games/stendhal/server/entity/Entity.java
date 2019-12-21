/***************************************************************************
 *                    (C) Copyright 2003-2018 - Marauroa                   *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity;

import static games.stendhal.server.core.engine.Translate.getText;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;

import games.stendhal.common.ItemTools;
import games.stendhal.common.constants.Events;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.slot.EntitySlot;
import games.stendhal.server.entity.slot.SlotNameInList;
import games.stendhal.server.entity.slot.Slots;
import marauroa.common.game.Definition;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

public abstract class Entity extends RPObject implements Killer {
	/**
	 * The logger.
	 */
	private static final Logger logger = Logger.getLogger(Entity.class);


	protected Rectangle2D.Double area = new Rectangle2D.Double();

	private int x;

	private int y;

    // Initial coordinates
    private Point origin;

	/**
	 * Amount of resistance this has with other entities (0-100).
	 */
	private int resistance;

	private StendhalRPZone zone;
	private StendhalRPZone lastZone;

	public Entity(final RPObject object) {
		super(object);

		if (!has("x")) {
			put("x", 0);
		}

		if (!has("y")) {
			put("y", 0);
		}

		if (!has("width")) {
			put("width", 1);
		}

		if (!has("height")) {
			put("height", 1);
		}

		if (!has("resistance")) {
			put("resistance", 100);
		}

		if (!has("visibility")) {
			put("visibility", 100);
		}

		update();
	}

	public Entity() {
		put("x", 0);
		put("y", 0);

		x = 0;
		y = 0;

		setSize(1, 1);
		area.setRect(x, y, 1, 1);

		setResistance(100);
		setVisibility(100);
	}

	public static void generateRPClass() {
		final RPClass entity = new RPClass("entity");

		// Some things may have a textual description
		entity.addAttribute("description", Type.LONG_STRING, Definition.HIDDEN);

		// TODO: Try to remove this attribute later (at DB reset?)
		entity.addAttribute("type", Type.STRING);

		/**
		 * Resistance to other entities (0-100). 0=Phantom, 100=Obstacle.
		 */
		entity.addAttribute("resistance", Type.BYTE, Definition.VOLATILE);

		entity.addAttribute("x", Type.SHORT);
		entity.addAttribute("y", Type.SHORT);

		/*
		 * The size of the entity (in world units).
		 */
		entity.addAttribute("width", Type.SHORT, Definition.VOLATILE);
		entity.addAttribute("height", Type.SHORT, Definition.VOLATILE);

		/*
		 * Obsolete and ignored by the client. Do not use.
		 */
		entity.addAttribute("server-only", Type.FLAG, Definition.VOLATILE);

		/*
		 * The current overlayed client effect.
		 */
		entity.addAttribute("effect", Type.STRING, Definition.VOLATILE);

		/*
		 * The visibility of the entity drawn on client (0-100). 0=Invisible,
		 * 100=Solid. Useful when mixed with effect.
		 */
		entity.addAttribute("visibility", Type.INT, Definition.VOLATILE);

		// cursor
		entity.addAttribute("cursor", Type.STRING);

		// menu (Make a wish,use)
		entity.addAttribute("menu", Type.STRING, Definition.VOLATILE);

		// sound events
		entity.addRPEvent(Events.SOUND, Definition.VOLATILE);
		// graphical effects
		entity.addRPEvent(Events.IMAGE, Definition.VOLATILE);
		entity.addRPEvent(Events.PUBLIC_TEXT, Definition.VOLATILE);
	}


	public void update() {
		final int oldX = x;
		final int oldY = y;
		boolean moved = false;

		if (has("x")) {
			x = getInt("x");
			area.x = x;

			if (x != oldX) {
				moved = true;
			}
		}

		if (has("y")) {
			y = getInt("y");
			area.y = y;

			if (y != oldY) {
				moved = true;
			}
		}

		if (moved && (zone != null)) {
			onMoved(oldX, oldY, x, y);
		}

		if (has("height")) {
			area.height = getInt("height");
		}

		if (has("width")) {
			area.width = getInt("width");
		}

		if (has("resistance")) {
			resistance = getInt("resistance");
		}

	}

	public boolean hasDescription() {
		if (has("description")) {
			return ((getDescription() != null) && (getDescription().length() > 0));
		}
		return (false);
	}

	public void setDescription(final String text) {
		if (text == null) {
			put("description", "");
		} else {
			put("description", text);
		}

	}

	public String getDescription() {
		String description = "";
		if (has("description")) {
			description = get("description");
		}
		return getText(description);
	}

	/**
	 * Get the nicely formatted entity title/name.
	 *
	 * @return The title, or <code>null</code> if unknown.
	 */
	public String getTitle() {
		if (has("subclass")) {
			return ItemTools.itemNameToDisplayName(get("subclass"));
		} else if (has("class")) {
			return ItemTools.itemNameToDisplayName(get("class"));
		} else if (has("type")) {
			return ItemTools.itemNameToDisplayName(get("type"));
		} else {
			return null;
		}
	}

	/**
	 * Get the entity X coordinate.
	 *
	 * @return The X coordinate.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Get the entity Y coordinate.
	 *
	 * @return The Y coordinate.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Get the zone this entity is in.
	 *
	 * @return A zone, or <code>null</code> if not in one.
	 */
	public StendhalRPZone getZone() {
		// Use onAdded()/onRemoved() to grab a reference to the zone and save
		// as a attribute.
		// During zone transfer zone is set to null for a short period of time
		// which causes lots of problems, so we use the old zone until the new
		// one is set.
		return lastZone;
	}

	/**
	 * Is this entity not moving?
	 *
	 * @return true, if it stopped, false if it is moving
	 */
	public boolean stopped() {
		return true;
	}

	/**
	 * Get the resistance this has on other entities (0-100).
	 *
	 * @return The amount of resistance, or 0 if in ghostmode.
	 */
	public int getResistance() {
		return resistance;
	}

	/**
	 * Get the resistance between this and another entity (0-100).
	 * @param entity other entity to be evaluated
	 *
	 * @return The amount of combined resistance.
	 */
	public int getResistance(final Entity entity) {
		return ((getResistance() * entity.getResistance()) / 100);
	}


	/**
	 * Determine if this is an obstacle for another entity.
	 *
	 * @param entity
	 *            The entity to check against.
	 *
	 * @return <code>true</code> if very high resistance.
	 */
	public boolean isObstacle(final Entity entity) {
		// > 95% combined resistance = obstacle
		return (getResistance(entity) > 95);
	}

	/**
	 * This returns square of the distance between this entity and the given
	 * one. We're calculating the square because the square root operation would
	 * be expensive. As long as we only need to compare distances, it doesn't
	 * matter if we compare the distances or the squares of the distances (the
	 * square operation is strictly monotonous for positive numbers).
	 *
	 * @param other
	 *            the entity to which the distance should be calculated
	 * @return double representing the squared distance
	 */
	public final double squaredDistance(final Entity other) {
		final Rectangle2D otherArea = other.getArea();
		final double otherMiddleX = otherArea.getCenterX();
		final double otherMiddleY = otherArea.getCenterY();

		final Rectangle2D thisArea = getArea();
		final double thisMiddleX = thisArea.getCenterX();
		final double thisMiddleY = thisArea.getCenterY();

		double xDistance = Math.abs(otherMiddleX - thisMiddleX) - (area.getWidth() + other.area.getWidth()) / 2;
		double yDistance = Math.abs(otherMiddleY - thisMiddleY) - (area.getHeight() + other.area.getHeight()) / 2;

		if (xDistance < 0) {
			xDistance = 0;
		}
		if (yDistance < 0) {
			yDistance = 0;
		}
		return xDistance * xDistance + yDistance * yDistance;
	}

	/**
	 * This returns square of the distance from this entity to a specific point.
	 * We're calculating the square because the square root operation would be
	 * expensive. As long as we only need to compare distances, it doesn't
	 * matter if we compare the distances or the squares of the distances (the
	 * square operation is strictly monotonous for positive numbers).
	 *
	 * @param x
	 *            The horizontal coordinate of the point
	 * @param y
	 *            The vertical coordinate of the point
	 * @return double representing the squared distance
	 */
	public final double squaredDistance(final int x, final int y) {


		final double otherMiddleX = x + 0.5;
		final double otherMiddleY = y + 0.5;


		final Rectangle2D thisArea = getArea();

		final double thisMiddleX = thisArea.getCenterX();
		final double thisMiddleY = thisArea.getCenterY();


		double xDistance = Math.abs(otherMiddleX - thisMiddleX) - (area.getWidth() + 1) / 2;
		double yDistance = Math.abs(otherMiddleY - thisMiddleY) - (area.getHeight() + 1) / 2;

		if (xDistance < 0) {
			xDistance = 0;
		}
		if (yDistance < 0) {
			yDistance = 0;
		}
		return xDistance * xDistance + yDistance * yDistance;
	}

	/**
	 * Checks whether the given entity is directly next to this entity. This
	 * method may be optimized over using nextTo(entity, 0.25).
	 *
	 * @param entity
	 *            The entity
	 *
	 * @return <code>true</code> if the entity is next to this.
	 */
	public boolean nextTo(final Entity entity) {
		// For now call old code (just a convenience function)
		return nextTo(entity, 0.25);
	}

	/**
	 * Checks whether the given entity is near this entity.
	 *
	 * @param entity
	 *            the entity
	 * @param step
	 *            The maximum distance
	 * @return true iff the entity is at most <i>step</i> steps away
	 */
	public boolean nextTo(final Entity entity, final double step) {
		// To check the overlapping between 'this' and the other 'entity'
		// we create two temporary rectangle objects and initialise them
		// with the position of the two entities.
		// The size is calculated from the original objects with the additional
		// 'step' distance on both sides of the two rectangles.
		// As the absolute position is not important, 'step' need not be
		// subtracted from the values of getX() and getY().
		final Rectangle2D thisArea = new Rectangle2D.Double(x - step, y - step, area.getWidth()
				+ 2 * step, area.getHeight() + 2 * step);

		return thisArea.intersects(entity.getArea());
	}

	/**
	 * Get horizontal & vertical nodes immediately adjacent to entity's
	 * current X,Y coordinates.
	 *
	 * NOTE: This does not compensate for the entity's width & height,
	 *       it is only meant to get the positions that the entity
	 *       could potentially move to.
	 */
	public List<Node> getAdjacentNodes() {
		List<Node> nodes = new ArrayList<>();
		final int x = getX();
		final int y = getY();

		for (final int lat: Arrays.asList(x-1, x+1)) {
			nodes.add(new Node(lat, y));
		}
		for (final int lon: Arrays.asList(y-1, y+1)) {
			nodes.add(new Node(x, lon));
		}

		return nodes;
	}

	/**
	 * Get the area this object currently occupies.
	 *
	 * @return A rectangular area.
	 */
	public Rectangle2D getArea() {
		return area;
	}

	/**
	 * Returns the area used by this entity.
	 *
	 * @param ex
	 *            x
	 * @param ey
	 *            y
	 * @return rectangle for the used area
	 */
	public Rectangle2D getArea(final double ex, final double ey) {
		final Rectangle2D tempRect = new Rectangle.Double();
		tempRect.setRect(ex, ey, area.getWidth(), area.getHeight());
		return tempRect;
	}


	/**
	 * Called when this object is added to a zone.
	 *
	 * @param zone
	 *            The zone this was added to.
	 */
	public void onAdded(final StendhalRPZone zone) {
		if (this.zone != null) {
			logger.error("Entity added while in another " + zone + ": " + this, new Throwable());
		}

		this.zone = zone;
		this.lastZone = zone;
	}

	/**
	 * Notification of intra-zone position change.
	 *
	 * @param oldX
	 *            The old X coordinate.
	 * @param oldY
	 *            The old Y coordinate.
	 * @param newX
	 *            The new X coordinate.
	 * @param newY
	 *            The new Y coordinate.
	 */
	protected void onMoved(final int oldX, final int oldY, final int newX, final int newY) {
		// sub classes can implement this method
	}

	/**
	 * Called when this object is being removed from a zone.
	 *
	 * @param zone
	 *            The zone this will be removed from.
	 */
	public void onRemoved(final StendhalRPZone zone) {
		if (this.zone != zone) {
			logger.error("Entity removed from wrong zone " + zone + " but it thinks it is in " + this.zone + ": ", new Throwable());
		}

		this.zone = null;
	}

	/**
	 * Notifies the StendhalRPWorld that this entity's attributes have changed.
	 *
	 */
	public void notifyWorldAboutChanges() {
		/*
		 * Only possible if in a zone. This does *NOT* use getZone(), because
		 * that can return a zone that currently does not really contain this
		 * entity, and MarauroaRPZone.modify() assumes the object modified is
		 * really contained there. Modifying an entity that's not really in the
		 * zone results in a perception for an entity that the client does not
		 * recognize.
		 */
		if (zone != null) {
			zone.modify(this);
		}
	}

	/**
	 * Describes the entity (if a players looks at it).
	 *
	 * @return description from the players point of view
	 */
	public String describe() {
		if (hasDescription()) {
			return getDescription();
		}

		return "You see " + getDescriptionName(false) + ".";
	}

	/**
	 * Returns the name or something that can be used to identify the entity for
	 * the player.
	 *
	 * @param definite
	 *            true for "the" and false for "a/an" in case the entity has no
	 *            name
	 * @return name
	 *
	 */
	public String getDescriptionName(final boolean definite) {
		if (has("subclass")) {
			return Grammar.article_noun(ItemTools.itemNameToDisplayName(get("subclass")), definite);
		} else if (has("class")) {
			return Grammar.article_noun(ItemTools.itemNameToDisplayName(get("class")), definite);
		} else {
			String ret = "something indescribably strange";
			if (has("type")) {
				ret += " of type " + ItemTools.itemNameToDisplayName(get("type"));
			}
			if (has("id")) {
				ret += " with id " + get("id");
			}
			if (has("zone")) {
				ret += " in zone " + get("zone");
			}
			logger.error("Missing description for " + this);
			return ret;
		}
	}

	/**
	 * Get the entity height.
	 *
	 * @return The height (in world units).
	 */
	public double getHeight() {
		return area.getHeight();
	}

	/**
	 * Get the entity width.
	 *
	 * @return The width (in world units).
	 */
	public double getWidth() {
		return area.getWidth();
	}

	/**
	 * Set the entity class.
	 *
	 * @param clazz
	 *            The class name.
	 */
	public final void setEntityClass(final String clazz) {
		put("class", clazz);
	}

	/**
	 * Set the entity sub-class.
	 *
	 * @param subclazz
	 *            The sub-class name.
	 */
	public final void setEntitySubclass(final String subclazz) {
		put("subclass", subclazz);
	}

	/**
	 * Sets the entity position.
	 *
	 *
	 * <p>
	 * This calls <code>onMoved()</code>. <strong>Note: When placing during a
	 * zone change, this call should be done after being removed from the old
	 * zone, but before adding to the zone to prevent an erroneous position jump
	 * in the zone.</strong>
	 *
	 * @param x
	 *            The x position (in world units).
	 * @param y
	 *            The y position (in world units).
	 */
	public final void setPosition(final int x, final int y) {
		final int oldX = this.x;
		final int oldY = this.y;
		boolean moved = false;

        // Set the original position of the entity
        if (origin == null) {
            origin = new Point(x, y);
        }

		if (x != oldX) {
			this.x = x;
			area.x = x;
			put("x", x);
			moved = true;
		}

		if (y != oldY) {
			this.y = y;
			area.y = y;
			put("y", y);
			moved = true;
		}

		if (moved && (zone != null)) {
			onMoved(oldX, oldY, x, y);
		}
	}

	/**
	 *
	 * @return The initial position of the entity
	 */
	public final Point getOrigin() {
	    return origin;
	}

	/**
	 * Set resistance this has with other entities.
	 *
	 * @param resistance
	 *            The amount of resistance (0-100).
	 */
	public final void setResistance(final int resistance) {
		this.resistance = resistance;
		put("resistance", resistance);
	}

	/**
	 * Set the entity size.
	 *
	 * @param width
	 *            The width (in world units).
	 * @param height
	 *            The height (in world units).
	 */
	public void setSize(final int width, final int height) {
		this.area.width = width;
		put("width", width);

		this.area.height = height;
		put("height", height);
	}

	/**
	 * gets the name of the mouse cursor or <code>null</code>.
	 *
	 * @return name of the mouse cursor or <code>null</code>.
	 */
	public String getCursor() {
		if (has("cursor")) {
			return get("cursor");
		}
		return null;
	}

	/**
	 * sets the name of the mouse cursor
	 *
	 * @param cursor name of cursor
	 */
	public void setCursor(String cursor) {
		if (cursor == null) {
			remove("cursor");
		} else {
			put("cursor", cursor);
		}
	}

	/**
	 * Set the entity's visibility.
	 *
	 * @param visibility
	 *            The visibility (0-100).
	 */
	public final void setVisibility(final int visibility) {
		put("visibility", visibility);
	}

	/**
	 * Check if the other Entity is near enough to be in sight on the client screen.
	 *
	 * @param other
	 * @return true if near enough
	 */
	public boolean isInSight(final Entity other) {
		if (other != null) {
			if (other.getZone() == getZone()) {
				// check distance: 640x480 client screen size for 32x32 pixel tiles
				// -> makes 20x15 tiles screen size
				if ((Math.abs(other.getX() - x) <= 20)
						&& (Math.abs(other.getY() - y) <= 15)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * gets the named entity slot
	 *
	 * @param name name of entity slot
	 * @return EntitySlot or <code>null</code>
	 */
	public EntitySlot getEntitySlot(String name) {
		RPSlot slot = super.getSlot(name);
		if (!(slot instanceof EntitySlot)) {
			return null;
		}
		return (EntitySlot) slot;
	}

	/**
	 * an iterator over slots
	 *
	 * @param slotTypes slot types to include in the iteration
	 * @return Iterator
	 */
	public Iterator<RPSlot> slotIterator(Slots slotTypes) {
		Predicate<RPSlot> p = new SlotNameInList(slotTypes.getNames());
		return Iterators.filter(slotsIterator(), p);
	}

	/**
	 * an Iterable over slots
	 *
	 * @param slotTypes slot types to include in the iteration
	 * @return Iterable
	 */
	public Iterable<RPSlot> slots(final Slots slotTypes) {
		return new Iterable<RPSlot>() {
			@Override
			public Iterator<RPSlot> iterator() {
				return slotIterator(slotTypes);
			}
		};
	}

	/**
	 * The menu to display on the client in the format:
	 * <pre>
	 *    Display Name 1|action1,
	 *    Display Name 2|action2
	 * </pre>
	 *
	 * @param menu menu string
	 */
	public void setMenu(String menu) {
		put("menu", menu);
	}

	/**
	 * gets the name of this entity
	 *
	 * @return name
	 */
	@Override
	public String getName() {
		return getRPClass().getName();
	}
}
