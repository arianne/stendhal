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

import games.stendhal.client.GameScreen;
import games.stendhal.client.StendhalClient;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.User;
import games.stendhal.client.gui.j2d.entity.EntityView;
import games.stendhal.common.Direction;

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
		if (e.isShiftDown()) {
			/*
			 * We are going to use shift to move to previous/next line of text
			 * with arrows so we just ignore the keys if shift is pressed.
			 */
			return;
		}

		switch (e.getKeyCode()) {
		case KeyEvent.VK_R:
			if (e.isControlDown()) {
				/*
				 * Ctrl+R Remove text bubbles
				 */
				screen.clearTexts();
			}
			break;

		case KeyEvent.VK_W:
			if (e.isControlDown()) {
				StendhalClient client = StendhalClient.get();
				RPAction action = new RPAction();

				/* Toggle auto-walk. */
				action.put("type", "walk");
				client.send(action);
			}
			break;

		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_UP:
		case KeyEvent.VK_DOWN:
			/*
			 * Ctrl means face, otherwise move
			 */
			final Direction direction = keyCodeToDirection(e.getKeyCode());

			if (e.isAltGraphDown()) {
				if (System.currentTimeMillis() - lastAction > 1000) {
					final User user = User.get();

					final EntityView<?> view = screen.getEntityViewAt(user.getX()
							+ direction.getdx(), user.getY() + direction.getdy());

					if (view != null) {
						final IEntity entity = view.getEntity();
						if (!entity.equals(user)) {
							view.onAction();
							lastAction = System.currentTimeMillis();
						}
					}
				}
			}

			processDirectionPress(direction, e.isControlDown());
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

	@Override
	public void keyReleased(final KeyEvent e) {
		switch (e.getKeyCode()) {
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