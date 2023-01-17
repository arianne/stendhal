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
package games.stendhal.client.sprite;

import java.io.InputStreamReader;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import games.stendhal.client.ClientSingletonRepository;


public class EmojiStore {

	private static final Logger logger = Logger.getLogger(EmojiStore.class);

	private static EmojiStore instance;

	private List<String> emojilist;
	private Map<String, String> emojimap;

	private static final String pathPrefix = "data/sprites/emoji/";

	/* The Java client currently implements javax.swing.text.Document
	 * which does not support displaying images, so this map is used to
	 * display a character instead. Once the chat log does support
	 * images this can be removed. It may be possible to switch to
	 * javax.swing.text.html.HTMLDocument.
	 */
	public static final Map<String, String> chatLogChars = new HashMap<String, String>() {{
		put("angermark", "💢");
		put("astonished", "😲");
		put("confounded", "😣");
		put("confused", "😕");
		put("cry", "😢");
		put("expressionless", "😑");
		put("frown", "☹");
		put("frownslight", "🙁");
		put("grin", "😀");
		put("heart", "❤");
		put("heartarrow", "💘");
		put("heartbroken", "💔");
		put("joy", "😂");
		put("neutral", "😐");
		put("savor", "😋");
		put("smile", "☺");
		put("smileinvert", "🙃");
		put("smileslight", "🙂");
		put("smilingeyes", "😊");
		put("sweat", "💧");
		put("tongue", "😛");
		put("wink", "😉");
		put("winktongue", "😜");
	}};


	public static EmojiStore get() {
		if (instance == null) {
			instance = new EmojiStore();
		}
		return instance;
	}

	/**
	 * Singleton.
	 */
	private EmojiStore() {
		emojilist = new LinkedList<>();
		emojimap = new HashMap<>();
	}

	/**
	 * Loads emoji data from JSON.
	 */
	public void init() {
		final String path = pathPrefix + "emojis.json";
		final URL url = DataLoader.getResource(path);
		if (url != null) {
			try {
				final InputStreamReader isr = new InputStreamReader(url.openStream(), StandardCharsets.UTF_8);
				final JSONObject document = (JSONObject) JSONValue.parse(isr);
				if (document == null) {
					logger.error("Failed to read emoji data file: " + path);
				} else {
					final Object el = document.get("emojilist");
					if (el != null && el instanceof List<?>) {
						for (final Object k: (List<?>) el) {
							emojilist.add((String) k);
						}
					}
					final Object em = document.get("emojimap");
					if (em != null && em instanceof Map<?, ?>) {
						for (final Map.Entry<?, ?> e: ((Map<?, ?>) em).entrySet()) {
							emojimap.put((String) e.getKey(), (String) e.getValue());
						}
					}
				}
			} catch (final IOException e) {
				logger.error("Error loading JSON emoji data from file: " + path, e);
			}
		}
	}

	/**
	 * Creates an emoji sprite.
	 *
	 * @param text
	 *     Text representing emoji.
	 * @return
	 *     <code>Image</code> or <code>undefined</code> if emoji isn't available.
	 */
	public Sprite create(final String text) {
		final String filename = absPath(text);
		if (filename == null) {
			return null;
		}
		return ClientSingletonRepository.getSpriteStore().getSprite(filename);
	}

	/**
	 * Checks if text represents an emoji.
	 *
	 * @param text
	 *     Text to be checked.
	 * @return
	 *     String representing emoji sprite filename or <code>undefined</code>.
	 */
	public String check(String text) {
		text = text.replace("\\\\", "\\");
		String name = emojimap.containsKey(text) ? emojimap.get(text) : null;
		if (name == null && (text.startsWith(":") && text.endsWith(":"))) {
			text = text.substring(0, text.length() - 1).substring(1);
			if (isAvailable(text)) {
				name = text;
			}
		}
		return name;
	}

	/**
	 * Retrieves full path to an emoji image.
	 *
	 * @param name
	 *     Text representing emoji image filename.
	 * @return
	 *     String path to emoji image.
	 */
	public String absPath(final String name) {
		final String checked = check(name);
		if (checked != null) {
			return pathPrefix + checked + ".png";
		}
		return null;
	}

	/**
	 * Checks if an emoji is registered.
	 *
	 * @param name
	 *     Text representing emoji image filename.
	 * @return
	 *     <code>true</code> if name is registered.
	 */
	public boolean isAvailable(String name) {
		if (name.startsWith(":") && name.endsWith(":")) {
			name = name.substring(1, name.length()-1);
		}
		return emojilist.contains(name);
	}

	/**
	 * Get a list of available emojis.
	 */
	public List<String> getEmojiList() {
		return emojilist;
	}
}
