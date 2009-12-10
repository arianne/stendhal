package games.stendhal.client.soundreview;

import games.stendhal.client.sprite.SpriteStore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;

class SoundFileReader {

	/** expected location of the sound definition file (classloader). */
	public static final String STORE_PROPERTYFILE = "data/sounds/";

	private static Properties soundprops;

	public void init() {
		init(STORE_PROPERTYFILE + "stensounds.properties");

	}

	private void init(final String propertyfile) {
		soundprops = loadSoundProperties(soundprops, propertyfile);
	}

	/**
	 * Obtains a resource input stream. Fetches currently from the main
	 * program's classloader.
	 * 
	 * @param name
	 * @return InputStream
	 * @throws IOException
	 */
	public static InputStream getResourceStream(final String name) throws IOException {
		final URL url = SpriteStore.get().getResourceURL(name);
		if (url == null) {
			return null;
		}
		return url.openStream();
	}

	/**
	 * @param prop
	 *            the Property Object to load to
	 * @param url
	 *            the Propertyfile
	 * @return Properties with name of the sound files
	 */
	public static Properties loadSoundProperties(Properties prop, final String url) {
		InputStream in1;
		if (prop == null) {
			prop = new Properties();
		}
		try {
			in1 = getResourceStream(url);

			prop.load(in1);
			in1.close();
		} catch (final Exception e) {
			// logger.error(e, e);
		}
		return prop;
	}

	byte[] getData(final String soundname) {
		byte[] data;

		String soundbase = SoundFileReader.soundprops.getProperty("soundbase");
		if (soundbase == null) {
			return null;
		}
		if (!soundbase.endsWith("/")) {
			soundbase = soundbase + "/";
		}
		final String filename = soundbase + soundname;
		InputStream in;
		ByteArrayOutputStream bout;
		bout = new ByteArrayOutputStream();
		try {
			in = getResourceStream(filename);
			if (in == null) {
				return null;
			}

			transferData(in, bout, 4096);
			in.close();
		} catch (final IOException e) {
			Logger.getLogger(SoundFileReader.class).error(
					"could not open soundfile " + filename);
			return null;
		}
		data = bout.toByteArray();

		return data;
	}

	/**
	 * Transfers the contents of the input stream to the output stream until the
	 * end of input stream is reached.
	 * 
	 * @param input
	 * @param output
	 * @param bufferSize
	 * @throws java.io.IOException
	 */
	static void transferData(final InputStream input, final OutputStream output,
			final int bufferSize) throws java.io.IOException {
		final byte[] buffer = new byte[bufferSize];
		int len;

		while ((len = input.read(buffer)) > 0) {
			output.write(buffer, 0, len);
		}
	} // transferData
}
