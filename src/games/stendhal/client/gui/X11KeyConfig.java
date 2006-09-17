// $Id$
package games.stendhal.client.gui;

import java.awt.Canvas;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * 
 *
 * @author hendrik
 */
public class X11KeyConfig extends Canvas implements KeyListener {
	private static X11KeyConfig instance = null;
	private static Logger logger = Logger.getLogger(X11KeyConfig.class);
	
	private static void load() {
		try {
			if (false) { // webstart
				System.loadLibrary("X11KeyConfig");
			} else {
				System.load(System.getProperty("user.home") + "/stendhal/libX11KeyConfig.so");
			}
		} catch (Exception e) {
			logger.error(e, e);
		} catch (Error e) {
			logger.error(e, e);
		}
	}

	private X11KeyConfig() {
		// hide constructor, this is a static class
	}
	
	public static synchronized X11KeyConfig get() {
		if (instance == null) {
			instance = new X11KeyConfig();
			/*String temp = System.getProperty("java.library.path", "");
			if (!temp.equals("")) {
				temp =  ":" + temp;
			}
			System.getProperties().list(System.out);
			temp = System.getProperty("java.class.path") + temp;
			System.setProperty("java.library.path", temp);
			System.out.println(System.getProperty("java.library.path", ""));
			try {
				Runtime.getRuntime().exec("export LD_LIBRARY_PATH=.");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			
			load();
		}
		return instance;
	}

	public static native boolean getSetDetectableAutoRepeat();

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

