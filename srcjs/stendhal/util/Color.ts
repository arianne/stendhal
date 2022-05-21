/***************************************************************************
 *                    Copyright © 2003-2022 - Arianne                      *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/


export class Color {
	public static readonly AQUA = "#00ffff";
	public static readonly BLACK = "#000000";
	public static readonly BLUE = "rgb(0, 0, 255)";
	public static readonly CYAN = Color.AQUA;
	public static readonly DARKGRAY = "#a9a9a9";
	public static readonly GRAY = "#808080";
	public static readonly GREEN = "#008000";
	public static readonly LIGHTGRAY = "#c0c0c0";
	public static readonly MAGENTA = "#ff00ff";
	public static readonly ORANGE = "#ffa500";
	public static readonly PINK = "#ffc0cb";
	public static readonly RED = "#ff0000";
	public static readonly VIOLET = "rgb(238, 130, 238)";
	public static readonly WHITE = "#ffffff";
	public static readonly YELLOW = "#ffff00";

	public static readonly CREATURE = Color.YELLOW;
	public static readonly DOMESTICANIMAL = "rgb(255, 150, 0)";
	public static readonly GHOST = Color.GRAY;
	public static readonly GROUP = "rgb(99, 61, 139)";
	public static readonly NPC = "rgb(0, 150, 0)";
	public static readonly PLAYER = Color.WHITE;
	public static readonly USER = Color.BLUE;

	public static readonly BACKGROUND = "#cccccc";
	public static readonly BLOCKED = Color.RED;
	public static readonly COLLISION = Color.RED;
	public static readonly FLYOVER = "rgb(212, 158, 72)";
	public static readonly PROTECTION = "rgb(202, 230, 202)";
	public static readonly WALKBLOCK = "rgb(209, 144, 224)";

	public static readonly MOVING = "rgb(200, 255, 200)";
	public static readonly PORTAL = Color.WHITE;
	public static readonly PORTALOUTLINE = Color.BLACK;
	public static readonly CHEST = Color.VIOLET;
}
