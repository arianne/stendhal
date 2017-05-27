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

import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import org.apache.log4j.Logger;

/**
 * A drop down menu for selecting special characters that players may want to
 * use in chat.
 */
public class CharacterMap extends JButton {
	/**
	 * Create a new CharacterMap.
	 *
	 * @param textField text field where selected character should be inserted
	 */
	public CharacterMap(final JTextComponent textField) {
		super("☺");
		setFocusable(false);
		setToolTipText("Insert a special character");

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
				if (source instanceof AbstractButton) {
					String str = ((AbstractButton) source).getText();
					int pos = textField.getCaretPosition();
					try {
						textField.getDocument().insertString(pos, str, null);
					} catch (BadLocationException ex) {
						Logger.getLogger(CharacterMap.class).error("Bug", ex);
					}
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
		String[][] characters = {
				{ "☺", "☹", "😃", "😲", "😇", "😈", "😊", "😌", "😍", "😎", "😏", "😐", "😴" },
				{ "🐭", "🐮", "🐱", "🐵", "🐯", "🐰", "🐴", "🐶", "🐷", "🐹", "🐺", "🐻", "🐼"  },
				{ "♥", "♡", "💔", "💡", "☠" },
				{ "£", "$", "€", "₤", "₱", "¥" },
				{ "♩", "♪", "♫", "♬", "♭", "♮", "♯", "𝄞", "𝄢" } };
		menu.setLayout(new GridLayout(0, characters[0].length));

		Insets insets = new Insets(1, 1, 1, 1);
		setMargin(insets);
		for (String[] row : characters) {
			for (String chr : row) {
				JMenuItem item = new JMenuItem(chr);
				item.setMargin(insets);
				item.addActionListener(listener);
				item.setBorder(null);
				item.setHorizontalTextPosition(CENTER);
				menu.add(item);
			}
		}
	}
}
