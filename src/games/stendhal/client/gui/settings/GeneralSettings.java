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
import games.stendhal.client.gui.layout.SLayout;
import games.stendhal.client.gui.styled.Style;
import games.stendhal.client.gui.styled.StyleUtil;
import games.stendhal.client.gui.wt.core.WtWindowManager;

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

class GeneralSettings {
	/** Default decorative font */
	private static final String DEFAULT_FONT = "BlackChancery";
	/** Property used for the decorative font */
	private static final String FONT_PROPERTY = "ui.logfont";
	/** Property used for the double click setting */
	private static final String DOUBLE_CLICK_PROPERTY = "ui.doubleclick";
	
	/** Container for the setting components */
	private final JComponent page;
	
	GeneralSettings() {
		int pad = SBoxLayout.COMMON_PADDING;
		page = SBoxLayout.createContainer(SBoxLayout.VERTICAL, pad);
		
		page.setBorder(BorderFactory.createEmptyBorder(pad, pad, pad, pad));
		
		// click mode
		JCheckBox clickModeToggle = new JCheckBox("Double Click Mode");
		clickModeToggle.setToolTipText("Move and attack with double click. If not checked, a single click is enough.");
		boolean selected = Boolean.parseBoolean(WtWindowManager.getInstance().getProperty(DOUBLE_CLICK_PROPERTY, "false"));
		clickModeToggle.setSelected(selected);
		
		clickModeToggle.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				boolean doubleClick = (e.getStateChange() == ItemEvent.SELECTED);
				WtWindowManager.getInstance().setProperty(DOUBLE_CLICK_PROPERTY, Boolean.toString(doubleClick));
			}
		});
		page.add(clickModeToggle);
		
		// blood
		JCheckBox showBloodToggle = new JCheckBox("Show blood and corpses");
		showBloodToggle.setToolTipText("Show blood spots on hits during fighting and corpse.");
		selected = Boolean.parseBoolean(WtWindowManager.getInstance().getProperty("gamescreen.blood", "true"));
		showBloodToggle.setSelected(selected);

		showBloodToggle.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				boolean enabled = (e.getStateChange() == ItemEvent.SELECTED);
				WtWindowManager.getInstance().setProperty("gamescreen.blood", Boolean.toString(enabled));
			}
		});
		page.add(showBloodToggle);
		
		page.add(createFontSelector(), SBoxLayout.constraint(SLayout.EXPAND_X));
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
