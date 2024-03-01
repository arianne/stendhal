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

import { QuickMenuButton } from "./QuickMenuButton";
import { ui } from "../UI";
import { UIComponentEnum } from "../UIComponentEnum";


/**
 * Button to toggle visibility of floating chat panel.
 */
export class ChatButton extends QuickMenuButton {

	constructor() {
		super("chat", UIComponentEnum.QMChat);
	}

	/**
	 * Updates button image.
	 */
	public override update() {
		const chatPanel = ui.get(UIComponentEnum.BottomPanel);
		if (chatPanel) {
			this.setImageBasename(chatPanel.isVisible() ? "chat" : "chat-disabled");
		}
	}

	/**
	 * Toggles chat panel visibility when clicked/tapped.
	 */
	protected override onClick(evt: Event) {
		// update panel visibility
		const chatPanel = ui.get(UIComponentEnum.BottomPanel);
		if (chatPanel) {
			chatPanel.setVisible(!chatPanel.isVisible());
		}
	}
}
