/***************************************************************************
 *                 Copyright © 2003-2024 - Faiumoni e. V.                  *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/


export class Color {
	public static readonly AQUA = "rgb(0, 255, 255)"; // #00FFFF
	public static readonly BLACK = "rgb(0, 0, 0)"; // #000000
	public static readonly BLUE = "rgb(0, 0, 255)"; // #0000FF
	public static readonly CYAN = Color.AQUA;
	public static readonly DARK_GRAY = "rgb(169, 169, 169)"; // #A9A9A9
	public static readonly GRAY = "rgb(128, 128, 128)"; // #808080
	public static readonly GREEN = "rgb(0, 128, 0)"; // #008000
	public static readonly LIGHT_GRAY = "rgb(192, 192, 192)"; // #C0C0C0
	public static readonly MAGENTA = "rgb(255, 0, 255)" // #FF00FF
	public static readonly ORANGE = "rgb(255, 165, 0)"; // #FFA500
	public static readonly PINK = "rgb(255, 192, 203)"; // #FFC0CB
	public static readonly RED = "rgb(255, 0, 0)"; // #FF0000
	public static readonly VIOLET = "rgb(238, 130, 238)"; // #EE82EE
	public static readonly WHITE = "rgb(255, 255, 255)"; // #FFFFFF
	public static readonly YELLOW = "rgb(255, 255, 0)"; // #FFFF00

	public static readonly CREATURE = Color.YELLOW;
	public static readonly DOMESTICANIMAL = "rgb(255, 150, 0)"; // #FF9600 (orange)
	public static readonly GHOST = Color.GRAY;
	public static readonly GROUP = "rgb(99, 61, 139)"; // #633D8B (dark violet)
	public static readonly NPC = "rgb(0, 150, 0)"; // #009600 (green)
	public static readonly PLAYER = Color.WHITE;
	public static readonly USER = Color.BLUE;

	public static readonly BACKGROUND = "rgb(204, 204, 204)"; // #CCCCCC (light gray)
	public static readonly BLOCKED = Color.RED;
	public static readonly COLLISION = Color.RED;
	public static readonly FLYOVER = "rgb(212, 158, 72)"; // #D49E48 (brown-orange)
	public static readonly PROTECTION = "rgb(202, 230, 202)"; // #CAE6CA (light green)
	public static readonly WALKBLOCK = "rgb(209, 144, 224)"; // #D190E0 (violet)

	public static readonly MOVING = "rgb(200, 255, 200)"; // #C8FFC8 (light green)
	public static readonly PORTAL = Color.WHITE;
	public static readonly PORTALOUTLINE = Color.BLACK;
	public static readonly CHEST = Color.VIOLET;

	public static readonly CHAT_CLIENT = Color.GRAY;
	public static readonly CHAT_DETAILED = Color.BLACK;
	public static readonly CHAT_EMOTE = Color.GROUP;
	public static readonly CHAT_ERROR = Color.RED;
	public static readonly CHAT_GROUP = "rgb(00, 00, 160)"; // #0000A0 (dark blue)
	public static readonly CHAT_HEAL = Color.GREEN;
	public static readonly CHAT_INFO = Color.ORANGE;
	public static readonly CHAT_NEGATIVE = Color.RED;
	public static readonly CHAT_NORMAL = Color.BLACK;
	public static readonly CHAT_POISON = Color.RED;
	public static readonly CHAT_POSITIVE = Color.GREEN;
	public static readonly CHAT_PRIVATE = Color.DARK_GRAY;
	public static readonly CHAT_RESPONSE = "rgb(0, 100, 0)"; // #006400 (dark green)
	public static readonly CHAT_SCENE = "rgb(87, 32, 2)"; // #572002 (brown)
	public static readonly CHAT_SERVER = Color.DARK_GRAY;
	public static readonly CHAT_SIG_NEGATIVE = Color.PINK;
	public static readonly CHAT_SIG_POSITIVE = "rgb(65, 105, 225)"; // #4169FF (light blue)
	public static readonly CHAT_SUPPORT = "rgb(255, 114, 0)"; // #FF7200 (orange)
	public static readonly CHAT_TUTORIAL = "rgb(172, 0, 172)"; // #AC00AC (violet)
	public static readonly CHAT_WARN = "rgb(160, 0, 0)"; // #A00000 // (red)


	/**
	 * Static members & methods only.
	 */
	private constructor() {
		// do nothing
	}

	public static getStatBarColor(ratio: number): string {
		const red = Math.floor(Math.min((1 - ratio) * 2, 1) * 255);
		const green = Math.floor(Math.min(ratio * 2, 1) * 255);
		return "rgb(" + red + ", " + green + ", 0)";
	}
}
