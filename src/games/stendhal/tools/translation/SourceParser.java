package games.stendhal.tools.translation;

import java.io.File;

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

	/**
	 * parses the java code
	 *
	 * @param packageName path of package to parse
	 */
	public void processJavaCode(String packageName) {
		File folder = new File(stendhalFolder + "/src/" + packageName);
		File[] files = folder.listFiles();
		for (File file : files) {
			if (file.getName().endsWith(".java")) {
				parseJavaFile(file.getAbsolutePath());
			}
		}
		for (File file : files) {
			if (file.isDirectory()) {
				processJavaCode(packageName + "/" + file.getName());
			}
		}
	}

	private void parseJavaFile(String filename) {
		// TODO Auto-generated method stub
		
	}


	/**
	 * 
	 */
	public SourceParser() {
		// TODO Auto-generated constructor stub
	}

}
