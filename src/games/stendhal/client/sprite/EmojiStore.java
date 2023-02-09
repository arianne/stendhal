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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.util.JSONLoader;


public class EmojiStore {

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
	@Deprecated
	public static final Map<String, String> chatLogChars = new HashMap<String, String>() {{
		/*
		put("angel", "😇");
		put("angermark", "💢");
		put("astonished", "😲");
		put("confounded", "😣");
		put("confused", "😕");
		put("cry", "😢");
		put("expressionless", "😑");
		put("frown", "☹");
		put("frownslight", "🙁");
		put("glasses", "🤓");
		put("grin", "😀");
		put("happycry", "🥲");
		put("heart", "❤");
		put("heartarrow", "💘");
		put("heartblue", "💙");
		put("heartbroken", "💔");
		put("heartgreen", "💚");
		put("heartviolet", "💜");
		put("heartyellow", "💛");
		put("joy", "😂");
		put("lips", "🗢");
		put("musicnoteeighth", "𝅘𝅥𝅮");
		put("musicnotequarter", "𝅘𝅥");
		put("musicnotesasc", "🎜");
		put("musicnotesdesc", "🎝");
		put("neutral", "😐");
		put("nomouth", "😶");
		put("rolledeyes", "🙄");
		put("savor", "😋");
		put("smile", "☺");
		put("smileinvert", "🙃");
		put("smileslight", "🙂");
		put("smilingeyes", "😊");
		put("sunglasses", "😎");
		put("sweat", "💧");
		put("tongue", "😛");
		put("wink", "😉");
		put("winktongue", "😜");
		*/
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
		final JSONLoader loader = new JSONLoader();
		loader.onDataReady = new Runnable() {
			public void run() {
				final JSONObject document = (JSONObject) loader.data;
				final Object el = document.get("emojilist");
				final Object em = document.get("emojimap");
				if (el != null && el instanceof List<?>) {
					for (final Object k: (List<?>) el) {
						emojilist.add((String) k);
					}
				}
				if (em != null && em instanceof Map<?, ?>) {
					for (final Map.Entry<?, ?> e: ((Map<?, ?>) em).entrySet()) {
						emojimap.put((String) e.getKey(), (String) e.getValue());
					}
				}
			}
		};
		loader.load(pathPrefix + "emojis.json");
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
	 *
	 * @return
	 *     A copy of the emoji list.
	 */
	public List<String> getEmojiList() {
		return new LinkedList<String>() {{ addAll(emojilist); }};
	}
}
