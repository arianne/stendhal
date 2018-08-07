/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.actions;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Factory to create all known {@link SlashAction}s that open a specified URL in the browser
 *
 * @author madmetzger
 */
class BareBonesBrowserLaunchCommandsFactory {

	private static Map<String, String> commandsAndUrls;

	private static void initialize() {
		commandsAndUrls = new HashMap<String, String>();
		commandsAndUrls.put("beginnersguide", "https://tzmm.com.cn/wiki/BeginnersGuide");
		commandsAndUrls.put("faq", "https://tzmm.com.cn/wiki/StendhalFAQ");
		commandsAndUrls.put("manual", "https://tzmm.com.cn/wiki/Stendhal_Manual/Controls_and_Game_Settings");
		commandsAndUrls.put("rules", "https://tzmm.com.cn/wiki/Stendhal_Rules");
		commandsAndUrls.put("changepassword", "https://tzmm.com.cn/account/change-password.html");
		commandsAndUrls.put("loginhistory", "https://tzmm.com.cn/account/history.html");
		commandsAndUrls.put("merge", "https://tzmm.com.cn/account/merge.html");
		commandsAndUrls.put("halloffame", "https://tzmm.com.cn/world/hall-of-fame/active_overview.html");
	}

	/**
	 * creates {@link SlashAction}s for all in initialize specified values
	 *
	 * @return map of the created actions
	 */
	static Map<String, SlashAction> createBrowserCommands() {
		initialize();
		Map<String, SlashAction> commandsMap = new HashMap<String, SlashAction>();
		for(Entry<String, String> entry : commandsAndUrls.entrySet()) {
			commandsMap.put(entry.getKey(), new BareBonesBrowserLaunchCommand(entry.getValue()));
		}
		return commandsMap;
	}

}
