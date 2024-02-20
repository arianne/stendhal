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


/**
 * App configuration information.
 */
class AppInfo {

	/**
	 * Retrieves configured build type string.
	 */
	public static String getBuildType() {
		return MainActivity.get().getResources().getString(R.string.build_type);
	}

	/**
	 * Retrieves configured build version string.
	 */
	public static String getBuildVersion() {
		return MainActivity.get().getResources().getString(R.string.build_version);
	}

	/**
	 * Retrieves configured intent URL scheme string.
	 */
	public static String getIntentUrlScheme() {
		return MainActivity.get().getResources().getString(R.string.intent_url_scheme);
	}
}
