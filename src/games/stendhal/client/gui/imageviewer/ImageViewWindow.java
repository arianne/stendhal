package games.stendhal.client.gui.imageviewer;

import games.stendhal.client.stendhal;
import games.stendhal.client.gui.j2DClient;
import games.stendhal.client.gui.wt.InternalManagedDialog;

import java.awt.Dimension;
import java.awt.Point;

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

	private ViewPanel viewPanel;

	public ImageViewWindow(String title, ViewPanel viewPanel) {
		super("examine", title);
		this.viewPanel = viewPanel;
		init();
	}

	public void init() {
		viewPanel.prepareView(genMaxSize());
		setContent(viewPanel);
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
