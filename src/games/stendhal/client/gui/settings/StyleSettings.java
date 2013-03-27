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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;

/**
 * Page for general settings.
 */
class StyleSettings {
	/** Container for the setting components. */
	private final JComponent page;
	
	/**
	 * Create new GeneralSettings.
	 */
	StyleSettings() {
		int pad = SBoxLayout.COMMON_PADDING;
		page = SBoxLayout.createContainer(SBoxLayout.VERTICAL, pad);
		
		page.setBorder(BorderFactory.createEmptyBorder(pad, pad, pad, pad));
		
		// Style selector
		JComponent hbox = SBoxLayout.createContainer(SBoxLayout.HORIZONTAL, pad);
		JComponent selectorLabel = new JLabel("Styles:");
		hbox.add(selectorLabel);
		JComponent selector = createStyleSelector();
		hbox.add(selector);
		selector.setToolTipText("<html>Sound output device. <b>auto</b> should"
				+ " work for most people,<br>but try others if you can not get"
				+ " sound to work otherwise</html>");
		page.add(hbox);

	}
	
	/**
	 * Get the component containing the style settings.
	 * 
	 * @return style settings page
	 */
	JComponent getComponent() {
		return page;
	}
	
	/**
	 * Create a selector for styles.
	 * 
	 * @return combo box with style options
	 */
	private JComponent createStyleSelector() {
		final JComboBox selector = new JComboBox();
		
		// Fill with available styles
		selector.addItem("Wood");
		selector.addItem("TileAqua");
		selector.addItem("BrickBrown");
		selector.addItem("Aubergine");
		selector.addItem("Honeycomb");
		selector.addItem("ParquetBrown");
		
		selector.setSelectedItem(0);
		 
		selector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object selected = selector.getSelectedItem();
			}
		});
		
		return selector;
	}
	
}
