package games.stendhal.client.gui.laf;

import games.stendhal.client.sprite.SpriteStore;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.plaf.synth.SynthConstants;
import javax.swing.plaf.synth.SynthContext;
import javax.swing.plaf.synth.SynthPainter;

/**
 * Experimental painter class for buttons in the Wood look and feel.
 *
 * @author Martin Fuchs
 */
public class WoodStyleButtonPainter extends SynthPainter {

	private BufferedImage img;
	private BufferedImage imgShifted;

	public WoodStyleButtonPainter(String textureName, int shift) {
		try {
	        img = ImageIO.read(SpriteStore.get().getResourceURL(textureName));

	        // create shifted button image
        	int w = img.getWidth();
        	int h = img.getHeight();

        	GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        	imgShifted = gc.createCompatibleImage(w, h);
	        Graphics mg = imgShifted.createGraphics();

	        mg.drawImage(img, shift, shift - h, w, h, null);
			mg.drawImage(img, shift, shift, w, h, null);
			mg.drawImage(img, shift - w, shift - h, w, h, null);
			mg.drawImage(img, shift - w, shift, w, h, null);

			mg.dispose();
		} catch (Exception e) {
	        e.printStackTrace();
	        img = null;
        }
	}


	@Override
    public void paintButtonBackground(SynthContext context, Graphics g, int x, int y, int w, int h) {
		if ((context.getComponentState() & SynthConstants.PRESSED) != 0) {
			WoodStylePainter.drawTiles(g, imgShifted, x, y, x + w, y + h);
		} else {
			WoodStylePainter.drawTiles(g, img, x, y, x + w, y + h);
		}
    }

	@Override
    public void paintButtonBorder(SynthContext context, Graphics g, int x, int y, int w, int h) {
    }


	@Override
    public void paintToggleButtonBackground(SynthContext context, Graphics g, int x, int y, int w, int h) {
		paintButtonBackground(context, g, x, y, w, h);
    }

	@Override
    public void paintToggleButtonBorder(SynthContext context, Graphics g, int x, int y, int w, int h) {
		paintButtonBorder(context, g, x, y, w, h);
    }

}
