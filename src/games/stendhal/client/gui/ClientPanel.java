package games.stendhal.client.gui;

import games.stendhal.client.gui.styled.WoodStyle;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.border.Border;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

/**
 * ClientPanel is the base class for windows in the new Swing based Stendhal client.
 *
 * @author Martin Fuchs
 */
@SuppressWarnings("serial")
public class ClientPanel extends JInternalFrame {

	private Dimension clntSize;

	/** List of registered CloseListener. */
	protected List<CloseListener> closeListeners = new ArrayList<CloseListener>();

	protected ClientPanel(String title, int width, int height) {
		super(title);

		// set name for unique mnemonics in SettingsPanel.addEntry()
		setName(title);

		clntSize = new Dimension(width, height);

		// Closing only hides the window for further usage.
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		setIconifiable(true);

		installFocusHandling();
	}


	// pretty ugly code to force keyboard input focus to the chat window

	private static Component focusTarget;

	public static void setFocusTarget(Component comp) {
	    focusTarget = comp;
    }

	protected void restoreTargetFocus() {
		if (focusTarget != null) {
			focusTarget.requestFocus();
		}
	}

	private void installFocusHandling() {
		/*
		 * Always redirect the keyboard focus to the chat input field.
		 */
		addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				restoreTargetFocus();
			}

			public void focusLost(FocusEvent e) {
			}
		});

		addInternalFrameListener(new InternalFrameListener() {
			public void internalFrameActivated(InternalFrameEvent e) {
			}

			public void internalFrameClosed(InternalFrameEvent e) {
            }

			public void internalFrameClosing(InternalFrameEvent e) {
            }

			public void internalFrameDeactivated(InternalFrameEvent e) {
            }

			public void internalFrameDeiconified(InternalFrameEvent e) {
				restoreTargetFocus();
            }

			public void internalFrameIconified(InternalFrameEvent e) {
            }

			public void internalFrameOpened(InternalFrameEvent e) {
            }
		});
	}


	/** Returns size of the client area.*/
	public Dimension getClientSize() {
		return clntSize;
	}

	public void setClientSize(int width, int height) {
		clntSize = new Dimension(width, height);

		resizeToFitClientArea();
	}

	/**
	 * Resizes the panel so that the client area has the given width and height.
	 * 
	 * @param width
	 *            width of client area
	 * @param height
	 *            height of client area
	 */
	private void resizeToFitClientArea() {
		Insets insets = getInsets();
		Point clnt = getClientPos();

		setSize(clnt.x + insets.left + clntSize.width + insets.right,
				clnt.y + insets.top + clntSize.height + insets.bottom);
	}

	/** Return position of the client area. */
	protected Point getClientPos() {
		Container clnt = getRootPane();
		Insets insets = getInsets();
		int x, y;

		try {
			x = clnt.getLocationOnScreen().x - getLocationOnScreen().x;
			y = clnt.getLocationOnScreen().y - getLocationOnScreen().y;
		} catch(RuntimeException e) {
			x = 0;
			y = 0;
		}

		return new Point(x + insets.left, y + insets.top);
	}

	/**
	 * Return position and size of the window client area.
	 *
	 * @return Rectangle
	 */
	public Rectangle getClientRect() {
		Container clnt = getRootPane();

		try {
			int x = clnt.getLocationOnScreen().x - getLocationOnScreen().x;
			int y = clnt.getLocationOnScreen().y - getLocationOnScreen().y;

		    return new Rectangle(x, y, clnt.getWidth(), clnt.getHeight());
		} catch(RuntimeException e) {
			return new Rectangle(0, 0, clnt.getWidth(), clnt.getHeight());
		}
    }

	@Override
    public void setVisible(boolean flag) {
		super.setVisible(flag);

		if (flag) {
			resizeToFitClientArea();
		} else if (closeListeners != null) {
			// inform all listeners we're closed
			CloseListener[] listeners = closeListeners.toArray(new CloseListener[closeListeners.size()]);

			for (CloseListener listener : listeners) {
				listener.onClose(getName());
			}
		}
	}

	/**
	 * Adds a CloseListener to this panel. All registered closelistener are
	 * notified before the panel is closed
	 */
	public void registerCloseListener(CloseListener listener) {
		closeListeners.add(listener);

		setClosable(true);
	}

	/** removes a (registered) closelistener. */
	public void removeCloseListener(CloseListener listener) {
		closeListeners.remove(listener);
	}

	public boolean setMinimized(boolean minimized) {
		try {
	        setIcon(true);
	        return true;
        } catch(PropertyVetoException e) {
	        return false;
        }
    }

	public boolean isMinimized() {
	    return isIcon();
    }

    /**
     * Returns the border of this component or <code>null</code> if no 
     * border is currently set.
     *
     * @return the border object for this component
     * @see #setBorder
     */
    public Border getBorder() {
    	return WoodStyle.getInstance().getBorder();
    }

    /**
     * If a border has been set on this component, returns the
     * border's insets; otherwise calls <code>super.getInsets</code>.
     *
     * @return the value of the insets property
     * @see #setBorder
     */
    public Insets getInsets() {
    	return WoodStyle.getInstance().getBorder().getBorderInsets(this);
    }

    @Override
    protected void paintBorder(Graphics g) {
        Border border = getBorder();

        if (border != null) {
            border.paintBorder(this, g, 0, 0, getWidth(), getHeight());
        }
    }

    @Override
    public void dispose() {
		closeListeners.clear();
    }

}
