package games.stendhal.client.soundreview;

import games.stendhal.client.WorldObjects;
import games.stendhal.client.WorldObjects.WorldListener;
import games.stendhal.client.gui.wt.core.WtWindowManager;

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

public class SoundMaster implements Runnable, WorldListener {
	private static SoundFileMap sfm;

	private static Cliplistener cliplisten;

	private static boolean isMute;

	public static ConcurrentHashMap<Object, Line> playingClips = new ConcurrentHashMap<Object, Line>();

	public void run() {
	}

	public void init() {
		sfm = new SoundFileMap();
		boolean play = Boolean.parseBoolean(WtWindowManager.getInstance().getProperty("sound.play", "true"));
		setMute(!play);

		cliplisten = new Cliplistener();
		WorldObjects.addWorldListener(this);
	}

	public static AudioClip play(final String soundName, final double x, final double y) {
		return play(soundName, x, y, false);
	}

	public static AudioClip play(final String soundName, final double x, final double y,
			final boolean loop) {
		if (!((x == 0) && (y == 0))) {
			if (HearingArea.contains(x, y)) {
				return play(soundName);
			}
		}
		return null;
	}

	public static AudioClip play(final String soundName) {
		final boolean shallLoop = false;
		return play(soundName, shallLoop);
	}

	public static AudioClip play(final String soundName, final boolean shallLoop) {
		//TODO: make it run in soundmasterthread so that it wont crash main if malfunct
		if (isMute) {
			return null;
		}
		if (soundName == null) {
			return null;
		}

		// Is the sound manager not initialized?
		if (sfm == null) {
			return null;
		}

		byte[] o;

		o = sfm.get(soundName);
		if (o == null) {
			return null;
		}
		try {
			final AudioClip ac = new AudioClip(AudioSystem.getMixer(null), o, 100);

			Clip cl;

			cl = ac.openLine();

			if (cl != null) {

				cl.addLineListener(cliplisten);
				playingClips.putIfAbsent(cl, cl);
				if (shallLoop) {
					cl.loop(Clip.LOOP_CONTINUOUSLY);
				} else {
					cl.start();
				}

				return ac;

			}
		} catch (final UnsupportedAudioFileException e) {

		} catch (final IOException e) {

		} catch (final LineUnavailableException e) {

		} catch (final IllegalArgumentException e) {
			
		}
		return null;
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
