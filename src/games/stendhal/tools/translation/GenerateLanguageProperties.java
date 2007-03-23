package games.stendhal.tools.translation;

import java.io.FileNotFoundException;

/**
 * Parses the source code for invokations of Translate._(...)
 * and generates a stub of the language files
 *
 * @author hendrik
 */
public class GenerateLanguageProperties {

	/**
	 * Generates a xx.properties file based on Translate._() invokations
	 *
	 * @param args
	 * 	<ol>
	 * 		<li>path to stendhal folder</lil>
	 * 		<li>language code (to load old dictionary</li>
	 * 		<li>name of output file</li>
	 * </ol>
	 * @throws FileNotFoundException if the output file cannot be written
	 */
	public static void main(String[] args) throws FileNotFoundException {

		// initialisiation
		LanguageWriter writer = new LanguageWriter(args[1], args[2]);
		SourceParser parser = new SourceParser(args[0], writer);

		// export
		parser.processCreatures();
		parser.processItems();
		parser.processJavaCode("");

		writer.close();
	}
}
