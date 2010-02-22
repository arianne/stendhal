package games.stendhal.client.sound;

import games.stendhal.client.WorldObjects.WorldListener;
import games.stendhal.client.entity.User;
import games.stendhal.client.gui.wt.core.WtWindowManager;
import games.stendhal.client.sound.manager.AudibleArea;
import games.stendhal.client.sound.manager.AudibleCircleArea;
import games.stendhal.client.sound.manager.SoundFile;
import games.stendhal.client.sound.manager.SoundFile.Type;
import games.stendhal.client.sound.manager.SoundManager;
import games.stendhal.client.sound.system.Time;
import games.stendhal.common.constants.SoundLayer;
import games.stendhal.common.math.Algebra;
import games.stendhal.common.math.Numeric;
import games.stendhal.common.resource.ResourceLocator;
import games.stendhal.common.resource.ResourceManager;

import java.util.HashMap;

import org.apache.log4j.Logger;

/**
 * this class is the main interface between the game logic and the low level
 * sound system. it is a refinement of the manager.SoundManager class.
 * 
 * @author hendrik, silvio
 */
public class SoundSystemFacade extends SoundManager implements WorldListener {

	public class Group {

		private boolean mEnabled = true;
		private float mVolume = 1.0f;
		private final HashMap<String, Sound> mSounds = new HashMap<String, Sound>();

		public boolean loadSound(String name, String fileURI, SoundFile.Type fileType, boolean enableStreaming) {
			Sound sound = SoundSystemFacade.this.mSounds.get(name);

			if (sound == null) {
				sound = openSound(mResourceLocator.getResource(fileURI), fileType, 256, enableStreaming);

				if (sound != null)
					SoundSystemFacade.this.mSounds.put(name, sound);
			}

			if (sound != null)
				mSounds.put(name, sound);

			return sound != null;
		}

		public float getVolume() {
			return mVolume;
		}

		public void changeVolume(float volume) {
			mVolume = mMasterVolume * volume;

			for (Sound sound : getActiveSounds()) {
				if (sound.getAttachment() == this) {
					SoundSystemFacade.this.changeVolume(sound, mVolume);
				}
			}
		}

		public void setSound(String name, Sound sound) {
			if (sound != null) {
				mSounds.put(name, sound);
			}
		}

		public Sound play(String soundName, int layerLevel, AudibleArea area, Time fadeInDuration, boolean autoRepeat, boolean cloneSound) {
			if (mEnabled) {				
				Sound sound = mSounds.get(soundName);

				if (sound != null) {
					if (cloneSound)
						sound = sound.clone();

					sound.setAttachment(this);
					SoundSystemFacade.this.play(sound, mVolume, layerLevel, area, autoRepeat, fadeInDuration);
				}

				return sound;
			}

			return null;
		}
	}
	
	private static final Logger logger = Logger.getLogger(SoundSystemFacade.class);
	private static final SoundSystemFacade mSingletonInstance = new SoundSystemFacade();
	private final HashMap<String, Sound> mSounds = new HashMap<String, Sound>();
	private final HashMap<String, Group> mGroups = new HashMap<String, Group>();
	private final ResourceLocator mResourceLocator = ResourceManager.get();
	private float mMasterVolume = 1.0f;

	public static SoundSystemFacade get() {
		return mSingletonInstance;
	}

	private SoundSystemFacade() {
		boolean mute = !Boolean.parseBoolean(WtWindowManager.getInstance().getProperty("sound.play", "true"));
		super.mute(mute, false, null);
		
		getGroup("music").changeVolume(0.5f);
		getGroup("ambient").changeVolume(1.0f);
		getGroup("creature").changeVolume(0.8f);
		getGroup("npc").changeVolume(0.8f);
		getGroup("sfx").changeVolume(0.4f);
		getGroup("gui").changeVolume(1.0f);
	}

	public void playerMoved() {
		float[] position = Algebra.vecf((float) User.get().getX(), (float) User.get().getY());
		super.setHearerPosition(position);
		update();
	}

	public void zoneEntered(String zoneName) {
		// ignored
	}

	public void zoneLeft(String zoneName) {
		// ignored
	}

	public Group getGroup(String groupName) {
		Group group = mGroups.get(groupName);

		if (group == null) {
			group = new Group();
			mGroups.put(groupName, group);
		}

		return group;
	}

	public Sound getSound(String soundName) {
		return mSounds.get(soundName);
	}

	public Sound openSound(String fileURI, SoundFile.Type fileType) {
		return super.openSound(mResourceLocator.getResource(fileURI), fileType);
	}

	public Sound openSound(String fileURI, SoundFile.Type fileType, int numSamplesPerChunk, boolean enableStreaming) {
		return super.openSound(mResourceLocator.getResource(fileURI), fileType, numSamplesPerChunk, enableStreaming);
	}

	public Sound loadSound(String name, String fileURI, SoundFile.Type fileType, boolean enableStreaming) {
		Sound sound = mSounds.get(name);

		if (sound == null) {
			sound = super.openSound(mResourceLocator.getResource(fileURI), fileType, 256, enableStreaming);

			if (sound != null) {
				mSounds.put(name, sound);
			}
		}

		return sound;
	}

	public void play(final String soundName, final SoundLayer soundLayer, int volume) {
		Sound sound = loadSound(soundName, "audio:/" + soundName + ".ogg", Type.OGG, false);
		super.play(sound, Numeric.intToFloat(volume, 100.0f), 0, null, false, new Time());
	}
}
