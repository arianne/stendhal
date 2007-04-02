package games.stendhal.client.soundreview;

import java.io.IOException;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundMaster implements Runnable {

	
	private static SoundFileMap sfm=null;
	private static Cliplistener cliplisten=null;

	public void run() {
		init();
	}

	private void init() {
		sfm = new SoundFileMap();

		cliplisten = new Cliplistener();
	}

	public static void play(String soundName) {
		if (soundName == null) {
	        return;
        }

		byte[] o;

		o = sfm.get(soundName);
		if (o == null) {
	        return;
        }
		try {
		AudioClip ac = new AudioClip(AudioSystem.getMixer(null), "hugo", o, 100);

		Clip cl;
		
			cl = ac.openLine();

			cl.addLineListener(cliplisten);
			cl.start();
			cl.drain();
			cl = null;
		} catch (UnsupportedAudioFileException e) {
			
		} catch (IOException e) {
			
		} catch (LineUnavailableException e) {
			
		}

	}

	class Cliplistener implements LineListener {

		public void update(LineEvent event) {
			if (event.getType().equals(LineEvent.Type.START)) {
	          
            }
			if (event.getType().equals(LineEvent.Type.CLOSE)) {
	            
            }
			if (event.getType().equals(LineEvent.Type.STOP)) {
	            event.getLine().close();
            }
			
			if (event.getType().equals(LineEvent.Type.OPEN)) {
	            
            }
		}

	}
}
