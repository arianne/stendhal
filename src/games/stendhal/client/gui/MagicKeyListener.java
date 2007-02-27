package games.stendhal.client.gui;

import java.awt.Component;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import javax.swing.SwingUtilities;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

/****************************************************************************
 * Copyright (C) 2001 by the Massachusetts Institute of Technology,
 *                       Cambridge, Massachusetts.
 *
 *                        All Rights Reserved
 *
 * Permission to use, copy, modify, and distribute this software and
 * its documentation for any purpose and without fee is hereby
 * granted, provided that the above copyright notice appear in all
 * copies and that both that copyright notice and this permission
 * notice appear in supporting documentation, and that MIT's name not
 * be used in advertising or publicity pertaining to distribution of
 * the software without specific, written prior permission.
 *
 * THE MASSACHUSETTS INSTITUTE OF TECHNOLOGY DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE, INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS.  IN NO EVENT SHALL THE MASSACHUSETTS
 * INSTITUTE OF TECHNOLOGY BE LIABLE FOR ANY SPECIAL, INDIRECT OR
 * CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS
 * OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT,
 * NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN
 * CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *
 ***************************************************************************/

/**
 * A MagicKeyListener is decorator for a KeyListener.
 * 
 * <p>
 * This class adds three pieces of functionality. First, it delays key events
 * (moving them to the back of the event queue). Second, it maintains state so
 * that when a press-and-release event pair is sitting in the queue, neither
 * event is propogated to the adaptee (decoratee). Finally, it can (optionally)
 * add to the semantics so that any release event implies that any still-pressed
 * keys have also been released.
 * 
 * <p>
 * Together, these additions may provide more meaningful semantics of key
 * listening in an environment where a key being held down generates repeated
 * key events, or where multiple keys pressed generate a release event for only
 * one of them.
 */
public class MagicKeyListener implements KeyListener {

	/**
	 * @requires adaptee != null
	 * 
	 * @effects creates a new MagicKeyListener without the generation of
	 *          additional key release events (the third option given in the
	 *          class overview is disabled).
	 */
	public MagicKeyListener(KeyListener adaptee) {
		this(adaptee, false);
	}

	/**
	 * @requires adaptee != null
	 * 
	 * @param assumeAllReleased
	 *            enables the third option listed in the class overview, namely
	 *            that any key release event implies that all keys have been
	 *            released.
	 * 
	 * @effects creates a new MagicKeyListener.
	 */
	public MagicKeyListener(KeyListener adaptee, boolean assumeAllReleased) {
		if (adaptee == null) {
			throw new IllegalArgumentException();
		}
		this.adaptee = adaptee;
		this.assumeAllReleased = assumeAllReleased;
	}

	private final KeyListener adaptee;

	private final Set<Integer> real = new HashSet<Integer>();

	private final Set<Integer> announced = new HashSet<Integer>();

	private final boolean assumeAllReleased;

	//
	// Rep Invariant:
	// adaptee, real, announced != null;
	//

	//
	// Abstration Function:
	// We represent a wrapper around <adaptee>. We know that the keys
	// in <real> are currently pressed by the user, but we have only
	// passed enough state to the adaptee for it to know that the keys
	// in <announced> are currently pressed by the user.
	//

	/**
	 * @returns an immutable object which is representative of the key
	 *          associated with the given event
	 */
	private static Integer marker(KeyEvent e) {
		return new Integer(e.getKeyCode());
	}

	/**
	 * @returns an event which is constructed from the given immutable key (from
	 *          the marker method) and a template event.
	 */
	private static KeyEvent eventFromMarker(Integer marker, KeyEvent e) {
		Component source = e.getComponent();
		int id = e.getID();
		long when = e.getWhen();
		int modifiers = e.getModifiers();
		int keyCode = marker.intValue();
		char keyChar = e.getKeyChar();

		return new KeyEvent(source, id, when, modifiers, keyCode, keyChar);
	}

	/**
	 * @effects Acts on the given event as specified in the class overview.
	 */
	public void keyPressed(KeyEvent e) {
		real.add(marker(e));
		SwingUtilities.invokeLater(new KeyPressedLater(e));
	}

	/**
	 * @effects Acts on the given event as specified in the class overview.
	 */
	public void keyReleased(KeyEvent e) {
		real.remove(marker(e));
		SwingUtilities.invokeLater(new KeyReleasedLater(e));

		if (assumeAllReleased) {
			while (!real.isEmpty()) {
				Integer marker;
				{
					Iterator<Integer> chooser = real.iterator();
					marker = chooser.next();
					chooser.remove();
				}
				KeyEvent event = eventFromMarker(marker, e);
				SwingUtilities.invokeLater(new KeyReleasedLater(event));
			}
		}
	}

	/**
	 * @effects Acts on the given event as specified in the class overview
	 */
	public void keyTyped(KeyEvent e) {
		SwingUtilities.invokeLater(new KeyTypedLater(e));
	}

	/**
	 * A simple class which forms a closure around a key typed event. When run,
	 * it fires the event to the keyTyped method of the adaptee.
	 */
	private class KeyTypedLater implements Runnable {
		private final KeyEvent event;

		private KeyTypedLater(KeyEvent event) {
			this.event = event;
		}

		public void run() {
			adaptee.keyTyped(event);
		}
	}

	/**
	 * A simple class which forms a closure around an key pressed event. When
	 * run, it fires the event to the keyPressed method of the adaptee only if
	 * the pressed keyset still contains this key and the adaptee has not
	 * already been informed.
	 */
	private class KeyPressedLater implements Runnable {
		private final KeyEvent event;

		private KeyPressedLater(KeyEvent event) {
			this.event = event;
		}

		public void run() {
			Integer key = marker(event);
			if (real.contains(key) && !announced.contains(key)) {
				announced.add(key);
				adaptee.keyPressed(event);
			}
		}
	}

	/**
	 * A simple class which forms a closure around an key released event. When
	 * run, it fires the event to the keyReleased method of the adaptee only if
	 * the pressed keyset does not contains this key and the adaptee has not
	 * already been informed of the release.
	 */
	private class KeyReleasedLater implements Runnable {
		private final KeyEvent event;

		private KeyReleasedLater(KeyEvent event) {
			this.event = event;
		}

		public void run() {
			Integer key = marker(event);
			if (!real.contains(key) && announced.contains(key)) {
				announced.remove(key);
				adaptee.keyReleased(event);
			}
		}
	}

}
