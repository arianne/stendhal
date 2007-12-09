package games.stendhal.client.gui.imageviewer;

import games.stendhal.client.gui.styled.WoodStyle;
import games.stendhal.client.gui.styled.swing.StyledJPanel;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

/**
 * A JPanel to be viewed from an ImageViewWindow.
 * @author timothyb89
 */
public class ImageViewPanel extends StyledJPanel {
    
    /**
     * The image to be displayed.
     */
    private Image image;
    
    private URL url;
    private String alt;
    private ImageViewWindow imw;

    private JLayeredPane layers;
    
    public static final String FONT_COLOR = "#FFFFFF";
    public static final String FONT_SIZE = "5";

    public ImageViewPanel(ImageViewWindow imw, URL url, String alt) {
        super(WoodStyle.getInstance());
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
            // we load the image twice for scaling purposes (height and width).
            // maybe there's a better way?
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
        Dimension max = imw.genMaxSize();
        int width = image.getWidth(null);        
        int height = image.getHeight(null);
        
        if (image.getWidth(null) > max.width) {
            width = max.width - 2;
        }
        if (image.getHeight(null) > max.height) {
            height = max.height - 2;
        }
        
        String caption = "";
        if (alt != null) {
            caption = "<b><i><font color=\"" + FONT_COLOR + "\" size=\"" + FONT_SIZE + "\">" + alt + "</big></i></b><br>";
        }
        String img = "<img width=" + width + " height=" + height + " src=" + url.toString() + ">";
        String text = "<html>" + caption + img;
        JLabel imageLabel = new JLabel(text);
        
        add(imageLabel);
        
        setVisible(true);
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
