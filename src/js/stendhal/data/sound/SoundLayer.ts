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

import { AbstractEnum } from "../enum/AbstractEnum";


/**
 * Available layers of sound.
 *
 * string names should match `games.stendhal.common.constants.SoundLayer`
 */
export class SoundLayer extends AbstractEnum<string> {

	/** Reference to layers. */
	private static readonly layers: SoundLayer[] = [];

	static readonly MUSIC = new SoundLayer("music");
	static readonly AMBIENT = new SoundLayer("ambient");
	static readonly CREATURE = new SoundLayer("creature");
	static readonly SFX = new SoundLayer("sfx");
	static readonly GUI = new SoundLayer("gui");


	private constructor(value: string) {
		super(value);
		SoundLayer.layers.push(this);
	}

	/**
	 * Retrieves all layer names.
	 *
	 * @return {string[]}
	 *   Names of available layers.
	 */
	static names(): string[] {
		const names: string[] = [];
		for (const layer of SoundLayer.layers) {
			names.push(layer.value!);
		}
		return names;
	}

	/**
	 * Retrieves sound layer corresponding to layer name.
	 *
	 * @param name {string}
	 *   Layer name.
	 * @return {data.sound.SoundLayer.SoundLayer}
	 *   Sound layer matching name or `undefined`.
	 */
	static checkLayerName(name: string): SoundLayer|undefined {
		for (const layer of SoundLayer.layers) {
			if (layer.value === name) {
				return layer;
			}
		}
		return undefined;
	}
}
