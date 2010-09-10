package games.stendhal.client.gui;

import games.stendhal.client.entity.EntityChangeListener;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.User;
import games.stendhal.client.entity.factory.EntityFactory;

import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

import org.apache.log4j.Logger;

/**
 * A window for showing contents of an entity's slot in a grid of ItemPanels
 */
public class SlotWindow extends InternalManagedWindow implements EntityChangeListener {
	/** Padding between the ItemPanels */
	private static final int PADDING = 1;
	/**
	 * when the player is this far away from the container, the panel is closed.
	 */
	private static final int MAX_DISTANCE = 4;
	private static final Logger logger = Logger.getLogger(SlotWindow.class);
	
	/** All shown item panels */
	private final List<ItemPanel> panels;
	/** The parent entity of the shown slot */
	private IEntity parent;
	/** Name of the shown slot */
	private String slotName;
	/** A slot containing the shown entities */
	private RPSlot shownSlot;
	
	/**
	 * Create a new EntityContainer.
	 * 
	 * @param title window title
	 * @param width number of slot columns
	 * @param height number of slot rows
	 */
	public SlotWindow(String title, int width, int height) {
		super(title, title);
		
		JComponent content = new JComponent() {};
		content.setLayout(new GridLayout(height, width, PADDING, PADDING));
		panels = new ArrayList<ItemPanel>();
		
		for (int i = 0; i < width * height; i++) {
			ItemPanel panel = new ItemPanel(null, null);
			panels.add(panel);
			content.add(panel);
		}
		setContent(content);
	}
	
	/**
	 * Sets the parent entity of the window.
	 * 
	 * @param parent
	 * @param slot
	 */
	public void setSlot(final IEntity parent, final String slot) {
		this.parent = parent;
		this.slotName = slot;

		/*
		 * Reset the container info for all holders
		 */
		for (final ItemPanel panel : panels) {
			panel.setParent(parent);
			panel.setName(slot);
		}

		parent.addChangeListener(this);
		shownSlot = null;
		rescanSlotContent();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		checkDistance();
	}
	
	/**
	 * Rescans the content of the slot.
	 * 
	 * @param gameScreen
	 */
	private void rescanSlotContent() {
		if ((parent == null) || (slotName == null)) {
			return;
		}

		final RPSlot rpslot = parent.getSlot(slotName);
		
		// Skip if not changed
		if ((shownSlot != null) && shownSlot.equals(rpslot)) {
			return;
		}

		final Iterator<ItemPanel> iter = panels.iterator();

		/*
		 * Fill from contents
		 */
		if (rpslot != null) {
			RPSlot newSlot = (RPSlot) rpslot.clone();

			for (final RPObject object : newSlot) {
				if (!iter.hasNext()) {
					logger.error("More objects than slots: " + slotName);
					break;
				}

				IEntity entity = EntityFactory.createEntity(object);

				if (entity == null) {
					logger.warn("Unable to find entity for: " + object,
							new Throwable("here"));
					entity = EntityFactory.createEntity(object);
				}

				iter.next().setEntity(entity);
			}
			
			shownSlot = newSlot;
		} else {
			shownSlot = null;
			logger.error("No slot found: " + slotName);
		}

		/*
		 * Clear remaining holders
		 */
		while (iter.hasNext()) {
			iter.next().setEntity(null);
		}
	}
	
	/**
	 * Check the distance of the player to the base item. When the player is too
	 * far away, this panel closes itself.
	 * 
	 * @param gameScreen
	 */
	private void checkDistance() {
		final User user = User.get();

		if ((user != null) && (parent != null)) {
			// null checks are fixes for Bug 1825678:
			// NullPointerException happened
			// after double clicking one
			// monster and a fast double
			// click on another monster

			if (parent.isUser()) {
				// We don't want to close our own stuff
				return;
			}

			checkDistance(user.getX(), user.getY());
		}
	}
	
	/**
	 * The user position changed.
	 * 
	 * @param x
	 *            The X coordinate (in world units).
	 * @param y
	 *            The Y coordinate (in world units).
	 */
	public void checkDistance(final double x, final double y) {
		/*
		 * Check if the user has moved too far away
		 */
		final int px = (int) x;
		final int py = (int) y;

		final int ix = (int) parent.getX();
		final int iy = (int) parent.getY();

		final Rectangle2D orig = parent.getArea();
		orig.setRect(orig.getX() - MAX_DISTANCE, orig.getY() - MAX_DISTANCE,
				orig.getWidth() + MAX_DISTANCE * 2, orig.getHeight()
						+ MAX_DISTANCE * 2);

		if (!orig.contains(px, py)) {
			logger.debug("Closing " + slotName + " container because " + px
					+ "," + py + " is too far from (" + ix + "," + iy + "):"
					+ orig);
			close();
		}
	}
	
	public void entityChanged(IEntity entity, Object property) {
		if (property == IEntity.PROP_CONTENT) {
			rescanSlotContent();
		}
	}
}
