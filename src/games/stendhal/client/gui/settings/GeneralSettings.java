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

import games.stendhal.client.gui.j2DClient;
import games.stendhal.client.gui.layout.SBoxLayout;
import games.stendhal.client.gui.layout.SLayout;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Page for general settings.
 */
class GeneralSettings {
	private static final String GAMESCREEN_AUTORAISECORPSE = "gamescreen.autoraisecorpse";
	
	/** Property used for the double click setting. */
	private static final String DOUBLE_CLICK_PROPERTY = "ui.doubleclick";
	
	private static final String HEALING_MESSAGE_PROPERTY = "ui.healingmessage";
	
	private static final String POISON_MESSAGE_PROPERTY = "ui.poisonmessage";
	
	private static final String DIMENSIONS_PROPERTY = "ui.dimensions";
	
	/** Container for the setting components. */
	private final JComponent page;
	
	/**
	 * Create new GeneralSettings.
	 */
	GeneralSettings() {
		int pad = SBoxLayout.COMMON_PADDING;
		page = SBoxLayout.createContainer(SBoxLayout.VERTICAL, pad);
		
		page.setBorder(BorderFactory.createEmptyBorder(pad, pad, pad, pad));
		
		// click mode
		JCheckBox clickModeToggle = SettingsComponentFactory.createSettingsToggle(DOUBLE_CLICK_PROPERTY, "false",
				"Double Click Mode", "Move and attack with double click. If not checked, a single click is enough.");
		page.add(clickModeToggle);
		
		// raising corpses
		JCheckBox autoRaiseToggle = SettingsComponentFactory.createSettingsToggle(GAMESCREEN_AUTORAISECORPSE, "true",
				"Auto inspect corpses", "Automatically open the loot window for corpses of creatures you can loot");
		page.add(autoRaiseToggle);
		
		// show healing messages
		JCheckBox showHealingToggle = SettingsComponentFactory.createSettingsToggle(HEALING_MESSAGE_PROPERTY, "false",
				"Show healing messages", "Show healing messages in the chat log");
		page.add(showHealingToggle);
		
		// show poison messages
		JCheckBox showPoisonToggle = SettingsComponentFactory.createSettingsToggle(POISON_MESSAGE_PROPERTY, "false",
										"Show poison messages", "Show poisoned messages in the chat log");
		page.add(showPoisonToggle);
		
		// Client dimensions
		JComponent clientSizeBox = SBoxLayout.createContainer(SBoxLayout.VERTICAL, pad);
		clientSizeBox.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEtchedBorder(),
				BorderFactory.createEmptyBorder(pad, pad, pad, pad)
				)
			);
		
		JLabel clientSizeText = new JLabel("Client Dimensions");
		clientSizeText.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		clientSizeBox.add(clientSizeText);
		
		// Save client dimensions
		JCheckBox saveDimensionsToggle =
				SettingsComponentFactory.createSettingsToggle(
						DIMENSIONS_PROPERTY, "true", "Save size",
						"Restores the client's width, height, and maximized state in future sessions");
		clientSizeBox.add(saveDimensionsToggle);
		
		// Reset client window to default dimensions
		JButton resetDimensions = new JButton("Reset");
		resetDimensions.setToolTipText(
				"Resets the client's width and height to their default dimensions");
		resetDimensions.setActionCommand("reset_dimensions");
		resetDimensions.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				resetClientDimensions();
			}
		});
		resetDimensions.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
		clientSizeBox.add(resetDimensions);
		
		page.add(clientSizeBox, SLayout.EXPAND_X);
	}
		
	/**
	 * Get the component containing the general settings.
	 * 
	 * @return general settings page
	 */
	JComponent getComponent() {
		return page;
	}
	
	/**
	 * Resets the clients width and height to their default values.
	 */
	public void resetClientDimensions() {
		j2DClient clientFrame = j2DClient.get();
		Frame mainFrame = clientFrame.getMainFrame();
		int frameState = mainFrame.getExtendedState();
		
		/*
		 *  Do not attempt to reset client dimensions if window is maximized.
		 *  Prevents resizing errors for child components.
		 */
		if (frameState != JFrame.MAXIMIZED_BOTH) {
			mainFrame.setSize(clientFrame.getFrameDefaultSize());
		}
	}
}
