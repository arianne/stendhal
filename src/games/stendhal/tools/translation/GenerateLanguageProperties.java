package games.stendhal.tools.translation;

import games.stendhal.server.util.Translate;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Properties;

/**
 * Parses the source code for invokations of Translate._(...)
 * and generates a stub of the language files
 *
 * @author hendrik
 */
public class GenerateLanguageProperties {

	private String stendhalFolder = null;
	private Properties dictionary = new Properties();
	private PrintStream out;

	/**
	 * Creates a new GenerateLanguageProperties object
	 *
	 * @param stendhalFolder stendhal root folder
	 */
	public GenerateLanguageProperties(String stendhalFolder) {
		this.stendhalFolder = stendhalFolder;
	}

	/**
	 * Prepares the output stream
	 *
	 * @param filename file name to write
	 * @throws FileNotFoundException if the folder does not exist
	 */
	private void prepareOutput(String filename) throws FileNotFoundException {
		out = new PrintStream(new FileOutputStream(filename));
	}

	/**
	 * Loads the specified language file, if it exists
	 *
	 * @param language language two letter language code
	 */
	public void loadLanguageFile(String language) {
		// init language support
		try {
			dictionary.load(Translate.class.getClassLoader().getResourceAsStream("data/languages/" + language + ".properties"));
		} catch (Exception e) {
			// ignore
		}
	}

	public void processItems() {
		// TODO: implement me
	}

	public void processCreatures() {
		// TODO: implement me
	}

	public void processJavaCode(String folder) {
		// TODO implement me
	}

	/**
	 * Closes the output stream
	 *
	 */
	private void close() {
		out.close();
	}

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {

		// initialisiation
		GenerateLanguageProperties generator = new GenerateLanguageProperties(args[0]);
		generator.loadLanguageFile(args[1]);
		generator.prepareOutput(args[2]);

		// export
		generator.processCreatures();
		generator.processItems();
		generator.processJavaCode("src");

		generator.close();
	}
}
