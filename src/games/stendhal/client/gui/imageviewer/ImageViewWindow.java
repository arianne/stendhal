package games.stendhal.client.gui.imageviewer;

import games.stendhal.client.gui.j2DClient;
import games.stendhal.client.stendhal;
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

	private final URL url;
	private String alt;

	public ImageViewWindow(final URL url) {
		this(url, "Examine", "Examine");
	}

	public ImageViewWindow(final URL url, final String title) {
		this(url, title, title);
	}

	public ImageViewWindow(final URL url, final String title, final String alt) {
		super("examine", title);

		this.url = url;
		this.alt = alt;

		init();
	}

	public void init() {
		final ImageViewPanel ivp = new ImageViewPanel(url, alt, genMaxSize());
		setContent(ivp);
		j2DClient.get().addWindow(this);
		final Point center = genCenterPoint();
		this.moveTo(center.x, center.y);
		view();
	}

	public void view() {
		setVisible(true);
	}

	public Dimension genMaxSize() {
		final int width = (int) (stendhal.screenSize.getWidth() - PADDING);
		final int height = (int) (stendhal.screenSize.getHeight() - PADDING);
		return new Dimension(width, height);
	}

	public Point genCenterPoint() {
		final int x = (int) ((stendhal.screenSize.getWidth()  - this.getDialog().getWidth()) / 2);
		final int y = (int) ((stendhal.screenSize.getHeight() - this.getDialog().getHeight()) / 2);

		return new Point(x, y);
	}

}
