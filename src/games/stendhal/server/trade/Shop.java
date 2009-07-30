package games.stendhal.server.trade;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.player.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import marauroa.common.game.RPObject;

public class Shop {
	
	public static Shop createShop(StendhalRPZone zone) {
		return new Shop(zone);
	}

	private final Map<String, Set<Earning>> earnings = new HashMap<String, Set<Earning>>();
	private final List<Offer> offers = new LinkedList<Offer>();
	private StendhalRPZone zone;
	
	private Shop(final StendhalRPZone zone) {
		this.zone = zone;
		for (final RPObject item : this.zone) {
			if (item.getRPClass().getName().equals("offer")) {
				final Offer offer = (Offer) item;
				this.offers.add(offer);
			}
			if(item.getRPClass().getName().equals("earning")) {
				Earning earning = (Earning) item;
				if(this.earnings.containsKey(earning.getSeller())) {
					this.earnings.put(earning.getSeller(),new HashSet<Earning>());
				}
				Set<Earning> sellersEarnings = this.earnings.get(earning.getSeller());
				sellersEarnings.add(earning);
			}
		}

	}
	
	public Offer createOffer(final Player offerer, final Item item,
			final Integer money) {
		offerer.drop(item);
		final Offer offer = new Offer(item, money, offerer.getName());
		offers.add(offer);
		this.zone.add(offer,false);
		return offer;
	}

	public void acceptOffer(final Offer offer, final Player acceptingPlayer) {
		if (offers.contains(offer)) {
			if (acceptingPlayer.drop("money", offer.getPrice().intValue())) {
				acceptingPlayer.equipOrPutOnGround(offer.getItem());
				if (!earnings.containsKey(offer.getOffererName())) {
					earnings.put(offer.getOffererName(), new HashSet<Earning>());
				}
				Earning earning = new Earning(offer.getItem(), offer.getPrice(), offer.getOffererName());
				earnings.get(offer.getOffererName()).add(earning);
				this.zone.add(earning, false);
				this.zone.remove(offer);
			}
		}
	}

	public void fetchEarnings(final Player earner) {
		if (earnings.containsKey(earner.getName())) {
			final StackableItem item = (StackableItem) SingletonRepository
					.getEntityManager().getItem("money");
			item.setQuantity(this.sumUpEarningsForPlayer(earner));
			earner.equipToInventoryOnly(item);
			this.removeAllEarningsFromZone(earnings.get(earner.getName()));
			earnings.remove(earner.getName());
		}
	}

	private void removeAllEarningsFromZone(Set<Earning> set) {
		for(Earning earning:set) {
			this.zone.remove(earning);
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
