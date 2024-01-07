/***************************************************************************
 *                     Copyright © 2022 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package org.arianne.stendhal.client.js;

import android.webkit.JavascriptInterface;



public class JSInterface {

	/** Singleton instance. */
	private static JSInterface instance;


	/**
	 * Retrieves the singleton instance.
	 */
	public static JSInterface get() {
		if (instance == null) {
			instance = new JSInterface();
		}
		return instance;
	}

	private JSInterface() {
		// singleton
	}
}
