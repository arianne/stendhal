package games.stendhal.tools.translation;

import games.stendhal.server.util.Translate;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Properties;

/**
 * writes the language file
 *
 * @author hendrik
 */
public class LanguageWriter {
	private Properties dictionary = new Properties();
	private PrintStream out;

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
			dictionary.load(Translate.class.getClassLoader().getResourceAsStream("data/languages/" + language + ".properties"));
		} catch (Exception e) {
			// ignore
		}
		out = new PrintStream(new FileOutputStream(filename));
	}

	/**
	 * Closes the output stream
	 *
	 */
	public void close() {
		out.close();
	}

}
