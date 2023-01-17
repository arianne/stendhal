/***************************************************************************
 *                    Copyright © 2003-2023 - Arianne                      *
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


public class EmojiEventLine extends EventLine {

	public EmojiEventLine(final String header, final String emojiPath) {
		super(header, emojiPath, NotificationType.EMOJI);
	}
}
