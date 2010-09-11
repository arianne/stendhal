package games.stendhal.client.gui;

import games.stendhal.client.entity.EntityChangeListener;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.factory.EntityFactory;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;

import org.apache.log4j.Logger;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * A view of an RPSlot in a grid of ItemPanels.
 */
public class SlotGrid extends JComponent implements EntityChangeListener {
	private static final int PADDING = 1;
	private static final Logger logger = Logger.getLogger(SlotGrid.class);
	
	/** All shown item panels */
	private final List<ItemPanel> panels;
	/** The parent entity of the shown slot */
	private IEntity parent;
	/** Name of the shown slot */
	private String slotName;
	/** A slot containing the shown entities */
	private RPSlot shownSlot;
	
	public SlotGrid(int width, int height) {
		setLayout(new GridLayout(height, width, PADDING, PADDING));
		panels = new ArrayList<ItemPanel>();
		
		for (int i = 0; i < width * height; i++) {
			ItemPanel panel = new ItemPanel(null, null);
			panels.add(panel);
			add(panel);
		}
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
	
	public void entityChanged(IEntity entity, Object property) {
		if (property == IEntity.PROP_CONTENT) {
			rescanSlotContent();
		}
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
}
