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
import static games.stendhal.common.constants.Actions.MODE;
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

/**
 * Main window keyboard handling.
 */
class GameKeyHandler implements KeyListener {
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
				 * 
				 * Check if the player is currently using auto-walk or the Alt
				 * key is pressed.
				 */
				User user = User.get();
				if ((user.getRPObject().has(AUTOWALK) || e.isAltDown())
						&& Testing.MOVEMENT) {
					/* Face direction pressed and toggle auto-walk. */
					this.processAutoWalk(direction, user);
				} else {
					if (e.isAltGraphDown()) {
						if (System.currentTimeMillis() - lastAction > 1000) {
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
	 * Handle direction press to invoke or disable auto-walk.
	 * 
	 * @param direction
	 *        The direction to move/face
	 * @param user
	 *        The entity to set/check
	 */
	private synchronized void processAutoWalk(final Direction direction,
			final User user) {
		RPAction walkAction = new RPAction();
		final boolean facing = direction == user.getDirection();

		/* Correct facing direction if necessary. */
		if (!facing) {
			RPAction faceAction = new RPAction();
			faceAction.put(TYPE, FACE);
			faceAction.put(DIR, direction.get());
			this.client.send(faceAction);
		}

		/* Check if player is already using auto-walk. */
		if (!user.getRPObject().has(AUTOWALK)) {
			walkAction.put(TYPE, WALK);

		} else if (facing) {
			/* Player can press key of current walking direction to toggle
			 * auto-walk off.
			 */
			walkAction.put(TYPE, WALK);
			walkAction.put(MODE, "stop");
		}

		/* Send auto-walk action to the server. */
		if (walkAction.has(TYPE)) {
			/* Toggle auto-walk. */
			this.client.send(walkAction);
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
