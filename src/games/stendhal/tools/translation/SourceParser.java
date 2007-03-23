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

	private static final String TRANSLATE = "Trans" + /* mask */"late._(";

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

	/**
	 * very simple configuration parser
	 *
	 * @param type Name of XML tag
	 * @param filename filename
	 */
	// TODO: (this should be an xml parser but this simple thing will do at the moment)
	private void parseConfFile(String type, String filename) {
		try {
			String token = "<" + type + " name=";
			BufferedReader br = new BufferedReader(new FileReader(stendhalFolder + "/data/conf/" + filename));
			String line = br.readLine();
			while (line != null) {
				// find entity names
				int pos = line.indexOf(token);
				if (pos > -1) {
					String name = line
					        .substring(pos + token.length() + 1, line.indexOf("\"", pos + token.length() + 1));
					writer.write(name);
				}

				line = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void processItems() {
		writer.writeLargeBanner("data/conf");
		writer.writeBanner("items.xml");
		parseConfFile("item", "items.xml");
	}

	public void processCreatures() {
		writer.writeLargeBanner("data/conf");
		writer.writeBanner("creatures.xml");
		parseConfFile("creature", "creatures.xml");
	}

	/**
	 * parses the java code
	 *
	 * @param packageName path of package to parse
	 */
	public void processJavaCode(String packageName) {
		writer.writeLargeBanner(packageName);
		File folder = new File(stendhalFolder + "/src/" + packageName);
		File[] files = folder.listFiles();
		for (File file : files) {
			if (file.getName().endsWith(".java")) {
				parseJavaFile(packageName, file.getName());
			}
		}
		for (File file : files) {
			if (file.isDirectory()) {
				if (!file.getName().endsWith("CVS")) {
					processJavaCode(packageName + "/" + file.getName());
				}
			}
		}
	}

	/**
	 * parses a java file
	 *
	 * @param filename filename
	 */
	private void parseJavaFile(String packageName, String filename) {
		writer.writeBanner(filename);
		try {
			BufferedReader br = new BufferedReader(new FileReader(stendhalFolder + "/src/" + packageName + "/"
			        + filename));
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

}
