package games.stendhal.client.gui;


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

	public static final DataFlavor standhalFlavor = new DataFlavor(IDraggable.class, "Stendhal draggable");

	private DataFlavor[] flavors = {standhalFlavor};

	private IDraggable draggedObject;

	public DragTransfer(IDraggable draggedObject) {
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
