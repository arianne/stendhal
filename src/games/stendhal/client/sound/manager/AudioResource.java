package games.stendhal.client.sound.manager;

import java.io.InputStream;

import games.stendhal.client.sprite.DataLoader;

/**
 * the last remaining piece of the very complicated resource framework, which has been replaced by DataLoader.
 *
 * This class is a helper for refactoring, it should be made obsolate.
 */
public class AudioResource {
	private String name = null;

	/**
	 * creates a new AudioResource
	 *
	 * @param name name
	 */
	public AudioResource(String name) {
		this.name = name;
	}

	/**
	 * gets the input stream
	 *
	 * @return input stream
	 */
	public InputStream getInputStream() {
		InputStream is = DataLoader.getResourceAsStream("/data/sounds/" + name);
		if (is == null) {
			is = DataLoader.getResourceAsStream("/data/music/" + name);
		}
		return is;
	}

	/**
	 * gets the name.
	 *
	 * @return name
	 */
	public String getName() {
		return name;
	}
}
