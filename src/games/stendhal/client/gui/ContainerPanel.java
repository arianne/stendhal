package games.stendhal.client.gui;

import games.stendhal.client.IGameScreen;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.gui.layout.SBoxLayout;
import games.stendhal.client.gui.wt.MoveableEntityContainer;
import games.stendhal.client.gui.wt.core.WtBaseframe;
import games.stendhal.client.gui.wt.core.WtDraggable;
import games.stendhal.client.gui.wt.core.WtPanel;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * A wrapper container for WtPanels outside the game screen.
 */
public class ContainerPanel extends JScrollPane {
	/** A map of the children to enable removing them. */
	private final Map<WtPanel, JComponent> children = new HashMap<WtPanel, JComponent>();
	/** The actual content panel. */
	private final JPanel panel;
	/** Components that should be repainted in the game loop.  */
	private final List<JComponent> repaintable = new LinkedList<JComponent>();

	/**
	 * Create a ContainerPanel.
	 */
	public ContainerPanel() {
		panel = new JPanel();
		panel.setLayout(new SBoxLayout(SBoxLayout.VERTICAL));
		setViewportView(panel);
		setBorder(null);
	}
	
	/**
	 * Add a Wt component to the ContainerPanel.
	 * 
	 * @param child component to be added
	 */
	public void addChild(WtPanel child) {
		JComponent wrapper = new WtWrapper(child);
		wrapper.setAlignmentX(LEFT_ALIGNMENT);
		children.put(child, wrapper);
		panel.add(wrapper);
	}
	
	/**
	 * Add a JComponent to the ContainerPanel.
	 * 
	 * @param child component to be added
	 * @param constraints packing constraints
	 */
	public void add(JComponent child, Object constraints) {
		child.setAlignmentX(LEFT_ALIGNMENT);
		panel.add(child, constraints);
	}
	
	/**
	 * Add a component that should be repainted in the drawing loop. This is
	 * not a particularly pretty way to do it, but individual timers for item
	 * slots end up being more expensive, and the RepaintManager merges the
	 * draw request anyway.
	 * 
	 * @param child
	 */
	public void addRepaintable(JComponent child) {
		panel.add(child);
		repaintable.add(child);
	}
	
	/**
	 * Remove a child component from the panel.
	 * 
	 * @param child The Wt component to be removed
	 */
	public void removeChild(WtPanel child) {
		JComponent wrapper = children.get(child);
		if (wrapper != null) {
			panel.remove(wrapper);
		}
	}
	
	/**
	 * Request repainting of all the child panels.
	 */
	public void repaintChildren() {
		for (JComponent child : children.values()) {
			child.repaint();
		}
		for (JComponent child : repaintable) {
			child.repaint();
		}
	}
	
	@Override
	public Dimension getPreferredSize() {
		Dimension size = panel.getPreferredSize();
		JComponent scrollBar = getVerticalScrollBar();
		if (scrollBar.isVisible()) {
			/*
			 * Try to claim a bit more space if the user enlarges the window and
			 * there's not enough space sidewise.
			 */
			size.width += scrollBar.getWidth();
		}
		return size;
	}
	
	/**
	 * A JComponent that can swallow WtPanels. It's meant to enclose slot
	 * containing panels, like the bag and character panel. Mouse handling
	 * will be from the point of the swallowed panel as if it was on the game
	 * screen.
	 */
	private static class WtWrapper extends JComponent implements DropTarget {
		private transient final WtPanel contents;
		private transient final MouseHandlerframe base;
		
		//private Dimension size;
		private boolean minimized;
		
		/**
		 * Create a new WtWrapper.
		 * 
		 * @param contents The Wt component to be wrapped
		 */
		public WtWrapper(WtPanel contents) {
			this.contents = contents;
			setOpaque(true);
			
			// Swing handles positioning
			contents.moveTo(0, 0);
			// Don't let the user close the window. Just allow minimize
			contents.setCloseable(false);
			// Restore any windows the user might have closed in previous
			// versions of the client. The user has no way to regain them
			// otherwise
			contents.setVisible(true);
			// Get the mouse handling from WtBaseFrame
			base = new MouseHandlerframe(contents.getWidth(), contents.getHeight(), null);
			addMouseListener(base);
			addMouseMotionListener(base);
			base.addChild(contents);
		}
		
		@Override
		public void paintComponent(Graphics g) {
			contents.draw((Graphics2D) g);
			
			/*
			 * A hack to get the closed and minimized states correct, until
			 * most of the wt has been properly ported to swing and they can
			 * properly handle their own size changes
			 */
			if (minimized != contents.isMinimized()) {
				minimized = contents.isMinimized();
				revalidate();
			}
		}
		
		@Override
		public Dimension getPreferredSize() {
			Dimension size = new Dimension(contents.getWidth(), contents.getHeight());
			if (contents.isMinimized()) {
				size.height -= contents.getClientHeight();
			}
			
			return size;
		}
		
		@Override
		public Dimension getMinimumSize() {
			return getPreferredSize();
		}

		public void dropEntity(IEntity entity, Point point) {
			base.dropEntity(entity, point);
		}
	}
	
	/**
	 * Mouse handler for WtWrappers
	 */
	private static class MouseHandlerframe extends WtBaseframe implements DropTarget {
		/**
		 * Create a new MouseHandlerframe.
		 * 
		 * @param width width of the enclosed component
		 * @param height height of the enclosed component
		 * @param gameScreen unused
		 */
		public MouseHandlerframe(int width, int height, IGameScreen gameScreen) {
			super(width, height, gameScreen);
		}
		
		@Override
		public WtDraggable getDragged(Point p) {
			WtDraggable dragged = super.getDragged(p);
			/*
			 * Don't allow dragging the panels anywhere even though they think
			 * themselves that they're movable windows.
			 */
			if (dragged instanceof WtPanel) {
				return null;
			}
			return dragged;
		}

		public void dropEntity(IEntity entity, Point point) {
			// Pack the entity in a way that WtDropTargets understand it, and pass
			// it to the Wt pipeline
			checkDropped(point.x, point.y, new MoveableEntityContainer(entity));
		}
	}
}
