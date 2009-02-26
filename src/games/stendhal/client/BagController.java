package games.stendhal.client;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

public class BagController implements PropertyChangeListener {
	private static JFrame frame; 
	private DefaultListModel dlm = new DefaultListModel();
	private JList list = new JList(dlm);
	
	BagController() {
		getBagGui();
			
	}

	private synchronized void  getBagGui() {
		if (frame == null) {
			
			
			frame = new JFrame();
			frame.add(list);
			frame.setVisible(true);
			frame.setSize(100, 400);
		}
	}
	
	public void propertyChange(final PropertyChangeEvent evt) {
	
		if (evt == null) {
			processDeleted();
			return;
		}
		System.out.println(evt.toString());
		if (evt.getNewValue() != null) {
			processAdded(evt);
		} else {
			processRemoved(evt);
		}

	}

	private void processRemoved(final PropertyChangeEvent evt) {
		//System.out.println("removed" + evt.getOldValue());
		if (evt.getOldValue() instanceof RPSlot) {
			RPSlot slot = (RPSlot) evt.getOldValue();
			for (RPObject item : slot) {
				dlm.removeElement(item.getID().getObjectID());
				
			}
		} 
	}

	private void processAdded(final PropertyChangeEvent evt) {
	
		//System.out.println("added" + evt.getNewValue());
		if (evt.getNewValue() instanceof RPSlot) {
			RPSlot slot = (RPSlot) evt.getNewValue();
			for (RPObject item : slot) {
				int objectID = item.getID().getObjectID();
				if (dlm.contains(objectID)) {
					dlm.set(dlm.indexOf(objectID), objectID);
				} else {
					dlm.addElement(objectID);
				}
				
			}
		} 
	}

	private void processDeleted() {

	}

}
