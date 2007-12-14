package games.stendhal.client.gui.imageviewer;

import games.stendhal.client.gui.j2DClient;
import games.stendhal.client.gui.wt.InternalManagedDialog;
import java.awt.Dimension;
import java.awt.Point;
import java.net.URL;

/**
 * Opens an image resource at a given URL, and displays it in the client.
 * 
 * @author timothyb89
 */
public class ImageViewWindow extends InternalManagedDialog {

	/**
	 * The padding of the window, in pixels, when generating the maximum size.
	 */
	public static final int PADDING = 100;

	private URL url;
	private String alt;

	public ImageViewWindow(URL url) {
		super("examine", "Examine");

		this.url = url;

		init();

	}

	public ImageViewWindow(URL url, String title) {
		super("examine", title);

		this.url = url;
		alt = title;

		init();
	}

	public ImageViewWindow(URL url, String title, String alt) {
		super("examine", title);

		this.url = url;
		this.alt = alt;

		init();
	}

	public void init() {
		ImageViewPanel ivp = new ImageViewPanel(this, url, alt);
		setContent(ivp);
		j2DClient.get().addWindow(this);
		Point center = genCenterPoint();
		this.moveTo(center.x, center.y);
		view();
	}

	public void view() {
		setVisible(true);
	}

	public Dimension genMaxSize() {
		int width = j2DClient.SCREEN_WIDTH - PADDING;
		int height = j2DClient.SCREEN_HEIGHT - PADDING;
		return new Dimension(width, height);
	}

	public Point genCenterPoint() {
		int x = (j2DClient.SCREEN_WIDTH - this.getDialog().getWidth()) / 2;
		int y = (j2DClient.SCREEN_HEIGHT - this.getDialog().getHeight()) / 2;

		return new Point(x, y);
	}

}
