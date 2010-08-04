package games.stendhal.client.gui;

import games.stendhal.client.entity.IEntity;
import games.stendhal.client.gui.j2d.entity.Entity2DView;
import games.stendhal.client.gui.j2d.entity.EntityViewFactory;
import games.stendhal.client.gui.j2d.entity.StackableItem2DView;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/**
 * A glass pane component for drawing dragged items. Using ideas (though no
 * code) from Alexander Potochkin of the swing team. His blog entry describing
 * the tricks used:
 * http://weblogs.java.net/blog/2006/09/20/well-behaved-glasspane
 */
public class DragLayer extends JComponent implements AWTEventListener {
	private static DragLayer instance;
	
	/** The dragged entity, or <code>null</code> if nothing is being dragged. */
	private Entity2DView dragged;
	/** Current mouse location. */
	private Point point;
	
	private int oldX, oldY, width, height;
	
	/**
	 * Create a new DragLayer.
	 */
	private DragLayer() {
		/*
		 * Not a pretty way to listen to mouse events, but adding a mouse
		 * movement listener eats any events that should go below. Redispatching
		 * them does not really work (despite being what the official swing
		 * tutorial recommends), because enter and leave events to the
		 * components have already been lost.
		 * 
		 * Adding a mouse movement listener after the drag has started does not
		 * work either, because it does not get the mouse events belonging to
		 * the drag, but starts working only later.
		 */
		Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
		
		setOpaque(false);
	}
	
	@Override
	public boolean contains(int x, int y) {
		/*
		 * To get the correct mouse cursors, the pane needs to lie a bit to
		 * AWT and claim to not contain any point of the screen. Then the mouse
		 * handling is properly passed to the components below.
		 */
		return false;
	}

	/**
	 * Get the DragLayer instance.
	 * 
	 * @return drag layer
	 */
	public static DragLayer get() {
		if (instance == null) {
			instance = new DragLayer(); 
		}
		return instance;
	}
	
	/**
	 * Start dragging an entity. The DragLayer will take care of drawing, 
	 * updating the position and dropping it to the right DropTarget.
	 * 
	 * @param entity dragged entity
	 */
	public void startDrag(IEntity entity) {
		if (entity != null) {
			Entity2DView dragged = (Entity2DView) EntityViewFactory.create(entity);
			/*
			 * Make it contained, so that the view knows to ignore the entity
			 * coordinates
			 */
			dragged.setContained(true);
			/*
			 * Hide quantity until it can be made context sensitive to drag
			 * modifiers.
			 */
			if (dragged instanceof StackableItem2DView) {
				((StackableItem2DView) dragged).setShowQuantity(false);
			}
			
			this.dragged = dragged;
		}
	}
	
	@Override
	public void paintComponent(Graphics g) {
		if ((point != null) && (dragged != null)) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.translate(point.x, point.y);
			dragged.draw(g2d);
		}
	}

	/**
	 * Stop dragging the item, and let the component below the cursor handle
	 * the dropped entity.
	 */
	private void stopDrag() {
		Container parent = getParent();
		Point containerPoint = SwingUtilities.convertPoint(this, point, parent);
		// Find out the underlying component  
		Component component = SwingUtilities.getDeepestComponentAt(parent, containerPoint.x, containerPoint.y);
		
		if ((component != null) && (component instanceof DropTarget)) {
			IEntity entity = dragged.getEntity();
			if (entity != null) {
				Point componentPoint = SwingUtilities.convertPoint(this, point, component);
				
				((DropTarget) component).dropEntity(entity, componentPoint);
			} 
		}

		dragged = null;
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.AWTEventListener#eventDispatched(java.awt.AWTEvent)
	 */
	public void eventDispatched(AWTEvent e) {
		if (e instanceof MouseEvent) { 
			MouseEvent event = (MouseEvent) e; 

			MouseEvent converted = SwingUtilities.convertMouseEvent(event.getComponent(), event, this); 
			point = converted.getPoint();

			if (e.getID() == MouseEvent.MOUSE_DRAGGED) {
				requestDraw();
			} else if ((event.getID() == MouseEvent.MOUSE_RELEASED) && (dragged != null)) {
				stopDrag();
				requestDraw();
				point = null;
			}
			// We are interested only in DnD, so we can ignore any other events
		}
	}
	
	/**
	 * Request drawing the component after a drag related mouse event
	 */
	private void requestDraw() {
		/*
		 * Optimize the repaints a bit. Drawing the whole layer is
		 * expensive due to the paint requests to the components below,
		 * so try to keep the drawn area small.
		 */
		if ((width != 0) && (height != 0)) {
			/*
			 * Paint over the old occupied area. This will probably be 
			 * merged with the next repaint by the RepaintManager.
			 */
			repaint(oldX, oldY, width, height);
		}
		oldX = point.x;
		oldY = point.y;
		if (dragged != null) {
			width = dragged.getWidth();
			height = dragged.getHeight();
			repaint(point.x, point.y, width, height);
		} else {
			width = 0;
			height = 0;
		}
	}
}
