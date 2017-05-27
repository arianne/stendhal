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
package games.stendhal.server.events;

import java.util.List;

import games.stendhal.common.constants.Events;
import marauroa.common.game.Definition.DefinitionClass;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPEvent;

/**
 * tell the client about the progress of the players for quests or producers
 *
 * @author hendrik
 */
public class ProgressStatusEvent extends RPEvent {

	/**
	 * Creates the rpclass.
	 */
	public static void generateRPClass() {
		final RPClass rpclass = new RPClass(Events.PROGRESS_STATUS_CHANGE);
		rpclass.add(DefinitionClass.ATTRIBUTE, "progress_type", Type.STRING);
		rpclass.add(DefinitionClass.ATTRIBUTE, "item", Type.STRING);
		rpclass.add(DefinitionClass.ATTRIBUTE, "description", Type.VERY_LONG_STRING);
		rpclass.add(DefinitionClass.ATTRIBUTE, "information", Type.VERY_LONG_STRING);
		rpclass.add(DefinitionClass.ATTRIBUTE, "data", Type.VERY_LONG_STRING);
	}

	/**
	 * creates a new ProgressStatusEvent
	 *
	 * @param progressTypes list of progressTypes supported
	 */
	public ProgressStatusEvent(List<String> progressTypes) {
		super(Events.PROGRESS_STATUS_CHANGE);
		put("data", progressTypes);
	}

	/**
	 * creates a new ProgressStatusEvent
	 *
	 * @param type type of status (open_quests, completed_quests, producing, ...)
	 * @param items list of items to display
	 */
	public ProgressStatusEvent(String type, List<String> items) {
		super(Events.PROGRESS_STATUS_CHANGE);
		put("progress_type", type);
		put("data", items);
	}

	/**
	 * creates a new ProgressStatusEvent
	 *
	 * @param type type of status (open_quests, completed_quests, producing, ...)
	 * @param item the selected item
	 * @param description description
	 * @param data details for the selected item
	 */
	public ProgressStatusEvent(String type, String item, String description, List<String> data) {
		super(Events.PROGRESS_STATUS_CHANGE);
		put("progress_type", type);
		put("item", item);
		put("description", description);
		put("data", data);
	}


	/**
	 * creates a new ProgressStatusEvent
	 *
	 * @param type type of status (open_quests, completed_quests, producing, ...)
	 * @param item the selected item
	 * @param description description
	 * @param information information e. g. quest too dangerous
	 * @param data details for the selected item
	 */
	public ProgressStatusEvent(String type, String item, String description, String information, List<String> data) {
		this(type, item, description, data);
		put("information", information);
	}
}
