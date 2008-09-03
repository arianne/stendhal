package games.stendhal.tools.updateprop;

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
	 */
	public void process() {
		loadOldUpdateProperties();
		update();
		writeNewUpdateProperties();
	}

	private void loadOldUpdateProperties() {
		// TODO Auto-generated method stub
	}

	private void update() {
		// TODO Auto-generated method stub
	}

	private void writeNewUpdateProperties() {
		// TODO Auto-generated method stub
	}

	public static void main(String[] args) {
		if (args.length != 4) {
			System.err.println("java " + UpdatePropUpdater.class.getName() + " oldFile newFile oldVersion newVersion");
			System.exit(1);
		}
		UpdatePropUpdater updater = new UpdatePropUpdater(args[0], args[1], args[2], args[3]);
	}
}
