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
import games.stendhal.client.gui.layout.SBoxLayout;
import games.stendhal.client.gui.layout.SLayout;
import games.stendhal.client.gui.wt.core.WtWindowManager;
import games.stendhal.client.sound.SoundGroup;
import games.stendhal.client.sound.system.Time;
import games.stendhal.common.math.Numeric;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Page for the sound settings.
 */
class SoundSettings {
	private static final String SOUND_PROPERTY = "sound.play";
	private static final String VOLUME_PROPERTY = "sound.volume.";
	
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
	SoundSettings() {
		int pad = SBoxLayout.COMMON_PADDING;
		page = SBoxLayout.createContainer(SBoxLayout.VERTICAL, pad);
		page.setBorder(BorderFactory.createEmptyBorder(pad, pad, pad, pad));

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
		masterVolume = createMasterVolumeSlider();
		masterVolume.setToolTipText("Volume of all sound channels");
		row.add(masterVolume);
		sliderComponents.add(label);
		sliderComponents.add(masterVolume);

		row = SBoxLayout.createContainer(SBoxLayout.HORIZONTAL, pad);
		page.add(row, SBoxLayout.constraint(SLayout.EXPAND_X));
		label = new JLabel("GUI");
		row.add(label);
		SBoxLayout.addSpring(row);
		guiVolume = createVolumeSlider("gui");
		guiVolume.setToolTipText("Volume of interactive operations, such as closing windows");
		row.add(guiVolume);
		sliderComponents.add(label);
		sliderComponents.add(guiVolume);

		row = SBoxLayout.createContainer(SBoxLayout.HORIZONTAL, pad);
		page.add(row, SBoxLayout.constraint(SLayout.EXPAND_X));
		label = new JLabel("Effects");
		row.add(label);
		SBoxLayout.addSpring(row);
		effectsVolume = createVolumeSlider("sfx");
		effectsVolume.setToolTipText("Volume of fighting, and other effects");
		row.add(effectsVolume);
		sliderComponents.add(label);
		sliderComponents.add(effectsVolume);

		row = SBoxLayout.createContainer(SBoxLayout.HORIZONTAL, pad);
		page.add(row, SBoxLayout.constraint(SLayout.EXPAND_X));
		label =new JLabel("Creatures");
		row.add(label);
		SBoxLayout.addSpring(row);
		creaturesVolume = createVolumeSlider("creature");
		creaturesVolume.setToolTipText("Volume of creature noises");
		row.add(creaturesVolume);
		sliderComponents.add(label);
		sliderComponents.add(creaturesVolume);

		row = SBoxLayout.createContainer(SBoxLayout.HORIZONTAL, pad);
		page.add(row, SBoxLayout.constraint(SLayout.EXPAND_X));
		label = new JLabel("Ambient");
		row.add(label);
		SBoxLayout.addSpring(row);
		ambientVolume = createVolumeSlider("ambient");
		row.add(ambientVolume);
		sliderComponents.add(label);
		sliderComponents.add(ambientVolume);

		row = SBoxLayout.createContainer(SBoxLayout.HORIZONTAL, pad);
		page.add(row, SBoxLayout.constraint(SLayout.EXPAND_X));
		label = new JLabel("Music");
		row.add(label);
		SBoxLayout.addSpring(row);
		musicVolume = createVolumeSlider("music");
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
	 * Create a volume slider for the master channel.
	 */
	private JSlider createMasterVolumeSlider() {
		JSlider slider = new JSlider(0, 100);
		float volume = ClientSingletonRepository.getSound().getVolume();
		slider.setValue(Numeric.floatToInt(volume, 100f));
		slider.addChangeListener(new MasterVolumeListener());
		
		return slider;
	}
	
	/**
	 * Create a volume slider, and initialize its value from the volume of a
	 * channel.
	 * 
	 * @param channel
	 * @return volume slider for the channel
	 */
	private JSlider createVolumeSlider(String channel) {
		JSlider slider = new JSlider(0, 100);
		SoundGroup group = ClientSingletonRepository.getSound().getGroup(channel);
		slider.setValue(Numeric.floatToInt(group.getVolume(), 100f));
		slider.addChangeListener(new ChannelChangeListener(channel, group));
		
		return slider;
	}
	
	/**
	 * Listener for toggling the sound on or off. Disables and enables the
	 * volume sliders as needed.
	 */
	private class MuteListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			boolean soundOn = (e.getStateChange() == ItemEvent.SELECTED);
			WtWindowManager.getInstance().setProperty(SOUND_PROPERTY, Boolean.toString(soundOn));
			ClientSingletonRepository.getSound().mute(!soundOn, true, new Time(2, Time.Unit.SEC));
			for (JComponent comp : sliderComponents) {
				comp.setEnabled(soundOn);
			}
		}
	}
	
	/**
	 * Listener for adjusting the master volume slider.
	 */
	private static class MasterVolumeListener implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			JSlider source = (JSlider) e.getSource();
			int value = source.getValue();
			ClientSingletonRepository.getSound().changeVolume(Numeric.intToFloat(value, 100.0f));
			WtWindowManager.getInstance().setProperty(VOLUME_PROPERTY + "master", Integer.toString(value));
		}
	}
	
	/**
	 * Listener for adjusting the sound channel sliders. Adjusts the volume of
	 * the sound group corresponding to the slider appropriately.
	 */
	private static class ChannelChangeListener implements ChangeListener {
		private final SoundGroup group;
		private final String groupName; 
		
		/**
		 * Create a ChannelChangeListener for a sound group.
		 * 
		 * @param groupName name of the sound group
		 * @param group
		 */
		public ChannelChangeListener(String groupName, SoundGroup group) {
			this.group = group;
			this.groupName = groupName;
		}
		
		public void stateChanged(ChangeEvent e) {
			JSlider source = (JSlider) e.getSource();
			int value = source.getValue();
			group.changeVolume(Numeric.intToFloat(value, 100f));
			WtWindowManager.getInstance().setProperty(VOLUME_PROPERTY + groupName, Integer.toString(value));
		}
	}
}