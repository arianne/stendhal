// $Id$
package games.stendhal.client.gui;

import org.apache.log4j.Logger;

/**
 * 
 *
 * @author hendrik
 */
public class X11KeyConfig {
	private static Logger logger = Logger.getLogger(X11KeyConfig.class);
	
	private static void load() {
		try {
			System.loadLibrary("X11KeyConfig");
			System.out.println(SetDetectableAutoRepeat());
		} catch (Exception e) {
			logger.error(e, e);
			e.printStackTrace(System.err);
		} catch (Error e) {
			logger.error(e, e);
			e.printStackTrace(System.err);
		}
	}

	private X11KeyConfig() {
		// hide constructor, this is a static class
	}

	public static native boolean SetDetectableAutoRepeat();
	
	public static void main(String[] args) {
		load();
	}
}

// gcc --shared -I/usr/lib/j2sdk1.5-sun/include -I/usr/lib/j2sdk1.5-sun/include/linux games_stendhal_client_gui_X11KeyConfig.c -o libX11KeyConfig.so
