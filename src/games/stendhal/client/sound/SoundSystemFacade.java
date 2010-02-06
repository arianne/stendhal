package games.stendhal.client.sound;

import games.stendhal.client.WorldObjects.WorldListener;
import games.stendhal.client.entity.User;
import games.stendhal.client.sound.manager.AudibleArea;
import games.stendhal.client.sound.manager.AudibleCircleArea;
import games.stendhal.client.sound.manager.SoundManager;
import games.stendhal.client.sound.system.Time;
import games.stendhal.common.constants.SoundLayer;

/**
 * this class is the main interface between the game logic and the low level sound system
 *
 * @author hendrik
 */
public class SoundSystemFacade implements WorldListener {
	private static SoundSystemFacade instance;
	private static Time fadingTime = new Time(100, Time.Unit.MILLI);

	public static SoundSystemFacade get() {
		if (instance == null) {
			instance = new SoundSystemFacade();
		}
		return instance;
	}

	public void playerMoved() {
		float[] position = new float[] {(float) User.get().getX(), (float) User.get().getY()};
		SoundManager.get().setHearerPosition(position);
	}

	public void zoneEntered(String zoneName) {
		// ignored
	}

	public void zoneLeft(String zoneName) {
		// ignored
	}

	public void playSound(String sound, double x, double y, int radius, int volume, int layer, boolean loop) {
		SoundManager soundManager = SoundManager.get();
		if (!soundManager.hasSoundName(sound)) {
			String mySoundName = sound + ".ogg";
			String type = "sounds";
			if (layer == SoundLayer.BACKGROUND_MUSIC.ordinal()) {
				type = "music";
			}
			soundManager.openSoundFile("data/" + type + "/" + mySoundName, sound);
		}
		AudibleArea area = new AudibleCircleArea(new float[]{ (float) x, (float) y}, radius, radius);
		Time myFadingTime = new Time();
		if (loop) {
			myFadingTime = fadingTime;
		}
		soundManager.play(sound, layer, area, loop, myFadingTime);
	}

	public void stopSound(String soundName) {
		SoundManager.get().stop(soundName, fadingTime);
	}


}
