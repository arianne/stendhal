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


import games.stendhal.client.stendhal;
import games.stendhal.client.actions.SlashActionRepository;
import games.stendhal.client.scripting.ChatLineParser;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;

public class ChatTextController {
	private static final String CHAT_LOG_FILE = System.getProperty("user.home")
	+ "/" + stendhal.STENDHAL_FOLDER + "chat.log";
	private final JTextField playerChatText;
	
	{
			playerChatText = new JTextField("");
	}
	
	private ChatCache cache;
	public ChatTextController() {
		playerChatText.setFocusTraversalKeysEnabled(false);
		playerChatText.addKeyListener(new ChatTextKeyListener());
		addActionListener(new ParserHandler());
		cache = new ChatCache(CHAT_LOG_FILE);
		cache.loadChatCache();
		setCache(cache);
	}
	
	public Component getPlayerChatText() {
		
		return playerChatText;
	}

	public void setChatLine(final String text) {
		
		playerChatText.setText(text);
	}

	class ChatTextKeyListener extends KeyAdapter {
		
			@Override
			public void keyPressed(final KeyEvent e) {
				final int keypressed = e.getKeyCode();

				if (e.isShiftDown()) {
					if (keypressed == KeyEvent.VK_UP) {
						if (cache.hasPrevious()) {
							setChatLine(cache.current());
							cache.previous();
						}

					} else if (keypressed == KeyEvent.VK_DOWN) {
						if (cache.hasNext()) {
							setChatLine(cache.next());
						}
					}

				}

				if (keypressed == KeyEvent.VK_F1) {
					SlashActionRepository.get("manual").execute(null, null);
				}
		};		

		
		
	}
	class ParserHandler implements ActionListener {

		public void actionPerformed(final ActionEvent e) {
			final String text = e.getActionCommand();

			if (ChatLineParser.parseAndHandle(text)) {
				clearLine();

			}

		}
	}
	public void addActionListener(final ActionListener l) {
		playerChatText.addActionListener(l);
		
	}
	
	public void addKeyListener(final KeyListener l) {
		playerChatText.addKeyListener(l);
	}
	

	public String getText() {
		return playerChatText.getText();
	}

	public void setCache(final ChatCache cache) {
		this.cache = cache;
		
	}

	public void clearLine() {
		cache.addlinetoCache(getText());

		setChatLine("");
		
	}

	public void saveCache() {
		cache.save();
		
	}
}
