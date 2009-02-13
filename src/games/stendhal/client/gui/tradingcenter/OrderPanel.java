package games.stendhal.client.gui.tradingcenter;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class OrderPanel extends JLabel {
	
	private final String playerName;
	
	OrderPanel(final String playerName, final String itemname, final int price) {
		this.playerName = playerName;
		setIcon(new ImageIcon("data/gui/buddy_online.png"));
		setText(Integer.valueOf(price).toString());
		this.addMouseListener(new OrderPanelMouseListener());
	}

}
