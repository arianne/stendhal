package games.stendhal.client.soundreview;

import java.io.IOException;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundMaster implements Runnable {

	
	private static SoundFileMap sfm;
	private static Cliplistener cliplisten;

	public void run() {
		init();
	}

	private void init() {
		sfm = new SoundFileMap();

		cliplisten = new Cliplistener();
	}

	public static void play(String soundName) {
		if (soundName == null)
			return;

		byte[] o;

		o = sfm.get(soundName);
		if (o == null)
			return;
		AudioClip ac = new AudioClip(AudioSystem.getMixer(null), "hugo", o, 100);

		Clip cl;
		try {
			cl = ac.openLine();

			cl.addLineListener(cliplisten);
			cl.start();
			cl.drain();
			cl = null;
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	class Cliplistener implements LineListener {

		public void update(LineEvent event) {
			if (event.getType().equals(LineEvent.Type.START))
				System.out.println("start");
			if (event.getType().equals(LineEvent.Type.CLOSE))
				System.out.println("close");
			if (event.getType().equals(LineEvent.Type.STOP))
				event.getLine().close();
			System.out.println("stop");
			if (event.getType().equals(LineEvent.Type.OPEN))
				System.out.println("open");
		}

	}
}
