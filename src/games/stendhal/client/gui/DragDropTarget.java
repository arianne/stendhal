package games.stendhal.client.gui;

import games.stendhal.client.gui.wt.core.WtDraggable;
import games.stendhal.client.gui.wt.core.WtDropTarget;

import java.awt.Component;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetListener;

/**
 * DragDropTarget is used to implement drag target objects.
 * 
 * @author Martin Fuchs
 */
public class DragDropTarget {

	private final DropTargetListener dtListener; 

	/**
	 * Constructor taking the owner as parameter.
	 *
	 * @param owner
	 */
	public DragDropTarget(final WtDropTarget callback) {
		dtListener = new DropTargetAdapter() {
			public void drop(DropTargetDropEvent dtde) {
				try {
	                callback.onDrop(dtde, (WtDraggable)dtde.getTransferable().getTransferData(DragTransfer.standhalFlavor));
                } catch(Exception e) {
                }
            }
		};
	}

	public void associate(final Component comp) {
		new DropTarget(comp, DnDConstants.ACTION_MOVE, dtListener, true);
	}

}
