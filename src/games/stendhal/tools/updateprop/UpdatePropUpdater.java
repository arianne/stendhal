package games.stendhal.tools.updateprop;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.Properties;

/**
 * Updates update.properties for a new release.
 *
 * @author hendrik
 */
public class UpdatePropUpdater {
	private String oldFile;
	private String newFile;
	private String oldVersion;
	private String newVersion;
	private Properties prop;
	
	/**
	 * Creates a new UpdatePropUpdater
	 *
	 * @param oldFile    name of old file
	 * @param newFile    name of new file
	 * @param oldVersion last version
	 * @param newVersion new version
	 */
	public UpdatePropUpdater(String oldFile, String newFile, String oldVersion, String newVersion) {
		this.newFile = newFile;
		this.newVersion = newVersion;
		this.oldFile = oldFile;
		this.oldVersion = oldVersion;
	}

	
	
	/**
	 * Updates the update.properties file
	 * @throws IOException 
	 */
	public void process() throws IOException {
		loadOldUpdateProperties();
		update();
		writeNewUpdateProperties();
	}

	private void loadOldUpdateProperties() throws IOException {
		prop = new Properties();
		InputStream is;
		if (oldFile.indexOf(":") > 2) {
			URL url = new URL(oldFile);
			is = url.openStream();
		} else {
			is = new FileInputStream(oldFile);
		}
		prop.load(is);
		is.close();
	}

	private void update() {
		// TODO Auto-generated method stub
		prop.list(System.out);
	}

	private void writeNewUpdateProperties() throws IOException {
		PrintStream ps = new PrintStream(new FileOutputStream(newFile));
		UpdatePropertiesWriter writer = new UpdatePropertiesWriter(prop, ps);
		writer.process();
		ps.close();
	}

	public static void main(String[] args) throws IOException {
		if (args.length != 4) {
			System.err.println("java " + UpdatePropUpdater.class.getName() + " oldFile newFile oldVersion newVersion");
			System.exit(1);
		}
		UpdatePropUpdater updater = new UpdatePropUpdater(args[0], args[1], args[2], args[3]);
		updater.process();
	}
}
