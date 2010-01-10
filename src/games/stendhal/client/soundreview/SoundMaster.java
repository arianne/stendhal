package games.stendhal.client.soundreview;

import games.stendhal.client.WorldObjects;
import games.stendhal.client.WorldObjects.WorldListener;
import games.stendhal.client.gui.wt.core.WtWindowManager;
import games.stendhal.client.sound.manager.SoundManager;
import games.stendhal.client.sound.system.Time;

import java.io.IOException;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.log4j.Logger;

public class SoundMaster implements WorldListener {
	private static Logger logger = Logger.getLogger(SoundMaster.class);

	public static final boolean USE_NEW_SOUND_SYSTEM = false;

	private static SoundFileMap sfm;

	private static Cliplistener cliplisten;

	private static boolean isMute;

	public static ConcurrentHashMap<Object, Line> playingClips = new ConcurrentHashMap<Object, Line>();

	public void init() {
		sfm = new SoundFileMap();
		boolean play = Boolean.parseBoolean(WtWindowManager.getInstance().getProperty("sound.play", "true"));
		setMute(!play);

		cliplisten = new Cliplistener();
		WorldObjects.addWorldListener(this);
		
		
	}

	public static void play(final String soundName, final double x, final double y) {
		if (!((x == 0) && (y == 0))) {
			if (HearingArea.contains(x, y)) {
				play(soundName);
			}
		}
	}

	public static void play(final String soundName) {
		if (isMute) {
			return;
		}
		if (soundName == null) {
			return;
		}

		// Is the sound manager not initialized?
		if (sfm == null) {
			return;
		}
		String mySoundName = soundName.replaceAll("\\.wav", "").replaceAll("\\.au", "").replaceAll("\\.aiff", "") + ".ogg";

		if (USE_NEW_SOUND_SYSTEM) {
			playUsingNewSoundSystem(mySoundName);
		} else {
			playUsingOldSoundSystem(soundName);
		}
	}

	private static void playUsingNewSoundSystem(String mySoundName) {
		SoundManager soundManager = SoundManager.get();
		if (!soundManager.hasSoundName(mySoundName)) {
			soundManager.openSoundFile("data/sounds/" + mySoundName, mySoundName);
		}
		//logger.info("soundName: " + mySoundName);
		soundManager.play(mySoundName, 0, SoundManager.INFINITE_AUDIBLE_AREA, false, new Time());
	}

	private static void playUsingOldSoundSystem(final String soundName) {
		byte[] o;
		o = sfm.get(soundName);
		if (o == null) {
			return;
		}
		try {
			final AudioClip ac = new AudioClip(AudioSystem.getMixer(null), o, 100);

			Clip cl;

			cl = ac.openLine();

			if (cl != null) {

				cl.addLineListener(cliplisten);
				playingClips.putIfAbsent(cl, cl);
				cl.start();

				return;

			}
		} catch (final UnsupportedAudioFileException e) {

		} catch (final IOException e) {

		} catch (final LineUnavailableException e) {

		} catch (final IllegalArgumentException e) {
			
		}
	}

	class Cliplistener implements LineListener {
		// dont remove this please astriddemma 12.04.2007
		public void update(final LineEvent event) {

			// if (event.getType().equals(LineEvent.Type.START)) {
			//
			// }
			// if (event.getType().equals(LineEvent.Type.CLOSE)) {
			// }
			if (event.getType().equals(LineEvent.Type.STOP)) {
				event.getLine().close();

				playingClips.remove(event.getLine());
			}

			// if (event.getType().equals(LineEvent.Type.OPEN)) {
			//
			// }
		}

	}

	public void playerMoved() {

	}

	// commented for release
	public void zoneEntered(final String zoneName) {
	}

	public void zoneLeft(final String zoneName) {
	}

	public static void setMute(final boolean on) {
		if (on) {
			final Enumeration<Line> enu = playingClips.elements();
			while (enu.hasMoreElements()) {
				final Line lin = enu.nextElement();
				lin.close();
			}
		}

		isMute = on;
	}
}
