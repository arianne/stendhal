/***************************************************************************
 *                       Copyright © 2024 - Stendhal                       *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { ButtonBase } from "./ButtonBase";
import { ui } from "../UI";
import { UIComponentEnum } from "../UIComponentEnum";


export class ChatButton extends ButtonBase {

	constructor() {
		super("chat");
	}

	/**
	 * Updates button icon.
	 */
	public override update() {
		const chatPanel = ui.get(UIComponentEnum.BottomPanel);
		if (chatPanel) {
			this.setImageBasename(chatPanel.isVisible() ? "chat" : "chat-disabled");
		}
	}

	protected override onClick(evt: Event) {
		// update panel visibility
		const chatPanel = ui.get(UIComponentEnum.BottomPanel);
		if (chatPanel) {
			chatPanel.setVisible(!chatPanel.isVisible());
			this.update();
		}
	}
}
