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

import { ui} from "../UI";
import { UIComponentEnum } from "../UIComponentEnum";
import { Component } from "../toolkit/Component";
import { ChatInputComponent } from "../component/ChatInputComponent";

import { marauroa } from "marauroa"
import { stendhal } from "../../stendhal";

export class ActionContextMenu extends Component {
	private entity: any;
	private actions!: any[];
	private appendActions: any[];

	constructor(entity: any, append: any[] = []) {
		super("contextmenu-template", true);
		this.entity = entity;
		this.appendActions = append;
		this.gatherActions();

		var content = "<div class=\"actionmenu verticalgroup\">";
		for (var i = 0; i < this.actions.length; i++) {
			// FIXME: the addition of "sub-actionbutton" class should no longer be needed
			content += "<button class=\"actionbutton" + (i > 0 ? " sub-actionbutton" : "") + "\" id=\"actionbutton." + i + "\">" + stendhal.ui.html.esc(this.actions[i].title) + "</button>";
		}
		content += "</div>";
		this.componentElement.innerHTML = content;

		this.componentElement.addEventListener("click", (event) => {
			this.onClick(event);
		});
	}

	private onClick(event: Event) {
		let iStr = (event.target as HTMLElement).getAttribute("id")?.substring(13);
		if (iStr === undefined || iStr === "") {
			return;
		}
		let i = parseInt(iStr);
		if (i < 0) {
			return;
		}

		this.componentElement.dispatchEvent(new Event("close"));

		if (i >= this.actions.length) {
			throw new Error("actions index is larger than number of actions");
		}

		if (this.actions[i].action) {
			this.actions[i].action(this.entity);
		} else {
			this.executeFallbackAction(this.actions[i].type)
		}
	}

	private executeFallbackAction(type: string) {
		let action: any = {
			"type": type,
			"target_path": this.entity.getIdPath(),
			"zone": marauroa.currentZoneName
		};
		// for top level entities, include "target", which is required for example on attack-action
		if ('[' + this.entity.id + ']' === this.entity.getIdPath()) {
			action['target'] = '#' + this.entity.id;
		}
		marauroa.clientFramework.sendAction(action);

	}

	private gatherActions(){
		let actions: any[] = [];
		this.entity.buildActions(actions);
		for (const action of this.appendActions) {
			actions.push(action);
		}
		if (marauroa.me["adminlevel"] && marauroa.me["adminlevel"] >= 600) {
			actions.push({
				title: "(*) Inspect",
				action: function(entity: any) {
					const action = {"type": "inspect"} as {[key: string]: string};
					if (entity.hasOwnProperty("id")) {
						action["target"] = "#" + entity["id"];
					}

					marauroa.clientFramework.sendAction(action);
				}
			});
			actions.push({
				title: "(*) Destroy",
				action: function(entity: any) {
					var action = {
						"type": "destroy",
					} as {[key: string]: string};

					if (entity.isContained()) {
						action["baseobject"] = marauroa.me["id"];
						action["baseslot"] = entity.getContainer()._name;
						action["baseitem"] = entity["id"];
					} else {
						action["target"] = "#" + entity["id"];
					}

					marauroa.clientFramework.sendAction(action);
				}
			});
			actions.push({
				title: "(*) Alter",
				action: function(entity: any) {
					(ui.get(UIComponentEnum.ChatInput) as ChatInputComponent).setText("/alter #"
							+ entity["id"]
							+ " ");
				}
			});
		}
		this.actions = actions;
	}

}
