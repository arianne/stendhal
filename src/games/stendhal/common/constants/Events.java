/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.common.constants;

/**
 * Constants for events
 */
public final class Events {

	/** private text events, including private chat and status messages */
	public static final String PRIVATE_TEXT = "private_text";

	/** public chat */
	public static final String PUBLIC_TEXT = "text";

	/** a sound event */
	public static final String SOUND = "sound_event";

	/** a player on the buddy list logged on */
	public static final String PLAYER_LOGGED_ON = "player_logged_on";

	/** a player on the buddy list logged off */
	public static final String PLAYER_LOGGED_OUT = "player_logged_out";

	/** attack event during a fight */
	public static final String ATTACK = "attack";

	/** player to player trade state change event */
	public static final String TRADE_STATE_CHANGE = "trade_state_change_event";

	/** moves the view point to another area (orbs) */
	public static final String VIEW_CHANGE = "view_change";

	/** changes within the current group */
	public static final String GROUP_CHANGE = "group_change_event";

	/** an invitation to join a group */
	public static final String GROUP_INVITE = "group_invite_event";

	/** progress log */
	public static final String PROGRESS_STATUS_CHANGE = "progress_status_event";

	/** achievement notiviation */
	public static final String REACHED_ACHIEVEMENT = "reached_achievement";

	/** Event for graphical effects. */
	public static final String IMAGE = "image_event";

	/** Event for visual effects affecting the entire game screen. */
	public static final String GLOBAL_VISUAL = "global_visual_effect";

	public static final String BESTIARY = Actions.BESTIARY;

	public static final String OUTFIT_LIST = "show_outfit_list";
}
