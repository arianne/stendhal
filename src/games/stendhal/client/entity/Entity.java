/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.entity;

import games.stendhal.client.GameScreen;
import games.stendhal.client.Sprite;
import games.stendhal.client.StendhalUI;
import games.stendhal.client.sound.SoundSystem;
import games.stendhal.common.Direction;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
//import java.util.Random;

import marauroa.common.Log4J;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

public abstract class Entity implements Comparable<Entity> {

	String[] moveSounds=null;
	/** session wide instance identifier for this class
	 * TODO: get rid of this only used by Soundsystem
	 *  
	**/
public final byte[] ID_Token = new byte[0];

	/** The current x location of this entity */
	protected double x;

	/** The current y location of this entity */
	protected double y;

	/**
	 * The current change serial.
	 */
	protected int	changeSerial;


	/** The current speed of this entity horizontally (pixels/sec) */
	protected double dx;

	/** The current speed of this entity vertically (pixels/sec) */
	protected double dy;

	/** The arianne object associated with this game entity */
	protected RPObject rpObject;

	private String type;

	/**
	 * The entity name.
	 */
	protected String name;

	/**
	 * defines the distance in which the entity is heard by Player
	 */
	protected double audibleRange = Double.POSITIVE_INFINITY;

	/**
	 * The "view" portion of an entity.
	 */
	protected Entity2DView	view;

	/**
	 * Quick work-around to prevent fireMovementEvent() from calling
	 * in onChangedAdded() from other onAdded() hack.
	 * TODO: Need to fix it all to work right, but not now.
	 */
	protected boolean inAdd = false;


	Entity() {
		x = 0.0;
		y = 0.0;
		dx = 0.0;
		dy = 0.0;

		changeSerial = 0;
	}


	public void init(final RPObject object) {
		type = object.get("type");

		if (object.has("name")) {
			name = object.get("name");
		} else {
			name = type.replace("_", " ");
		}

		rpObject = object;
	
		view = createView();
		view.buildRepresentation(object);
	}


	/**
	 * Mark this entity changed in some way that might effect it's
	 * observed state .
	 */
	protected void changed() {
		changeSerial++;
	}


	/**
	 * Get an opaque value (using equality compare) to determine if
	 * the entity has changed. This will do until real notification
	 * can be implemented.
	 *
	 * @return	A value that should only be compared to other calls
	 *		to this function (for this entity instance).
	 */
	public int getChangeSerial() {
		return changeSerial;
	}


	/** Returns the represented arianne object id */
	public RPObject.ID getID() {
		return rpObject != null ? rpObject.getID() : null;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public RPObject getRPObject() {
		return rpObject;
	}

	public double distance(final User user) {
		return (user.getX() - x) * (user.getX() - x)
			+ (user.getY() - y) * (user.getY() - y);
	}
	

	/**
	 * This is used by old code.
	 *
	 */
	public Sprite getSprite() {
		return view.getSprite();
	}


	/**
	 * Returns the absolute world area (coordinates) to which audibility of
	 * entity sounds is confined. Returns <b>null</b> if confines do not exist
	 * (audible everywhere).
	 */
	public Rectangle2D getAudibleArea() {
		if (audibleRange == Double.POSITIVE_INFINITY) {
			return null;
		}

		double width = audibleRange * 2;
		return new Rectangle2D.Double(getX() - audibleRange, getY() - audibleRange, width, width);
	}

	/**
	 * Sets the audible range as radius distance from this entity's position,
	 * expressed in coordinate units. This reflects an abstract capacity of this
	 * unit to emit sounds and influences the result of
	 * <code>getAudibleArea()</code>.
	 * 
	 * @param range
	 *            double audibility area radius in coordinate units
	 */
	public void setAudibleRange(final double range) {
		audibleRange = range;
	}


	/**
	 * compares to floating point values
	 * 
	 * @param d1
	 *            first value
	 * @param d2
	 *            second value
	 * @param diff
	 *            acceptable diff
	 * @return true if they are within diff
	 */
	private static boolean compareDouble(final double d1, final double d2, final double diff) {
		return Math.abs(d1 - d2) < diff;
	}

	/**
	 * calculates the movement if the server an client are out of sync. for some
	 * miliseconds. (server turns are not exactly 300 ms) Most times this will
	 * slow down the client movement
	 * 
	 * @param clientPos
	 *            the postion the client has calculated
	 * @param serverPos
	 *            the postion the server has reported
	 * @param delta
	 *            the movement based on direction
	 * @return the new delta to correct the movement error
	 */
	public static double calcDeltaMovement(final double clientPos, final double serverPos, final double delta) {
		double moveErr = clientPos - serverPos;
		double moveCorrection = (delta - moveErr) / delta;
		return (delta + delta * moveCorrection) / 2;
	}

	// When rpentity moves, it will be called with the data.
	public void onMove(final int x,final  int y,final  Direction direction,final  double speed) {

		this.dx = direction.getdx() * speed;
		this.dy = direction.getdy() * speed;
		

		if ((Direction.LEFT.equals(direction)) || (Direction.RIGHT.equals(direction))) {
			this.y = y;
			if (compareDouble(this.x, x, 1.0)) {
				// make the movement look more nicely: + this.dx * 0.1
				this.dx = calcDeltaMovement(this.x + this.dx * 0.1, x, direction.getdx()) * speed;
			} else {
				this.x = x;
			}
			this.dy = 0;
		} else if ((Direction.UP.equals(direction)) || (Direction.DOWN.equals(direction))) {
			this.x = x;
			this.dx = 0;
			if (compareDouble(this.y, y, 1.0)) {
				// make the movement look more nicely: + this.dy * 0.1
				this.dy = calcDeltaMovement(this.y + this.dy * 0.1, y, direction.getdy()) * speed;
			} else {
				this.y = y;
			}
		} else {
			// placing entities
			this.x = x;
			this.y = y;
		}
	}

	// When rpentity stops
	public void onStop(final int x,final  int y) {
	
		
		this.dx = 0;
		this.dy = 0;

		// set postion to the one reported by server
		this.x = x;
		this.y = y;
	}

	// When rpentity reachs the [x,y,1,1] area.
	public void onEnter(final int x, final int y) {

	}

	// When rpentity leaves the [x,y,1,1] area.
	public void onLeave(final int x,final  int y) {
	}

	// Called when entity enters a new zone
	public void onEnterZone(final String zone) {
	}

	// Called when entity leaves a zone
	public void onLeaveZone(final String zone) {
	}

	public void onAdded(final RPObject base) {
		// BUG: Work around for Bugs at 0.45
		inAdd = true;
		onChangedAdded(new RPObject(), base);
		inAdd = false;

		fireMovementEvent(base, null);
		fireZoneChangeEvent(base, null);
	}

	public void onChangedAdded(final RPObject base, final RPObject diff) {
		
		if (!inAdd) {
			fireMovementEvent(base, diff);
		}
	}

	public void onChangedRemoved(final RPObject base, final RPObject diff) {
		
	}

	public void onRemoved() {
		SoundSystem.stopSoundCycle(ID_Token);

		fireMovementEvent(null, null);
		fireZoneChangeEvent(null, null);
	}

	// Called when entity collides with another entity
	public void onCollideWith(final Entity entity) {
	}

	// Called when entity collides with collision layer object.
	public void onCollide(final int x,final  int y) {
	}

	protected void fireZoneChangeEvent(final RPObject base, final RPObject diff) {
		final RPObject.ID id = getID();
		if ((diff == null) && (base == null)) {
			// Remove case
			onLeaveZone(id.getZoneID());
		} else if (diff == null) {
			// First time case.
			onEnterZone(id.getZoneID());
		}
	}

	protected final void fireMovementEvent(final RPObject base, final RPObject diff) {
		if ((diff == null) && (base == null)) {
			// Remove case
		} else if (diff == null) {
			// First time case.
			Direction direction = Direction.STOP;
			if (base.has("dir")) {
				direction = Direction.build(base.getInt("dir"));
			}

			double speed = 0;
			if (base.has("speed")) {
				speed = base.getDouble("speed");
			}

			onMove(base.getInt("x"), base.getInt("y"), direction, speed);
		} else {
			// Real movement case
			int oldx = base.getInt("x");
			int oldy = base.getInt("y");

			int newX=oldx;
			int newY=oldy;

			if (diff.has("x")) {
				newX = diff.getInt("x");
			}
			if (diff.has("y")) {
				newY = diff.getInt("y");
			}

			Direction direction = Direction.STOP;
			if (base.has("dir")) {
				direction = Direction.build(base.getInt("dir"));
			}
			if (diff.has("dir")) {
				direction = Direction.build(diff.getInt("dir"));
			}

			double speed = 0;
			if (base.has("speed")) {
				speed = base.getDouble("speed");
			}
			if (diff.has("speed")) {
				speed = diff.getDouble("speed");
			}

			onMove(newX, newY, direction, speed);

			if ((Direction.STOP.equals(direction)) || (speed == 0)) {
				onStop(newX, newY);
			}

			if ((oldx != newX) && (oldy != newY)) {
				onLeave(oldx, oldy);
				onEnter(newX, newY);
			}
		}
	}

	public void draw(final GameScreen screen) {
		view.draw(screen);
	}

	public void move(final long delta) {
		// update the location of the entity based on move speeds
		x += (delta * dx) / 300;
		y += (delta * dy) / 300;
	}

	public boolean stopped() {
		return (dx == 0) && (dy == 0);
	}

	/** returns the number of slots this entity has */
	public int getNumSlots() {
		return rpObject.slots().size();
	}

	/**
	 * returns the slot with the specified name or null if the entity does not
	 * have this slot
	 */
	public RPSlot getSlot(final String name) {
		if (rpObject.hasSlot(name)) {
			return rpObject.getSlot(name);
		}

		return null;
	}

	/** returns a list of slots */
	public List<RPSlot> getSlots() {
		return new ArrayList<RPSlot>(rpObject.slots());
	}

	
	

	public abstract Rectangle2D getArea();

	public Rectangle2D getDrawedArea() {
		return view.getDrawnArea();
	}

	public ActionType defaultAction() {
		return ActionType.LOOK;
	}

	public final String[] offeredActions() {
		List<String> list = new ArrayList<String>();
		buildOfferedActions(list);
		if (defaultAction() != null) {
			list.remove(defaultAction().getRepresentation());
			list.add(0, defaultAction().getRepresentation());
		}

		/*
		 * Special admin options
		 */
		if (User.isAdmin()) {
			list.add(ActionType.ADMIN_INSPECT.getRepresentation());
			list.add(ActionType.ADMIN_DESTROY.getRepresentation());
			list.add(ActionType.ADMIN_ALTER.getRepresentation());
		}

		return list.toArray(new String[list.size()]);
	}

	protected void buildOfferedActions(final List<String> list) {
		list.add(ActionType.LOOK.getRepresentation());

	}

	public void onAction(final ActionType at, final String... params) {
		int id;
		RPAction rpaction;
		switch (at) {
			case LOOK:
				rpaction = new RPAction();
				rpaction.put("type", at.toString());
				id = getID().getObjectID();

				if (params.length > 0) {
					rpaction.put("baseobject", params[0]);
					rpaction.put("baseslot", params[1]);
					rpaction.put("baseitem", id);
				} else {
					rpaction.put("target", id);
				}
				at.send(rpaction);
				break;
			case ADMIN_INSPECT:
				rpaction = new RPAction();
				rpaction.put("type", at.toString());
				id = getID().getObjectID();
				rpaction.put("targetid", id);
				at.send(rpaction);
				break;
			case ADMIN_DESTROY:
				rpaction = new RPAction();
				rpaction.put("type", at.toString());
				id = getID().getObjectID();
				rpaction.put("targetid", id);
				at.send(rpaction);
				break;
			case ADMIN_ALTER:
				id = getID().getObjectID();
				StendhalUI.get().setChatLine("/alter #" + id + " ");
				break;
			default:

				Log4J.getLogger(Entity.class).error(at.toString() + ": Action not processed");
				break;
		}

	}

	/**
	 * Checks if this entity should be drawn on top of the given entity, if the
	 * given entity should be drawn on top, or if it doesn't matter.
	 * 
	 * In the first case, this method returns a positive integer. In the second
	 * case, it returns a negative integer. In the third case, it returns 0.
	 * 
	 * Also, players can only interact with the topmost entity.
	 * 
	 * Note: this comparator imposes orderings that are inconsistent with
	 * equals().
	 * 
	 * @param other
	 *            another entity to compare this one to
	 * @return a negative integer, zero, or a positive integer as this object is
	 *         less than, equal to, or greater than the specified object.
	 */
	public int compareTo(final Entity other) {
		// commented out until someone fixes bug [ 1401435 ] Stendhal: Fix
		// positions system
		// if (this.getY() < other.getY()) {
		// // this entity is standing behind the other entity
		// return -1;
		// } else if (this.getY() > other.getY()) {
		// // this entity is standing in front of the other entity
		// return 1;
		// } else {
		// one of the two entities is standing on top of the other.
		// find out which one.
		return this.getZIndex() - other.getZIndex();
		// }
	}

	/**
	 * Determines on top of which other entities this entity should be drawn.
	 * Entities with a high Z index will be drawn on top of ones with a lower Z
	 * index.
	 * 
	 * Also, players can only interact with the topmost entity.
	 * 
	 * @return drawing index
	 */
	public int getZIndex() {
		return view.getZIndex();
	}


	/**
	 * Transition method. Create the screen view for this entity.
	 *
	 * @return	The on-screen view of this entity.
	 */
	protected abstract Entity2DView createView();
}
