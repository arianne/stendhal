/***************************************************************************
 *                     Copyright © 2020 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.events;

import java.util.ArrayList;
import java.util.List;


/**
 * XXX: hack to manually register LogoutListener
 */
public class LogoutListenerFactory {

	private static LogoutListenerFactory instance;

	private static final List<LogoutListener> listeners = new ArrayList<>();


	public static LogoutListenerFactory get() {
		if (instance == null) {
			instance = new LogoutListenerFactory();
		}

		return instance;
	}

	public void register(final LogoutListener listener) {
		listeners.add(listener);
	}

	public List<LogoutListener> getListeners() {
		return listeners;
	}
}
