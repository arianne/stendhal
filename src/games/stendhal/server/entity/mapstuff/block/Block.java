/***************************************************************************
 *                   (C) Copyright 2012-2016 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.mapstuff.block;

import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.common.Direction;
import games.stendhal.common.MathHelper;
import games.stendhal.common.Rand;
import games.stendhal.common.constants.SoundLayer;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.MovementListener;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.ZoneEnterExitListener;
import games.stendhal.server.entity.ActiveEntity;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.SoundEvent;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;

/**
 * A solid, movable block on a map. It can have different apearances,
 * for example a farm cart.
 *
 * @author madmetzger
 */
public class Block extends ActiveEntity implements ZoneEnterExitListener,
		MovementListener, TurnListener {

	private static final Logger logger = Logger.getLogger(Block.class);

	/** number of seconds until a block is reset to its original position */
	static final int RESET_TIMEOUT_IN_SECONDS = 5 * MathHelper.SECONDS_IN_ONE_MINUTE;

	/** number of seconds until another attempt to rest the block to its original position is attempted */
	static final int RESET_AGAIN_DELAY = 10;

	private static final String Z_ORDER = "z";

	private int startX;
	private int startY;
	private boolean multi;

	private final List<String> sounds;

	private boolean resetBlock = true;
	private boolean wasMoved = false;

	public static void generateRPClass() {
		RPClass clazz = new RPClass("block");
		clazz.isA("area");
		// z order to control client side drawing
		clazz.addAttribute(Z_ORDER, Type.INT);
		clazz.addAttribute("class", Type.STRING);
		clazz.addAttribute("shape", Type.STRING);
	}

	/**
	 * Create a new Block with default style at (startX, startY)
	 *
	 * @param startX
	 *            initial x-coordinate
	 * @param startY
	 *            initial y-coordinate
	 * @param multiPush
	 *            is pushing multiple times allowed
	 */
	public Block(boolean multiPush) {
		this(multiPush, "block", null, Arrays.asList("scrape-1", "scrape-2"));
	}

	/**
	 *
	 * @param multiPush
	 * @param style
	 */
	public Block(boolean multiPush, String style) {
		this(multiPush, style, null, Collections.<String> emptyList());
	}

	public Block(boolean multiPush, String style, String shape) {
		this(multiPush, style, shape, Collections.<String> emptyList());
	}

	/**
	 * Create a new block at startX, startY with a different style at client
	 * side
	 *
	 * @param startX
	 *            initial x-coordinate
	 * @param startY
	 *            initial y-coordinate
	 * @param multiPush
	 *            is pushing multiple times allowed
	 * @param style
	 *            what style should the client use?
	 * @param shape
	 * @param sounds
	 *            what sounds should be played on push?
	 */
	public Block(boolean multiPush, String style, String shape, List<String> sounds) {
		super();
		this.put(Z_ORDER, 8000);
		this.multi = Boolean.valueOf(multiPush);
		setRPClass("block");
		put("type", "block");
		put("class", "block");
		this.sounds = sounds;
		// Count as collision for the client and pathfinder
		setResistance(100);
		setDescription("You see a solid block of rock. Are you strong enough to push it away?");
		if (style != null) {
			put("name", style);
		} else {
			put("name", "block");
		}
		if (shape != null) {
			put("shape", shape);
		}
	}

	/**
	 * Resets the block position to its initial state
	 */
	public void reset() {
		wasMoved = false;
		List<BlockTarget> blockTargetsAt = this.getZone().getEntitiesAt(getX(), getY(), BlockTarget.class);
		for (BlockTarget blockTarget : blockTargetsAt) {
			blockTarget.untrigger();
		}
		this.setPosition(startX, startY);
		SingletonRepository.getTurnNotifier().dontNotify(this);
		this.notifyWorldAboutChanges();
	}

	/**
	 * Push this Block into a given direction
	 *
	 * @param p
	 * @param d
	 *            the direction, this block is pushed into
	 */
	public void push(Player p, Direction d) {
		if (!this.mayBePushed(d)) {
			return;
		}
		// before push
		List<BlockTarget> blockTargetsAt = this.getZone().getEntitiesAt(getX(), getY(), BlockTarget.class);
		for (BlockTarget blockTarget : blockTargetsAt) {
			blockTarget.untrigger();
		}

		// after push
		int x = getXAfterPush(d);
		int y = getYAfterPush(d);
		this.setPosition(x, y);
		blockTargetsAt = this.getZone().getEntitiesAt(x, y, BlockTarget.class);
		for (BlockTarget blockTarget : blockTargetsAt) {
			if (blockTarget.doesTrigger(this, p)) {
				blockTarget.trigger(this, p);
			}
		}
		if (resetBlock) {
			SingletonRepository.getTurnNotifier().dontNotify(this);
			SingletonRepository.getTurnNotifier().notifyInSeconds(RESET_TIMEOUT_IN_SECONDS, this);
		}
		wasMoved = true;
		this.sendSound();
		this.notifyWorldAboutChanges();
		if (logger.isDebugEnabled()) {
			logger.debug("Block [" + this.getID().toString() + "] pushed to (" + this.getX() + "," + this.getY() + ").");
		}
	}

	/**
	 * should the block reset to its original position after some time?
	 *
	 * @param resetBlock true, if the block should be reset; false otherwise
	 */
	public void setResetBlock(boolean resetBlock) {
		this.resetBlock = resetBlock;
	}

	private void sendSound() {
		if (this.sounds != null && !this.sounds.isEmpty()) {
			SoundEvent e = new SoundEvent(Rand.rand(sounds), SoundLayer.AMBIENT_SOUND);
			this.addEvent(e);
			this.notifyWorldAboutChanges();
		}
	}

	public int getYAfterPush(Direction d) {
		return this.getY() + d.getdy();
	}

	public int getXAfterPush(Direction d) {
		return this.getX() + d.getdx();
	}

	private boolean wasPushed() {
		boolean xChanged = this.getInt("x") != this.startX;
		boolean yChanged = this.getInt("y") != this.startY;
		return xChanged || yChanged;
	}

	private boolean mayBePushed(Direction d) {
		boolean pushed = wasPushed();
		int newX = this.getXAfterPush(d);
		int newY = this.getYAfterPush(d);

		if (!multi && pushed) {
			return false;
		}

		// additional checks: new position must be free
		boolean collision = this.getZone().collides(this, newX, newY);

		return !collision;
	}

	/**
	 * Get the shape of this Block
	 *
	 * @return the shape or null if this Block has no shape
	 */
	public String getShape() {
		if (this.has("shape")) {
			return this.get("shape");
		}
		return null;
	}

	@Override
	public void onEntered(ActiveEntity entity, StendhalRPZone zone, int newX, int newY) {
		// do nothing
	}

	@Override
	public void onExited(ActiveEntity entity, StendhalRPZone zone, int oldX, int oldY) {
		if (logger.isDebugEnabled()) {
			logger.debug("Block [" + this.getID().toString() + "] notified about entity [" + entity + "] exiting [" + zone.getName() + "].");
		}
		resetInPlayerlessZone(zone, entity);
	}

	@Override
	public void onMoved(ActiveEntity entity, StendhalRPZone zone, int oldX,
			int oldY, int newX, int newY) {
		// do nothing on move
	}

	@Override
	public void onEntered(RPObject object, StendhalRPZone zone) {
		// do nothing
	}

	@Override
	public void onExited(RPObject object, StendhalRPZone zone) {
		if (logger.isDebugEnabled()) {
			logger.debug("Block [" + this.getID().toString() + "] notified about object [" + object + "] exiting [" + zone.getName() + "].");
		}
		resetInPlayerlessZone(zone, object);
	}

	private void resetInPlayerlessZone(StendhalRPZone zone, RPObject object) {
		if (!resetBlock || !wasMoved) {
			return;
		}

		// reset to initial position if zone gets empty of players
		final List<Player> playersInZone = zone.getPlayers();
		int numberOfPlayersInZone = playersInZone.size();
		if (numberOfPlayersInZone == 0 || numberOfPlayersInZone == 1 && playersInZone.contains(object)) {
			resetIfInitialPositionFree();
		}
	}

	@Override
	public boolean isObstacle(Entity entity) {
		if (entity instanceof RPEntity) {
			return true;
		}

		return super.isObstacle(entity);
	}

	@Override
	public void beforeMove(ActiveEntity entity, StendhalRPZone zone, int oldX,
			int oldY, int newX, int newY) {
		if (entity instanceof Player) {
			Rectangle2D oldA = new Rectangle2D.Double(oldX, oldY, entity.getWidth(), entity.getHeight());
			Rectangle2D newA = new Rectangle2D.Double(newX, newY, entity.getWidth(), entity.getHeight());
			Direction d = Direction.getAreaDirectionTowardsArea(oldA, newA);
			this.push((Player) entity, d);
		}
	}

	@Override
	public void onTurnReached(int currentTurn) {
		resetIfInitialPositionFree();
	}

	@Override
	public void onAdded(StendhalRPZone zone) {
		super.onAdded(zone);
		this.startX = getX();
		this.startY = getY();
		zone.addMovementListener(this);
		zone.addZoneEnterExitListener(this);
	}

	@Override
	public void onRemoved(StendhalRPZone zone) {
		super.onRemoved(zone);
		zone.removeMovementListener(this);
		zone.removeZoneEnterExitListener(this);
	}

	/**
	 * Reset to initial position if no collision there, try again later if not
	 * possible
	 */
	private void resetIfInitialPositionFree() {
		if (!this.getZone().collides(this, this.startX, this.startY)) {
			this.reset();
		} else {
			// try again in a few moments
			SingletonRepository.getTurnNotifier().notifyInSeconds(RESET_AGAIN_DELAY, this);
		}
	}
}
