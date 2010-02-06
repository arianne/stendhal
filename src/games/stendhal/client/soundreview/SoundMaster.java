package games.stendhal.client.soundreview;

import games.stendhal.client.sound.manager.AudibleArea;
import games.stendhal.client.sound.manager.AudibleCircleArea;
import games.stendhal.client.sound.manager.SoundManager;
import games.stendhal.client.sound.system.Time;
import games.stendhal.common.constants.SoundLayer;


public class SoundMaster {

	private static boolean isMute;


	public static void play(final SoundLayer soundLayer, final String soundName) {
		AudibleArea area = SoundManager.INFINITE_AUDIBLE_AREA;
		playUsingNewSoundSystem(soundLayer.ordinal(), soundName, area);
	}

	public static void play(final SoundLayer soundLayer, final String soundName, final double x, final double y) {
		AudibleArea area = new AudibleCircleArea(new float[]{ (float) x, (float) y}, 8, 16);
		playUsingNewSoundSystem(soundLayer.ordinal(), soundName, area);
	}

	private static void playUsingNewSoundSystem(int soundLayer, String soundName, AudibleArea area) {
		if (isMute) {
			return;
		}
		if (soundName == null) {
			return;
		}

		// TODO: SoundSystemFacade.get().playSound(sound, 1, 1, 100000, 100, soundLayer, false);

		soundName = soundName.replaceAll("\\.wav", ".ogg").replaceAll("\\.au", ".ogg").replaceAll("\\.aiff", ".ogg");
		SoundManager soundManager = SoundManager.get();
		if (!soundManager.hasSoundName(soundName)) {
			soundManager.openSoundFile("data/sounds/" + soundName, soundName);
		}
		//logger.info("soundName: " + mySoundName);
		soundManager.play(soundName, soundLayer, area, false, new Time());
	}

}
