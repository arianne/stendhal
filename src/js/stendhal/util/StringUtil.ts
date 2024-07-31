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
