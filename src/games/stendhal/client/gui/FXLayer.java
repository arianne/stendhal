package games.stendhal.client.gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Transparency;

public class FXLayer {

	private Image image;

	private Graphics2D g;

	private int width, height;

	private AlphaComposite myAlpha;

	private RenderingHints speedHint;

	public static final int NIGHT = 1;

	public static final int OVERCAST = 2;

	public static final int DISABLED = 0;

	private int mode = DISABLED;

	public FXLayer(int width, int height) {
		this.width = width;
		this.height = height;

		// create an image the size of the screen.
		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		image = gc.createCompatibleImage(width, height, Transparency.BITMASK);
		g = (Graphics2D) image.getGraphics();

		// 50% transparent (or 50% opaque, depending on how you look at it).
		myAlpha = AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.5f);
		speedHint = new RenderingHints(RenderingHints.KEY_ALPHA_INTERPOLATION,
				RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public void draw(Graphics2D screen) {
		switch (mode) {
		case NIGHT:
			g.setColor(new Color(0, 0, 150));
			break;
		case OVERCAST:
			g.setColor(Color.black);
			break;
		case DISABLED:
			return;
		default:

		}

		g.fillRect(0, 0, width, height);

		Composite orig = screen.getComposite();

		screen.setRenderingHints(speedHint);
		screen.setComposite(myAlpha);

		screen.drawImage(image, 0, 0, null);

		screen.setComposite(orig);
	}

	/*
	 * if(e.getKeyChar() == 'n') { fx.setMode(FXLayer.NIGHT); } else
	 * if(e.getKeyChar() == 'd') { fx.setMode(FXLayer.DISABLED); } else
	 * if(e.getKeyChar() == 'o') { fx.setMode(FXLayer.OVERCAST); }
	 */
}
