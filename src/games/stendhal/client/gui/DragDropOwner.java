package games.stendhal.client.gui;


import java.awt.Point;

/**
 * Interface DragDropOwner for drag&drop callbacks.
 *
 * @author Martin Fuchs
 */
public interface DragDropOwner {

	IDraggable getDragged(Point pt);

	Point getClientPos();

}
