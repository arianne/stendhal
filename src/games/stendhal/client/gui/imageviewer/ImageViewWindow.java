package games.stendhal.client.gui.imageviewer;

import games.stendhal.client.gui.ClientPanel;
import games.stendhal.client.gui.j2DClient;

import java.awt.Dimension;
import java.awt.Point;
import java.net.URL;

/**
 * Opens an image resource at a given URL, and displays it in the client.
 * 
 * @author timothyb89
 */
@SuppressWarnings("serial")
public final class ImageViewWindow extends ClientPanel {

	/**
	 * The padding of the window, in pixels, when generating the maximum size.
	 */
	public static final int PADDING = 100;

	private URL url;
	private String alt;

	public ImageViewWindow(URL url) {
		super("Examine", 100, 100);
		setName("examine");

		this.url = url;

		init();
	}

	public ImageViewWindow(URL url, String title) {
		super(title, 100, 100);
		setName("examine");

		this.url = url;
		alt = title;

		init();
	}

	public ImageViewWindow(URL url, String title, String alt) {
		super(title, 100, 100);
		setName("examine");

		this.url = url;
		this.alt = alt;

		init();
	}

	public void init() {
		ImageViewPanel ivp = new ImageViewPanel(this, url, alt);

		add(ivp);

		j2DClient.get().addDialog(this);

		Point center = genCenterPoint();
		setLocation(center.x, center.y);

		setVisible(true);
	}

	public Dimension genMaxSize() {
		int width = j2DClient.SCREEN_WIDTH - PADDING;
		int height = j2DClient.SCREEN_HEIGHT - PADDING;
		return new Dimension(width, height);
	}

	public Point genCenterPoint() {
		int x = (j2DClient.SCREEN_WIDTH - getWidth()) / 2;
		int y = (j2DClient.SCREEN_HEIGHT - getHeight()) / 2;

		return new Point(x, y);
	}

}
