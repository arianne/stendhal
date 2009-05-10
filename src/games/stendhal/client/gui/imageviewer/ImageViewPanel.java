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
 * 
 * @author timothyb89
 */
@SuppressWarnings("serial")
public class ImageViewPanel extends StyledJPanel {

	public static final String FONT_COLOR = "#FFFFFF";
	public static final String FONT_SIZE = "5";
	/**
	 * The image to be displayed.
	 */
	private Image image;

	private final URL url;
	private final String alt;
	public ImageViewPanel(final URL url, final String alt, final Dimension maxSize) {
		super(WoodStyle.getInstance());
		this.url = url;
		this.alt = alt;
		initImage();
		initComponents(maxSize);
	}

	/**
	 * Loads the image. Will cause problems if the image does not exist.
	 */
	private void initImage() {
		try {
			// we load the image twice for scaling purposes (height and width).
			// maybe there's a better way?
			image = ImageIO.read(url);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates and adds components to draw the image.
	 * @param maxSize TODO
	 */
	private void initComponents(final Dimension maxSize) {
		final Dimension max = maxSize;
		int width = image.getWidth(null);
		int height = image.getHeight(null);

		if (image.getWidth(null) > max.width) {
			width = max.width - 2;
		}
		if (image.getHeight(null) > max.height) {
			height = max.height - 2;
		}

		// only display when not null
		String caption = "";
		if (alt != null) {
			caption = "<b><i><font color=\"" + FONT_COLOR + "\" size=\""
					+ FONT_SIZE + "\">" + alt + "</big></i></b><br>";
		}

		
		final String img = "<img width=" + width + " height=" + height + " src="
					+ url.toString() + ">";
		
		final String text = "<html>" + caption + img;
		final JLabel imageLabel = new JLabel(text);

		add(imageLabel);

		setVisible(true);
	}

}
