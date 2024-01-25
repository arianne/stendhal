/***************************************************************************
 *                 Copyright © 2022-2024 - Faiumoni e. V.                  *
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


public class WebClientInfo {

	/** Singleton instance. */
	private static WebClientInfo instance;


	/**
	 * Retrieves the singleton instance.
	 */
	public static WebClientInfo get() {
		if (WebClientInfo.instance == null) {
			WebClientInfo.instance = new WebClientInfo();
		}
		return WebClientInfo.instance;
	}

	private WebClientInfo() {
		// singleton
	}
}
