package games.stendhal.client.sound;

import games.stendhal.client.WorldObjects.WorldListener;
import games.stendhal.client.entity.User;
import games.stendhal.client.gui.wt.core.WtWindowManager;
import games.stendhal.client.sound.manager.AudibleArea;
import games.stendhal.client.sound.manager.SoundFile;
import games.stendhal.client.sound.manager.SoundManager;
import games.stendhal.client.sound.system.Time;
import games.stendhal.common.math.Algebra;
import games.stendhal.common.resource.ResourceLocator;
import games.stendhal.common.resource.ResourceManager;

import java.util.Collection;
import java.util.HashMap;

/**
 * this class is the main interface between the game logic and the low level
 * sound system. it is a refinement of the manager.SoundManager class.
 * 
 * @author hendrik, silvio
 */
public class SoundSystemFacade extends SoundManager implements WorldListener {

	private static class Multiplicator {

		public Multiplicator(float v, Group g) {
			value = v;
			group = g;
		}

		float value;
		Group group;
	}

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
			mVolume = volume;

			for (Sound sound : getActiveSounds()) {
				Multiplicator multiplicator = sound.getAttachment(Multiplicator.class);

				if (multiplicator != null && multiplicator.group == this) {
					SoundSystemFacade.this.changeVolume(sound, (mMasterVolume * mVolume * multiplicator.value));
				}
			}
		}

		public Sound play(String soundName, int layerLevel, AudibleArea area, Time fadeInDuration, boolean autoRepeat, boolean clone) {
			return play(soundName, 1.0f, layerLevel, area, fadeInDuration, autoRepeat, clone);
		}

		public Sound play(String soundName, float volume, int layerLevel, AudibleArea area, Time fadeInDuration, boolean autoRepeat, boolean clone) {
			
			if (mEnabled) {
				Sound sound = mSounds.get(soundName);

				if (sound != null) {
					if (clone)
						sound = sound.clone();

					sound.setAttachment(new Multiplicator(volume, this));
					SoundSystemFacade.this.play(sound, (mMasterVolume * mVolume * volume), layerLevel, area, autoRepeat, fadeInDuration);
				}

				return sound;
			}

			return null;
		}
	}
	
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
	}

	public void playerMoved() {
		float[] position = Algebra.vecf((float) User.get().getX(), (float) User.get().getY());
		super.setHearerPosition(position);
		super.update();
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

	public Collection<String> getGroupNames() {
		return mGroups.keySet();
	}

	public float getVolume() {
		return mMasterVolume;
	}

	public void changeVolume(float volume) {
		mMasterVolume = volume;

		for (Sound sound : getActiveSounds()) {
			Multiplicator multiplicator = sound.getAttachment(Multiplicator.class);

			if (multiplicator != null) {
				super.changeVolume(sound, (mMasterVolume * multiplicator.group.mVolume * multiplicator.value));
			}
		}
	}
}
