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
package games.stendhal.common.constants;

import static games.stendhal.client.gui.settings.SettingsProperties.MOVE_CONTINUOUS_PROPERTY;

/**
 * contains constants needed for server and client to process actions.
 * Constants only used by either side should be declared on the side where they are used.
 *
 */
public final class Actions {
	public static final String ACTION = "action";

	//forsake
	public static final String SPECIES = "species";
	public static final String FORSAKE = "forsake";
	public static final String PET = "pet";
	public static final String SHEEP = "sheep";

	//push
	public static final String PUSH = "push";

	//knock
	public static final String KNOCK = "knock";

	//own
	public static final String OWN = "own";

	//list quests
	public static final String PROGRESS_STATUS = "progressstatus";

	//list quests
	public static final String LISTQUESTS = "listquests";

	//list producers
	public static final String LISTPRODUCERS = "listproducers";

	//support
	public static final String SUPPORTANSWER = "supportanswer";

	//outfit
	public static final String OUTFIT = "outfit_ext";
	public static final String REMOVEDETAIL = "removedetail";

	public static final String MOVETO = "moveto";

	//teleclickmode
	public static final String TELECLICKMODE = "teleclickmode";

	//tellall
	public static final String TELLALL = "tellall";

	//sentence
	public static final String SENTENCE = "sentence";

	//jail
	public static final String JAIL = "jail";

	//gag
	public static final String GAG = "gag";

	//playersquery
	public static final String WHERE = "where";
	public static final String WHO = "who";

	//inspect
	public static final String INSPECT = "inspect";
	public static final String INSPECTQUEST = "inspectquest";
	public static final String INSPECTKILL = "inspectkill";

	//teleport
	public static final String ZONE = "zone";
	public static final String TELEPORT = "teleport";

	//teleportto
	public static final String TELEPORTTO = "teleportto";

	//ignore
	public static final String REASON = "reason";
	public static final String DURATION = "duration";

	//ghostmode
	public static final String INVISIBLE = "invisible";
	public static final String GHOSTMODE = "ghostmode";

	//lookaction
	public static final String LOOK = "look";

	//faceaction
	public static final String DIR = "dir";
	public static final String FACE = "face";

	//moveaction
	public static final String MOVE = "move";

	//away
	public static final String AWAY = "away";

	//CStatus
	public static final String CSTATUS = "cstatus";
	public static final String CID = "cid";
	public static final String ID = "id";
	public static final String CIDLIST = "cidlist";

	//attack
	public static final String ATTACK = "attack";

	//destroy
	public static final String NAME = "name";

	//chat actions
	public static final String ANSWER = "answer";
	public static final String CHAT = "chat";
	public static final String EMOTE = "emote";
	public static final String GROUP_MESSAGE = "group_message";
    public static final String REPORT_ERROR = "report_error";
    public static final String SUPPORT = "support";
	public static final String TELL = "tell";

	//summon
	public static final String CREATURE = "creature";
	public static final String SUMMON = "summon";

	//summonat
	public static final String AMOUNT = "amount";
	public static final String ITEM = "item";
	public static final String SLOT = "slot";
	public static final String SUMMONAT = "summonat";

	//castspell
	public static final String CASTSPELL = "cast_spell";

	//buddy
	public static final String BUDDYONLINE = "1";
	public static final String BUDDY_OFFLINE = "0";

	public static final String GRUMPY = "grumpy";
	public static final String UNIGNORE = "unignore";
	public static final String REMOVEBUDDY = "removebuddy";
	public static final String IGNORE = "ignore";
	public static final String ADDBUDDY = "addbuddy";

	//adminlevel
	public static final String ATTR_HP = "hp";
	public static final String SUB = "sub";
	public static final String ADD = "add";
	public static final String SET = "set";
	public static final String TITLE = "title";
	public static final String ADMINLEVEL = "adminlevel";
	public static final String NEWLEVEL = "newlevel";

	//altercreature
	public static final String ALTERCREATURE = "altercreature";

	public static final String VALUE = "value";
	public static final String MODE = "mode";
	public static final String STAT = "stat";

	public static final String ALTER = "alter";
	public static final String UNSET = "unset";
	public static final String ALTERKILL = "alterkill";

	// for listing e.g. ignore list
	public static final String LIST = "list";

	public static final String TARGET = "target";
	public static final String TARGET_PATH = "target_path";
	public static final String BASESLOT = "baseslot";
	public static final String BASEOBJECT = "baseobject";
	public static final String BASEITEM = "baseitem";
	public static final String USE = "use";
	public static final String TYPE = "type";
	public static final String X = "x";
	public static final String Y = "y";
	public static final String MINUTES = "minutes";
	public static final String MESSAGE = "message";
	public static final String TEXT = "text";

	public static final String READ = "read";
	public static final String LOOK_CLOSELY = "look_closely";
	public static final String LANGUAGE = "language";

	// Movement/Control
	public static final String WALK = "walk";
	/* Property indicating player is using auto-walk. */
	public final static String AUTOWALK = "autowalk";
	/* Property indicating player is using continuous movement. */
	public final static String MOVE_CONTINUOUS = MOVE_CONTINUOUS_PROPERTY;
	public final static String COND_STOP = "conditional_stop";

	public final static String BESTIARY = "bestiary";

	private Actions() {
		// hide constructor
	}
}
