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

type AnimationTuple = [string, number];
type AnimationEntry = Record<string, string|AnimationTuple>;


/**
 * Registry for persistent entity overlay effects.
 *
 * TODO:
 * - move declarations to JSON file if we want support in Java client
 * - support alternate animation for side view (example: necrosophia sprite) or...
 * - scale to height (using scale2x algorithm)
 */
export namespace EntityOverlayRegistry {
	/** Table containing animation definitions. */
	const AnimationTable: Record<string, AnimationEntry> = {
		"NPC": {},

		"creature": {}
	}

	/**
	 * Function for handling retrieval of animation definitions.
	 *
	 * @param {string} type
	 *   Entity type.
	 * @param {string} name
	 *   Entity name.
	 * @returns {AnimationTuple|undefined}
	 *   Animation definition or `undefined` if no definition available.
	 */
	export function get(type: string, name: string): AnimationTuple|undefined {
		const group = AnimationTable[type];
		if (typeof(group) === "undefined") {
			return undefined;
		}
		const aniDef = group[name];
		if (typeof(aniDef) === "string") {
			// use a default delay value if none specified
			return [aniDef as string, 0];
		}
		return aniDef as AnimationTuple;
	}
}
