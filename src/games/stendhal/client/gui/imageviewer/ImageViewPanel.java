package games.stendhal.client.gui.imageviewer;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * A JPanel to be viewed from an ImageViewWindow.
 * @author timothyb89
 */
public class ImageViewPanel extends JPanel {
    
    /**
     * The image to be displayed.
     */
    private Image image;
    
    private URL url;
    private String alt;
    private ImageViewWindow imw;

    public ImageViewPanel(ImageViewWindow imw, URL url, String alt) {
        this.url = url;
        this.alt = alt;
        this.imw = imw;
        
        initImage();
        initComponents();
    }
    
    /**
     * Loads the image. Will cause problems if the image does not exist.
     */
    private void initImage() {
        try {
            image = ImageIO.read(url);
            scaleImage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Creates and adds components to draw the image.
     */
    private void initComponents() {
        ImageIcon img = new ImageIcon(image);
        JLabel imageLabel = new JLabel(img);
        add(imageLabel);
        
        if (alt != null) {
            //display the alternate text- 
            //TODO. This works, but doesn't put the caption on top of the image (Graphics.drawString might be better)
            //JLabel altText = new JLabel(alt);
            //altText.setLocation(10, 10);
            //add(altText);
        }
    }
    
    /**
     * Scales the image if it it too large; does nothing if not.
     */
    private void scaleImage() {
        Dimension max = imw.genMaxSize();
        
        int nw = 0;
        int nh = 0;
        boolean scale = false;
        
        // see if we should scale
        if (image.getWidth(null) > max.width) {
            nw = max.width - 2;
            scale = true;
        }
        
        if (image.getHeight(null) > max.height) {
            nh = max.height - 2;
            scale = true;
        }
        
        //scale if needed.
        if (scale) {
            image = image.getScaledInstance(nw, nh, BufferedImage.SCALE_AREA_AVERAGING);
        }
    }
    
}
