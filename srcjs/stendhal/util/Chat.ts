/***************************************************************************
 *                   (C) Copyright 2022 - Faiumoni e. V.                   *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { ui } from "../ui/UI";
import { UIComponentEnum } from "../ui/UIComponentEnum";
import { ChatLogComponent } from "../ui/component/ChatLogComponent";

/**
 * chat logger
 */
export class Chat {

	/**
	 * adds a line to the chat log
	 *
	 * @param type type of message
	 * @param message message to log
	 */
	public static log(type: string, message: string) {
		(ui.get(UIComponentEnum.ChatLog) as ChatLogComponent).addLine(type, message);
	}

}
