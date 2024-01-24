/***************************************************************************
 *                (C) Copyright 2015-2024 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { UIComponentEnum } from "./UIComponentEnum";
import { ApplicationMenuDialog } from "./dialog/ApplicationMenuDialog";
import { ButtonBase } from "./quickmenu/ButtonBase";
import { QMButton as QuickMenu } from "./quickmenu/QMButton";
import { Component } from "./toolkit/Component";
import { SingletonFloatingWindow } from "./toolkit/SingletonFloatingWindow";

declare var stendhal: any;


class UI {

	/** Attribute denoting readiness of display. */
	private displayReady = false;

	private wellKnownComponents: Map<UIComponentEnum, Component> = new Map();


	public createSingletonFloatingWindow(title: string, contentComponent: Component, x: number, y: number) {
		return new SingletonFloatingWindow(title, contentComponent, x, y);
	}

	public registerComponent(key: UIComponentEnum, component: Component) {
		this.wellKnownComponents.set(key, component);
	}

	public unregisterComponent(component: Component) {
		for (let entry of this.wellKnownComponents.entries()) {
			if (entry[1] === component) {
				this.wellKnownComponents.delete(entry[0]);
				return;
			}
		}
	}

	public get(key: UIComponentEnum): Component|undefined {
		return this.wellKnownComponents.get(key);
	}

	public getPageOffset() {
		const body = document.body;
		const delem = document.documentElement;
		const offsetX = window.pageXOffset || delem.scrollLeft || body.scrollLeft;
		const offsetY = window.pageYOffset || delem.scrollTop || body.scrollTop;

		return {x: offsetX, y: offsetY};
	}

	/**
	 * Creates and displays application menu window.
	 */
	public showApplicationMenu() {
		const dialogState = stendhal.config.getWindowState("menu");
		const menuContent = new ApplicationMenuDialog();
		const menuFrame = ui.createSingletonFloatingWindow("Menu", menuContent, dialogState.x, dialogState.y);
		menuFrame.setId("menu");
		menuContent.setFrame(menuFrame);
	}

	/**
	 * Checks for attribute denoting display has been initialized.
	 *
	 * @return
	 *   `true` if display is initialized and ready.
	 */
	public isDisplayReady(): boolean {
		return this.displayReady;
	}

	/**
	 * Instructions to execute when display initialized.
	 */
	public onDisplayReady() {
		if (this.displayReady) {
			console.warn("display state was previously set to \"ready\"");
			return;
		}
		this.displayReady = true;

		const chatPanel = this.get(UIComponentEnum.BottomPanel)!;
		chatPanel.refresh();
		chatPanel.setVisible(stendhal.config.getBoolean("client.chat.visible"));
		// initialize on-screen joystick
		stendhal.ui.gamewindow.updateJoystick();
		QuickMenu.init();

		// update sound buttons
		this.onSoundUpdate();
	}

	/**
	 * Instructions to execute when display attributes are updated.
	 */
	public onDisplayUpdate() {
		if (!this.displayReady) {
			console.debug("display not in \"ready\" state");
			return;
		}
		this.get(UIComponentEnum.BottomPanel)!.refresh();
		QuickMenu.refresh();
	}

	/**
	 * Instructions to execute when sound muted state changes.
	 */
	public onSoundUpdate() {
		(this.get(UIComponentEnum.QMSound)! as ButtonBase).update();
		document.getElementById("soundbutton")!.textContent = stendhal.config.getBoolean("ui.sound")
				? "🔊" : "🔇";
	}
}

export let ui = new UI();
