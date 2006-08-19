/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.*;

/**
 * Summary description for GameLogDialog
 * 
 */
public class GameLogDialog extends JDialog {
	private static final long serialVersionUID = 18385076971138410L;

	// Variables declaration
	private KTextEdit jEditArea;

	private JPanel contentPane;

	private JTextField playerChat;

	// End of variables declaration

	public GameLogDialog(Frame w, JTextField textField) {
		super(w);
		initializeComponent(w);
		playerChat = textField;

		addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				playerChat.requestFocus();
			}

			public void focusLost(FocusEvent e) {
			}
		});

		this.setVisible(true);
	}

	private void initializeComponent(Frame w) {
		jEditArea = new KTextEdit();

		contentPane = (JPanel) this.getContentPane();

		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));
		addComponent(contentPane, jEditArea, 1, 0,
				(int) w.getSize().getWidth() - 8, 171);

		this.setTitle("Game chat and events log");

		Dimension size = w.getSize();
		Point location = w.getLocation();

		this.setLocation(new Point((int) location.getX(), (int) (location
				.getY() + size.getHeight())));
		this.setSize(new Dimension((int) w.getSize().getWidth(), 200));
	}

	/** Add Component Without a Layout Manager (Absolute Positioning) */
	private void addComponent(Container container, Component c, int x, int y,
			int width, int height) {
		c.setBounds(x, y, width, height);
		container.add(c);
	}

	public void addLine(String header, String line, Color color) {
		jEditArea.addLine(header, line, color);
	}

	public void addLine(String header, String line) {
		jEditArea.addLine(header, line, Color.black);
	}

	public void addLine(String line, Color color) {
		jEditArea.addLine(line, color);
	}

	public void addLine(String line) {
		addLine(line, Color.black);
	}
}
