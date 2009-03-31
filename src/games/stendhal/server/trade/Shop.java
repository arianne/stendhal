package games.stendhal.server.trade;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.player.Player;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Shop implements ZoneConfigurator {
	
	// TODO Map<String, Set<Earning>> Earning knows offered item
	private final Map<String, Integer> earnings = new HashMap<String, Integer>();
	private final List<Offer> offers = new LinkedList<Offer>();

	public Offer createOffer(final Player offerer, final Item item,
			final Integer money) {
		offerer.drop(item);
		final Offer offer = new Offer(item, money, offerer.getName());
		offers.add(offer);
		
		return offer;
	}

	public void acceptOffer(final Offer offer, final Player player) {
		
		if (offers.contains(offer)) {
			if (player.drop("money", offer.getPrice().intValue())) {
				player.equipOrPutOnGround(offer.getItem());
				earnings.put(offer.getOffererName(), offer.getPrice());
			}
		}
	}

	public void fetchEarnings(final Player earner) {
		if (earnings.containsKey(earner.getName())) {
			final StackableItem item = (StackableItem) SingletonRepository
					.getEntityManager().getItem("money");
			item.setQuantity(earnings.get(earner.getName()));
			earner.equipToInventoryOnly(item);
		}
	}


	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		// TODO Auto-generated method stub
		
	}

}
