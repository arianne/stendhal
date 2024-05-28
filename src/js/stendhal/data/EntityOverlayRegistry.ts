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

declare var stendhal: any;

import { Entity } from "../entity/Entity";

import { DirectionalSkillEffect } from "../sprite/action/DirectionalSkillEffect";
import { SkillEffect } from "../sprite/action/SkillEffect";

type AnimationTuple = [string, number];
type AnimationEntry = Record<string, string|AnimationTuple>;


/**
 * Registry for persistent entity overlay effects.
 *
 * TODO:
 * - move declarations to JSON file if we want support in Java client
 * - better animation for electricity
 */
export namespace EntityOverlayRegistry {
	/** Table containing animation definitions. */
	const AnimationTable: Record<string, AnimationEntry> = {
		"NPC": {
			"Tywysoga": "magic_sparkles"
		},

		"creature": {
			"albino elf magician": "beams",
			"archrat": "flame",
			"dark elf master": "electricity_violet",
			"dark elf viceroy": "electricity_blue",
			"emperor dalmung": "flame_blue",
			"ice golem": "ice_sparkles",
			"imperial demon lord": "electricity_red"
		}
	}

	/** Entities with direction dependent animation. */
	const directional = [];

	/**
	 * Function for handling retrieval of animation definitions.
	 *
	 * @param {string} type
	 *   Entity type.
	 * @param {Entity} entity
	 *   Entity with which the overlay is associated.
	 * @returns {SkillEffect|undefined}
	 *   Overlay effect or `undefined` if no effect available.
	 */
	export function get(type: string, entity: Entity): SkillEffect|undefined {
		if (!stendhal.config.getBoolean("effect.entity-overlay")) {
			return undefined;
		}
		const group = AnimationTable[type];
		if (!group) {
			return undefined;
		}
		const name = entity["name"];
		let aniDef: string|AnimationTuple = group[name];
		if (!aniDef) {
			return undefined;
		}
		if (typeof(aniDef) === "string") {
			// use a default delay value if none specified
			aniDef = [aniDef as string, 0];
		}
		if (directional.indexOf(name) > -1) {
			return new DirectionalSkillEffect(entity, aniDef[0], aniDef[1]);
		}
		return new SkillEffect(aniDef[0], aniDef[1]);
	}
}
