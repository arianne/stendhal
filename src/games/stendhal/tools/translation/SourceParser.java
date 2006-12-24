package games.stendhal.tools.translation;

/**
 * Parses the source code for translateable strings.
 *
 * @author hendrik
 */
public class SourceParser {
	private String stendhalFolder = null;
	private LanguageWriter writer = null;

	/**
	 * Creates a new GenerateLanguageProperties object
	 *
	 * @param stendhalFolder stendhal root folder
	 */
	public SourceParser(String stendhalFolder, LanguageWriter writer) {
		this.stendhalFolder = stendhalFolder;
		this.writer = writer;
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
	 * 
	 */
	public SourceParser() {
		// TODO Auto-generated constructor stub
	}

}
