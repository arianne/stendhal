package games.stendhal.server.script;

import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ListRaids extends ScriptImpl {

	@SuppressWarnings("unchecked")
	@Override
	public void execute(final Player admin, final List<String> args) {
		String textToSend = "Known RaidScripts:\n";
		ArrayList<Class> dir;
		try {
			dir = getClasses("games.stendhal.server.script");
			for (final Class clazz : dir) {
				if (CreateRaid.class.isAssignableFrom(clazz)) {
					textToSend += clazz.getSimpleName() + "\n";
				}
			}

		} catch (final ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		admin.sendPrivateText(textToSend);
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<Class> getClasses(final String pckgname)
			throws ClassNotFoundException {
		final ArrayList<Class> classes = new ArrayList<Class>();
		// Get a File object for the package
		File directory = null;
		try {
			final ClassLoader cld = Thread.currentThread().getContextClassLoader();
			if (cld == null) {
				throw new ClassNotFoundException("Can't get class loader.");
			}
			final String path = pckgname.replace('.', '/');
			final URL resource = cld.getResource(path);
			if (resource == null) {
				throw new ClassNotFoundException("No resource for " + path);
			}
			directory = new File(resource.getFile());
		} catch (final NullPointerException x) {
			throw new ClassNotFoundException(pckgname + " (" + directory
					+ ") does not appear to be a valid package");
		}
		if (directory.exists()) {
			// Get the list of the files contained in the package
			final String[] files = directory.list();
			for (int i = 0; i < files.length; i++) {
				// we are only interested in .class files
				if (files[i].endsWith(".class")) {
					// removes the .class extension
					classes.add(Class.forName(pckgname + '.'
							+ files[i].substring(0, files[i].length() - 6)));
				}
			}
		} else {
			throw new ClassNotFoundException(pckgname
					+ " does not appear to be a valid package");
		}

		return classes;
	}
}
