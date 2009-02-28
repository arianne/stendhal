package games.stendhal.client.gui.tradingcenter;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class OrderPanel extends JLabel {
	
	private final String itemname;
	
	private final int price;
	
	
	OrderPanel(final String itemname, final int price) {
		this.itemname = itemname;
		this.price = price;
		setIcon(new ImageIcon("data/gui/buddy_online.png"));
		setText(Integer.valueOf(this.price).toString());
	}


	/**
	 * @return the itemname
	 */
	public String getItemname() {
		return itemname;
	}


	/**
	 * @return the price
	 */
	public int getPrice() {
		return price;
	}
	
	

}
