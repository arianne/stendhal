package games.stendhal.client.gui.bag;

import games.stendhal.client.gui.styled.WoodStyle;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

public class BagPanelControler implements PropertyChangeListener {

	private final BagPanel bagPanel;
	private static BagPanelControler instance;

	public BagPanelControler() {
		bagPanel = new BagPanel(WoodStyle.getInstance());
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
				bagPanel.removeItem(object);

			}
		}

		final RPSlot bagslot = (RPSlot) evt.getNewValue();
		if (bagslot != null) {
			for (final RPObject object : bagslot) {
				bagPanel.addItem(object);
			}
		}
	}

	public Component getComponent() {
		return bagPanel;
	}

	public static PropertyChangeListener get() {
		return instance;
	}

}
