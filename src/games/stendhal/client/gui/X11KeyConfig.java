// $Id$
package games.stendhal.client.gui;

import java.awt.Canvas;
import java.awt.Graphics;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.apache.log4j.Logger;

/**
 * trys to enable DetectableKeyRepeat on X11 using a native library
 * called libX11KeyConfig.so. On webstart it is stored in a native jar
 * called stendhal-webstart-jni-X.xx.jar. In the normal client it is
 * stored in stendhal-data-X.xx.jar and written to $HOME/stendhal.
 *
 * <p>To use it, put it as 1*1 pixel canvas somewhere it has to be drawn
 * (like StendhalFirstScreen). The drawing routine is somehow special as
 * it invokes the native magic. This class tries very hard not to
 * propagate any errors outside. Use getResult() to detect whether it
 * worked. But do not invoke it on non Linux systems.</p>
 *
 * @author hendrik
 */
public class X11KeyConfig extends Canvas {
	private static X11KeyConfig instance = null;
	private static Logger logger = Logger.getLogger(X11KeyConfig.class);

	// don't put this in a static init because it should only be invoked on linux
	private static void load() {
		// first try webstart
		Throwable error = null;
		try {
			System.loadLibrary("X11KeyConfig");
		} catch (Exception e) {
			error = e;
		} catch (Error e) {
			error = e;
		}
		
		if (error != null) {
			error = null;
			try {
				String filename = System.getProperty("user.home") + "/stendhal/libX11KeyConfig.so";
				copyLibraryToHomeFolder(filename);
				System.load(filename);
			} catch (Exception e) {
				error = e;
			} catch (Error e) {
				error = e;
			}
		}
		if (error != null) {
			logger.error(error, error);
		}
	}

	/**
	 * write library to home as there seems to be no way to load a
	 * native library from inside a jar (unless you are on webstart).
	 * @throws IOException on an input/output error
	 */
	private static void copyLibraryToHomeFolder(String filename) throws IOException {
		URL url = X11KeyConfig.class.getClassLoader().getResource("libX11KeyConfig.so");
		InputStream is = url.openConnection().getInputStream();
		OutputStream os = new FileOutputStream(filename);
		try {
			byte[] buffer = new byte[1024];
			while (true) {
				int counter = is.read(buffer);
				if (counter < 0) {
					break;
				}
				os.write(buffer, 0, counter);
			}
			os.close();
		} catch (RuntimeException e) {
			os.close();
			throw e;
		} catch (IOException e) {
			os.close();
			throw e;
		}
	}

	private X11KeyConfig() {
		// hide constructor, this is a static class
	}

	/**
	 * Gets X11KeyConfig (singleton pattern)
	 *
	 * @return X11KeyConfig
	 */
	public static synchronized X11KeyConfig get() {
		if (instance == null) {
			load();
			instance = new X11KeyConfig();
		}
		return instance;
	}

	private static native boolean getSetDetectableAutoRepeat();

	/**
	 * Did the setting of DetectableAutoRepeat work?
	 *
	 * @return true on success; false otherwise
	 */
	public static boolean getResult() {
		try {
			return getSetDetectableAutoRepeat();
		} catch (Exception e) {
			logger.error(e, e);
		} catch (Error e) {
			logger.error(e, e);
		}
		return false;
	}

	/**
	 * draws some stuff as prove of concept but realy invokes
	 * XkbSetDetectableAutoRepeat. We need to do this stupid
	 * drawing thingy in order for the native code to get access
	 * to the x11display (connect to x11 server).
	 */
	@Override
	public native void paint(Graphics g);
}
