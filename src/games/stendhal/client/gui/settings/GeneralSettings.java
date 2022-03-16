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

import static games.stendhal.client.gui.settings.SettingsProperties.DOUBLE_TAP_AUTOWALK_PROPERTY;
import static games.stendhal.client.gui.settings.SettingsProperties.MOVE_CONTINUOUS_PROPERTY;
import static games.stendhal.client.gui.settings.SettingsProperties.MSG_BLINK;
import static games.stendhal.client.gui.settings.SettingsProperties.MSG_SOUND;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.border.TitledBorder;

import games.stendhal.client.actions.MoveContinuousAction;
import games.stendhal.client.gui.j2DClient;
import games.stendhal.client.gui.layout.SBoxLayout;
import games.stendhal.client.gui.layout.SLayout;
import games.stendhal.client.gui.styled.Style;
import games.stendhal.client.gui.styled.StyleUtil;
import games.stendhal.client.gui.wt.core.SettingChangeListener;
import games.stendhal.client.gui.wt.core.WtWindowManager;

/**
 * Page for general settings.
 */
class GeneralSettings {
	private static final String GAMESCREEN_AUTORAISECORPSE = "gamescreen.autoinspectcorpses";

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
		JCheckBox clickModeToggle = SettingsComponentFactory.createSettingsToggle(DOUBLE_CLICK_PROPERTY, false,
				"Double Click Mode", "Move and attack with double click. If not checked, a single click is enough.");
		page.add(clickModeToggle);

		// raising corpses
		JCheckBox autoRaiseToggle = SettingsComponentFactory.createSettingsToggle(GAMESCREEN_AUTORAISECORPSE, true,
				"Auto inspect corpses", "Automatically open the loot window for corpses of creatures you can loot");
		page.add(autoRaiseToggle);

		// show healing messages
		JCheckBox showHealingToggle = SettingsComponentFactory.createSettingsToggle(HEALING_MESSAGE_PROPERTY, false,
				"Show healing messages", "Show healing messages in the chat log");
		page.add(showHealingToggle);

		// show poison messages
		JCheckBox showPoisonToggle = SettingsComponentFactory.createSettingsToggle(POISON_MESSAGE_PROPERTY, false,
										"Show poison messages", "Show poisoned messages in the chat log");
		page.add(showPoisonToggle);

		// Double-tap direction for auto-walk
		JCheckBox doubleTapAutowalkToggle = SettingsComponentFactory.createSettingsToggle(DOUBLE_TAP_AUTOWALK_PROPERTY, false,
										"Double-tap direction for auto-walk (experimental)",
										"Initiates auto-walk when direction key is double-tapped");
		page.add(doubleTapAutowalkToggle);

		// Continuous movement
		final JCheckBox moveContinuousToggle = SettingsComponentFactory.createSettingsToggle(MOVE_CONTINUOUS_PROPERTY, false,
										"Continuous movement", "Change maps and pass through portals without stopping");
		moveContinuousToggle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				new MoveContinuousAction().sendAction(moveContinuousToggle.isSelected());
			}
		});
		WtWindowManager.getInstance().registerSettingChangeListener(MOVE_CONTINUOUS_PROPERTY,
				new SettingChangeListener() {
			@Override
			public void changed(String newValue) {
				moveContinuousToggle.setSelected(Boolean.parseBoolean(newValue));
			}
		});
		page.add(moveContinuousToggle);

		final JCheckBox msgBlinkToggle = SettingsComponentFactory.createSettingsToggle(
			MSG_BLINK, true, "Blink on channel message", "Chat channel tab blinks on message when not focused");
		page.add(msgBlinkToggle);

		final JCheckBox msgSoundToggle = SettingsComponentFactory.createSettingsToggle(
			MSG_SOUND, true, "Personal message audio notification", "Play sound for personal messages channel when not focused");
		page.add(msgSoundToggle);

		// Client dimensions
		JComponent clientSizeBox = SBoxLayout.createContainer(SBoxLayout.VERTICAL, pad);
		TitledBorder titleB = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
				"Client Dimensions");

		// There seems to be no good way to change the default background color
		// of all components. The color is needed for making the etched border.
		Style style = StyleUtil.getStyle();
		if (style != null) {
			clientSizeBox.setBackground(style.getPlainColor());
			titleB.setTitleColor(style.getForeground());
		}
		clientSizeBox.setBorder(BorderFactory.createCompoundBorder(titleB,
				BorderFactory.createEmptyBorder(pad, pad, pad, pad)));

		// Save client dimensions
		JCheckBox saveDimensionsToggle =
				SettingsComponentFactory.createSettingsToggle(
						DIMENSIONS_PROPERTY, true, "Save size",
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
		resetDimensions.setAlignmentX(Component.RIGHT_ALIGNMENT);
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
	private void resetClientDimensions() {
		j2DClient clientFrame = j2DClient.get();
		clientFrame.resetClientDimensions();
	}
}
