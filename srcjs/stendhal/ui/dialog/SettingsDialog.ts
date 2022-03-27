/***************************************************************************
 *                     Copyright © 2003-2022 - Arianne                     *
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
import { DialogContentComponent } from "../component/DialogContentComponent";
import { TravelLogDialog } from "./TravelLogDialog";
import { UIComponentEnum } from "../UIComponentEnum";

declare var marauroa: any;
declare var stendhal: any;


export class SettingsDialog extends DialogContentComponent {

	private reloadRequired = false;


	constructor() {
		super("settingsdialog-template");

		const chk_blood = this.createCheckBox("chk_blood")!;
		chk_blood.checked = stendhal.config.getBoolean("gamescreen.blood");
		const tt_blood = new CheckTooltip("Gory images are enabled",
				"Gory images are disabled");
		chk_blood.parentElement!.title = tt_blood.getValue(chk_blood.checked);
		chk_blood.addEventListener("change", (e) => {
			stendhal.config.set("gamescreen.blood", chk_blood.checked);
			chk_blood.parentElement!.title = tt_blood.getValue(chk_blood.checked);
			this.reloadRequired = true; // only required to immediately update corpses & tiles
		});

		const chk_nonude = this.createCheckBox("chk_nonude")!;
		chk_nonude.checked = stendhal.config.getBoolean("gamescreen.nonude");
		const tt_nonude = new CheckTooltip("Naked entities have undergarments",
				"Naked entities are not covered");
		chk_nonude.parentElement!.title = tt_nonude.getValue(chk_nonude.checked);
		chk_nonude.addEventListener("change", (e) => {
			stendhal.config.set("gamescreen.nonude", chk_nonude.checked);
			chk_nonude.parentElement!.title = tt_nonude.getValue(chk_nonude.checked);
		});

		const chk_shadows = this.createCheckBox("chk_shadows")!;
		chk_shadows.checked = stendhal.config.getBoolean("gamescreen.shadows");
		const tt_shadows = new CheckTooltip("Shadows are enabled",
				"Shadows are disabled");
		chk_shadows.parentElement!.title = tt_shadows.getValue(chk_shadows.checked);
		chk_shadows.addEventListener("change", (e) => {
			stendhal.config.set("gamescreen.shadows", chk_shadows.checked);
			chk_shadows.parentElement!.title = tt_shadows.getValue(chk_shadows.checked);
		});

		const chk_dblclick = this.createCheckBox("chk_dblclick")!;
		chk_dblclick.checked = stendhal.config.getBoolean("input.item_doubleclick");
		const tt_dblclick = new CheckTooltip("Items are used/consumed with double click/touch",
				"Items are used/consumed with single click/touch");
		chk_dblclick.parentElement!.title = tt_dblclick.getValue(chk_dblclick.checked);
		chk_dblclick.addEventListener("change", (e) => {
			stendhal.config.set("input.item_doubleclick", chk_dblclick.checked);
			chk_dblclick.parentElement!.title = tt_dblclick.getValue(chk_dblclick.checked);
		});

		const chk_movecont = this.createCheckBox("chk_movecont")!;
		chk_movecont.checked = stendhal.config.getBoolean("input.movecont");
		const tt_movecont = new CheckTooltip("Player will continue to walk after changing areas",
				"Player will stop after changing areas");
		chk_movecont.parentElement!.title = tt_movecont.getValue(chk_movecont.checked);
		chk_movecont.addEventListener("change", (e) => {
			stendhal.config.set("input.movecont", chk_movecont.checked);
			chk_movecont.parentElement!.title = tt_movecont.getValue(chk_movecont.checked);

			const action = {"type": "move.continuous"} as {[index: string]: string;};
			if (chk_movecont.checked) {
				action["move.continuous"] = "";
			}
			marauroa.clientFramework.sendAction(action);
		});

		const themes = {} as {[index: string]: string};
		for (const t of Object.keys(stendhal.config.themes.map)) {
			if (t === "wood") {
				themes[t] = t + " (default)";
			} else {
				themes[t] = t;
			}
		}

		const sel_theme = this.createFontSelect("selecttheme", themes,
				Object.keys(themes).indexOf(stendhal.config.getTheme()));

		sel_theme.addEventListener("change", (o) => {
			stendhal.config.setTheme(Object.keys(themes)[sel_theme.selectedIndex]);
			this.reloadRequired = true;
		});

		/* TODO:
		 *   - create components to change font size, weight, style, etc.
		 */
		const fonts = {
			"sans-serif": "system default",
			"serif": "system default (serif)"
		} as {[index: string]: string};
		for (const f of ["Amaranth", "Black Chancery", "Carlito"]) {
			fonts[f] = f;
		}

		const sel_fontbody = this.createFontSelect("selfontbody", fonts,
				Object.keys(fonts).indexOf(stendhal.config.get("ui.font.body")));
		sel_fontbody.addEventListener("change", (e) => {
			const new_font = Object.keys(fonts)[sel_fontbody.selectedIndex];
			stendhal.config.set("ui.font.body", new_font);
			document.body.style.setProperty("font-family", new_font);
		});

		const sel_fontchat = this.createFontSelect("selfontchat", fonts,
				Object.keys(fonts).indexOf(stendhal.config.get("ui.font.chat")));
		sel_fontchat.addEventListener("change", (e) => {
			stendhal.config.set("ui.font.chat", Object.keys(fonts)[sel_fontchat.selectedIndex]);
			(ui.get(UIComponentEnum.ChatLog) as ChatLogComponent).refresh();
		});

		const sel_fonttlog = this.createFontSelect("selfonttlog", fonts,
				Object.keys(fonts).indexOf(stendhal.config.get("ui.font.tlog")))
		sel_fonttlog.addEventListener("change", (e) => {
			stendhal.config.set("ui.font.tlog", Object.keys(fonts)[sel_fonttlog.selectedIndex]);
			(ui.get(UIComponentEnum.TravelLogDialog) as TravelLogDialog).refresh();
		});


		/* *** buttons *** */

		const btn_reload = this.createButton("btn_config_reload",
				"Reloads page if required by changes");
		btn_reload.addEventListener("click", (e: Event) => {
			this.close();
			if (this.reloadRequired) {
				location.reload();
			}
		});

		const btn_close = this.createButton("btn_config_close",
				"Close settings without reloading page");
		btn_close.addEventListener("click", (e: Event) => {
			this.close();
		});

		const button_layout = btn_reload.parentElement!;
		button_layout.style.setProperty("padding-top", "15px");
	}

	public override getConfigId(): string {
		return "settings";
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
	private createCheckBox(id: string, tooltip?: string): HTMLInputElement {
		const checkbox = <HTMLInputElement> this.componentElement.querySelector(
			"input[type=checkbox][id=" + id + "]")!;
		if (tooltip) {
			checkbox.title = tooltip;
		}

		return checkbox;
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
		const button = <HTMLButtonElement> this.componentElement.querySelector(
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
	private createFontSelect(id: string, options: {[index: string]: string},
			idx: number, tooltip?: string): HTMLSelectElement {

		const sel = <HTMLSelectElement> this.componentElement.querySelector(
			"select[id=" + id + "]")!;
		sel.style.setProperty("width", "9em");
		sel.parentElement!.style.setProperty("margin-right", "0");
		sel.parentElement!.style.setProperty("margin-left", "auto");
		sel.parentElement!.style.setProperty("padding-bottom", "5px");

		for (const key of Object.keys(options)) {
			const opt = document.createElement("option");
			opt.value = key;
			opt.innerHTML = options[key];
			sel.appendChild(opt);
		}
		sel.selectedIndex = idx;

		if (tooltip) {
			sel.title = tooltip;
		}

		return sel;
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
