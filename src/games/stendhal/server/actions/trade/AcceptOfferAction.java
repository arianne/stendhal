package games.stendhal.server.actions.trade;

import static games.stendhal.common.Constants.OFFER_ITEM;
import static games.stendhal.common.Constants.OFFER_OFFERERNAME;
import static games.stendhal.common.Constants.OFFER_PRICE;
import static games.stendhal.common.Constants.ACCEPT_OFFER_TYPE;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.trade.Offer;
import games.stendhal.server.trade.Shop;
import marauroa.common.game.RPAction;

public class AcceptOfferAction implements ActionListener {
	
	public static void register() {
		CommandCenter.register(ACCEPT_OFFER_TYPE, new AcceptOfferAction());
	}

	public void onAction(final Player player, final RPAction action) {
		// accept offer at trading center
		final int price = action.getInt(OFFER_PRICE);
		final String itemName = action.get(OFFER_ITEM);
		final String offererName = action.get(OFFER_OFFERERNAME);
		final Shop shop = Shop.createShop(player.getZone());
		final Item item = SingletonRepository.getEntityManager().getItem(itemName);
		final Offer offer = new Offer(item, price, offererName);
		shop.acceptOffer(offer , player);
	}

}
