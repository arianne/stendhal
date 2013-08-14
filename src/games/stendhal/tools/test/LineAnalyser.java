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
package games.stendhal.tools.test;

import java.util.Arrays;
import java.util.List;

/**
 * Analyzes a chat log line.
 *
 * @author hendrik
 */
class LineAnalyser {
	private final List<String> playerNames = Arrays.asList("hendrikus", "player",
			"rosie", "gambit", "superkym", "Heman", "jellybean", "jammyjam", "lula", "kymara", "madmetzger",
			"omerob", "nanometer", "bluelads", "krupi", "pinch", "silvia");

	private final String line;
	private String stripped;
	private String protagonist;
	private boolean comment = false;

	protected LineAnalyser(final String line) {
		this.line = line.trim();
		stripTimeStamp();
		stripComment();
		extractProtagonist();
	}

	private void stripTimeStamp() {
		stripped = line;
		final int pos = stripped.indexOf(']');
		if (pos < 0) {
			return;
		}
		stripped = stripped.substring(pos + 2);
	}

	private void stripComment() {
		if (line.startsWith("//")) {
			comment = true;
			stripped = line.substring(2).trim();
		}
	}

	private void extractProtagonist() {
		if (comment) {
			return;
		}
		final int posStart = stripped.indexOf('<');
		final int posEnd = stripped.indexOf('>');
		if ((posEnd < posStart) || (posStart < 0)) {
			return;
		}
		protagonist = stripped.substring(posStart + 1, posEnd);
	}

	protected String getLine() {
		return line;
	}

	protected String getProtagonist() {
		return protagonist;
	}

	protected String getStripped() {
		return stripped;
	}

	public String getText() {
		if (protagonist == null) {
			return stripped;
		}
		return line.substring(line.indexOf("> ") + 1).trim();
	}

	public boolean isEmpty() {
		return line.trim().equals("");
	}

	public boolean isComment() {
		return comment;
	}

	public boolean isNPCSpeaking() {
		return (protagonist != null) && !playerNames.contains(protagonist);
	}

	public boolean isPlayerSpeaking() {
		return (protagonist != null) && playerNames.contains(protagonist);
	}

	public boolean isStatus() {
		return !isEmpty() && (protagonist == null);
	}
}
