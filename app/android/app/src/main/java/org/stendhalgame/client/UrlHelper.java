/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package org.stendhalgame.client;

import android.net.Uri;


class UrlHelper {

	private UrlHelper() {
		// static methods only
	}

	/**
	 * Creates a URI from a string HTTP URL.
	 *
	 * @return
	 *   `android.net.Uri`.
	 */
	public static Uri toUri(String url) {
		if (!url.startsWith("https://") && !url.startsWith("http://") && !url.startsWith("about:")) {
			// Uri.getHost returns `null` if localhost not prefixed with protocol
			url = "http://" + url;
		}
		return Uri.parse(url);
	}
}
