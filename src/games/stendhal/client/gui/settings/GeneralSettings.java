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

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.gui.chatlog.EventLine;
import games.stendhal.client.gui.layout.SBoxLayout;
import games.stendhal.client.gui.layout.SLayout;
import games.stendhal.client.gui.styled.Style;
import games.stendhal.client.gui.styled.StyleUtil;
import games.stendhal.client.gui.styled.StyledLookAndFeel;
import games.stendhal.client.gui.wt.core.WtWindowManager;
import games.stendhal.common.MathHelper;
import games.stendhal.common.NotificationType;

import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;

/**
 * Page for general settings.
 */
class GeneralSettings {
	private static final String GAMESCREEN_BLOOD = "gamescreen.blood";
	
	private static final String GAMESCREEN_AUTORAISECORPSE = "gamescreen.autoraisecorpse";
	/** Default decorative font. */
	private static final String DEFAULT_FONT = "BlackChancery";
	/** Default font size. */
	private static final int DEFAULT_FONT_SIZE = 12;
	/** Property used for the decorative font. */
	private static final String FONT_PROPERTY = "ui.logfont";
	/** Property used for the decorative font. */
	private static final String FONT_SIZE_PROPERTY = "ui.font_size";
	/** Property used for the double click setting. */
	private static final String DOUBLE_CLICK_PROPERTY = "ui.doubleclick";
	
	private static final String HEALING_MESSAGE_PROPERTY = "ui.healingmessage";
	
	private static final String POISON_MESSAGE_PROPERTY = "ui.poisonmessage";
	
	/** Property used for toggling map coloring on. */
	private static final String MAP_COLOR_PROPERTY = "ui.colormaps";
	private static final String SCALE_SCREEN_PROPERTY = "ui.scale_screen";
	
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
		
		// blood
		JCheckBox showBloodToggle = SettingsComponentFactory.createSettingsToggle(GAMESCREEN_BLOOD, "true",
				"Show blood and corpses", "Show blood spots on hits during fighting and corpse.");
		page.add(showBloodToggle);
		
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
		
		// Lighting effects
		JCheckBox mapColoring = SettingsComponentFactory.createSettingsToggle(MAP_COLOR_PROPERTY, "true",
				"Light effects", "Show night time lighting, and other coloring effects");
		page.add(mapColoring);
		// Coloring setting needs a map change to take an effect, so we need to
		// inform the player about the delayed effect.
		mapColoring.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				boolean enabled = (e.getStateChange() == ItemEvent.SELECTED);
				String tmp = enabled ? "enabled" : "disabled";
				String msg = "Lighting effects are now " + tmp
						+ ". You may need to change map or relogin for it to take effect.";
				ClientSingletonRepository.getUserInterface().addEventLine(new EventLine("", msg, NotificationType.CLIENT));
			}
		});
		
		final JCheckBox scaleScreenToggle = SettingsComponentFactory.createSettingsToggle(SCALE_SCREEN_PROPERTY,
				"true", "Scale view to fit window", "<html>If selected, the game view will scale to fit the available space,<br>otherwise the default sized graphics are used.</html>");
		page.add(scaleScreenToggle);
		page.add(createFontSizeSelector());
		page.add(createFontSelector(), SBoxLayout.constraint(SLayout.EXPAND_X));
	}
	
	/**
	 * Create selector for the default font size.
	 * 
	 * @return component containing the selector
	 */
	private JComponent createFontSizeSelector() {
		JComponent container = SBoxLayout.createContainer(SBoxLayout.HORIZONTAL, SBoxLayout.COMMON_PADDING);
		container.add(new JLabel("Text size"));
		
		final JComboBox selector = new JComboBox();
		
		// Fill the selector, and set current size as the selection
		int current = WtWindowManager.getInstance().getPropertyInt(FONT_SIZE_PROPERTY, DEFAULT_FONT_SIZE);
		selector.addItem("default (12)");
		for (int size = 8; size <= 20; size += 2) {
			Integer obj = size;
			selector.addItem(obj);
			if ((size == current) && (size != DEFAULT_FONT_SIZE)) {
				selector.setSelectedItem(obj);
			}
		}
		
		selector.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object selected = selector.getSelectedItem();
				if ("default (12)".equals(selected)) {
					selected = "12";
				}
				WtWindowManager.getInstance().setProperty(FONT_SIZE_PROPERTY, selected.toString());
				
				LookAndFeel look = UIManager.getLookAndFeel();
				if (look instanceof StyledLookAndFeel) {
					int size = MathHelper.parseIntDefault(selected.toString(), DEFAULT_FONT_SIZE);
					((StyledLookAndFeel) look).setDefaultFontSize(size);
				}
			}
		});
		container.add(selector);
		container.setToolTipText("Common text size");
		return container;
	}
	
	/**
	 * Create selector for the font used in the quest log and achievements.
	 * 
	 * @return component for specifying a font
	 */
	private JComponent createFontSelector() {
		int pad = SBoxLayout.COMMON_PADDING;
		JComponent fontBox = SBoxLayout.createContainer(SBoxLayout.VERTICAL, pad);
		fontBox.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
				BorderFactory.createEmptyBorder(pad, pad, pad, pad)));
		
		// There seems to be no good way to change the default background color
		// of all components. The color is needed for making the etched border.
		Style style = StyleUtil.getStyle();
		if (style != null) {
			fontBox.setBackground(style.getPlainColor());
		}
		
		JCheckBox fontToggle = new JCheckBox("Custom Decorative Font");
		fontToggle.setToolTipText("Set a custom font for the travel log and achievements");
		fontBox.add(fontToggle);
		
		JComponent fontRow = SBoxLayout.createContainer(SBoxLayout.HORIZONTAL, pad);
		SBoxLayout.addSpring(fontRow);
		fontBox.add(fontRow, SBoxLayout.constraint(SLayout.EXPAND_X));
		final JLabel label = new JLabel("Font");
		fontRow.add(label);
		final JComboBox fontList = new JComboBox();
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		for (String font : ge.getAvailableFontFamilyNames()) {
			fontList.addItem(font);
		}
		// Show the user what's in use at the moment
		String font = WtWindowManager.getInstance().getProperty(FONT_PROPERTY, DEFAULT_FONT);
		fontList.setSelectedItem(font);
		fontRow.add(fontList);
		
		// Detect if the font property had been changed from the default.
		boolean changed = fontChanged(); 
		fontToggle.setSelected(changed);
		fontList.setEnabled(changed);
		label.setEnabled(changed);
		
		// Bind the toggle button to enabling and disabling the selector
		fontToggle.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				boolean enabled = (e.getStateChange() == ItemEvent.SELECTED);
				if (enabled) {
					String selected = fontList.getSelectedItem().toString();
					WtWindowManager.getInstance().setProperty(FONT_PROPERTY, selected);
				} else {
					WtWindowManager.getInstance().setProperty(FONT_PROPERTY, DEFAULT_FONT);
				}
				fontList.setEnabled(enabled);
				label.setEnabled(enabled);
			}
		});
		
		// Bind changing the selection to changing the font. The selector is
		// enabled only when font changing is enabled, so this should be safe
		fontList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String selected = fontList.getSelectedItem().toString();
				WtWindowManager.getInstance().setProperty(FONT_PROPERTY, selected);
			}
		});
		
		return fontBox;
	}
	
	/**
	 * Check if a custom font is in use.
	 * 
	 * @return <code>true</code> if the user has changed the font from the
	 * 	default, <code>false</code> otherwise 
	 */
	private boolean fontChanged() {
		String currentSetting = WtWindowManager.getInstance().getProperty(FONT_PROPERTY, DEFAULT_FONT);
		return !currentSetting.equals(DEFAULT_FONT);
	}
	
	/**
	 * Get the component containing the general settings.
	 * 
	 * @return general settings page
	 */
	JComponent getComponent() {
		return page;
	}
}
