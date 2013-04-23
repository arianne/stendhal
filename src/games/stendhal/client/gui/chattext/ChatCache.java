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
package games.stendhal.client.gui.chattext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;

class ChatCache {
	private final static Logger logger = Logger.getLogger(ChatCache.class);

	private final String chatCacheFile;
	private int current;

	ChatCache(final String chatLogFile) {
		this.chatCacheFile = chatLogFile;
	}

	public LinkedList<String> getLines() {
		return lines;
	}

	private final LinkedList<String> lines = new LinkedList<String>();

	void loadChatCache() {
		if (chatCacheFile == null) {
			return;
		}
		try {
			final File chatfile = new File(chatCacheFile);

			if (chatfile.exists()) {
				final FileInputStream fis = new FileInputStream(chatfile);
				final BufferedReader br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));

				try {
					String line = null;
					while (null != (line = br.readLine())) {
						lines.add(line);
					}
				} finally {
					br.close();
				}
				fis.close();
			}
			setCurrent(lines.size());
		} catch (final IOException e) {
			logger.error(e, e);
		}
	}

	/**
	 * Save the contents of the cache.
	 */
	void save() {
		if (chatCacheFile == null) {
			return;
		}
		try {
			new File(chatCacheFile).getParentFile().mkdirs();
			final PrintStream ps = new PrintStream(chatCacheFile, "UTF-8");

			/*
			 * Keep size of chat.log in a reasonable size.
			 */
			while (lines.size() > 200) {
				lines.removeFirst();
			}

			final ListIterator<String> iterator = lines.listIterator();
			while (iterator.hasNext()) {
				ps.println(iterator.next());
			}
			ps.close();
		} catch (final IOException ex) {
			logger.error(ex, ex);
		}
	}

	void setCurrent(final int current) {
		this.current = current;
	}

	int getCurrent() {
		return current;
	}

	void addlinetoCache(final String text) {
		getLines().add(text);
		setCurrent(getLines().size());

		if (getLines().size() > 50) {
			getLines().removeFirst();
			setCurrent((getCurrent() - 1));
		}
	}

	String current() {
		return getLines().get(current - 1);
	}

	boolean hasNext() {
		return lines.size() > current;
	}

	boolean hasPrevious() {
		return current > 1;
	}

	String previous() {
		try {
			current--;
			return current();
		} catch (final IndexOutOfBoundsException e) {
			Logger.getLogger(ChatCache.class).error(e.getMessage());
			current++;
			throw new NoSuchElementException();
		}
	}

	String next() {
		try {
			current++;
			return current();
		} catch (final IndexOutOfBoundsException e) {
			current--;
			throw new NoSuchElementException();
		}
	}
}
