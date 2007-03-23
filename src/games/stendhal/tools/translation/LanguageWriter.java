package games.stendhal.tools.translation;

import games.stendhal.server.util.Translate;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

/**
 * writes the language file
 *
 * @author hendrik
 */
public class LanguageWriter {

	private Properties dictionary = new Properties();

	private PrintStream out;

	private Set<String> known = new HashSet<String>();

	private String lastBanner = null;

	private String lastLargeBanner = null;

	/**
	 * Creates a new GenerateLanguageProperties object
	 *
	 * @param language two letter language code (to load the old dictionary)
	 * @param filename name of file to write
	 * @throws FileNotFoundException 
	 * @throws FileNotFoundException if the folder does not exist
	 */
	public LanguageWriter(String language, String filename) throws FileNotFoundException {
		try {
			dictionary.load(Translate.class.getClassLoader().getResourceAsStream(
			        "data/languages/" + language + ".properties"));
		} catch (Exception e) {
			// ignore
		}
		out = new PrintStream(new FileOutputStream(filename));
	}

	/**
	 * writes a banner
	 *
	 * @param bannerText text to write into the banner
	 */
	public void writeBanner(String bannerText) {
		if (!bannerText.equals(lastBanner)) {
			out.println();
			out.println();
			out.println("#############################################################################");
			out.println("##   " + bannerText);
			out.println("#############################################################################");
			out.println();
			lastBanner = bannerText;
		}
	}

	/**
	 * writes a large banner (for folders)
	 *
	 * @param bannerText text to write into the banner
	 */
	public void writeLargeBanner(String bannerText) {
		if (!bannerText.equals(lastLargeBanner)) {
			out.println();
			out.println();
			out.println();
			out.println();
			out.println("#############################################################################");
			out.println("#############################################################################");
			out.println("####");
			out.println("####   " + bannerText);
			out.println("####");
			out.println("#############################################################################");
			out.println("#############################################################################");
			out.println();
			out.println();
			lastLargeBanner = bannerText;
		}
	}

	/**
	 * Writes a key to the file. Known keys are not written again but
	 * replaced by pointer.
	 *
	 * @param key key
	 */
	public void write(String key) {
		if (!known.contains(key)) {
			String value = dictionary.getProperty(key, "$TODO");
			dictionary.remove(key);
			out.println(key + "=" + value);
		} else {
			out.println("# defined elsewhere: " + key);
		}
	}

	/**
	 * writes all old translation which are not used anymore 
	 */
	@SuppressWarnings("unchecked")
	public void writeDeprecated() {
		Set<String> deprecated = new TreeSet<String>(((Map<String, String>) (Map) dictionary).keySet());
		if (!deprecated.isEmpty()) {
			writeBanner("deprecated");
			for (String key : deprecated) {
				write(key);
			}
		}
	}

	/**
	 * Closes the output stream
	 *
	 */
	public void close() {
		out.close();
	}

}
