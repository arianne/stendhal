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
 * 
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

	public native void paint(Graphics g);

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

