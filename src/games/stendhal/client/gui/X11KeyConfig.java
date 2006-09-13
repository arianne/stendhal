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
	
	static {
		try {
			System.loadLibrary("stendhalx11keyconfig");
		} catch (Exception e) {
			logger.error(e, e);
		} catch (Error e) {
			logger.error(e, e);
		}
	}

	private X11KeyConfig() {
		// hide constructor, this is a static class
		System.loadLibrary("");
	}

	public static native boolean SetDetectableAutoRepeat();
}
