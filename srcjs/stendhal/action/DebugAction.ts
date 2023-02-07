/***************************************************************************
 *                   (C) Copyright 2022-2023 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { singletons } from "../SingletonRepo";

import { ui } from "../ui/UI";
import { UIComponentEnum } from "../ui/UIComponentEnum";
import { FloatingWindow } from "../ui/toolkit/FloatingWindow";

import { SlashAction } from "./SlashAction";
import { Chat } from "../util/Chat";
import { ShowFloatingWindowComponent } from "../ui/component/ShowFloatingWindowComponent";
import { Panel } from "ui/toolkit/Panel";

declare var marauroa: any;
declare var stendhal: any;

/**
 * performances debugging actions
 */
export class DebugAction extends SlashAction {
	readonly minParams = 0;
	readonly maxParams = 0;
	private uiPopped = false;

	execute(_type: string, params: string[], _remainder: string): boolean {
		if (params.length < 1) {
			Chat.logH("error", "Expected parameters")
			this.showUsage();
		} else if (["help", "?"].indexOf(params[0]) > -1) {
			this.showUsage();
		} else if (params[0] === "ui") {
			this.uiAction(params);
		} else if (params[0] === "weather") {
			this.debugWeather(params[1]);
		}
		return true;
	}

	showUsage() {
		const usage = [
			"Usage:",
			"  /debug ui [pop]",
			"  /debug weather [<name>]"
		];
		Chat.log("client", usage);
	}

	uiAction(params: string[]) {
		console.log(UIComponentEnum);
		for (let i in UIComponentEnum) {
			console.log("in", i);
		}
		if (params[1] === "pop") {
			this.uiPop();
		}
	}

	uiPop() {
		this.uiFloatComponent(UIComponentEnum.LeftPanel, "Left panel", 10, 10);
		this.uiFloatComponent(UIComponentEnum.RightPanel, "Right panel", 500, 10);
		this.uiFloatComponent(UIComponentEnum.TopPanel, "Top panel", 200, 10);
		this.uiFloatComponent(UIComponentEnum.BottomPanel, "Bottom panel", 10, 500);

		this.uiFloatComponent(UIComponentEnum.MiniMap, "Map", 10, 50);
		this.uiFloatComponent(UIComponentEnum.PlayerStats, "Stats", 10, 190);
		this.uiFloatComponent(UIComponentEnum.BuddyList, "Buddies", 10, 250);

		this.uiFloatComponent(UIComponentEnum.PlayerEquipment, marauroa.me["_name"], 500, 50);
		this.uiFloatComponent(UIComponentEnum.Bag, "Bag", 500, 200);
		this.uiFloatComponent(UIComponentEnum.Keyring, "Keyring", 500, 300);
		this.uiFloatComponent(UIComponentEnum.ChatInput, "Chat", 100, 500);
		this.uiFloatComponent(UIComponentEnum.ChatLog, "Chat log", 100, 560);

		this.uiPopped = true;
	}


	uiFloatComponent(uiComponentEnum: UIComponentEnum, title: string, x: number, y: number) {
		let component = ui.get(uiComponentEnum);
		if (!component) {
			return;
		}
		component.componentElement.dispatchEvent(new Event("close"));
		component.componentElement.remove();
		new FloatingWindow(title, component, x, y);

		if (!this.uiPopped) {
			let bottomPannel = ui.get(UIComponentEnum.BottomPanel) as Panel;
			bottomPannel.add(new ShowFloatingWindowComponent(uiComponentEnum, title, x, y))
		}
	}

	/**
	 * Sets weather animation for debugging.
	 *
	 * @param weather
	 *     Name of weather to be loaded. <code>undefined</code> turns
	 *     weather animation off.
	 */
	private debugWeather(weather?: string) {
		const usage = ["Usage:", "  /debug weather [<name>]"];
		if (weather && ["help", "?"].indexOf(weather) > -1) {
			Chat.log("client", usage);
			return;
		}
		if (!stendhal.config.getBoolean("gamescreen.weather")) {
			Chat.logH("warning", "Weather is disabled.");
		}

		if (weather) {
			weather = weather.replace(/ /g, "_");
			const wfilename = stendhal.paths.weather + "/" + weather + ".png";
			if (!stendhal.data.sprites.getCached(wfilename)) {
				Chat.logH("error", "unknown weather: " + wfilename);
				return;
			}
		}

		singletons.getWeatherRenderer().update(weather);
	}

};
