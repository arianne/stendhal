/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { ui } from "./UI";
import { UIComponentEnum } from "./UIComponentEnum";
import { ChatButton } from "./quickmenu/ChatButton";
import { Panel } from "./toolkit/Panel";
import { singletons } from "../SingletonRepo";


export class ChatPanel extends Panel {

	constructor() {
		super("bottomPanel");
	}

	/**
	 * Updates element using viewport attributes.
	 */
	public override refresh() {
		const rect = singletons.getViewPort().getElement().getBoundingClientRect();
		const halfHeight = Math.abs(rect.height / 2);
		this.componentElement.style["width"] = rect.width + "px";
		this.componentElement.style["height"] = halfHeight + "px";
		this.componentElement.style["left"] = rect.left + "px";
		this.componentElement.style["top"] = (rect.top + halfHeight) + "px";
	}

	/**
	 * Shows chat panel when enter key is pressed.
	 */
	public onEnterPressed() {
		this.setVisible(!this.isVisible());
	}

	/**
	 * Hides chat panel after sending message if auto-hiding enabled.
	 */
	public onMessageSent() {
		if (this.isVisible() && singletons.getConfigManager().getBoolean("client.chat.autohide")) {
			this.setVisible(false);
		}
	}

	public override setVisible(visible=true) {
		super.setVisible(visible);
		// update config
		singletons.getConfigManager().set("client.chat.visible", visible);
		// update quick menu button
		const chatButton = ui.get(UIComponentEnum.ChatButton);
		if (chatButton) {
			(chatButton as ChatButton).update();
		}
	}
}
