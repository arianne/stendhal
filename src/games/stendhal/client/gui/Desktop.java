package games.stendhal.client.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.image.BufferedImage;

import javax.swing.JDesktopPane;

/**
 * Desktop with double buffered game screen background.
 *
 * @author Martin Fuchs
 */
@SuppressWarnings("serial")
public class Desktop extends JDesktopPane {

	final private Dimension bufferSize;

	private BufferedImage imgDraw;
	private BufferedImage imgPaint;

	public Desktop(GraphicsConfiguration gc, int w, int h) {
		setOpaque(false);

		bufferSize = new Dimension(w, h);

		imgDraw = gc.createCompatibleImage(w, h);
		imgPaint = gc.createCompatibleImage(w, h);
	}

	public Dimension getBufferSize() {
	    return bufferSize;
    }

	/**
	 * Return the drawing context and flip the two buffers.
	 * @return
	 */
	public synchronized Graphics2D getDrawingBuffer() {
		BufferedImage img = imgPaint;

		imgPaint = imgDraw;
		imgDraw = img;

		return (Graphics2D) img.getGraphics();
	}

	/**
	 * Enable transparent mode to paint the game screen in the background.
	 */
	@Override
    public boolean isOpaque() {
        return false;
    }

	@Override
	public void paint(Graphics g) {
		synchronized (this) {
			g.drawImage(imgPaint, 0, 0, imgPaint.getWidth(), imgPaint.getHeight(), Color.black, null);
        }

		super.paint(g);
	}

}
