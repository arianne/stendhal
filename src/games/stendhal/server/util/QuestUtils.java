/***************************************************************************
 *                   (C) Copyright 2003-2023 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.util;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class QuestUtils {

	/**
	 * evaluates slot names by replacing variables
	 *
	 * @param name name of slot
	 * @return evaluated slot
	 */
	public static String evaluateQuestSlotName(String name) {
		Map<String, String> params = new HashMap<String, String>();
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		params.put("year", Integer.toString(year).substring(2));
		calendar.add(Calendar.MONTH, -2);
		year = calendar.get(Calendar.YEAR);
		params.put("seasonyear", Integer.toString(year).substring(2));
		return StringUtils.substitute(name, params);
	}

}
