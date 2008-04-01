package games.stendhal.client.gui;

import games.stendhal.client.IGameScreen;
import games.stendhal.client.StendhalClient;
import games.stendhal.client.entity.EntityView;
import games.stendhal.client.gui.wt.MoveableEntityContainer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Point;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.swing.JDesktopPane;

import marauroa.common.game.RPAction;

/**
 * Desktop with double buffered game screen background.
 *
 * @author Martin Fuchs
 */
@SuppressWarnings("serial")
public class Desktop extends JDesktopPane implements IDropTarget, DragDropOwner {

	final private Dimension bufferSize;

	private final Point offset;

	private BufferedImage imgDraw;
	private BufferedImage imgPaint;

	public Desktop(GraphicsConfiguration gc, int x, int w, int h) {
		offset = new Point(x, 0);
		setOpaque(false);

		bufferSize = new Dimension(w, h);

		imgDraw = gc.createCompatibleImage(w, h);
		imgPaint = gc.createCompatibleImage(w, h);

		new DragDropTarget(this).associate(this);
		new DragDropSource(this).associate(this);
	}

	public Point getOffset() {
	    return offset;
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
			g.drawImage(imgPaint, offset.x, offset.y, imgPaint.getWidth(), imgPaint.getHeight(), Color.black, null);
        }

		super.paint(g);
	}

	
	// from GroundContainer

	public Point getClientPos() {
	    return offset;
    }

	public IDraggable getDragged(Point pt) {
		IGameScreen screen = StendhalClient.get().getScreen();

		Point2D point = screen.convertScreenViewToWorld(pt);
		EntityView view = screen.getMovableEntityViewAt(point.getX(), point.getY());

		// only Items can be dragged
		if (view != null) {
			return new MoveableEntityContainer(view.getEntity());
		}

		return null;
    }


	/** called when an object is dropped. */
	public boolean onDrop(DropTargetDropEvent dsde, IDraggable droppedObject) {
		// Not an entity?
		if (!(droppedObject instanceof MoveableEntityContainer)) {
			return false;
		}

		MoveableEntityContainer container = (MoveableEntityContainer) droppedObject;

		RPAction action = new RPAction();

		if (container.isContained()) {
			// looks like an drop
			action.put("type", "drop");
		} else {
			// it is a displace
			action.put("type", "displace");
		}

		// HACK: if ctrl is pressed, attempt to split stackables
		int dragAction = dsde.getDropAction();

		// Was CTRL pressed?
		if (dragAction == DnDConstants.ACTION_COPY) {
			action.put("quantity", 1);
		}

		// fill 'moved from' parameters
		container.fillRPAction(action);

		StendhalClient client = StendhalClient.get();

		// 'move to'
		Point pt = dsde.getLocation();

		pt.x -= offset.x;
		pt.y -= offset.y;

		Point2D point = client.getScreen().convertScreenViewToWorld(pt);
		action.put("x", (int) point.getX());
		action.put("y", (int) point.getY());

		client.send(action);

		return true;
	}

}
