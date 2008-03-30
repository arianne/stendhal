package games.stendhal.client.gui;

import games.stendhal.client.gui.wt.core.WtDraggable;

import java.awt.Point;

/**
 * Interface DragDropOwner for drag&drop callbacks.
 *
 * @author Martin Fuchs
 */
public interface DragDropOwner {

	WtDraggable getDragged(Point pt);

	Point getClientPos();

}
