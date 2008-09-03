package games.stendhal.tools.updateprop;

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

	public static void main(String[] args) {
		if (args.length != 4) {
			System.err.println("java " + UpdatePropUpdater.class.getName() + " oldFile newFile oldVersion newVersion");
			System.exit(1);
		}
		UpdatePropUpdater updater = new UpdatePropUpdater();
	}
}
