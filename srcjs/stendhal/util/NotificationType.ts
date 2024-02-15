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

import { Color } from "./Color";


export const NotificationType = {
	"client": Color.CHAT_CLIENT,
	"detailed": Color.CHAT_DETAILED,
	"emote": Color.CHAT_EMOTE,
	"error": Color.CHAT_ERROR,
	"group": Color.CHAT_GROUP,
	"heal": Color.CHAT_HEAL,
	"information": Color.CHAT_INFO,
	"negative": Color.CHAT_NEGATIVE,
	"normal": Color.CHAT_NORMAL,
	"poison": Color.CHAT_POISON,
	"positive": Color.CHAT_POSITIVE,
	"privmsg": Color.CHAT_PRIVATE,
	"response": Color.CHAT_RESPONSE,
	"scene_setting": Color.CHAT_SCENE,
	"server": Color.CHAT_SERVER,
	"significant_negative": Color.CHAT_SIG_NEGATIVE,
	"significant_positive": Color.CHAT_SIG_POSITIVE,
	"support": Color.CHAT_SUPPORT,
	"tutorial": Color.CHAT_TUTORIAL,
	"warning": Color.CHAT_WARN
} as {[index: string]: string;};

// immutable
Object.freeze(NotificationType);
