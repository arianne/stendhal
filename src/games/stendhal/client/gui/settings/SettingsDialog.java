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
package games.stendhal.client.gui.settings;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTabbedPane;

import games.stendhal.client.gui.WindowUtils;
import games.stendhal.client.gui.layout.SBoxLayout;

/**
 * Dialog for game settings.
 */
@SuppressWarnings("serial")
public class SettingsDialog extends JDialog {
	/**
	 * Create a new SettingsDialog.
	 *
	 * @param parent parent window, or <code>null</code>
	 */
	public SettingsDialog(Frame parent) {
		super(parent, "Settings");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		int pad = SBoxLayout.COMMON_PADDING;
		setLayout(new SBoxLayout(SBoxLayout.VERTICAL, pad));
		JTabbedPane tabs = new JTabbedPane();
		add(tabs);
		tabs.add("General", new GeneralSettings().getComponent());
		tabs.add("Visuals", new VisualSettings().getComponent());
		tabs.add("Sound", new SoundSettings().getComponent());
		setResizable(false);
		JButton closeButton = new JButton("Close");
		closeButton.setAlignmentX(RIGHT_ALIGNMENT);
		closeButton.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(pad, pad, pad, pad),
				closeButton.getBorder()));
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		add(closeButton);
		WindowUtils.closeOnEscape(this);
		WindowUtils.watchFontSize(this);
		WindowUtils.trackLocation(this, "settings", false);
		pack();
	}
}
