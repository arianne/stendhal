/*
 *
 *
 * $Id$
 */

package games.stendhal.client.gui;

//
//

import games.stendhal.client.StendhalClient;
import games.stendhal.client.StendhalUI;
import games.stendhal.common.Direction;
import games.stendhal.common.NotificationType;

import java.awt.Color;
import java.awt.event.KeyEvent;

import org.apache.log4j.Logger;

/**
 * A base graphical user interface for Stendhal. This implements code that uses
 * AWT, but not any specific display format (e.g. 2D, 3D).
 */
public abstract class StendhalGUI extends StendhalUI {
	/**
	 * Color for local client messages.
	 */
	protected static final Color COLOR_CLIENT = Color.gray;

	/**
	 * Color for informational messages.
	 */
	protected static final Color COLOR_INFORMATION = Color.orange;

	/**
	 * Color for negative impact messages.
	 */
	protected static final Color COLOR_NEGATIVE = Color.red;

	/**
	 * Color for normal messages.
	 */
	protected static final Color COLOR_NORMAL = Color.black;

	/**
	 * Color for positive impact messages.
	 */
	protected static final Color COLOR_POSITIVE = Color.green;

	/**
	 * Color for private messages.
	 */
	protected static final Color COLOR_PRIVMSG = Color.darkGray;

	/**
	 * Color for response (to some inquery) messages.
	 */
	protected static final Color COLOR_RESPONSE = new Color(0x006400);

	/**
	 * Color for significant negative impact messages.
	 */
	protected static final Color COLOR_SIGNIFICANT_NEGATIVE = Color.pink;

	/**
	 * Color for significant positive impact messages.
	 */
	protected static final Color COLOR_SIGNIFICANT_POSITIVE = new Color(65,
			105, 225);

	/**
	 * Color for tutorial messages.
	 */
	protected static final Color COLOR_TUTORIAL = new Color(172, 0, 172);

	/**
	 * The logger.
	 */
	private static final Logger logger = Logger.getLogger(StendhalGUI.class);

	/**
	 * Whether the Ctrl key is currently down.
	 */
	private boolean ctrlDown;

	/**
	 * Whether the Shift key is currently down.
	 */
	private boolean shiftDown;

	/**
	 * Whether the Alt key is currently down.
	 */
	private boolean altDown;

	/**
	 * Delayed direction release holder.
	 */
	protected DelayedDirectionRelease directionRelease;

	/**
	 * Create a stendhal graphical user interface.
	 * 
	 * @param client
	 *            The stendhal client.
	 */
	public StendhalGUI(final StendhalClient client) {
		super(client);
	}

	//
	// StendhalGUI
	//

	/**
	 * Get the color that is tied to a notification type.
	 * 
	 * TODO: Make dynamic/user configurable.
	 * 
	 * @param type
	 *            The notification type.
	 * 
	 * @return The appropriate color.
	 */
	public Color getNotificationColor(final NotificationType type) {
		switch (type) {
		case CLIENT:
			return COLOR_CLIENT;

		case INFORMATION:
			return COLOR_INFORMATION;

		case NEGATIVE:
			return COLOR_NEGATIVE;

		case NORMAL:
			return COLOR_NORMAL;

		case POSITIVE:
			return COLOR_POSITIVE;

		case PRIVMSG:
			return COLOR_PRIVMSG;

		case RESPONSE:
			return COLOR_RESPONSE;

		case SIGNIFICANT_NEGATIVE:
			return COLOR_SIGNIFICANT_NEGATIVE;

		case SIGNIFICANT_POSITIVE:
			return COLOR_SIGNIFICANT_POSITIVE;

		case TUTORIAL:
			return COLOR_TUTORIAL;

		default:
			logger.warn("Unknown notification type: " + type);
			return COLOR_NORMAL;
		}
	}

	/**
	 * Convert a keycode to the corresponding direction.
	 * 
	 * @param keyCode
	 *            The keycode.
	 * 
	 * @return The direction, or <code>null</code>.
	 */
	protected Direction keyCodeToDirection(final int keyCode) {
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
	 * @param direction
	 *            The direction.
	 * @param facing
	 *            If facing only.
	 */
	protected void processDirectionPress(final Direction direction,
			final boolean facing) {
		if (directionRelease != null) {
			if (directionRelease.check(direction, facing)) {
				/*
				 * Cancel pending release
				 */
				logger.debug("Repeat suppressed");
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

		client.addDirection(direction, facing);
	}

	/**
	 * Handle direction release actions.
	 * 
	 * @param direction
	 *            The direction.
	 * @param facing
	 *            If facing only.
	 */
	protected void processDirectionRelease(final Direction direction,
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
	 * Determine if the Alt key is held down.
	 * 
	 * @return <code>true</code> if down.
	 */
	@Override
	public boolean isAltDown() {
		return altDown;
	}

	/**
	 * Determine if the <Ctrl> key is held down.
	 * 
	 * @return <code>true</code> if down.
	 */
	@Override
	public boolean isCtrlDown() {
		return ctrlDown;
	}

	/**
	 * Determine if the <Shift> key is held down.
	 * 
	 * @return <code>true</code> if down.
	 */
	@Override
	public boolean isShiftDown() {
		return shiftDown;
	}

	/**
	 * Save the current keyboard modifier (i.e. Alt/Ctrl/Shift) state.
	 * 
	 * @param ev
	 *            The keyboard event.
	 */
	protected void updateModifiers(final KeyEvent ev) {
		altDown = ev.isAltDown();
		ctrlDown = ev.isControlDown();
		shiftDown = ev.isShiftDown();
	}

	//
	//

	protected static class DelayedDirectionRelease {
		/**
		 * The maximum delay between auto-repeat release-press
		 */
		protected static final long DELAY = 50L;

		/**
		 * The point at which the event is accepted.
		 */
		protected long expiration;

		/**
		 * The direction.
		 */
		protected Direction dir;

		/**
		 * Whether facing modifier is in effect.
		 */
		protected boolean facing;

		/**
		 * 
		 * 
		 */
		public DelayedDirectionRelease(final Direction dir, final boolean facing) {
			this.dir = dir;
			this.facing = facing;

			expiration = System.currentTimeMillis() + DELAY;
		}

		//
		// DelayedDirectionRelease
		//

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

		/**
		 * Check if a new direction matches the existing one, and if so, reset
		 * the expiration point.
		 * 
		 * @param dir
		 *            The direction.
		 * @param facing
		 *            The facing flag.
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

			long now = System.currentTimeMillis();

			if (now >= expiration) {
				return false;
			}

			expiration = now + DELAY;

			return true;
		}
	}
}
