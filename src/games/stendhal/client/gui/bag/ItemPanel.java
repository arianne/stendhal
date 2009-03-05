package games.stendhal.client.gui.bag;

import javax.swing.JPanel;
import javax.swing.JTextField;

import marauroa.common.game.RPObject;

public class ItemPanel extends JPanel {

	public boolean isEmpty() {
		return getComponentCount() == 0;
	}

	public void addNew(RPObject object) {
		this.add(new JTextField(object.get("id") + object.get("class")));
		
	}

	public void updateValues(RPObject object) {
		
		try {
			JTextField field = (JTextField) this.getComponent(0);
					for ( String text : object){
						field.setText(field.getText() + " "+ text +":"+object.get(text));
					
					}
		} catch (Exception e) {
			System.out.println(e);
		}
		
	}

	public void removeItem(RPObject object) {
		this.removeAll();
		revalidate();
		repaint();
		
	}
	

}
