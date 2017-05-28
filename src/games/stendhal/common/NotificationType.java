/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.common;

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
	ERROR("error") {
		@Override
		public Color getColor() {
			return COLOR_ERROR;
		}
	},
	HEAL("heal") {
		@Override
		public Color getColor() {
			return COLOR_POSITIVE;
		}
		@Override
		public String getStyleDescription() {
			return REGULAR;
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
		@Override
		public String getStyleDescription() {
			return REGULAR;
		}
	},
	NORMAL("normal") {
		@Override
		public Color getColor() {
			return COLOR_NORMAL;
		}
	},
	POISON("poison") {
		@Override
		public Color getColor() {
			return COLOR_NEGATIVE;
		}
		@Override
		public String getStyleDescription() {
			return REGULAR;
		}
	},
	POSITIVE("positive") {
		@Override
		public Color getColor() {
			return COLOR_POSITIVE;
		}
		@Override
		public String getStyleDescription() {
			return REGULAR;
		}
	},
	EMOTE("emote") {
		@Override
		public Color getColor() {
			return COLOR_EMOTE;
		}
	},
	GROUP("group") {
		@Override
		public Color getColor() {
			return COLOR_GROUP;
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
	SCENE_SETTING("scene_setting") {
		@Override
		public Color getColor() {
			return COLOR_SCENE_SETTING;
		}
	},
	SERVER("server") {
		@Override
		public Color getColor() {
			return COLOR_PRIVMSG;
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
	},
	SUPPORT("support") {
		@Override
		public Color getColor() {
			return COLOR_SUPPORT;
		}
	},
	DETAILED("detailed") {
		@Override
		public String getStyleDescription() {
			return REGULAR;
		}
	},
	WARNING("warning") {
		@Override
		public Color getColor() {
			return COLOR_WARNING;
		}
	};
	public static final Color COLOR_CLIENT = Color.gray;

	public static final Color COLOR_ERROR = Color.red;

	public static final Color COLOR_INFORMATION = Color.orange;

	public static final Color COLOR_NEGATIVE = Color.red;

	public static final Color COLOR_NORMAL = Color.black;

	public static final Color COLOR_POSITIVE = Color.green;

	/** dark blue */
	public static final Color COLOR_GROUP = new Color(00, 00, 160);

	/** muted purple */
	public static final Color COLOR_EMOTE = new Color(99, 61, 139);

	public static final Color COLOR_PRIVMSG = Color.darkGray;

	/** dark green */
	public static final Color COLOR_RESPONSE = new Color(0x006400);

	/** dark brown */
	public static final Color COLOR_SCENE_SETTING = new Color(0x572002);

	public static final Color COLOR_SIGNIFICANT_NEGATIVE = Color.pink;

	/** bright turquoise blue */
	public static final Color COLOR_SIGNIFICANT_POSITIVE = new Color(65,
			105, 225);

	/** purple */
	public static final Color COLOR_TUTORIAL = new Color(172, 0, 172);

	/** strong bright orange */
	public static final Color COLOR_SUPPORT = new Color(0xff7200);

	/** dark red */
	public static final Color COLOR_WARNING = new Color(0xa00000);

	// TODO: review thinking here of using constants.
	// these are tied to the ones in client.KTextEdit.gui.initStylesForTextPane
	// so should we tie them together somehow?
	// also the definitions are crazy.

	/** normal is bold */
	public static final String NORMALSTYLE = "normal";
	/** regular is not bold */
	public static final String REGULAR = "regular";
	// fwiw, "bold" is blue, italic, bigger than normal, bold and blue.

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
	 * Get the mapping mnemonic (programmatic name).
	 *
	 * @return The mapping mnemonic.
	 */
	public String getMnemonic() {
		return mnemonic;
	}

	/**
	 * Get the color that is tied to a notification type.
	 *
	 * @return The appropriate color.
	 */
	public Color getColor() {
		return COLOR_NORMAL;
	}

	/**
	 * Get the style that is tied to a notification type.
	 *
	 * @return The appropriate style.
	 */
	public String getStyleDescription() {
		return NORMALSTYLE;
	}

	/**
	 * Get notification type for server messages that the client can show
	 * without problems. Call this instead of using SERVER directly.
	 *
	 * @param clientVersion version of the client
	 * @return appropriate type
	 */
	public static NotificationType getServerNotificationType(String clientVersion) {
		if ((clientVersion != null) && (Version.compare(clientVersion, "1.00") > 0)) {
			return NotificationType.SERVER;
		} else {
			return NotificationType.PRIVMSG;
		}
	}
}
