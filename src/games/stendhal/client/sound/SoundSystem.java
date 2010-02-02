/*
 *  SoundSystem in games.stendhal.client
 *  file: SoundSystem.java
 *
 *  Project stendhal
 *  @author Janet Hunt
 *  Created 25.12.2005
 *
 *  Copyright (c) 2005 by Jane Hunt
 *
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along with
 this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 Place - Suite 330, Boston, MA 02111-1307, USA, or go to
 http://www.gnu.org/copyleft/gpl.html.
 */

package games.stendhal.client.sound;

import games.stendhal.client.WorldObjects;
import games.stendhal.client.entity.Entity;
import games.stendhal.client.entity.User;
import games.stendhal.client.gui.wt.core.WtWindowManager;
import games.stendhal.client.soundreview.AudioClip;
import games.stendhal.client.soundreview.SoundMaster;
import games.stendhal.client.sprite.SpriteStore;
import games.stendhal.common.MathHelper;

import java.awt.geom.Point2D;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Mixer.Info;

import org.apache.log4j.Logger;

/**
 * This sound system makes available a library of sounds which can be performed
 * under their library sound names. Sounds can be played as one-time occurrences
 * under various operation modi. (Not all combinations of modi are implemented.)
 * Sound volume can be set globally for all played sounds (system level) and per
 * particular sound performance or sound cycle schedule. The sound system also
 * has a global Mute switch.
 * <p>
 * Operation Modi <br>
 * Sounds can be played GLOBAL or MAP-LOCALIZED (attributed with a map location)
 * <br>
 * Sound can be played SINGULAR or in a CYCLIC pattern (which is governed by
 * some random pattern). <br>
 * Sounds can be played CERTAIN or PROBABLE (definable chances). <br>
 * Furthermore, there are the concepts of AUDIBILITY of map sounds and HEARING
 * RANGE of the player, which are both mutable settings.
 * <p>
 * Nature of Library Sounds <br>
 * Library sounds may be multifold and by this consist of a series of singular
 * sound samples. When such a sound is called to perform, one of the alternative
 * samples is randomly selected to play. The definition file determines the
 * inner structure of library sounds, including possible equalizing volume
 * settings.
 * <p>
 * External Files <br>
 * This sound system requires a definition file and a sound database. The
 * definition file is a properties file located in
 * <code>STORE_PROPERTYFILE</code>. The sound database is a ZIP file
 * containing PCM formatted sound samples stored under their sample names.
 * Library sound names, as used in the interface, are related to sample names in
 * the definition file. The location of the database may be also defined in the
 * definition file under property "soundbase".
 * 
 * @author Jane Hunt
 */
@Deprecated
public class SoundSystem implements WorldObjects.WorldListener {

	private static final String INT_SEMOS_BLACKSMITH = "int_semos_blacksmith";

	private static final String ZERO_SEMOS_ROAD_E = "0_semos_road_e";

	private static final String ZERO_SEMOS_CITY = "0_semos_city";

	private static final String ZERO_SEMOS_VILLAGE_W = "0_semos_village_w";

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(SoundSystem.class);

	/** expected location of the sound definition file (classloader). */
	private static final String STORE_PROPERTYFILE = "data/sounds/stensounds.properties";

	private static SoundSystem singleton;

	/** */
	private final Map<byte[], SoundCycle> cycleMap = Collections.synchronizedMap(new HashMap<byte[], SoundCycle>());

	/** */
	private final ArrayList<AmbientSound> ambientList = new ArrayList<AmbientSound>();

	/** the used mixer. */
	private Mixer mixer;

	/** global volume control. */
	private FloatControl volumeCtrl;

	/** current volume setting. */
	private int volumeSetting = 100;

	private float volumeDelta;

	/** true when mute is enabled. */
	private boolean muteSetting;

	/** true when sound is initialized and operative .*/
	private boolean operative;

	
	
	/**
	 * plays (?) and registers an ambient sound.
	 * 
	 * @param ambient
	 *            the sound to be registered
	 */
	void playAmbientSound(final AmbientSound ambient) {

		ambient.play();

		synchronized (ambientList) {
			ambientList.add(ambient);
		}
	} 

	/**
	 * removes the ambient sound from the internal list. It should already be
	 * stopped.
	 * 
	 * @param ambient
	 *            the ambient sound to be removed
	 */
	static void stopAmbientSound(final AmbientSound ambient) {
		final SoundSystem sys = get();

		synchronized (sys.ambientList) {
			sys.ambientList.remove(ambient);
		}
	}

	/** Stops and removes all ambient sounds. */
	private void clearAmbientSounds() {
		synchronized (ambientList) {
			final List<AmbientSound> list = new ArrayList<AmbientSound>(ambientList);
			for (final AmbientSound sound : list) {
				sound.terminate();
			}
		}
	}

	/**
	 * Starts cyclic performance of a given library sound, attributed to a
	 * specific entity on the map. There can only be one sound cycle for an
	 * entity at a given time. If an sound cycle is started while a previous
	 * cycle is defined for the entity, the previous cycle is discarded and any
	 * ongoing sound performance stopped.
	 * 
	 * @param entity
	 *            the game object that makes the sound
	 * @param token
	 *            the library sound
	 * @param period
	 *            maximum time period for one sound occurrence
	 * @param volBot
	 *            bottom volume
	 * @param volTop
	 *            top volume
	 * @param chance
	 *            percent chance of performance
	 * @return SoundCycle
	 */
	public static SoundCycle startSoundCycle(final Entity entity, final String token,
			final int period, final int volBot, final int volTop, final int chance) {
		final SoundSystem sys = get();
		SoundCycle cycle;
		SoundCycle c1;

		if (!(sys.isOperative())) {
			return null;
		}

		cycle = null;
		synchronized (sys.cycleMap) {
			try {
				cycle = new SoundCycle(entity, token, period, volBot, volTop,
						chance);
				cycle.play();

				c1 = sys.cycleMap.get(entity.ID_Token);
				if (c1 != null) {
					c1.terminate();
				}

				sys.cycleMap.put(entity.ID_Token, cycle);
			} catch (final IllegalStateException e) {
				logger.error("Undefined sound sample: " + token, e);
			}
		}
		return cycle;
	} // startSoundCycle

	/**
	 * Stops execution of the sound cycle for a specific map entity. This will
	 * interrupt any ongoing sound performance immediately.
	 * 
	 * @param entity_ID
	 *            byte[] identity token of the map entity
	 */
	public static void stopSoundCycle(final byte[] entity_ID) {
		SoundCycle cycle;
		SoundSystem sys;

		sys = get();
		cycle = sys.cycleMap.get(entity_ID);
		if (cycle != null) {
			synchronized (sys.cycleMap) {
				sys.cycleMap.remove(entity_ID);
				cycle.terminate();
			}
		}
	}

	/**
	 * Loads a junk of data from the jar soundfile and returns it as a byte
	 * array.
	 * 
	 * @param name
	 * @return the data in the Zipentry
	 * @throws IOException
	 */
	private byte[] getData(final String name) throws IOException {
		InputStream in;
		ByteArrayOutputStream bout;

		in = getResourceStream(name);
		if (in == null) {
			return null;
		}
		bout = new ByteArrayOutputStream();
		transferData(in, bout, 4096);
		in.close();
		return bout.toByteArray();
	}

	/**
	 * Whether the parameter sound is available in this sound system.
	 * 
	 * @param name
	 *            token of sound
	 * @return true, iif it is available
	 */
	boolean contains(final String name) {
		return (name != null) && SoundEffectMap.getInstance().containsKey(name);
	}

	/**
	 * Obtains a resource input stream. Fetches currently from the main
	 * program's classloader.
	 * 
	 * @param name
	 * @return InputStream
	 * @throws IOException
	 */
	public static InputStream getResourceStream(final String name) throws IOException {
		final URL url = SpriteStore.get().getResourceURL(name);
		if (url == null) {

			return null;
		}
		return url.openStream();
	}

	private void init() {

		boolean play = Boolean.parseBoolean(WtWindowManager.getInstance().getProperty("sound.play", "true"));
		setMute(!play);

		String hstr;
		int loaded;
		/**
		 * count the amount of file that could not be loaded
		 */
		int failedCounted;
		int count;
		int pos;

		int loudness;

		if (!initJavaSound()) {
			logger.warn("Soundsystem Java Init Error");
			return;
		}

		try {
			Properties prop;
			// load sound properties
			prop = new Properties();
			loadSoundProperties(prop);

			// get sound library filepath
			final String soundBase = prop.getProperty("soundbase", "data/sounds/");

			if (prop.isEmpty()) {
				return;
			}
			final Enumeration<Object> maps = prop.keys();
			// read all load-permitted sounds listed in properties
			// from soundfile into cache map
			failedCounted = 0;
			loaded = 0;
			count = 0;

			for (String key = (String) maps.nextElement(); maps.hasMoreElements(); key = (String) maps.nextElement()) {
				byte[] soundData;
				String value;
				String name;
				if (isValidEntry(key, prop.getProperty(key))) {
					name = key.substring(4);
					value = prop.getProperty(key);

					logger.debug("- sound definition: " + name + " = " + value);

					String filename = null;
					pos = value.indexOf(',');
					if (pos > -1) {
						filename = value.substring(0, pos);
					} else {
						filename = value;
					}
					soundData = getData(soundBase + filename);
					if (soundData == null) {
						continue;
					}

					// construct sound clip from sample data
					// (we always do that to verify sound sample format)
					AudioClip sound;
					try {
						// determine equalizing loudness setting
						loudness = 100;
						pos = value.lastIndexOf(',');
						if (pos != -1) {
							loudness = MathHelper.parseIntDefault(
									value.substring(pos + 1), 100);
						}

						// investigate sample status
						final int i = name.indexOf('.');
						if (i != -1) {
							name = name.substring(0, i);
						}

						// sound = new ClipRunner(this, name + "@" + path,
						// soundData, loudness);
						sound = new AudioClip(mixer, soundData, loudness);
						count++;
					} catch (final Exception e) {
						// could not validate sound file content
						hstr = "corruped sound: " + name + "=" + filename;
						logger.error(hstr, e);
						failedCounted++;
						continue;
					}

					// store new sound object into soundsystem library map if
					// opted
					// if (load) {
					logger.debug("- storing mem-library soundclip: " + name);

					// stores the clip sound in memory
					ClipRunner clip = SoundEffectMap.getInstance().getSoundClip(
							name);
					if (clip == null) {
						clip = new ClipRunner(name);
						SoundEffectMap.getInstance().put(name, clip);
					}
					clip.addSample(sound);

					// memorizes the sound data (only for init purposes)
					// dataList.put(path, soundData);
					loaded++;
				}
			} // for

			// report to startup console

			hstr = "Stendhal Soundsystem OK: " + count + " samples approved / "
					+ loaded + " loaded / "
					+ SoundEffectMap.getInstance().size() + " library sounds";
			logger.info(hstr);
			if (failedCounted != 0) {
				hstr = "missing or corrupted sounds: " + failedCounted;
				logger.info(hstr);
			}

			// register listeners
			WorldObjects.addWorldListener(this);
			operative = true;
		} catch (final IOException e) {
			hstr = "Sound System load error";
			logger.error(hstr, e);
			return;
		}
	} // init

	//

	// (do not load when ",x" trailing path;
	// always load when "." in
	// name)

	/**
	 * A key/value pair is assumed valid if
	 * <ul>
	 * <li>key starts with "sfx." <b>and </b></li>
	 * <li>key does not end with ",x"</li>
	 * <li>or value contains a "."</li>
	 * </ul>.
	 * 
	 * @param key
	 * @param value
	 * @return true, if it is valid, false otherwise
	 */
	boolean isValidEntry(final String key, final String value) {
		boolean load;
		int pos1;
		if (key.startsWith("sfx.")) {
			pos1 = value.indexOf(',');
			if (pos1 > -1) {
				load = value.substring(pos1 + 1).charAt(0) != 'x';
			} else {
				load = true;
			}
			load |= value.indexOf('.') != -1;
			return load;
		} else {
			return false;
		}
	}

	/**
	 * @param prop
	 *            the Property Object to load to
	 * @throws IOException
	 */
	private void loadSoundProperties(Properties prop) throws IOException {
		InputStream in1;

		in1 = getResourceStream(STORE_PROPERTYFILE);
		if (in1 == null) {
			logger.info("Soundproperties not found deactivating Soundsystem");
			prop = null;
			this.operative = false;
			return;
		}
		prop.load(in1);
		in1.close();
	}

	/**
	 * @return <b>true</b> if javaSound init is successful,
	 *         <p>
	 *         <b>false</b> otherwise
	 */
	private boolean initJavaSound() {
		Info info;
		Info[] mixInfos;
		String hstr;
		mixInfos = AudioSystem.getMixerInfo();
		if ((mixInfos == null) || (mixInfos.length == 0)) {
			logger.error("SoundSystem: no sound driver available!");
			return false;
		}

		mixer = AudioSystem.getMixer(null); 
		info = mixer.getMixerInfo();
		hstr = "Sound driver: " + info.getName() + "(" + info.getDescription()
				+ ")";
		logger.info(hstr);

		// try a master volume control
		try {
			if (mixer.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
				volumeCtrl = (FloatControl) mixer.getControl(FloatControl.Type.MASTER_GAIN);
				volumeCtrl.setValue(0f);
			}
		} catch (final Exception e) {
			logger.debug("SoundSystem: no master volume controls");
		}

		return true;
	} // initJavaSound

	/**
	 * Sets the global Mute switch of this sound system. Does nothing on
	 * duplicate call.
	 * @param ismute false if sound is to be heard
	 */
	public void setMute(final boolean ismute) {
		if (ismute == muteSetting) {
			return;
		}

		String muteString;
		if (ismute) {
			muteString = "- sound system setting mute = ON";
		} else {
			muteString = "- sound system setting mute = OFF";
		}
		logger.info(muteString);
		muteSetting = ismute;

		synchronized (ambientList) {
			for (final AmbientSound ambient : ambientList) {
				if (ismute) {
					ambient.stop();
				} else {
					ambient.play();
				}
			}
		}
	} // setMute

	/**
	 * Returns the actual state of the global Mute switch of this sound system.
	 * 
	 * @return <b>true</b> if and only if Mute is ON (silent)
	 */
	public boolean isMute() {
		return muteSetting;
	}

	/**
	 * Sets a global volume level for all sounds played with this sound system.
	 * The volume value ranges between 0 (silent) and 100 (loudest).
	 * 
	 * @param volume
	 *            0 .. 100
	 */
	public void setVolume(final int volume) {
		float dB;
		int tempVolume;
		if (volume < 0) {
			tempVolume = 0;
		} else if (volume > 100) {
			tempVolume = 100;
		} else {
			tempVolume = volume;
		}
		
		dB = DBValues.getDBValue(tempVolume);
		logger.info("- sound system setting volume dB = " + dB + "  (gain "
				+ tempVolume + ")");

		volumeSetting = tempVolume;
		if (volumeCtrl != null) {
			volumeCtrl.setValue(dB);
		} else {
			volumeDelta = dB;

			// update ambient sounds
			synchronized (ambientList) {
				for (final AmbientSound amb : ambientList) {
					amb.updateVolume();
				}
			}
		}
	} // setVolume

	/**
	 * Returns the current value of this sound system's voume setting.
	 * 
	 * @return volume ranging 0 (silent) .. 100 (loudest)
	 */
	public int getVolume() {
		return volumeSetting;
	}

	/**
	 * Whether the sound system has been initialized and is ready to operate.
	 * 
	 * @return true, iff the sound system was initialized
	 */
	public boolean isOperative() {
		return operative;
	}

	/**
	 * @return the singleton instance of the Stendhal sound system.
	 */
	public static SoundSystem get() {
		if (singleton == null) {
			singleton = new SoundSystem();
		}
		return singleton;
	}

	/**
	 * Releases any resources associated with this sound system. The system is
	 * rendered inoperative.
	 */
	public void exit() {
		clearAmbientSounds();
		logger.info("sound system exit performed, inactive");
	}

	private SoundSystem() {
		if (SoundMaster.USE_NEW_SOUND_SYSTEM) {
			logger.info("using ogg based sound system");
		} else {
			logger.info("using wav based sound system");
			init();
		}
	}

	/**
	 * Transfers the contents of the input stream to the output stream until the
	 * end of input stream is reached.
	 * 
	 * @param input
	 * @param output
	 * @param bufferSize
	 * @throws java.io.IOException
	 */
	static void transferData(final InputStream input, final OutputStream output,
			final int bufferSize) throws java.io.IOException {
		final byte[] buffer = new byte[bufferSize];
		int len;

		while ((len = input.read(buffer)) > 0) {
			output.write(buffer, 0, len);
		}
	} // transferData

	// ************* INNER CLASSES ***********************

	private String actualZone = "";

	public void zoneEntered(final String zone) {
		if (SoundMaster.USE_NEW_SOUND_SYSTEM) {
			return;
		}
		AmbientSound baseAmb;
		AmbientSound ambient;
		Point2D soundPos;
		String hstr;

		hstr = "-- SoundSys: ZONE ENTERED: " + zone;
		logger.debug(hstr);

		actualZone = zone;

		// 0_SEMOS_VILLAGE
		if (zone.equals(SoundSystem.ZERO_SEMOS_VILLAGE_W)) {
			// global ambient
			ambient = new AmbientSound("semos-village-overall-1", 10);
			ambient.addLoop("wind-loop-1", 25, 0);
			playAmbientSound(ambient);

			// creaking tree and wind
			ambient = AmbientStore.getAmbient("wind-tree-1");

			soundPos = new Point2D.Double(13, 42);
			ambient = new AmbientSound(ambient, "semos-village-tree", soundPos,
					30, 25);
			playAmbientSound(ambient);

			// larks
			baseAmb = AmbientStore.getAmbient("meadow-larks-1");

			soundPos = new Point2D.Double(50, 16);
			ambient = new AmbientSound(baseAmb, "semos-village-larks-1",
					soundPos, 30, 50);
			playAmbientSound(ambient);

			// blackbirds
			baseAmb = AmbientStore.getAmbient("blackbirds-1");

			soundPos = new Point2D.Double(16, 20);
			ambient = new AmbientSound(baseAmb, "semos-village-blackbirds-1",
					soundPos, 30, 50);
			playAmbientSound(ambient);

			// frog
			baseAmb = AmbientStore.getAmbient("single-frog-1");

			soundPos = new Point2D.Double(28, 15);
			ambient = new AmbientSound(baseAmb, "semos-village-frog-1",
					soundPos, 6, 30);
			playAmbientSound(ambient);

		} else if (zone.equals(SoundSystem.ZERO_SEMOS_CITY)) {
			// blackbirds
			baseAmb = AmbientStore.getAmbient("blackbirds-1");

			soundPos = new Point2D.Double(29, 8);
			ambient = new AmbientSound(baseAmb, "semos-city-blackbirds-1",
					soundPos, 30, 80);
			playAmbientSound(ambient);

			// chicken
			baseAmb = AmbientStore.getAmbient("chicken-1");

			soundPos = new Point2D.Double(8, 30);
			ambient = new AmbientSound(baseAmb, "semos-city-fowl-1", soundPos,
					12, 50);
			playAmbientSound(ambient);

			soundPos = new Point2D.Double(47, 25);
			ambient = new AmbientSound(baseAmb, "semos-city-fowl-2", soundPos,
					15, 50);
			playAmbientSound(ambient);

			// worksounds
			baseAmb = AmbientStore.getAmbient("build-works-1");

			soundPos = new Point2D.Double(12, 38);
			ambient = new AmbientSound(baseAmb, "semos-city-works-1", soundPos,
					8, 25);
			playAmbientSound(ambient);

			// tavern noise
			baseAmb = AmbientStore.getAmbient("tavern-noise-1");

			soundPos = new Point2D.Double(45, 37);
			ambient = new AmbientSound(baseAmb, "semos-city-tavern-1",
					soundPos, 10, 40);
			playAmbientSound(ambient);

		} else if (zone.equals(SoundSystem.INT_SEMOS_BLACKSMITH)) {
			// global ambient
			ambient = new AmbientSound("blacksmith-overall-1", 20);
			ambient.addCycle("hammer", 45000, 20, 40, 65);
			playAmbientSound(ambient);

			soundPos = new Point2D.Double(11, 3);
			ambient = new AmbientSound("blacksmith-forgefire-main", soundPos,
					30, 50);
			ambient.addLoop("forgefire-1", 50, 0);
			ambient.addCycle("firesparks-1", 60000, 10, 50, 80);
			playAmbientSound(ambient);

			soundPos = new Point2D.Double(3, 3);
			ambient = new AmbientSound("blacksmith-forgefire-side", soundPos,
					6, 50);
			ambient.addLoop("forgefire-2", 50, 0);
			ambient.addLoop("forgefire-3", 50, 0);
			playAmbientSound(ambient);

		} else if (zone.equals(SoundSystem.ZERO_SEMOS_ROAD_E)) {
			// creaking tree and wind
			baseAmb = AmbientStore.getAmbient("wind-tree-1");

			soundPos = new Point2D.Double(10, 45);
			ambient = new AmbientSound(baseAmb, "road-ados-tree-1", soundPos,
					30, 30);
			playAmbientSound(ambient);

			soundPos = new Point2D.Double(54, 59);
			ambient = new AmbientSound(baseAmb, "road-ados-tree-2", soundPos,
					100, 50);
			playAmbientSound(ambient);

			soundPos = new Point2D.Double(65, 31);
			ambient = new AmbientSound(baseAmb, "road-ados-tree-3", soundPos,
					100, 30);
			playAmbientSound(ambient);

			// beach water
			baseAmb = AmbientStore.getAmbient("water-beach-1");

			soundPos = new Point2D.Double(32, 46);
			ambient = new AmbientSound(baseAmb, "road-ados-beachwater-1",
					soundPos, 7, 25);
			playAmbientSound(ambient);

			soundPos = new Point2D.Double(43, 47);
			ambient = new AmbientSound(baseAmb, "road-ados-beachwater-2",
					soundPos, 7, 25);
			playAmbientSound(ambient);

			soundPos = new Point2D.Double(32, 55);
			ambient = new AmbientSound(baseAmb, "road-ados-beachwater-3",
					soundPos, 12, 35);
			playAmbientSound(ambient);

			// water at bridge
			baseAmb = AmbientStore.getAmbient("water-flow-1");

			soundPos = new Point2D.Double(47, 47);
			ambient = new AmbientSound(baseAmb, "road-ados-bridge-1", soundPos,
					3, 50);
			playAmbientSound(ambient);

			// larks
			baseAmb = AmbientStore.getAmbient("meadow-larks-1");

			soundPos = new Point2D.Double(15, 15);
			ambient = new AmbientSound(baseAmb, "road-ados-larks-1", soundPos,
					30, 50);
			playAmbientSound(ambient);

			soundPos = new Point2D.Double(32, 33);
			ambient = new AmbientSound(baseAmb, "road-ados-larks-2", soundPos,
					30, 50);
			playAmbientSound(ambient);

			// bushbirds
			baseAmb = AmbientStore.getAmbient("bushbirds-1");

			soundPos = new Point2D.Double(83, 56);
			ambient = new AmbientSound(baseAmb, "road-ados-bushbirds-1",
					soundPos, 20, 80);
			playAmbientSound(ambient);

			soundPos = new Point2D.Double(118, 57);
			ambient = new AmbientSound(baseAmb, "road-ados-bushbirds-2",
					soundPos, 20, 90);
			playAmbientSound(ambient);

		}
	}

	/**
	 * @return the volume delta
	 */
	public float getVolumeDelta() {
		return volumeDelta;
	}

	/*
	 * Overridden:
	 * 
	 * @see games.stendhal.client.WorldObjects.WorldListener#zoneLeft(java.lang.String)
	 */
	public void zoneLeft(final String zone) {
		final String hstr = "-- SoundSys: ZONE LEFT: " + zone;
		logger.debug(hstr);
		if (zone.equals(actualZone)) {
			clearAmbientSounds();
		}
	}

	/*
	 * Overridden:
	 * 
	 * @see games.stendhal.client.WorldObjects.WorldListener#playerMoved(games.stendhal.client.entity.Player)
	 */
	public void playerMoved() {
		if (!SoundMaster.USE_NEW_SOUND_SYSTEM) {
			return;
		}

		// update ambient sounds about player position
		if (isOperative()) {
			if (!isMute()) {
				if (!User.isNull()) {
					synchronized (ambientList) {
						for (final AmbientSound a : ambientList) {
							a.performPlayerMoved();
						}
					}
				}
			}
		}

	}


}
