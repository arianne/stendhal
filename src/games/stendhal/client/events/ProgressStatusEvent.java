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

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.entity.RPEntity;
import games.stendhal.client.gui.chatlog.HeaderLessEventLine;
import games.stendhal.common.NotificationType;

import org.apache.log4j.Logger;

/**
 * adjust the quest progress view
 *
 * @author hendrik
 */
public class ProgressStatusEvent extends Event<RPEntity> {
	private static Logger logger = Logger.getLogger(ProgressStatusEvent.class);

	/**
	 * executes the event
	 */
	@Override
	public void execute() {
		try {
			String message = "Current group status:  leader: " 
				+ event.get("leader") 
				+ "; members: " + event.get("members").replace("\t", ", ");
			
			if (!event.has("progress_type")) {
				message = "Open progress window with pages for " + event.getList("data");
			} else if (!event.has("item")) {
				message = "Item list for " + event.get("progress_type") + ": " + event.getList("data");
			} else {
				message = "Details for item " + event.get("item") + " on page " + event.get("progress_type") + ": " + event.getList("data");
			}

			ClientSingletonRepository.getUserInterface().addEventLine(
				new HeaderLessEventLine(message, NotificationType.CLIENT));
		} catch (RuntimeException e) {
			logger.error("Failed to process progress status. Event: " + event, e);
		}
	}

}
