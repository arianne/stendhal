/***************************************************************************
 *                   (C) Copyright 2003-2018 - Arianne                     *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui.settings;


/**
 * Public properties for UI settings.
 */
public class SettingsProperties {
	/** property used for double-tap direction to initiate auto-walk. */
	public static final String DOUBLE_TAP_AUTOWALK_PROPERTY = "move.doubletapautowalk";

	/** Property for continuous movement through map changes & portals (currently disabled for portals). */
	public static final String  MOVE_CONTINUOUS_PROPERTY = "move.continuous";

	/** property for display HP bar */
	public static final String HP_BAR_PROPERTY =  "ui.hp_bar";

	public static final String MSG_BLINK = "ui.msg_blink";

	public static final String MSG_SOUND = "ui.msg_sound";
}
