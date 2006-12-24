package games.stendhal.tools.translation;

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

	/**
	 * Loads the specified language file, if it exists
	 *
	 * @param string language code
	 */
	public void loadLanguageFile(String string) {
		// TODO Auto-generated method stub
		
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
	 */
	public static void main(String[] args) {
		GenerateLanguageProperties generator = new GenerateLanguageProperties(args[0]);
		
		generator.loadLanguageFile(args[1]);
		generator.processCreatures();
		generator.processItems();
		generator.processJavaCode("src");
	}


}
