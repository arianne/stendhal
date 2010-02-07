package games.stendhal.client.sound;

import games.stendhal.client.WorldObjects.WorldListener;
import games.stendhal.client.entity.User;
import games.stendhal.client.gui.wt.core.WtWindowManager;
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
	private boolean mute = false;

	public static SoundSystemFacade get() {
		if (instance == null) {
			instance = new SoundSystemFacade();
		}
		return instance;
	}

	private SoundSystemFacade() {
		boolean play = Boolean.parseBoolean(WtWindowManager.getInstance().getProperty("sound.play", "true"));
		setMute(!play);
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

	public void playSound(String sound, double x, double y, int radius, int volume, SoundLayer layer, boolean loop) {
		if (mute) {
			return;
		}

		SoundManager soundManager = SoundManager.get();
		if (!soundManager.hasSoundName(sound)) {
			String mySoundName = sound + ".ogg";
			String type = "sounds";
			if (layer == SoundLayer.BACKGROUND_MUSIC) {
				type = "music";
			}
			soundManager.openSoundFile("data/" + type + "/" + mySoundName, sound);
		}
		AudibleArea area = new AudibleCircleArea(new float[]{ (float) x, (float) y}, radius / 2, radius);
		Time myFadingTime = new Time();
		if (loop) {
			myFadingTime = fadingTime;
		}
		soundManager.changeVolume(sound, ((float) volume) / 100);
		soundManager.play(sound, 0, area, loop, myFadingTime);
	}

	public void stopSound(String soundName) {
		SoundManager.get().stop(soundName, fadingTime);
	}

	public void exit() {
		// exits  the sound system
	}

	public void setMute(boolean mute) {
		this.mute = mute;
	}


}
