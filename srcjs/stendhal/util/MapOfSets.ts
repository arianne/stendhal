/***************************************************************************
 *                   (C) Copyright 2022 - Faiumoni e. V.                   *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

/**
 * a map which contains sets
 */
export class MapOfSets<K, V> extends Map<K, Set<V>> {

	/**
	 * adds an entry to the map. If the key is already known, 
	 * the value is added to the associated set. Otherwise
	 * a new set containing the value is created.
	 *
	 * @param key key of the entry
	 * @param value value of the entry
	 */
	public add(key: K, value: V): void {
		let set = this.get(key);
		if (set === undefined) {
			set = new Set();
			this.set(key, set);
		}
		set.add(value);
	}

}
