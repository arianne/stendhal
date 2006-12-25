package games.stendhal.tools.translation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Parses the source code for translateable strings.
 *
 * @author hendrik
 */
public class SourceParser {
	private static final String TRANSLATE = "Trans" + /* mask */ "late._(";
	private String stendhalFolder = null;
	private LanguageWriter writer = null;

	/**
	 * Creates a new GenerateLanguageProperties object
	 *
	 * @param stendhalFolder stendhal root folder
	 * @param writer the output writer
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

	/**
	 * parses a java file
	 *
	 * @param filename filename
	 */
	private void parseJavaFile(String filename) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line = br.readLine();
			while (line != null) {
				parseJavaLine(line);
				line = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * parses a line of source code
	 *
	 * @param line source code line
	 */
	// TODO: improve this, it is a very simple implementation
	private void parseJavaLine(String line) {
		int pos = line.indexOf(TRANSLATE);
		while (pos > -1) {
			int end = line.indexOf("\"", pos + TRANSLATE.length() + 1);
			if (end > -1) {
				writer.write(line.substring(pos + TRANSLATE.length() + 1, end));
			}
			pos = line.indexOf(TRANSLATE, pos + 2);
		}
	}


	/**
	 * 
	 */
	public SourceParser() {
		// TODO Auto-generated constructor stub
	}

}
