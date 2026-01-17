/***************************************************************************
 *                    Copyright © 2003-2023 - Stendhal                     *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Entity } from "./Entity";
import { singletons } from "../SingletonRepo";
import { MenuItem } from "../action/MenuItem";

const config = singletons.getConfigManager();

import { marauroa } from "marauroa"

const defaultUse = {
	title: "Use",
	type: "use",
	index: 0
}


export const ItemMap: {[index: string]: any} = {
	["class"]: {
		["box"]: {
			cursor: "bag",
			actions: [
				{
					title: "Open",
					type: "use",
					index: 0
				}
			]
		},
		["drink"]: {
			cursor: "itemuse"
		},
		["food"]: {
			cursor: "itemuse"
		},
		["scroll"]: {
			cursor: "itemuse"
		}
	},

	["name"]: {
		["ashen holy water"]: {
			actions: [defaultUse]
		},
		["bestiary"]: {
			cursor: "itemuse"
		},
		["bulb"]: {
			cursor: "itemuse"
		},
		["empty scroll"]: {
			actions: function(e: Entity) {
				const count = parseInt(e["quantity"], 10);
				if (count > 1 && e._parent) {
					return [{
						title: "Mark all",
						index: 1,
						action: function(entity: Entity) {
							// FIXME: doesn't work if scrolls are on ground
							//        tries to pull scrolls from inventory
							marauroa.clientFramework.sendAction(
								{
									"type": "markscroll",
									"quantity": ""+count
								});
						}
					}];
				}
			}
		},
		["food mill"]: {
			cursor: "itemuse"
		},
		["metal detector"]: {
			cursor: "itemuse"
		},
		["picture in wooden frame"]: {
			cursor: "itemuse"
		},
		["rotary cutter"]: {
			cursor: "itemuse"
		},
		["scroll eraser"]: {
			cursor: "itemuse"
		},
		["seed"]: {
			cursor: "itemuse"
		},
		["snowglobe"]: {
			cursor: "itemuse"
		},
		["sugar mill"]: {
			cursor: "itemuse"
		},
		["teddy"]: {
			cursor: "itemuse"
		},
		["wedding ring"]: {
			cursor: "itemuse",
			actions: [defaultUse]
		}
	},

	/**
	 * Retrieves cursor registered for item.
	 *
	 * @param clazz
	 *     Item class.
	 * @param name
	 *     Item name.
	 * @return
	 *     Cursor name.
	 */
	getCursor: function(clazz: string, name: string) {
		let cursor = "normal";
		for (const imap of [ItemMap["class"][clazz], ItemMap["name"][name]]) {
			if (imap && imap.cursor) {
				cursor = imap.cursor;
			}
		}
		if (cursor === "itemuse" && !config.getBoolean("inventory.double-click")) {
			cursor = "activity";
		}
		return cursor;
	},

	/**
	 * Retrieves list of menu actions defined for item.
	 *
	 * @param item
	 *     Object containing item defintion.
	 * @return
	 *     Actions.
	 */
	getActions: function(item: any): MenuItem[] {
		let actions: MenuItem[] = [];
		if (!item) {
			return actions;
		}

		for (const imap of [ItemMap["class"][item["class"]], ItemMap["name"][item["name"]]]) {
			if (imap && imap.actions) {
				let a: MenuItem[];
				if (typeof(imap.actions) === "function") {
					a = imap.actions(item) || [];
				} else {
					a = imap.actions;
				}
				actions = actions.concat(a);
			}
		}
		return actions;
	}
};
