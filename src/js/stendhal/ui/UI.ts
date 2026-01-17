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

import { marauroa } from "marauroa"
import { stendhal } from "../stendhal";

import { UIComponentEnum } from "./UIComponentEnum";
import { SoftwareJoystickController } from "./SoftwareJoystickController";
import { ApplicationMenuDialog } from "./dialog/ApplicationMenuDialog";
import { QuickMenu } from "./quickmenu/QuickMenu";
import { QuickMenuButton } from "./quickmenu/QuickMenuButton";
import { Component } from "./toolkit/Component";
import { SingletonFloatingWindow } from "./toolkit/SingletonFloatingWindow";


class UI {

	/** Attribute denoting readiness of display. */
	private static displayReady = false;
	/** Attribute denoting readiness of user. */
	private static userReady = false;

	/** List of registered components. */
	private wellKnownComponents: Map<UIComponentEnum, Component> = new Map();


	/**
	 * Builds and displays a dialog window that is automatically closed if another is opened.
	 *
	 * @param title {string}
	 *   Text displayed in window title bar.
	 * @param contentComponent {ui.toolkit.Component.Component}
	 *   Component containing contents of dialog window.
	 * @param x {number}
	 *   Window positioning on X axis.
	 * @param y {number}
	 *   Window positioning on Y axis.
	 * @return {ui.toolkit.SingletonFloatingWindow.SingletonFloatingWindow}
	 *   New dialog window.
	 */
	public createSingletonFloatingWindow(title: string, contentComponent: Component, x: number, y: number) {
		return new SingletonFloatingWindow(title, contentComponent, x, y);
	}

	/**
	 * Adds a component to list of known components.
	 *
	 * @param key {ui.UIComponentEnum.UIComponentEnum}
	 *   The ID component will be indexed under.
	 * @param component {ui.toolkit.Component.Component}
	 *   The component to be registered.
	 */
	public registerComponent(key: UIComponentEnum, component: Component) {
		this.wellKnownComponents.set(key, component);
	}

	/**
	 * Removes a component from list of known components.
	 *
	 * @param component {ui.toolkit.Component.Component}
	 *   The component to be unregistered.
	 */
	public unregisterComponent(component: Component) {
		for (let entry of this.wellKnownComponents.entries()) {
			if (entry[1] === component) {
				this.wellKnownComponents.delete(entry[0]);
				return;
			}
		}
	}

	/**
	 * Retrieves a registered component.
	 *
	 * @param key {ui.UIComponentEnum.UIComponentEnum}
	 *   Component ID.
	 * @return {ui.toolkit.Component.Component}
	 *   Component instance or `undefined` if ID isn't registered.
	 */
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
		return UI.displayReady;
	}

	/**
	 * Instructions to execute when display initialized.
	 */
	public onDisplayReady() {
		if (UI.displayReady) {
			console.warn("display state was previously set to \"ready\"");
			return;
		}
		UI.displayReady = true;

		const chatPanel = this.get(UIComponentEnum.BottomPanel)!;
		chatPanel.refresh();
		chatPanel.setVisible(stendhal.config.getBoolean("chat.visible"));
		// initialize on-screen joystick
		SoftwareJoystickController.get().update();
		QuickMenu.init();

		// update menu buttons
		this.onSoundUpdate();
		this.onMenuUpdate();
	}

	/**
	 * Instructions to execute when display attributes are updated.
	 */
	public onDisplayUpdate() {
		if (!UI.displayReady) {
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
		(this.get(UIComponentEnum.QMSound)! as QuickMenuButton).update();
		document.getElementById("soundbutton")!.textContent = stendhal.config.getBoolean("sound")
				? "🔊" : "🔇";
	}

	/**
	 * Instructions to execute when menu style changes.
	 */
	public onMenuUpdate() {
		switch (stendhal.ui.getMenuStyle()) {
			case "traditional":
				document.getElementById("menupanel")!.style["display"] = "";
				QuickMenu.setVisible(false);
				break;
			case "floating":
				document.getElementById("menupanel")!.style["display"] = "none";
				QuickMenu.setVisible(true);
				break;
		}
	}

	/**
	 * Called when the initial `User` instance is constructed.
	 */
	public onUserReady() {
		if (UI.userReady) {
			// do not repeat instructions intended for call only when user is initially constructed
			return;
		}
		UI.userReady = true;

		// continuous movement server attribute is volatile so that initial state is managed by solely by client
		if (stendhal.config.getBoolean("move.cont")) {
			marauroa.clientFramework.sendAction({
				"type": "move.continuous",
				"move.continuous": ""
			});
		}
	}
}

export let ui = new UI();
