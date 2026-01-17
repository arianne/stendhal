/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { marauroa } from "marauroa"

import { AbstractSettingsTab } from "./AbstractSettingsTab";

import { SettingsDialog } from "../SettingsDialog";
import { TravelLogDialog } from "../TravelLogDialog";

import { ChatPanel } from "../../ChatPanel";
import { ui } from "../../UI";
import { UIComponentEnum } from "../../UIComponentEnum";

import { ChatLogComponent } from "../../component/ChatLogComponent";
import { ItemInventoryComponent } from "../../component/ItemInventoryComponent";
import { PlayerEquipmentComponent } from "../../component/PlayerEquipmentComponent";
import { PlayerStatsComponent } from "../../component/PlayerStatsComponent";

import { singletons } from "../../../SingletonRepo";

import { Globals } from "../../../util/Globals";


export class GeneralTab extends AbstractSettingsTab {

	constructor(parent: SettingsDialog, element: HTMLElement) {
		super(element);
		const config = singletons.getConfigManager();
		const chatLog = (ui.get(UIComponentEnum.ChatLog) as ChatLogComponent);

		/* *** left panel *** */

		parent.createCheckBox("chk_speechcr", "speech.creature",
				"Creature speech bubbles are enabled", "Creature speech bubbles are disabled");

		const player_stats = ui.get(UIComponentEnum.PlayerStats) as PlayerStatsComponent;

		const chk_charname = parent.createCheckBox("chk_charname", "panel.stats.charname",
				undefined, undefined,
				function() {
					player_stats.enableCharName(chk_charname.checked);
				})!;

		const chk_hpbar = parent.createCheckBox("chk_hpbar", "panel.stats.hpbar",
				undefined, undefined,
				function() {
					player_stats.enableBar("hp", chk_hpbar.checked);
				})!;

		const chk_floatchat = parent.createCheckBox("chk_floatchat", "chat.float",
				undefined, undefined,
				function() {
					(ui.get(UIComponentEnum.BottomPanel) as ChatPanel).setFloating(chk_floatchat.checked);
				});

		parent.createCheckBox("chk_hidechat", "chat.autohide",
				"Chat panel will be hidden after sending text", "Chat panel will remain on-screen");

		// FIXME: are there unique properties for pinch & tap zooming?
		parent.createCheckBox("chk_zoom", "zoom.touch",
				"Touch zooming enabled (may not work with all browsers)",
				"Touch zooming disabled (may not work with all browsers)",
				function() {
					singletons.getSessionManager().update();
				});


		/* *** center panel *** */

		parent.createCheckBox("chk_dblclick", "inventory.double-click",
				"Items are used/consumed with double click/touch",
				"Items are used/consumed with single click/touch",
				function() {
					// update cursors
					(ui.get(UIComponentEnum.PlayerEquipment) as PlayerEquipmentComponent).markDirty();
					for (const cid of [UIComponentEnum.Bag, UIComponentEnum.Keyring]) {
						(ui.get(cid) as ItemInventoryComponent).markDirty();
					}
				});

		// FIXME: open chest windows are not refreshed
		parent.createCheckBox("chk_chestqp", "inventory.quick-pickup",
				"Click tranfers items from chests and corpses to player inventory",
				"Click executes default action on items in chests and corpses");

		const chk_movecont = parent.createCheckBox("chk_movecont", "move.cont",
				"Player will continue to walk after changing areas",
				"Player will stop after changing areas",
				function() {
					const action = {"type": "move.continuous"} as {[index: string]: string;};
					if (chk_movecont.checked) {
						action["move.continuous"] = "";
					}
					marauroa.clientFramework.sendAction(action);
				})!;

		// TODO: make this multiple choice
		const chk_pvtsnd = parent.createCheckBox("chk_pvtsnd", "chat.private.sound",
				"Private message audio notifications enabled",
				"Private message audio notifications disabled",
				undefined, "ui/notify_up", "null");
		chk_pvtsnd.checked = config.get("chat.private.sound") === "ui/notify_up";

		parent.createCheckBox("chk_nativeemojis", "emojis.native",
				"Using native emojis", "Using built-in emojis",
				function() {
					singletons.getChatInput().refresh();
				});


		/* *** right panel *** */

		const themes = {} as {[index: string]: string};
		for (const t of Object.keys(config.getThemesMap())) {
			if (t === "wood") {
				themes[t] = t + " (default)";
			} else {
				themes[t] = t;
			}
		}

		const sel_theme = parent.createSelect("selecttheme", themes,
				Object.keys(themes).indexOf(config.getTheme()));

		sel_theme.addEventListener("change", (o) => {
			config.setTheme(Object.keys(themes)[sel_theme.selectedIndex]);
			config.refreshTheme();
		});

		/* TODO:
		 *   - create components to change font size, weight, style, etc.
		 */

		const fonts = Object.keys(config.getFontsMap());

		const sel_fontbody = parent.createFontSelect("selfontbody",
				fonts.indexOf(config.get("font.body")!));
		sel_fontbody.addEventListener("change", (e) => {
			const new_font = fonts[sel_fontbody.selectedIndex];
			config.set("font.body", new_font);
			document.body.style.setProperty("font-family", new_font);
		});

		const sel_fontchat = parent.createFontSelect("selfontchat",
				fonts.indexOf(config.get("font.chat")!));
		sel_fontchat.addEventListener("change", (e) => {
			config.set("font.chat", fonts[sel_fontchat.selectedIndex]);
			// make sure component is open before trying to refresh
			if (chatLog) {
				chatLog.refresh();
			}
		});

		const sel_fonttlog = parent.createFontSelect("selfonttlog",
				fonts.indexOf(config.get("font.travel-log")!))
		sel_fonttlog.addEventListener("change", (e) => {
			config.set("font.travel-log", fonts[sel_fonttlog.selectedIndex]);
			const tlog = (ui.get(UIComponentEnum.TravelLogDialog) as TravelLogDialog);
			// make sure component is open before trying to refresh
			if (tlog) {
				tlog.refresh();
			}
		});

		parent.createSelectFromConfig("selmenustyle", "menu.style",
				undefined,
				function(e: Event) {
					ui.onMenuUpdate();
				});
		const menuStyleElement = this.child("#selmenustyle")! as HTMLSelectElement;
		menuStyleElement.selectedIndex = Globals.getMenuStyle() === "traditional" ? 0 : 1;

		// common chat keyword options
		const txt_chatopts = parent.createTextInput("txtchatopts", config.get("chat-opts.custom")!,
				"Comma-separated list accessible from the chat options dialog");
		txt_chatopts.addEventListener("change", (e) => {
			config.set("chat-opts.custom", txt_chatopts.value);
		});
	}
}
