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
import { MenuItem } from "../action/MenuItem";
import singletons from "../util/SingletonRepo";

const config = singletons.getConfigManager();

declare var marauroa: any;


/**
 * Retrieves cursor to be shown for "use" action.
 *
 * @return
 *     Dependent on "action.item.doubleclick" config setting.
 */
function getItemUseCursor() {
	if (config.getBoolean("action.item.doubleclick")) {
		return "itemuse";
	}
	return "activity";
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
			cursor: getItemUseCursor
		},
		["food"]: {
			cursor: getItemUseCursor
		},
		["scroll"]: {
			cursor: getItemUseCursor
		}
	},

	["name"]: {
		["bestiary"]: {
			cursor: getItemUseCursor
		},
		["bulb"]: {
			cursor: getItemUseCursor
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
			cursor: getItemUseCursor
		},
		["metal detector"]: {
			cursor: getItemUseCursor
		},
		["rotary cutter"]: {
			cursor: getItemUseCursor
		},
		["scroll eraser"]: {
			cursor: getItemUseCursor
		},
		["seed"]: {
			cursor: getItemUseCursor
		},
		["snowglobe"]: {
			cursor: getItemUseCursor
		},
		["sugar mill"]: {
			cursor: getItemUseCursor
		},
		["teddy"]: {
			cursor: getItemUseCursor
		},
		["wedding ring"]: {
			cursor: getItemUseCursor
		}
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

		for (const imap of [ItemMap["name"][item["name"]], ItemMap["class"][item["class"]]]) {
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
