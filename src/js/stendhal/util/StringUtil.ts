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


/**
 * String helper functions.
 */
export namespace StringUtil {

	/**
	 * Checks if a string is empty or contains only whitespace characters.
	 *
	 * @param {string} st
	 *   String to be checked.
	 * @returns {boolean}
	 *   `true` if string length excluding whitespace characters is 0.
	 */
	export function isEmpty(st: string): boolean {
		if (!st) {
			return true;
		}
		return st.trim() === "";
	};

	/**
	 * Adds character to left side of string if needed.
	 *
	 * @param {string} st
	 *   String value being modified.
	 * @param {string} c
	 *   Character or set of characters to use for padding.
	 * @param {number} len
	 *   Length of resulting string (if padded).
	 */
	export function padLeft(st: string, c: string, len: number): string {
		if (st.length >= len) {
			// no padding needed
			return st;
		}
		while (st.length < len) {
			st = c + st;
		}
		return st.slice(-len);
	};

	/**
	 * Converts a plain text string to data URL.
	 *
	 * @param text {string}
	 *   Text to be converted.
	 * @param mime {string}
	 *   MIME type (default: "text/plain").
	 * @param charset {string}
	 *   Character set encoding (default: "utf-8").
	 * @return {string}
	 *   Data URL encoded string.
	 */
	export function toDataURL(text: string, mime: string="text/plain", charset: string="utf-8"): string {
		return "data:" + mime + ";charset=" + charset + "," + window.encodeURIComponent(text);
	};
}
