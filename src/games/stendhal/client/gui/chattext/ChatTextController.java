/***************************************************************************
 *                   (C) Copyright 2003-2015 - Stendhal                    *
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.JTextComponent;

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.StendhalClient;
import games.stendhal.client.stendhal;
import games.stendhal.client.actions.SlashActionRepository;
import games.stendhal.client.scripting.ChatLineParser;
import games.stendhal.common.constants.SoundLayer;

public class ChatTextController {
	/** Maximum text length. Public chat is limited to 1000 server side. */
	private static final int MAX_TEXT_LENGTH = 1000;

	private final JTextField playerChatText = new JTextField("");

	private ChatCache cache;
	public ChatTextController() {
		playerChatText.setFocusTraversalKeysEnabled(false);
		Document doc = playerChatText.getDocument();
		if (doc instanceof AbstractDocument) {
			((AbstractDocument) doc).setDocumentFilter(new SizeFilter(MAX_TEXT_LENGTH));
		}
		setupKeys();
		playerChatText.addActionListener(new ParserHandler());
		StendhalClient client = StendhalClient.get();
		String logFile = null;
		if (client != null) {
			// StendhalClient is null during test runs
			logFile = stendhal.getGameFolder() + "chat/out-" + client.getCharacter() + ".log";
		}
		cache = new ChatCache(logFile);
		cache.loadChatCache();
		setCache(cache);
	}

	public JTextComponent getPlayerChatText() {
		return playerChatText;
	}

	public void setChatLine(final String text) {
		playerChatText.setText(text);
	}

	/**
	 * Add the special key bindings.
	 */
	private void setupKeys() {
		InputMap input = playerChatText.getInputMap();
		input.put(KeyStroke.getKeyStroke("shift UP"), "history_previous");
		input.put(KeyStroke.getKeyStroke("shift DOWN"), "history_next");
		input.put(KeyStroke.getKeyStroke("F1"), "manual");

		ActionMap actions = playerChatText.getActionMap();
		actions.put("history_previous", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (cache.hasPrevious()) {
					setChatLine(cache.previous());
				}
			}
		});
		actions.put("history_next", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (cache.hasNext()) {
					setChatLine(cache.next());
				}
			}
		});
		actions.put("manual", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SlashActionRepository.get("manual").execute(null, null);
			}
		});
	}

	private class ParserHandler implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			final String text = e.getActionCommand();

			if (ChatLineParser.parseAndHandle(text)) {
				clearLine();
			}
		}
	}

	public void addKeyListener(final KeyListener l) {
		playerChatText.addKeyListener(l);
	}

	public String getText() {
		return playerChatText.getText();
	}

	private void setCache(final ChatCache cache) {
		this.cache = cache;
	}

	private void clearLine() {
		cache.addlinetoCache(getText());

		setChatLine("");
	}

	public void saveCache() {
		cache.save();
	}

	/**
	 * A document filter that limits the maximum allowed length of a document.
	 */
	private static class SizeFilter extends DocumentFilter {
		/** Sound to play if the user tries to enter too long string. */
		private static final String sound = "click-1";
		/** Maximum length of the document. */
		final int maxSize;

		/**
		 * Create a new SizeFilter.
		 *
		 * @param maxSize maximum length of the document
		 */
		SizeFilter(int maxSize) {
			this.maxSize = maxSize;
		}

		@Override
		public void insertString(FilterBypass fb, int offs, String str,
				AttributeSet a) throws BadLocationException {
			if ((fb.getDocument().getLength() + str.length()) <= maxSize) {
				super.insertString(fb, offs, str, a);
			} else {
				fail();
			}
		}

		@Override
		public void replace(FilterBypass fb, int offs, int length, String str,
				AttributeSet a) throws BadLocationException {
			if ((fb.getDocument().getLength() + str.length() - length) <= maxSize) {
				super.replace(fb, offs, length, str, a);
			} else {
				fail();
			}
		}

		/**
		 * Called when the document change is rejected. Notify the user.
		 */
		private void fail() {
			ClientSingletonRepository.getSound().getGroup(SoundLayer.USER_INTERFACE.groupName).play(sound, 0, null, null, false, true);
		}
	}
}
