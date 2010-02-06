package games.stendhal.server.entity.mapstuff.sound;

import games.stendhal.common.constants.SoundLayer;

/**
 * Plays an ambient sound in a loop.
 *
 * @author hendrik
 */
public class BackgroundMusicSource extends LoopedSoundSource  {

	/**
	 * Create an ambient sound area.
	 */
	public BackgroundMusicSource(String sound, int radius, int volume, SoundLayer layer) {
		super(sound, radius, volume, SoundLayer.BACKGROUND_MUSIC);
	}

}
