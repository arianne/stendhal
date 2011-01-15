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

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.gui.layout.SBoxLayout;
import games.stendhal.client.gui.layout.SLayout;
import games.stendhal.client.gui.wt.core.WtWindowManager;
import games.stendhal.client.sound.system.Time;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSlider;

/**
 * Page for the sound settings.
 */
public class SoundSettings {
	private static final String SOUND_PROPERTY = "sound.play";
	
	/** Container for the setting components */
	private final JComponent page;
	
	/** Toggle for mute */
	private final JCheckBox muteToggle;
	/**
	 * Container for the volume sliders. Exist to help turning them all,
	 * and their labels easily on or off.
	 */
	private List<JComponent> sliderComponents = new ArrayList<JComponent>(12);
	/** Volume adjuster for master channel */
	private final JSlider masterVolume;
	/** Volume adjuster for GUI channel */
	private final JSlider guiVolume;
	/** Volume adjuster for effects channel */
	private final JSlider effectsVolume;
	/** Volume adjuster for creatures channel */
	private final JSlider creaturesVolume;
	/** Volume adjuster for ambient channel */
	private final JSlider ambientVolume;
	/** Volume adjuster for music channel */
	private final JSlider musicVolume;

	/**
	 * Create sound settings page.
	 */
	public SoundSettings() {
		int pad = SBoxLayout.COMMON_PADDING;
		page = SBoxLayout.createContainer(SBoxLayout.VERTICAL, pad);
		page.setBorder(BorderFactory.createEmptyBorder(pad, pad, pad, pad));
		page.setName("Sound");

		// click mode
		muteToggle = new JCheckBox("Play Sounds");
		boolean soundOn = Boolean.parseBoolean(WtWindowManager.getInstance().getProperty(SOUND_PROPERTY, "true"));
		muteToggle.setSelected(soundOn);
		muteToggle.addItemListener(new MuteListener());
		page.add(muteToggle);

		// Sliders for the sound channels
		JComponent row = SBoxLayout.createContainer(SBoxLayout.HORIZONTAL, pad);
		page.add(row, SBoxLayout.constraint(SLayout.EXPAND_X));
		JLabel label = new JLabel("Master");
		row.add(label);
		SBoxLayout.addSpring(row);
		masterVolume = new JSlider(0, 100);
		masterVolume.setToolTipText("Volume of all sound channels");
		row.add(masterVolume);
		sliderComponents.add(label);
		sliderComponents.add(masterVolume);

		row = SBoxLayout.createContainer(SBoxLayout.HORIZONTAL, pad);
		page.add(row, SBoxLayout.constraint(SLayout.EXPAND_X));
		label = new JLabel("GUI");
		row.add(label);
		SBoxLayout.addSpring(row);
		guiVolume = new JSlider(0, 100);
		guiVolume.setToolTipText("Volume of interactive operations, such as closing windows");
		row.add(guiVolume);
		sliderComponents.add(label);
		sliderComponents.add(guiVolume);

		row = SBoxLayout.createContainer(SBoxLayout.HORIZONTAL, pad);
		page.add(row, SBoxLayout.constraint(SLayout.EXPAND_X));
		label = new JLabel("Effects");
		row.add(label);
		SBoxLayout.addSpring(row);
		effectsVolume = new JSlider(0, 100);
		effectsVolume.setToolTipText("Volume of fighting, and other effects");
		row.add(effectsVolume);
		sliderComponents.add(label);
		sliderComponents.add(effectsVolume);

		row = SBoxLayout.createContainer(SBoxLayout.HORIZONTAL, pad);
		page.add(row, SBoxLayout.constraint(SLayout.EXPAND_X));
		label =new JLabel("Creatures");
		row.add(label);
		SBoxLayout.addSpring(row);
		creaturesVolume = new JSlider(0, 100);
		creaturesVolume.setToolTipText("Volume of creature noises");
		row.add(creaturesVolume);
		sliderComponents.add(label);
		sliderComponents.add(creaturesVolume);

		row = SBoxLayout.createContainer(SBoxLayout.HORIZONTAL, pad);
		page.add(row, SBoxLayout.constraint(SLayout.EXPAND_X));
		label = new JLabel("Ambient");
		row.add(label);
		SBoxLayout.addSpring(row);
		ambientVolume = new JSlider(0, 100);
		row.add(ambientVolume);
		sliderComponents.add(label);
		sliderComponents.add(ambientVolume);

		row = SBoxLayout.createContainer(SBoxLayout.HORIZONTAL, pad);
		page.add(row, SBoxLayout.constraint(SLayout.EXPAND_X));
		label = new JLabel("Music");
		row.add(label);
		SBoxLayout.addSpring(row);
		musicVolume = new JSlider(0, 100);
		musicVolume.setToolTipText("Music volume");
		row.add(musicVolume);
		sliderComponents.add(label);
		sliderComponents.add(musicVolume);
		
		// Disable the sliders if the sound is off
		for (JComponent comp : sliderComponents) {
			comp.setEnabled(soundOn);
		}
	}
	
	/**
	 * Get the component containing the sound settings.
	 * 
	 * @return sound settings page
	 */
	JComponent getComponent() {
		return page;
	}
	
	/**
	 * Listener for toggling the sound on or off. Disables and enables the
	 * volume sliders as needed.
	 */
	private class MuteListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			boolean soundOn = (e.getStateChange() == ItemEvent.SELECTED);
			WtWindowManager.getInstance().setProperty("sound.play", Boolean.toString(soundOn));
			ClientSingletonRepository.getSound().mute(!soundOn, true, new Time(2, Time.Unit.SEC));
			for (JComponent comp : sliderComponents) {
				comp.setEnabled(soundOn);
			}
		}
	}
}