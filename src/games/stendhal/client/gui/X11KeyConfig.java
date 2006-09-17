// $Id$
package games.stendhal.client.gui;

import java.awt.Canvas;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
public class X11KeyConfig extends Canvas implements KeyListener {
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

			// TODO: write file to home directory
		
			// then try home directory
			try {
				System.load(System.getProperty("user.home") + "/stendhal/libX11KeyConfig.so");
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

	private X11KeyConfig() {
		// hide constructor, this is a static class
	}
	
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
	public native void paint(Graphics g);

	// ------------------------------------------------------------------------
	//                             Test code below
	// ------------------------------------------------------------------------
	public static void main(String[] args) throws InterruptedException {
        Frame f = new Frame();
        f.setBounds(0, 0, 500, 110);
        X11KeyConfig x = X11KeyConfig.get();
        f.add( x );
        f.addWindowListener( new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                System.exit(0);
            }
        } );

        f.addKeyListener(x);
        f.show();
        Thread.sleep(2000);
        System.out.println("Success: " + getSetDetectableAutoRepeat());
    }

	public void keyPressed(KeyEvent e) {
		System.out.println(".");
	}

	public void keyReleased(KeyEvent e) {
		System.out.println("O");
	}

	public void keyTyped(KeyEvent e) {
		System.out.println("_");
		
	}
}

// java -Djava.library.path=data/precompiled -cp classes games.stendhal.client.gui.X11KeyConfig

