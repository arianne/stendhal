/**
 * 
 */
package games.stendhal.test;

import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JInternalFrame;
import javax.swing.border.CompoundBorder;

/**
 * @author mtotz
 * 
 */
public class SInternalFrame extends JInternalFrame {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public SInternalFrame(String name) {
		super(name, true, true, false, true);
		setFrameIcon(null);
		setBorder(new CompoundBorder(new ImageBorder(
				ImageBorder.RaisedState.RAISED, 3), new CompoundBorder(
				new ImageBorder(ImageBorder.RaisedState.NORMAL, 3),
				new ImageBorder(ImageBorder.RaisedState.LOWERED, 3))));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JInternalFrame#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		Rectangle r = g.getClipBounds();
		g.fillRect(r.x, r.y, r.width, r.height);

		// for (Component component : getComponents())
		// {
		// System.out.println(component);
		// }
		// super.paintComponent(g);

	}

}
