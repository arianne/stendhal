package games.stendhal.server.actions;

import marauroa.common.game.RPAction;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.trade.Offer;
import games.stendhal.server.trade.Shop;

import static games.stendhal.common.Constants.*;

public class AcceptOfferAction implements ActionListener {
	
	public static void register() {
		CommandCenter.register(ACCEPT_OFFER_TYPE,new AcceptOfferAction());
	}

	@Override
	public void onAction(Player player, RPAction action) {
		// TODO Auto-generated method stub
		// accept offer at trading center
		int price = action.getInt(ACCEPT_OFFER_PRICE);
		String itemName = action.get(ACCEPT_OFFER_ITEM);
		String offererName = action.get(ACCEPT_OFFER_OFFERERNAME);
		Shop shop = getShop();
		Offer offer = createOffer(price,itemName,offererName);
		shop.acceptOffer(offer ,player);
	}

	private Offer createOffer(int price, String itemName, String offererName) {
		Item item = SingletonRepository.getEntityManager().getItem(itemName);
		Offer offer = new Offer(item, price,offererName);
		return offer;
	}

	private Shop getShop() {
		Shop shop = new Shop();
		return shop;
	}

}
