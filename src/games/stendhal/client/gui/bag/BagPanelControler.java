package games.stendhal.client.gui.bag;

import games.stendhal.client.gui.styled.WoodStyle;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

public class BagPanelControler implements PropertyChangeListener {
	
	private final Map<String, ItemPanelControler> panelItemMap = new HashMap<String, ItemPanelControler>();

	private static final int CAPACITY = 12;
	private final BagPanel bagPanel;
	private static BagPanelControler instance;
	ItemPanelControler[] itempanels = new ItemPanelControler[CAPACITY];
	public BagPanelControler() {
		Component[] panels = new Component[CAPACITY];
		for (int i = 0; i < CAPACITY; i++) {
			
			itempanels[i] = new ItemPanelControler();
			
			panels[i] = itempanels[i].getComponent();
			
		}
		bagPanel = new BagPanel(WoodStyle.getInstance(), panels);
		bagPanel.setSize(200, 200);
		bagPanel.setVisible(true);
		instance = this;
	}

	public void propertyChange(final PropertyChangeEvent evt) {
		if (evt == null) {
			return;
		}

		final RPSlot bagslotOld = (RPSlot) evt.getOldValue();
		if (bagslotOld != null) {
			for (final RPObject object : bagslotOld) {
				removeItem(object);

			}
		}

		final RPSlot bagslot = (RPSlot) evt.getNewValue();
		if (bagslot != null) {
			for (final RPObject object : bagslot) {
				addItem(object);
			}
		}
	}

	public Component getComponent() {
		return bagPanel;
	}

	public static PropertyChangeListener get() {
		return instance;
	}
	
	void addItem(final RPObject object) {

		try {
			ItemPanelControler panelControler = panelItemMap.get(object.get("id"));

			if (panelControler == null) {
				panelControler = findEmptyPanel();
				panelItemMap.put(object.get("id"), panelControler);
				panelControler.addNew(object);

			} else {
				panelControler.updateValues(object);
			}

		} catch (final Exception e) {
			Logger.getLogger(BagPanelControler.class).error(e);
		}

	}

	private ItemPanelControler findEmptyPanel() {
		for (final ItemPanelControler panel : itempanels) {
			if (panel.isEmpty()) {
				return panel;
			}
		}
		return itempanels[0];
	}

	public void removeItem(final RPObject object) {
		final ItemPanelControler panel = panelItemMap.get(object.get("id"));
		panel.removeItem(object);
	}

}
