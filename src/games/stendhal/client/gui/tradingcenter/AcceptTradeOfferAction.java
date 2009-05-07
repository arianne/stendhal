package games.stendhal.client.gui.tradingcenter;

import static games.stendhal.common.Constants.*;

import games.stendhal.client.StendhalClient;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import marauroa.common.game.RPAction;

class AcceptTradeOfferAction implements ActionListener {
	
	private final String item;
	
	private final int price;

	private final String offererName;

	protected AcceptTradeOfferAction(final String item, final int price, final String offererName) {
		this.item = item;
		this.price = price;
		this.offererName = offererName;
	}

	public void actionPerformed(final ActionEvent e) {
		final RPAction action = new RPAction();
		action.put(ACTION_TYPE, ACCEPT_OFFER_TYPE);
		action.put(ACCEPT_OFFER_ITEM, item);
		action.put(ACCEPT_OFFER_PRICE, price);
		action.put(ACCEPT_OFFER_OFFERERNAME, offererName);
		StendhalClient.get().send(action);
	}

}
