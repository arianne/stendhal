/*
 * @(#) src/games/stendhal/client/NotificationType.java
 *
 * $Id$
 */

package games.stendhal.client;

import java.awt.Color;

//
//

/**
 * A logical notification type, which can be mapped to UI specific contexts.
 * This would be similar to logical styles vs. physical styles in HTML.
 */
public enum NotificationType {
	CLIENT("client") {
		@Override
		public Color getColor() {

			return COLOR_CLIENT;
		}
	},
	INFORMATION("information") {
		@Override
		public Color getColor() {
			return COLOR_INFORMATION;
		}
	},
	NEGATIVE("negative") {
		@Override
		public Color getColor() {
			return COLOR_NEGATIVE;
		}
	},
	NORMAL("normal") {
		@Override
		public Color getColor() {

			return COLOR_NORMAL;
		}
	},
	POSITIVE("positive") {
		@Override
		public Color getColor() {

			return COLOR_POSITIVE;
		}
	},
	PRIVMSG("privmsg") {
		@Override
		public Color getColor() {

			return COLOR_PRIVMSG;
		}
	},
	RESPONSE("response") {
		@Override
		public Color getColor() {
			return COLOR_RESPONSE;
		}
	},
	SIGNIFICANT_NEGATIVE("significant_negative") {
		@Override
		public Color getColor() {
			return COLOR_SIGNIFICANT_NEGATIVE;
		}
	},
	SIGNIFICANT_POSITIVE("significant_positive") {
		@Override
		public Color getColor() {
			return COLOR_SIGNIFICANT_POSITIVE;
		}
	},
	TUTORIAL("tutorial") {
		@Override
		public Color getColor() {

			return COLOR_TUTORIAL;
		}
	};

	/**
	 * the associated Color
	 */
	public abstract Color getColor();

	/**
	 * The mapping mnemonic.
	 */
	protected String mnemonic;

	/**
	 * Create a notification type.
	 *
	 * @param mnemonic
	 *            The mapping mnemonic.
	 */
	private NotificationType(final String mnemonic) {
		this.mnemonic = mnemonic;
	}

	//
	// NotificationType
	//

	/**
	 * Get the mapping mnemonic (programatic name).
	 *
	 * @return The mapping mnemonic.
	 */
	public String getMnemonic() {
		return mnemonic;
	}

	protected static final Color COLOR_CLIENT = Color.gray;

	protected static final Color COLOR_INFORMATION = Color.orange;

	protected static final Color COLOR_NEGATIVE = Color.red;

	protected static final Color COLOR_NORMAL = Color.black;

	protected static final Color COLOR_POSITIVE = Color.green;

	protected static final Color COLOR_PRIVMSG = Color.darkGray;

	protected static final Color COLOR_RESPONSE = new Color(0x006400);

	protected static final Color COLOR_SIGNIFICANT_NEGATIVE = Color.pink;

	protected static final Color COLOR_SIGNIFICANT_POSITIVE = new Color(65,
			105, 225);

	protected static final Color COLOR_TUTORIAL = new Color(172, 0, 172);
}
