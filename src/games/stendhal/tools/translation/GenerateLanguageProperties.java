package games.stendhal.tools.translation;

import java.io.FileNotFoundException;

/**
 * Parses the source code for invokations of Translate._(...)
 * and generates a stub of the language files
 *
 * @author hendrik
 */
public class GenerateLanguageProperties {

	private String stendhalFolder = null;

	/**
	 * Creates a new GenerateLanguageProperties object
	 *
	 * @param stendhalFolder stendhal root folder
	 */
	public GenerateLanguageProperties(String stendhalFolder) {
		this.stendhalFolder = stendhalFolder;
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
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {

		// initialisiation
		GenerateLanguageProperties generator = new GenerateLanguageProperties(args[0]);
		
		LanguageWriter writer = new LanguageWriter(args[1], args[2]);

		// export
		generator.processCreatures();
		generator.processItems();
		generator.processJavaCode("src");

		writer.close();
	}
}
