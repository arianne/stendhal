/***************************************************************************
 *                    Copyright © 2003-2024 - Stendhal                     *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/


/**
 * Static class for data paths.
 */
export class Paths {
	public static readonly data = Paths.extractPath("data-data-path");
	public static readonly font = Paths.data + "/font";
	public static readonly gui = Paths.data + "/gui";
	public static readonly music = Paths.data + "/music";
	public static readonly sounds = Paths.data + "/sounds";
	public static readonly sprites = Paths.data + "/sprites";
	public static readonly weather = Paths.sprites + "/weather";
	public static readonly achievements = Paths.sprites + "/achievements";
	public static readonly tileset = Paths.data + "/maps/tileset";
	public static readonly ws = Paths.extractPath("data-ws");

	/**
	 * Extracts the path information from DOM.
	 *
	 * @return {string}
	 *   Path determining data root.
	 */
	private static extractPath(ref: string): string {
		let path = document.getElementsByTagName("html")[0].getAttribute(ref);

		// make sure that there is no javascript:// or similary shinanigans
		if (!path || !path.startsWith("/")) {
			throw new Error("Path reference " + ref + " is not a relative path.");
		}
		return path;
	}

	/**
	 * Static members & methods only.
	 */
	private constructor() {
		// do nothing
	}
}
