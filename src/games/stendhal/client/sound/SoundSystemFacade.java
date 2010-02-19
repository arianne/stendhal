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
	private static final Logger logger = Logger.getLogger(SoundSystemFacade.class);

	private static final SoundSystemFacade singletonInstance = new SoundSystemFacade();
	private final HashMap<String, Sound> sounds = new HashMap<String, Sound>();
	private final ResourceLocator resourceLocator = ResourceManager.get();
	
	public static SoundSystemFacade get() {
		return singletonInstance;
	}

	private SoundSystemFacade() {
		boolean mute = !Boolean.parseBoolean(WtWindowManager.getInstance().getProperty("sound.play", "true"));
		super.mute(mute, null);
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

	public void exit() {
		// exits the sound system
	}

	public void stop(String soundName, Time fadeOutDuration) {
		super.stop(getSound(soundName), fadeOutDuration);
	}

	public Sound getSound(String soundName) {
		return sounds.get(soundName);
	}

	public Sound openSound(String fileURI, SoundFile.Type fileType) {
		return super.openSound(resourceLocator.getResource(fileURI), fileType);
	}

	public Sound openSound(String fileURI, SoundFile.Type fileType, int numSamplesPerChunk, boolean enableStreaming) {
		return super.openSound(resourceLocator.getResource(fileURI), fileType, numSamplesPerChunk, enableStreaming);
	}

	public Sound loadSound(String name, String fileURI, SoundFile.Type fileType, boolean enableStreaming) {
		Sound sound = sounds.get(name);

		if(sound == null) {
			sound = super.openSound(resourceLocator.getResource(fileURI), fileType, 256, enableStreaming);

			if(sound != null) {
				sounds.put(name, sound);
			}
		}

		return sound;
	}

	public void play(final String soundName, final SoundLayer soundLayer, int volume) {
		AudibleArea area = SoundManager.INFINITE_AUDIBLE_AREA;
		Sound sound = loadSound(soundName, "audio:/" + soundName + ".ogg", Type.OGG, false);
		super.play(sound, Numeric.intToFloat(volume, 100.0f), 0, area, false, new Time());
	}

	public void play(final String soundName, final double x, final double y, final SoundLayer soundLayer, int volume) {
		AudibleArea area = new AudibleCircleArea(Algebra.vecf((float) x, (float) y), 3, 20);
		Sound sound = loadSound(soundName, "audio:/" + soundName + ".ogg", Type.OGG, false);
		super.play(sound, Numeric.intToFloat(volume, 100.0f), 0, area, false, new Time());
	}

	public void play(final String soundName, final double x, final double y, int radius, final SoundLayer soundLayer, int volume) {
		AudibleArea area = new AudibleCircleArea(Algebra.vecf((float) x, (float) y), radius / 4.0f, radius);
		Sound sound = loadSound(soundName, "audio:/" + soundName + ".ogg", Type.OGG, false);
		super.play(sound, Numeric.intToFloat(volume, 100.0f), 0, area, false, new Time());
	}

}
