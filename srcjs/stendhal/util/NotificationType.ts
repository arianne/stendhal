/***************************************************************************
 *                    Copyright © 2003-2023 - Arianne                      *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Color } from "./Color";


export const NotificationType = {
	"client": Color.GRAY,
	"detailed": Color.BLACK,
	"emote": "rgb(99, 61, 139)",
	"error": Color.RED,
	"group": "rgb(00, 00, 160)",
	"heal": Color.GREEN,
	"information": Color.ORANGE,
	"negative": Color.RED,
	"normal": Color.BLACK,
	"poison": Color.RED,
	"positive": Color.GREEN,
	"privmsg": Color.DARKGRAY,
	"response": "#006400",
	"scene_setting": "#572002",
	"server": Color.DARKGRAY,
	"significant_negative": Color.PINK,
	"significant_positive": "rgb(65, 105, 225)",
	"support": "#ff7200",
	"tutorial": "rgb(172, 0, 172)",
	"warning": "#a00000"
} as {[index: string]: string;};
