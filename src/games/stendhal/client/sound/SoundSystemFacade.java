package games.stendhal.client.sound;

import games.stendhal.client.WorldObjects.WorldListener;
import games.stendhal.client.entity.User;
import games.stendhal.client.gui.wt.core.WtWindowManager;
import games.stendhal.client.sound.manager.AudibleArea;
import games.stendhal.client.sound.manager.AudibleCircleArea;
import games.stendhal.client.sound.manager.SoundFile;
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
	private static final Logger logger = Logger.getLogger(SoundSystemFacade.class);

	private static final SoundSystemFacade singletonInstance = new SoundSystemFacade();
	private static final Time fadingTime = new Time(100, Time.Unit.MILLI);
	private final HashMap<String, Sound> sounds = new HashMap<String, Sound>();
	private final ResourceLocator resourceLocator = ResourceManager.get();
	private boolean mute = false;

	public static SoundSystemFacade get() {
		return singletonInstance;
	}

	private SoundSystemFacade() {
		mute = !Boolean.parseBoolean(WtWindowManager.getInstance().getProperty("sound.play", "true"));
	}

	public void playerMoved() {
		float[] position = Algebra.vecf((float) User.get().getX(), (float) User.get().getY());
		setHearerPosition(position);
		update();
	}

	public void zoneEntered(String zoneName) {
		// ignored
	}

	public void zoneLeft(String zoneName) {
		// ignored
	}

	public void exit() {
		// exits the sound system
	}

	public Sound start(String soundName, double x, double y, int radius, SoundLayer layer, int volume, boolean loop) {
		if (!mute) {
			Sound sound = prepareSound(soundName);

			AudibleArea area = new AudibleCircleArea(new float[] { (float) x, (float) y }, radius / 2.0f, radius);
			Time myFadingTime = new Time();

			if (loop) {
				myFadingTime = fadingTime;
			}

			play(sound, Numeric.intToFloat(volume, 100.0f), 0, area, loop, myFadingTime);
			return sound;
		}
		return null;
	}

	public void stop(Sound sound) {
		stop(sound, fadingTime);
	}

	private Sound setSound(String soundName, Sound sound) {
		return sounds.put(soundName, sound);
	}

	private Sound getSound(String soundName) {
		return sounds.get(soundName);
	}

	private Sound openSound(String fileURI, SoundFile.Type fileType) {
		return super.openSound(resourceLocator.getResource(fileURI), fileType);
	}

	public void setMute(boolean mute) {
		this.mute = mute;
	}

	public void play(final String soundName, final SoundLayer soundLayer, int volume) {
		AudibleArea area = SoundManager.INFINITE_AUDIBLE_AREA;
		playNonLoopedSound(soundName, area, soundLayer.ordinal(), volume);
	}

	public void play(final String soundName, final double x, final double y, final SoundLayer soundLayer, int volume) {
		AudibleArea area = new AudibleCircleArea(new float[] { (float) x, (float) y }, 8, 12);
		playNonLoopedSound(soundName, area, soundLayer.ordinal(), volume);
	}

	public void play(final String soundName, final double x, final double y, int radius, final SoundLayer soundLayer, int volume) {
		AudibleArea area = new AudibleCircleArea(new float[] { (float) x, (float) y }, radius / 2, radius);
		playNonLoopedSound(soundName, area, soundLayer.ordinal(), volume);
	}

	public void playNonLoopedSound(String soundName, AudibleArea area, int soundLayer, int volume) {
		if (mute) {
			return;
		}

		SoundSystemFacade.Sound sound = prepareSound(soundName);
		play(sound, Numeric.intToFloat(volume, 100.0f), 0, area, false, new Time());
	}

	public Sound prepareSound(String soundName) {
		if (soundName == null) {
			return null;
		}

		Sound sound = getSound(soundName);

		if (sound == null) {
			sound = openSound("audio:/" + soundName + ".ogg", SoundFile.Type.OGG);
			setSound(soundName, sound);
		}

		if (sound != null) {
			sound = sound.clone();
		}

		return sound;
	}
}
