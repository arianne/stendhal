package games.stendhal.client.sound;

import games.stendhal.client.sound.manager.AudibleArea;
import games.stendhal.client.sound.manager.SoundFile.Type;
import games.stendhal.client.sound.system.Time;

public interface SoundGroup {

	public boolean loadSound(String name, String fileURI, Type fileType, boolean enableStreaming);

	public SoundHandle play(String soundName, int layerLevel, AudibleArea area, Time fadeInDuration, boolean autoRepeat, boolean clone);

	public SoundHandle play(String soundName, float volume, int layerLevel, AudibleArea area, Time fadeInDuration, boolean autoRepeat, boolean clone);

	public float getVolume();

	public void changeVolume(float intToFloat);

}
