package games.stendhal.client.gui;

import games.stendhal.client.gui.wt.core.WtDraggable;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * DragTransfer represents draggable objects in the Java DND classes.
 *
 * @author Martin Fuchs
 */
public class DragTransfer implements Transferable {

	public final static DataFlavor standhalFlavor = new DataFlavor(WtDraggable.class, "Stendhal draggable");

	private DataFlavor[] flavors = {standhalFlavor};

	private WtDraggable draggedObject;

	public DragTransfer(WtDraggable draggedObject) {
		this.draggedObject = draggedObject;
	}

	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (flavor.equals(standhalFlavor)) {
			return draggedObject;
		} else {
			return null;
		}
	}

	public DataFlavor[] getTransferDataFlavors() {
		return flavors;
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return standhalFlavor.equals(flavor);
	}

}
