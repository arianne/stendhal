package games.stendhal.client.gui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceAdapter;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.image.BufferedImage;

/**
 * DragDropSource is used for implementing drag source objects.
 *
 * @author Martin Fuchs
 */
public class DragDropSource extends DragSourceAdapter implements DragGestureListener {

	private DragDropOwner owner;

	/** The currently dragged object or null if there is no such drag operation. */
	private IDraggable draggedObject;

	/**
	 * Constructor taking the owner as parameter.
	 *
	 * @param owner
	 */
	public DragDropSource(DragDropOwner owner) {
		this.owner = owner;
	}

	public void associate(Component comp) {
		DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(comp, DnDConstants.ACTION_COPY_OR_MOVE, this);
	}

	/**
	 * Invoked when a mouse button is pressed on a component and then dragged.
	 * This event will find the panel just under the mouse cursor and starts to
	 * drag (if the panel allows it)
	 */
	public void dragGestureRecognized(DragGestureEvent dge) {
		Point pt = dge.getDragOrigin();

		Point clnt = owner.getClientPos();

		pt.x -= clnt.x;
		pt.x -= clnt.y;

		draggedObject = owner.getDragged(pt);

		// did we get an object
		if (draggedObject != null) {
			// do the object want to be dragged
			if (draggedObject.dragStarted()) {
				// start drag
				Transferable transferable = new DragTransfer(draggedObject);

			    Toolkit tk = Toolkit.getDefaultToolkit();
			    Dimension size = tk.getBestCursorSize(32, 32);
			    Image image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);

			    Graphics g = image.getGraphics();
			    draggedObject.drawDragged(g);
			    g.dispose();

				Cursor crsr = tk.createCustomCursor(image, new Point(0,0), draggedObject.toString());

				dge.startDrag(crsr, null, pt, transferable, this);
			} else {
				// dragging disabled
				draggedObject = null;
			}
		}
	}

	public void dragDropEnd(DragSourceDropEvent dsde) {
		// be sure to stop dragging operations when the left button is released
		if (draggedObject != null) {
			draggedObject.dragFinished(dsde.getLocation());
		}
    }

}
