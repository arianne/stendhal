package games.stendhal.server.actions.trade;

import static games.stendhal.common.Constants.*;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.trade.Offer;
import games.stendhal.server.trade.Shop;
import marauroa.common.game.RPAction;

public class AddOfferAction implements ActionListener {
	
	public static void register() {
		CommandCenter.register(ADD_OFFER_TYPE,new AddOfferAction());
	}

	public void onAction(Player player, RPAction action) {
		Shop shop = Shop.createShop(player.getZone());
		createOffer(player, shop, action);
	}

	private Offer createOffer(Player player, Shop shop, RPAction action) {
		Item item = null;
		Integer money = null;
		return shop.createOffer(player,item,money);
	}

}
