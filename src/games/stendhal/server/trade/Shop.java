package games.stendhal.server.trade;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.player.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import marauroa.common.game.RPObject;

public class Shop {
	
	private final Map<String, Set<Earning>> earnings = new HashMap<String, Set<Earning>>();
	private final List<Offer> offers = new LinkedList<Offer>();
	
	public Shop(final StendhalRPZone zone) {
		for (final RPObject item : zone) {
			if (item.getRPClass().getName().equals("offer")) {
				final Offer offer = (Offer) item;
				this.offers.add(offer);
			}
		}

	}
	
	public Offer createOffer(final Player offerer, final Item item,
			final Integer money) {
		offerer.drop(item);
		final Offer offer = new Offer(item, money, offerer.getName());
		offers.add(offer);
		
		return offer;
	}

	public void acceptOffer(final Offer offer, final Player acceptingPlayer) {
		if (offers.contains(offer)) {
			if (acceptingPlayer.drop("money", offer.getPrice().intValue())) {
				acceptingPlayer.equipOrPutOnGround(offer.getItem());
				if (!earnings.containsKey(offer.getOffererName())) {
					earnings.put(offer.getOffererName(), new HashSet<Earning>());
				}
				earnings.get(offer.getOffererName()).add(new Earning(offer.getItem(), offer.getPrice()));
			}
		}
	}

	public void fetchEarnings(final Player earner) {
		if (earnings.containsKey(earner.getName())) {
			final StackableItem item = (StackableItem) SingletonRepository
					.getEntityManager().getItem("money");
			item.setQuantity(this.sumUpEarningsForPlayer(earner));
			earner.equipToInventoryOnly(item);
			earnings.remove(earner.getName());
		}
	}

	private int sumUpEarningsForPlayer(final Player earner) {
		Set<Earning> earningsForPlayer = earnings.get(earner.getName());
		int sum = 0;
		for (Earning earning : earningsForPlayer) {
			sum += earning.getValue().intValue();
		}
		return sum;
	}

}
