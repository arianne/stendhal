/***************************************************************************
 *                   (C) Copyright 2003-2013 - Stendhal                    *
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

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;

import games.stendhal.client.gui.wt.core.WtWindowManager;

class SettingsComponentFactory {
	static JCheckBox createSettingsToggle(final String parameter, boolean defaultValue, String label, String tooltip) {
		boolean selected = false;
		JCheckBox toggle = new JCheckBox(label);
		toggle.setToolTipText(tooltip);
		selected = WtWindowManager.getInstance().getPropertyBoolean(parameter, defaultValue);
		toggle.setSelected(selected);

		toggle.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e) {
				boolean enabled = (e.getStateChange() == ItemEvent.SELECTED);
				WtWindowManager.getInstance().setProperty(parameter, Boolean.toString(enabled));
			}
		});
		return toggle;
	}

}
