package games.stendhal.client.gui.tradingcenter;

import static games.stendhal.common.Constants.ACCEPT_OFFER_ITEM;
import static games.stendhal.common.Constants.ACCEPT_OFFER_PRICE;
import static games.stendhal.common.Constants.ACCEPT_OFFER_TYPE;
import static games.stendhal.common.Constants.ACTION_TYPE;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import marauroa.common.game.RPAction;

public class AcceptTradeOfferAction implements ActionListener {
	
	private String item;
	
	private int price;

	/**
	 * @param item
	 * @param price
	 */
	public AcceptTradeOfferAction(String item, int price) {
		this.item = item;
		this.price = price;
	}

	public void actionPerformed(ActionEvent e) {
		RPAction action = new RPAction();
		action.put(ACTION_TYPE,ACCEPT_OFFER_TYPE);
		action.put(ACCEPT_OFFER_ITEM,item);
		action.put(ACCEPT_OFFER_PRICE,price);
		System.out.println(action);
		//StendhalClient.get().send(action);
	}

}
