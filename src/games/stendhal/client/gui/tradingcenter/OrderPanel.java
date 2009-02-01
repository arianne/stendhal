package games.stendhal.client.gui.tradingcenter;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class OrderPanel extends JLabel {
	
	OrderPanel(final String itemname, final int price) {

		setIcon(new ImageIcon("data/gui/buddy_online.png"));
		setText(Integer.valueOf(price).toString());
		
	}

}
