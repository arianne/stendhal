package games.stendhal.client.soundreview;

import games.stendhal.client.sound.SoundSystem;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class SoundFileReader {

	/** expected location of the sound definition file (classloader). */
	public static final String STORE_PROPERTYFILE = "data/sounds/stensounds.properties";

	private SoundFileMap fileMap;

	static Properties soundprops;

	public SoundFileReader() {
		fileMap = new SoundFileMap();

	}

	public void init() {
		init(STORE_PROPERTYFILE);

	}

	private void init(String propertyfile) {

		soundprops = loadSoundProperties(soundprops, propertyfile);

	}

	/**
	 * @param prop
	 *            the Property Object to load to
	 * @param url the Propertyfile
	 * @throws IOException
	 */
	public static Properties loadSoundProperties(Properties prop, String url) {
		InputStream in1;
		if (prop == null) {
			prop = new Properties();
		}
		try {
			in1 = SoundSystem.getResourceStream(url);

			prop.load(in1);
			in1.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return prop;
	}

	byte[] getData(String soundname) {
		byte[] data;
		data = fileMap.get(soundname);
		if (data != null) {
			return data;
		}

		String url = SoundFileReader.soundprops.getProperty("soundbase") + "/" + soundname;
		InputStream in;
		ByteArrayOutputStream bout;
		bout = new ByteArrayOutputStream();
		try {
			in = new FileInputStream(url);

			transferData(in, bout, 4096);
			in.close();
		} catch (IOException e) {
			return null;
		}
		data = bout.toByteArray();
		fileMap.put(soundname, data);
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
	static void transferData(InputStream input, OutputStream output, int bufferSize) throws java.io.IOException {
		byte[] buffer = new byte[bufferSize];
		int len;

		while ((len = input.read(buffer)) > 0) {
			output.write(buffer, 0, len);
		}
	} // transferData
}
