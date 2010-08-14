package games.stendhal.client.gui;

import games.stendhal.client.IGameScreen;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.gui.layout.SBoxLayout;
import games.stendhal.client.gui.wt.MoveableEntityContainer;
import games.stendhal.client.gui.wt.core.WtBaseframe;
import games.stendhal.client.gui.wt.core.WtPanel;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.HashMap;
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
	/** The actual content panel */
	private final JPanel panel;

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
		children.put(child, wrapper);
		panel.add(wrapper);
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

		public void dropEntity(IEntity entity, Point point) {
			// Pack the entity in a way that WtDropTargets understand it, and pass
			// it to the Wt pipeline
			checkDropped(point.x, point.y, new MoveableEntityContainer(entity));
		}
	}
}
