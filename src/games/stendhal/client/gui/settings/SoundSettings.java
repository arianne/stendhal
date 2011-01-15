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

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSlider;

/**
 * Page for the sound settings.
 */
public class SoundSettings {
	private JComponent page;

	/**
	 * Create sound settings page.
	 */
	public SoundSettings() {
		int pad = SBoxLayout.COMMON_PADDING;
		page = SBoxLayout.createContainer(SBoxLayout.VERTICAL, pad);
		page.setBorder(BorderFactory.createEmptyBorder(pad, pad, pad, pad));
		page.setName("Sound");

		// click mode
		JCheckBox muteToggle = new JCheckBox("Mute");
		muteToggle.setToolTipText("Turn off all sound.");
		page.add(muteToggle);

		// Sliders for the sound channels
		JComponent row = SBoxLayout.createContainer(SBoxLayout.HORIZONTAL, pad);
		page.add(row, SBoxLayout.constraint(SLayout.EXPAND_X));
		row.add(new JLabel("Master"));
		SBoxLayout.addSpring(row);
		JSlider masterVolume = new JSlider(0, 100);
		masterVolume.setToolTipText("Volume of all sound channels");
		row.add(masterVolume);

		row = SBoxLayout.createContainer(SBoxLayout.HORIZONTAL, pad);
		page.add(row, SBoxLayout.constraint(SLayout.EXPAND_X));
		row.add(new JLabel("GUI"));
		SBoxLayout.addSpring(row);
		JSlider guiVolume = new JSlider(0, 100);
		guiVolume.setToolTipText("Volume of interactive operations, such as closing windows");
		row.add(guiVolume);

		row = SBoxLayout.createContainer(SBoxLayout.HORIZONTAL, pad);
		page.add(row, SBoxLayout.constraint(SLayout.EXPAND_X));
		row.add(new JLabel("Effects"));
		SBoxLayout.addSpring(row);
		JSlider effectsVolume = new JSlider(0, 100);
		effectsVolume.setToolTipText("Volume of fighting, and other effects");
		row.add(effectsVolume);

		row = SBoxLayout.createContainer(SBoxLayout.HORIZONTAL, pad);
		page.add(row, SBoxLayout.constraint(SLayout.EXPAND_X));
		row.add(new JLabel("Creatures"));
		SBoxLayout.addSpring(row);
		JSlider creaturesVolume = new JSlider(0, 100);
		creaturesVolume.setToolTipText("Volume of creature noises");
		row.add(creaturesVolume);

		row = SBoxLayout.createContainer(SBoxLayout.HORIZONTAL, pad);
		page.add(row, SBoxLayout.constraint(SLayout.EXPAND_X));
		row.add(new JLabel("Ambient"));
		SBoxLayout.addSpring(row);
		JSlider ambientVolume = new JSlider(0, 100);
		row.add(ambientVolume);

		row = SBoxLayout.createContainer(SBoxLayout.HORIZONTAL, pad);
		page.add(row, SBoxLayout.constraint(SLayout.EXPAND_X));
		row.add(new JLabel("Music"));
		SBoxLayout.addSpring(row);
		JSlider musicVolume = new JSlider(0, 100);
		creaturesVolume.setToolTipText("Music volume");
		row.add(musicVolume);
	}
	
	/**
	 * Get the component containing the sound settings.
	 * 
	 * @return sound settings page
	 */
	JComponent getComponent() {
		return page;
	}
}