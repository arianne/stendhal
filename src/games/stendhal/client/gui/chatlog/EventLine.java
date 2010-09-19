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
package games.stendhal.client.gui.chatlog;

import games.stendhal.common.NotificationType;

public class EventLine {
	private String header;
	private String text;
	private NotificationType type;

	public EventLine(final String header, final String text, final NotificationType type) {
		this.header = header;
		this.text = text;
		this.type = type;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(final String header) {
		this.header = header;
	}

	public String getText() {
		return text;
	}

	public void setText(final String text) {
		this.text = text;
	}

	public NotificationType getType() {
		return type;
	}

	public void setType(final NotificationType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(type);
		sb.append(": ");
		if (header.length() > 0) {
			sb.append(header);
			sb.append(": ");
		}
		sb.append(text);
		return sb.toString();
	}
}
