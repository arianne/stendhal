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

import { InvisibleEntity } from "./InvisibleEntity";

import { LoopedSoundSourceManager } from "../ui/LoopedSoundSourceManager";


export class LoopedSoundSource extends InvisibleEntity {

	private readonly lssMan = LoopedSoundSourceManager.get();


	/**
	 * Checks if a looped sound source has been loaded with this
	 * entity's ID.
	 *
	 * @return
	 *     <code>true</code> if the ID is found in the sources list.
	 */
	isLoaded(): boolean {
		return typeof(this.lssMan.getSources()[this["id"]]) !== "undefined";
	}
}
