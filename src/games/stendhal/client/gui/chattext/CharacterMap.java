/***************************************************************************
 *                    (C) Copyright 2013 Faiumoni e.V.                     *
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

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import games.stendhal.client.scripting.ChatLineParser;
import games.stendhal.client.sprite.EmojiStore;
import games.stendhal.client.sprite.ImageSprite;

/**
 * A drop down menu for selecting special characters that players may want to
 * use in chat.
 */
public class CharacterMap extends JButton {

	private static final EmojiStore emojis = EmojiStore.get();


	/**
	 * Create a new CharacterMap.
	 */
	public CharacterMap() {
		super();
		final Font origFont = getFont();
		setFont(new Font("Noto Emoji", origFont.getStyle(), origFont.getSize()+2));
		setFocusable(false);
		setToolTipText("Emojis");

		final ImageSprite icon = (ImageSprite) emojis.create(":smile:");
		if (icon != null) {
			setIcon(new ImageIcon(icon.getImage()));
		} else {
			setText("☺");
		}

		final JPopupMenu menu = new JPopupMenu();

		addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Place the menu right justified to the button
				menu.show(CharacterMap.this, getWidth() - menu.getPreferredSize().width, getHeight());
			}
		});

		ActionListener selectionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				Object source = ev.getSource();
				if (source instanceof EmojiButton) {
					ChatLineParser.parseAndHandle(((EmojiButton) source).getEmojiText());
				}
			}
		};

		fillMenu(menu, selectionListener);
	}

	/**
	 * Fill the popup menu with characters.
	 *
	 * @param menu popup menu
	 * @param listener action listener that should be attached to the menu items
	 */
	private void fillMenu(JComponent menu, ActionListener listener) {
		//~ String[][] characters = {
				//~ { "☺", "☹", "😃", "😲", "😇", "😈", "😊", "😌", "😍", "😎", "😏", "😐", "😴" },
				//~ { "🐭", "🐮", "🐱", "🐵", "🐯", "🐰", "🐴", "🐶", "🐷", "🐹", "🐺", "🐻", "🐼"  },
				//~ { "♥", "♡", "💔", "💡", "☠" },
				//~ { "£", "$", "€", "₤", "₱", "¥" },
				//~ { "♩", "♪", "♫", "♬", "♭", "♮", "♯", "𝄞", "𝄢" } };
		//~ menu.setLayout(new GridLayout(0, characters[0].length));
		menu.setLayout(new GridLayout(0, 13));

		Insets insets = new Insets(1, 1, 1, 1);
		setMargin(insets);
		for (String st: emojis.getEmojiList()) {
			st = ":" + st + ":";
			final ImageSprite emoji = (ImageSprite) emojis.create(st);
			if (emoji != null) {
				EmojiButton item = new EmojiButton(emoji, st);
				item.setMargin(insets);
				item.addActionListener(listener);
				item.setBorder(null);
				menu.add(item);
			}
		}
	}

	private class EmojiButton extends JMenuItem {
		private final String emojiText;

		public EmojiButton(final ImageSprite emoji, final String text) {
			super(new ImageIcon(emoji.getImage()));
			emojiText = text;
			setIconTextGap(0);
			setToolTipText(text);
		}

		public String getEmojiText() {
			return emojiText;
		}
	}
}
