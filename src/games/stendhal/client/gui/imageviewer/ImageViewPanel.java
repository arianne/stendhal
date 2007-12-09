package games.stendhal.client.gui.imageviewer;

import games.stendhal.client.gui.styled.WoodStyle;
import games.stendhal.client.gui.styled.swing.StyledJPanel;
import java.awt.Dimension;
import java.awt.Image;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.JLabel;

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
        
        //only display when not null
        String caption = "";
        if (alt != null) {
            caption = "<b><i><font color=\"" + FONT_COLOR + "\" size=\"" + FONT_SIZE + "\">" + alt + "</big></i></b><br>";
        }
        
        //only display when not null. we can simply use this to send notifications to the player.
        String img = "";
        if (image != null) {
            img = "<img width=" + width + " height=" + height + " src=" + url.toString() + ">";
        }
        String text = "<html>" + caption + img;
        JLabel imageLabel = new JLabel(text);
        
        add(imageLabel);
        
        setVisible(true);
    }
    
}
