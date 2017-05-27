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
package games.stendhal.client.events;

import org.apache.log4j.Logger;

import games.stendhal.client.entity.RPEntity;
import games.stendhal.client.gui.progress.ProgressLogController;

/**
 * adjust the quest progress view
 *
 * @author hendrik
 */
class ProgressStatusEvent extends Event<RPEntity> {
	private static Logger logger = Logger.getLogger(ProgressStatusEvent.class);

	/**
	 * executes the event
	 */
	@Override
	public void execute() {
		try {
			if (!event.has("progress_type")) {
				ProgressLogController.get().showCategories(event.getList("data"));
			} else if (!event.has("item")) {
				if (event.get("progress_type").equals("repeatable")) {
					ProgressLogController.get().setRepeatable(event.getList("data"));
				} else {
					ProgressLogController.get().showCategorySummary(event.get("progress_type"), event.getList("data"));
				}
			} else {
				ProgressLogController.get().showDescription(event.get("progress_type"), event.get("item"), event.get("description"), event.get("information"), event.getList("data"));
			}
		} catch (RuntimeException e) {
			logger.error("Failed to process progress status. Event: " + event, e);
		}
	}

}
