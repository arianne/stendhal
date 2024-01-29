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


class AppInfo {

	public static String getBuildType() {
		return MainActivity.get().getResources().getString(R.string.build_type);
	}

	public static String getBuildVersion() {
		return MainActivity.get().getResources().getString(R.string.build_version);
	}
}
