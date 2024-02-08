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
import { ChatLogComponent } from "./component/ChatLogComponent";
import { QuickMenu } from "./quickmenu/QuickMenu";
import { QuickMenuButton } from "./quickmenu/QuickMenuButton";
import { Panel } from "./toolkit/Panel";
import { singletons } from "../SingletonRepo";


/**
 * Panel containing elements associated with chat.
 *
 * TODO: move to ui/component directory
 */
export class ChatPanel extends Panel {

	constructor() {
		super("bottomPanel");
		this.setFloating(singletons.getConfigManager().getBoolean("chat.float"));
	}

	/**
	 * Updates chat panel to float or be positioned statically.
	 *
	 * @param floating
	 *   `true` if panel should float.
	 */
	public setFloating(floating: boolean) {
		singletons.getConfigManager().set("chat.float", floating);
		let propPosition = "static";
		let propOpacity = "1.0";
		if (floating) {
			propPosition = "absolute";
			propOpacity = "0.5";
		} else {
			// ensure visible when not floating
			this.setVisible(true);
		}
		this.componentElement.style.setProperty("position", propPosition);
		this.componentElement.style.setProperty("opacity", propOpacity);
		this.refresh();
	}

	/**
	 * Checks if panel is in floating state.
	 *
	 * @return
	 *   `true` if "position" style property is set to "absolute".
	 */
	public isFloating(): boolean {
		return getComputedStyle(this.componentElement).getPropertyValue("position") === "absolute";
	}

	/**
	 * Updates element using viewport attributes.
	 */
	public override refresh() {
		const floating = this.isFloating();
		if (floating) {
			const rect = singletons.getViewPort().getElement().getBoundingClientRect();
			const halfHeight = Math.abs(rect.height / 2);
			this.componentElement.style["width"] = rect.width + "px";
			this.componentElement.style["height"] = halfHeight + "px";
			this.componentElement.style["left"] = rect.left + "px";
			this.componentElement.style["top"] = (rect.top + halfHeight) + "px";
			// remove theming when floating
			this.componentElement.classList.remove("background");
		} else {
			for (const prop of ["width", "height", "left", "top"]) {
				this.componentElement.style.removeProperty(prop);
			}
			// add theming when inline
			this.componentElement.classList.add("background");
		}

		// show or hide button to toggle chat panel visibility
		QuickMenu.setButtonEnabled(UIComponentEnum.QMChat, floating);

		// adapt viewport layout
		singletons.getViewPort().onChatPanelRefresh(floating);
	}

	/**
	 * Shows chat panel when enter key is pressed.
	 */
	public onEnterPressed() {
		if (this.isFloating()) {
			this.setVisible(!this.isVisible());
		}
	}

	/**
	 * Hides chat panel after sending message if auto-hiding enabled.
	 */
	public onMessageSent() {
		if (this.isFloating() && this.isVisible() && singletons.getConfigManager().getBoolean("chat.autohide")) {
			this.setVisible(false);
		}
	}

	public override setVisible(visible=true) {
		// FIXME: there may be a problem if panel is not floating & not visible when client starts
		const stateChanged = visible != this.isVisible();
		super.setVisible(visible);
		// update config
		singletons.getConfigManager().set("chat.visible", visible);
		// update quick menu button
		const chatButton = ui.get(UIComponentEnum.QMChat) as QuickMenuButton;
		if (chatButton) {
			chatButton.update();
		}
		// update chat log
		const chatLog = (ui.get(UIComponentEnum.ChatLog) as ChatLogComponent);
		if (stateChanged && chatLog) {
			this.isVisible() ? chatLog.onUnhide() : chatLog.onHide();
		}
	}
}
