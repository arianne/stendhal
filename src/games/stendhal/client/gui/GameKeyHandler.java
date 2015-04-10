/***************************************************************************
 *                   (C) Copyright 2003-2012 - Stendhal                    *
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

import static games.stendhal.common.constants.Actions.AUTOWALK;
import static games.stendhal.common.constants.Actions.DIR;
import static games.stendhal.common.constants.Actions.FACE;
import static games.stendhal.common.constants.Actions.TYPE;
import static games.stendhal.common.constants.Actions.WALK;
import games.stendhal.client.GameScreen;
import games.stendhal.client.StendhalClient;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.User;
import games.stendhal.client.gui.j2d.entity.EntityView;
import games.stendhal.common.Direction;
import games.stendhal.common.constants.Testing;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import marauroa.common.game.RPAction;

import org.apache.log4j.Logger;

/**
 * Main window keyboard handling.
 */
class GameKeyHandler implements KeyListener {
	/* Logger instance. */
	private static final Logger logger = Logger.getLogger(GameKeyHandler.class);

	private final StendhalClient client;
	private final GameScreen screen;
	private long lastAction = 0;

	/**
	 * Delayed direction release holder.
	 */
	private DelayedDirectionRelease directionRelease;

	/**
	 * Create a new GameKeyHandler.
	 * 
	 * @param client client to send direction commands
	 * @param screen screen where to direct game screen related commands
	 */
	GameKeyHandler(StendhalClient client, GameScreen screen) {
		this.client = client;
		this.screen = screen;
	}

	@Override
	public void keyPressed(final KeyEvent e) {
		final int keyCode = e.getKeyCode();

		/* Ignore if the key is already pressed down. */
		if (!client.keyIsPressed(keyCode)) {
			/* Add keyCode to pressedStateKeys list. */
			client.onKeyPressed(keyCode);

			if (e.isShiftDown()) {
				/*
				 * We are going to use shift to move to previous/next line of text
				 * with arrows so we just ignore the keys if shift is pressed.
				 */
				return;
			}

			switch (keyCode) {
			case KeyEvent.VK_R:
				if (e.isControlDown()) {
					/*
					 * Ctrl+R Remove text bubbles
					 */
					screen.clearTexts();
				}
				break;

			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_UP:
			case KeyEvent.VK_DOWN:
				/*
				 * Ctrl means face, otherwise move. Alt turns on auto-walk.
				 */
				final Direction direction = keyCodeToDirection(e.getKeyCode());

				/* TODO: Remove MOTION condition when auto-walk testing is
				 * finished.
				 */
				if (e.isAltDown() && Testing.MOVEMENT) {
					/* Face direction pressed and toggle auto-walk. */
					this.processDirectionPress(direction, false, true);
				} else {
					if (e.isAltGraphDown()) {
						if (System.currentTimeMillis() - lastAction > 1000) {
							final User user = User.get();

							final EntityView<?> view = screen.getEntityViewAt(
									user.getX()
											+ direction.getdx(), user.getY()
											+ direction.getdy());

							if (view != null) {
								final IEntity entity = view.getEntity();
								if (!entity.equals(user)) {
									view.onAction();
									lastAction = System.currentTimeMillis();
								}
							}
						}
					}

					this.processDirectionPress(direction, e.isControlDown());
				}
				break;
			case KeyEvent.VK_0:
			case KeyEvent.VK_1:
			case KeyEvent.VK_2:
			case KeyEvent.VK_3:
			case KeyEvent.VK_4:
			case KeyEvent.VK_5:
			case KeyEvent.VK_6:
			case KeyEvent.VK_7:
			case KeyEvent.VK_8:
			case KeyEvent.VK_9:
				switchToSpellCastingState(e);
				break;
			}
		}
	}

	@Override
	public void keyReleased(final KeyEvent e) {
		final int keyCode = e.getKeyCode();

		/* Ignore if the key is not found in the pressedStateKeys list. */
		if (client.keyIsPressed(keyCode)) {
			/* Remove keyCode from pressedStateKeys list. */
			client.onKeyReleased(keyCode);

			switch (keyCode) {
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_UP:
			case KeyEvent.VK_DOWN:
				/*
				 * Ctrl means face, otherwise move
				 */
				processDirectionRelease(keyCodeToDirection(e.getKeyCode()),
						e.isControlDown());
			}
		} else {
			logger.warn("Released key " + Integer.toString(keyCode)
					+ " was not found in pressedStateKeys list");
		}
	}

	@Override
	public void keyTyped(final KeyEvent e) {
		// Ignore. All the work is done in keyPressed and keyReleased methods.
	}

	/**
	 * Process delayed direction release.
	 */
	synchronized void processDelayedDirectionRelease() {
		if ((directionRelease != null) && directionRelease.hasExpired()) {
			client.removeDirection(directionRelease.getDirection(),
					directionRelease.isFacing());

			directionRelease = null;
		}
	}

	/**
	 * Convert a keycode to the corresponding direction.
	 * 
	 * @param keyCode The keycode.
	 * 
	 * @return The direction, or <code>null</code>.
	 */
	private Direction keyCodeToDirection(final int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_LEFT:
			return Direction.LEFT;

		case KeyEvent.VK_RIGHT:
			return Direction.RIGHT;

		case KeyEvent.VK_UP:
			return Direction.UP;

		case KeyEvent.VK_DOWN:
			return Direction.DOWN;

		default:
			return null;
		}
	}

	/**
	 * Handle direction press actions.
	 * 
	 * @param direction The direction.
	 * @param facing If facing only.
	 */
	private synchronized void processDirectionPress(final Direction direction,
			final boolean facing) {
		if (directionRelease != null) {
			if (directionRelease.check(direction, facing)) {
				directionRelease = null;
				return;
			} else {
				/*
				 * Flush pending release
				 */
				client.removeDirection(directionRelease.getDirection(),
						directionRelease.isFacing());

				directionRelease = null;
			}
		}

		if (client.addDirection(direction, facing)) {
    		// Movement prediction.
    		User user = User.get();
    		if (user != null) {
    			user.predictMovement(direction, facing);
    		}
		}
	}

	/**
	 * Handle direction press actions and optionally set auto-walk.
	 * 
	 * @param direction
	 *        The direction to move/face
	 * @param facing
	 *        If facing only (ignored if autoWalk is <code>true</code>)
	 * @param autoWalk
	 *        Toggle auto-walk on/off
	 */
	private synchronized void processDirectionPress(final Direction direction,
			final boolean facing, final boolean autoWalk) {
		if (autoWalk) {
			boolean toggle = true;
			User user = User.get();

			RPAction walkAction = new RPAction();
			walkAction.put(TYPE, WALK);

			/* Correct user's direction if needed. */
			if (direction != user.getDirection()) {
				RPAction faceAction = new RPAction();

				faceAction.put(TYPE, FACE);
				faceAction.put(DIR, direction.get());
				this.client.send(faceAction);

				if (user.getRPObject().has(AUTOWALK)) {
					/* If player changes directions while auto-walk is on we
					 * do not need to turn it off.
					 */
					toggle = false;
				}
			}

			if (toggle) {
				/* Toggle auto-walk. */
				this.client.send(walkAction);
			}
		} else {
			/* If not using auto-walk switch to normal behavior. */
			this.processDirectionPress(direction, facing);
		}
	}

	/**
	 * Handle direction release actions.
	 * 
	 * @param direction The direction.
	 * @param facing If facing only.
	 */
	private synchronized void processDirectionRelease(final Direction direction,
			final boolean facing) {
		if (directionRelease != null) {
			if (directionRelease.check(direction, facing)) {
				/*
				 * Ignore repeats
				 */
				return;
			} else {
				/*
				 * Flush previous release
				 */
				client.removeDirection(directionRelease.getDirection(),
						directionRelease.isFacing());
			}
		}

		directionRelease = new DelayedDirectionRelease(direction, facing);
	}

	/**
	 * Switch the screen to spell casting state.
	 * 
	 * @param e
	 */
	private void switchToSpellCastingState(KeyEvent e) {
		screen.switchToSpellCasting(e);
	}
	
	private static class DelayedDirectionRelease {
		/**
		 * The maximum delay between auto-repeat release-press.
		 */
		protected static final long DELAY = 50L;

		protected long expiration;

		protected Direction dir;

		protected boolean facing;

		public DelayedDirectionRelease(final Direction dir, final boolean facing) {
			this.dir = dir;
			this.facing = facing;

			expiration = System.currentTimeMillis() + DELAY;
		}

		/**
		 * Check if a new direction matches the existing one, and if so, reset
		 * the expiration point.
		 * 
		 * @param dir The direction.
		 * @param facing The facing flag.
		 * 
		 * @return <code>true</code> if this is a repeat.
		 */
		public boolean check(final Direction dir, final boolean facing) {
			if (!this.dir.equals(dir)) {
				return false;
			}

			if (this.facing != facing) {
				return false;
			}

			final long now = System.currentTimeMillis();

			if (now >= expiration) {
				return false;
			}

			expiration = now + DELAY;

			return true;
		}

		/**
		 * Get the direction.
		 * 
		 * @return The direction.
		 */
		public Direction getDirection() {
			return dir;
		}

		/**
		 * Determine if the delay point has been reached.
		 * 
		 * @return <code>true</code> if the delay time has been reached.
		 */
		public boolean hasExpired() {
			return System.currentTimeMillis() >= expiration;
		}

		/**
		 * Determine if the facing only option was used.
		 * 
		 * @return <code>true</code> if facing only.
		 */
		public boolean isFacing() {
			return facing;
		}
	}
}
