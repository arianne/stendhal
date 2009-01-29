package games.stendhal.server.trade;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.player.Player;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Shop {
	
	// TODO Map<String, Set<Earning>> Earning knows offered item
	private Map<String, Integer> earnings = new HashMap<String, Integer>();
	private List<Offer> offers = new LinkedList<Offer>();

	public Offer createOffer(final Player offerer, final Item item,
			final Integer money) {
		offerer.drop(item);
		Offer offer = new Offer(item, money, offerer.getName());
		offers.add(offer);
		
		return offer;
	}

	public void acceptOffer(final Offer offer, final Player player) {
		
		if (offers.contains(offer)) {
			if(	player.drop("money", offer.getPrice().intValue())){
				player.equip(offer.getItem(), true);
				earnings.put(offer.getOffererName(), offer.getPrice());
			}
		}
	}

	public void fetchEarnings(Player earner) {
		if (earnings.containsKey(earner.getName())) {
			StackableItem item = (StackableItem) SingletonRepository
					.getEntityManager().getItem("money");
			item.setQuantity(earnings.get(earner.getName()));
			earner.equip(item);
		}
	}

}
