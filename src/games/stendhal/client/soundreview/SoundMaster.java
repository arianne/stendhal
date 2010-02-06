package games.stendhal.client.soundreview;

import games.stendhal.client.sound.manager.SoundManager;
import games.stendhal.client.sound.system.Time;
import games.stendhal.common.constants.SoundLayer;


public class SoundMaster {

	private static boolean isMute;


	public static void play(final SoundLayer soundLayer, final String soundName, final double x, final double y) {
		if (!((x == 0) && (y == 0))) {
			if (HearingArea.contains(x, y)) {
				play(soundLayer, soundName);
			}
		}
	}

	public static void play(final SoundLayer soundLayer, final String soundName) {
		if (isMute) {
			return;
		}
		if (soundName == null) {
			return;
		}

		String mySoundName = soundName.replaceAll("\\.wav", ".ogg").replaceAll("\\.au", ".ogg").replaceAll("\\.aiff", ".ogg");
		playUsingNewSoundSystem(soundLayer.ordinal(), mySoundName);
	}

	private static void playUsingNewSoundSystem(int soundLayer, String mySoundName) {
		// TODO: SoundSystemFacade.get().playSound(sound, 1, 1, 100000, 100, soundLayer, false);

		SoundManager soundManager = SoundManager.get();
		if (!soundManager.hasSoundName(mySoundName)) {
			soundManager.openSoundFile("data/sounds/" + mySoundName, mySoundName);
		}
		//logger.info("soundName: " + mySoundName);
		soundManager.play(mySoundName, soundLayer, SoundManager.INFINITE_AUDIBLE_AREA, false, new Time());
	}

	public static void setMute(final boolean on) {
		// TODO: stop current active sounds (including loops)
		isMute = on;
	}
}
