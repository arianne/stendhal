// $Id$
package games.stendhal.client.gui;

import java.awt.Canvas;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * 
 *
 * @author hendrik
 */
public class X11KeyConfig extends Canvas implements KeyListener {
	
	private static void load() {
		try {
			System.loadLibrary("X11KeyConfig");
			System.out.println(SetDetectableAutoRepeat());
		} catch (Exception e) {
			e.printStackTrace(System.err);
		} catch (Error e) {
			e.printStackTrace(System.err);
		}
	}

	private X11KeyConfig() {
		// hide constructor, this is a static class
	}

	public static native boolean SetDetectableAutoRepeat();

	public native void paint(Graphics g);

	public static void main(String[] args) {
		load();
		
        Frame f = new Frame();
        f.setBounds(0, 0, 500, 110);
        f.add( new X11KeyConfig() );
        f.addWindowListener( new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                System.exit(0);
            }
        } );
        f.addKeyListener(new X11KeyConfig());
        f.show();
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

