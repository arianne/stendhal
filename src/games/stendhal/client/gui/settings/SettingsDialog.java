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

import games.stendhal.client.gui.layout.SBoxLayout;
import games.stendhal.client.gui.wt.core.WtWindowManager;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

/**
 * Dialog for game settings.
 */
public class SettingsDialog extends JDialog {
	private JTabbedPane tabs;
	
	/**
	 * Create a new SettingsDialog.
	 * 
	 * @param parent parent window, or <code>null</code>
	 */
	public SettingsDialog(Frame parent) {
		super(parent, "Settings");
		int pad = SBoxLayout.COMMON_PADDING;
		setLayout(new SBoxLayout(SBoxLayout.VERTICAL, pad));
		tabs = new JTabbedPane();
		add(tabs);
		createGeneralSettingsPage();
		tabs.add("Sound", new SoundSettings().getComponent());
		setResizable(false);
		JButton closeButton = new JButton("Close");
		closeButton.setAlignmentX(RIGHT_ALIGNMENT);
		closeButton.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(pad, pad, pad, pad),
				closeButton.getBorder()));
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		add(closeButton);
		pack();
	}
	
	/**
	 * Create the contents of the general settings page, and add a tab for it.
	 */
	private void createGeneralSettingsPage() {
		int pad = SBoxLayout.COMMON_PADDING;
		JComponent page = SBoxLayout.createContainer(SBoxLayout.VERTICAL, pad);
		page.setBorder(BorderFactory.createEmptyBorder(pad, pad, pad, pad));
		tabs.add("General", page);
		
		// click mode
		JCheckBox clickModeToggle = new JCheckBox("Double Click Mode");
		clickModeToggle.setToolTipText("Move and attack with double click. If not checked, a single click is enough.");
		boolean selected = Boolean.parseBoolean(WtWindowManager.getInstance().getProperty("ui.doubleclick", "false"));
		clickModeToggle.setSelected(selected);
		
		clickModeToggle.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				boolean doubleClick = (e.getStateChange() == ItemEvent.SELECTED);
				WtWindowManager.getInstance().setProperty("ui.doubleclick", Boolean.toString(doubleClick));
			}
		});
		page.add(clickModeToggle);
	}
	
	/**
	 * Main method to help debugging the dialog.
	 *  
	 * @param args unused
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SettingsDialog dlg = new SettingsDialog(null);
				dlg.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
				dlg.setVisible(true);
			}
		});
	}
}
