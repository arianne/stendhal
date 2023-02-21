/***************************************************************************
 *                     Copyright © 2003-2023 - Arianne                     *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { ui } from "../UI";
import { ChatLogComponent } from "../component/ChatLogComponent";
import { PlayerEquipmentComponent } from "../component/PlayerEquipmentComponent";
import { PlayerStatsComponent } from "../component/PlayerStatsComponent";
import { ItemInventoryComponent } from "../component/ItemInventoryComponent";
import { DialogContentComponent } from "../toolkit/DialogContentComponent";
import { TravelLogDialog } from "./TravelLogDialog";
import { UIComponentEnum } from "../UIComponentEnum";

declare var marauroa: any;
declare var stendhal: any;


export class SettingsDialog extends DialogContentComponent {

	private initialStates: {[index: string]: string};
	private btn_reload: HTMLButtonElement;


	constructor() {
		super("settingsdialog-template");

		const clog = (ui.get(UIComponentEnum.ChatLog) as ChatLogComponent);

		this.initialStates = {
			"gamescreen.blood": stendhal.config.get("gamescreen.blood"),
		};


		/* *** left panel *** */

		const chk_light = this.createCheckBox("chk_light", "gamescreen.lighting",
				"Lighting effects are enabled", "Lighting effects are disabled")!;
		// FIXME: lighting effects not yet supported
		chk_light.disabled = true;
		chk_light.parentElement!.title = "Lighting effects not currently supported";

		const chk_weather = this.createCheckBox("chk_weather", "gamescreen.weather",
				"Weather is enabled", "Weather is disabled", function() {
					if (clog) {
						clog.addLine("client", "Weather changes will take effect after you change maps.");
					}
				})!;
		chk_weather.parentElement!.title = "Weather effects not currently supported";

		const sd = this;
		this.createCheckBox("chk_blood", "gamescreen.blood",
				"Gory images are enabled", "Gory images are disabled");

		this.createCheckBox("chk_nonude", "gamescreen.nonude",
				"Naked entities have undergarments", "Naked entities are not covered");

		this.createCheckBox("chk_shadows", "gamescreen.shadows",
				"Shadows are enabled", "Shadows are disabled");

		this.createCheckBox("chk_speechcr", "gamescreen.speech.creature",
				"Creature speech bubbles are enabled", "Creature speech bubbles are disabled");

		const chk_hpbar = this.createCheckBox("chk_hpbar", "ui.stats.hpbar",
				undefined, undefined,
				function() {
					(ui.get(UIComponentEnum.PlayerStats) as PlayerStatsComponent)
							.enableBar("hp", chk_hpbar.checked);
				})!;


		/* *** center panel *** */

		this.createCheckBox("chk_dblclick", "action.item.doubleclick",
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
		this.createCheckBox("chk_chestqp", "action.chest.quickpickup",
				"Click tranfers items from chests to player inventory",
				"Click executes default action on items in chests");

		const chk_movecont = this.createCheckBox("chk_movecont", "input.movecont",
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
		const chk_pvtsnd = this.createCheckBox("chk_pvtsnd", "event.pvtmsg.sound",
				"Private message audio notifications enabled",
				"Private message audio notifications disabled",
				undefined, "ui/notify_up", "null");
		chk_pvtsnd.checked = stendhal.config.get("event.pvtmsg.sound") === "ui/notify_up";


		/* *** right panel *** */

		const themes = {} as {[index: string]: string};
		for (const t of Object.keys(stendhal.config.themes.map)) {
			if (t === "wood") {
				themes[t] = t + " (default)";
			} else {
				themes[t] = t;
			}
		}

		const sel_theme = this.createSelect("selecttheme", themes,
				Object.keys(themes).indexOf(stendhal.config.getTheme()));

		sel_theme.addEventListener("change", (o) => {
			stendhal.config.setTheme(Object.keys(themes)[sel_theme.selectedIndex]);
			stendhal.config.refreshTheme();
		});

		/* TODO:
		 *   - create components to change font size, weight, style, etc.
		 */

		const fonts = Object.keys(stendhal.config.fonts);

		const sel_fontbody = this.createFontSelect("selfontbody",
				fonts.indexOf(stendhal.config.get("ui.font.body")));
		sel_fontbody.addEventListener("change", (e) => {
			const new_font = fonts[sel_fontbody.selectedIndex];
			stendhal.config.set("ui.font.body", new_font);
			document.body.style.setProperty("font-family", new_font);
		});

		const sel_fontchat = this.createFontSelect("selfontchat",
				fonts.indexOf(stendhal.config.get("ui.font.chat")));
		sel_fontchat.addEventListener("change", (e) => {
			stendhal.config.set("ui.font.chat", fonts[sel_fontchat.selectedIndex]);
			// make sure component is open before trying to refresh
			if (clog) {
				clog.refresh();
			}
		});

		const sel_fonttlog = this.createFontSelect("selfonttlog",
				fonts.indexOf(stendhal.config.get("ui.font.tlog")))
		sel_fonttlog.addEventListener("change", (e) => {
			stendhal.config.set("ui.font.tlog", fonts[sel_fonttlog.selectedIndex]);
			const tlog = (ui.get(UIComponentEnum.TravelLogDialog) as TravelLogDialog);
			// make sure component is open before trying to refresh
			if (tlog) {
				tlog.refresh();
			}
		});


		/* *** buttons *** */

		this.btn_reload = this.createButton("btn_config_reload",
				"Reloads page if required by changes");
		this.btn_reload.disabled = true;
		this.btn_reload.addEventListener("click", (e: Event) => {
			this.close();
			location.reload();
		});

		const btn_close = this.createButton("btn_config_close",
				"Close this dialog without reloading page");
		btn_close.addEventListener("click", (e: Event) => {
			this.close();
		});

		const button_layout = this.btn_reload.parentElement!;
		button_layout.style.setProperty("padding-top", "15px");
	}

	public override getConfigId(): string {
		return "settings";
	}

	/**
	 * Updates state of reload button.
	 */
	public override refresh() {
		let reloadRequired = false;
		for (const key of Object.keys(this.initialStates)) {
			if (stendhal.config.get(key) !== this.initialStates[key]) {
				reloadRequired = true;
				break;
			}
		}

		this.btn_reload.disabled = !reloadRequired;
	}

	/**
	 * Creates a checkbox element.
	 *
	 * @param id
	 *     Identifier of element to retrieve.
	 * @param tooltip
	 *     Optional popup tooltip text.
	 * @return
	 *     HTMLInputElement.
	 */
	private createCheckBoxSkel(id: string, tooltip?: string): HTMLInputElement {
		const checkbox = <HTMLInputElement> this.child(
			"input[type=checkbox][id=" + id + "]")!;
		if (tooltip) {
			checkbox.title = tooltip;
		}

		return checkbox;
	}

	/**
	 * Creates a fully function checkbox element.
	 *
	 * @param id
	 *     Identifier of element to retrieve.
	 * @param setid
	 *     Identifier of associated configuration setting.
	 * @param ttpos
	 *     Tooltip to display when setting is enabled.
	 * @param ttneg
	 *     Tooltip to display when setting is disabled.
	 * @param action
	 *     Action to execute when state changed.
	 * @param von
	 *     Optional value to set when enabled (<code>null</code> can be
	 *     used).
	 * @param voff
	 *     Optional value to set when disabled (<code>null</code> can be
	 *     used).
	 * @return
	 *     HTMLInputElement.
	 */
	private createCheckBox(id: string, setid: string, ttpos: string="",
			ttneg: string="", action?: Function,
			von?: string, voff?: string): HTMLInputElement {

		const chk = this.createCheckBoxSkel(id)!;
		chk.checked = stendhal.config.getBoolean(setid);
		const tt = new CheckTooltip(ttpos, ttneg);
		chk.parentElement!.title = tt.getValue(chk.checked);
		chk.addEventListener("change", (e) => {
			if (chk.checked && typeof(von) !== "undefined") {
				stendhal.config.set(setid, von);
			} else if (!chk.checked && typeof(voff) !== "undefined") {
				stendhal.config.set(setid, voff);
			} else {
				stendhal.config.set(setid, chk.checked);
			}
			chk.parentElement!.title = tt.getValue(chk.checked);
			if (action) {
				action();
			}
			this.refresh();
		});

		return chk;
	}

	/**
	 * Creates a button element.
	 *
	 * @param id
	 *     Identifier of element to retrieve.
	 * @param tooltip
	 *     Optional popup tooltip text.
	 * @return
	 *     HTMLButtonElement.
	 */
	private createButton(id: string, tooltip?: string): HTMLButtonElement {
		const button = <HTMLButtonElement> this.child(
			"button[id=" + id + "]")!;
		if (tooltip) {
			button.title = tooltip;
		}

		return button;
	}

	/**
	 * Creates a select element.
	 *
	 * @param id
	 *     Identifier of element to retrieve.
	 * @param options
	 *     A {string: string} (identifier: visible text) object for available
	 *     options.
	 * @param idx
	 *     The index to set as selected on construction.
	 * @param tooltip
	 *     Optional popup tooltip text.
	 * @return
	 *     HTMLSelectElement.
	 */
	private createSelect(id: string, options: {[index: string]: string},
			idx: number, tooltip?: string): HTMLSelectElement {

		const sel = <HTMLSelectElement> this.child(
			"select[id=" + id + "]")!;
		sel.style.setProperty("width", "9em");
		sel.parentElement!.style.setProperty("margin-right", "0");
		sel.parentElement!.style.setProperty("margin-left", "auto");
		sel.parentElement!.style.setProperty("padding-bottom", "5px");

		for (const key of Object.keys(options)) {
			const opt = document.createElement("option");
			opt.value = key;
			opt.textContent = options[key];
			sel.appendChild(opt);
		}
		sel.selectedIndex = idx;

		if (tooltip) {
			sel.title = tooltip;
		}

		return sel;
	}

	/**
	 * Creates a select element for registered fonts.
	 *
	 * @param id
	 *     Identifier of element to retrieve.
	 * @param idx
	 *     The index to set as selected on construction.
	 * @param tooltip
	 *     Optional popup tooltip text.
	 * @return
	 *     HTMLSelectElement.
	 */
	private createFontSelect(id: string, idx: number, tooltip?: string): HTMLSelectElement {
		const options = {} as {[index: string]: string};
		for (const key of Object.keys(stendhal.config.fonts)) {
			let value = stendhal.config.fonts[key];
			if (value === "") {
				value = key;
			}
			options[key] = value;
		}

		return this.createSelect(id, options, idx, tooltip);
	}
}


class CheckTooltip {
	private valueEnabled: string;
	private valueDisabled: string;
	constructor(e: string, d: string) {
		this.valueEnabled = e;
		this.valueDisabled = d;
	}
	public getValue(enabled: boolean): string {
		if (enabled) {
			return this.valueEnabled;
		}
		return this.valueDisabled;
	}
}
