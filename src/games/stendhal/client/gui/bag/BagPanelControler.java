package games.stendhal.client.gui.bag;

import games.stendhal.client.gui.styled.WoodStyle;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

public class BagPanelControler implements PropertyChangeListener {

	private BagPanel bagPanel;
	private static BagPanelControler instance;

	
	public BagPanelControler() {
		bagPanel = new BagPanel(WoodStyle.getInstance());
		bagPanel.setSize(200, 200);
		
		bagPanel.setVisible(true);

		
		instance = this;	
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt == null) {
			return;
		}
		System.out.println(evt.getPropertyName() +" was: " + evt.getOldValue() +" now:" +evt.getNewValue() );
		RPSlot bagslotOld = (RPSlot) evt.getOldValue();
		if (bagslotOld != null) {
			for (RPObject object : bagslotOld) {
				bagPanel.removeItem(object);
				

			}
		}

		
		
		
		RPSlot bagslot = (RPSlot) evt.getNewValue();
		if (bagslot != null) {
			for (RPObject object : bagslot) {
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
