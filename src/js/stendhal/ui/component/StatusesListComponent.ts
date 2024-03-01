/***************************************************************************
 *                       Copyright © 2023 - Arianne                        *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Component } from "../toolkit/Component";

import { User } from "../../entity/User";

declare var stendhal: any;


export class StatusesListComponent extends Component {

	private static readonly iconPaths: {[index: string]: string} = {
		"choking": "/ideas/choking.png",
		"eating": "/ideas/eat.png"
	};

	// list of active statuse IDs
	private active: string[];

	constructor() {
		super("statuses-list");
		this.active = [];
	}

	public update(user: User) {
		const changes: string[] = [];
		for (const key of Object.keys(user)) {
			if (Object.keys(StatusesListComponent.iconPaths).indexOf(key) !== -1) {
				changes.push(key);
			} else if (key === "poisoned") {
				changes.push("poison");
			} else if (key.startsWith("status_")) {
				const id = key.substring(key.indexOf("_") + 1);
				if (id !== "") {
					changes.push(id);
				}
			}
		}

		if (this.changed(changes)) {
			this.active = changes;
			this.updateInternal();
		}
	}

	private updateInternal() {
		for (const icon of Array.from(this.componentElement.children)) {
			// remove old icons
			this.componentElement.removeChild(icon);
		}
		const choking = this.active.indexOf("choking") !== -1;
		for (const id of this.active) {
			if (id === "eating" && choking) {
				// don't show "eating" & "choking" at the same time
				continue;
			}
			let iconPath: string;
			if (Object.keys(StatusesListComponent.iconPaths).indexOf(id) !== -1) {
				iconPath = stendhal.paths.sprites + StatusesListComponent.iconPaths[id];
			} else {
				iconPath = stendhal.paths.sprites + "/status/panel/" + id + ".png";
			}
			const icon = stendhal.data.sprites.get(iconPath);
			icon.className = "status-icon";
			this.componentElement.appendChild(icon);
		}
	}

	private changed(changes: string[]): boolean {
		if (changes.length != this.active.length) {
			return true;
		}
		for (let idx = 0; idx < changes.length; idx++) {
			if (changes[idx] !== this.active[idx]) {
				return true;
			}
		}
		return false;
	}
}
