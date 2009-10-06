package games.stendhal.server.trade;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.player.Player;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

public class Shop extends RPEntity {
	
	private static final String SHOP_RPCLASS_NAME = "shop";
	private static final String EARNINGS_SLOT_NAME = "earnings";
	private static final String OFFERS_SLOT_NAME = "offers";
	private final Set<Earning> earnings = new HashSet<Earning>();
	private final List<Offer> offers = new LinkedList<Offer>();
	
	public static void generateRPClass() {
		final RPClass shop = new RPClass(SHOP_RPCLASS_NAME);
		shop.isA("entity");
		shop.addRPSlot(OFFERS_SLOT_NAME,0);
		shop.addRPSlot(EARNINGS_SLOT_NAME, 0);
	}
	
	public Shop(final RPObject object) {
		this.setRPClass(SHOP_RPCLASS_NAME);
		for(final RPObject rpo : object.getSlot(OFFERS_SLOT_NAME)) {
			this.offers.add((Offer) rpo);
			this.getSlot(OFFERS_SLOT_NAME).add(rpo);
		}
		for(final RPObject rpo : object.getSlot(EARNINGS_SLOT_NAME)) {
			final Earning earning = (Earning) rpo;
			this.earnings.add(earning);
			this.getSlot(EARNINGS_SLOT_NAME).add(rpo);
		}
	}
	
	public static Shop createShop() {
		Shop shop = new Shop();
		shop.store();
		return shop;
	}

	private Shop() {
		setRPClass(SHOP_RPCLASS_NAME);
		store();
	}
	
	public Offer createOffer(final Player offerer, final Item item,
			final Integer money) {
		offerer.drop(item);
		final Offer offer = new Offer(item, money, offerer.getName());
		getOffers().add(offer);
		RPSlot slot = this.getSlot(OFFERS_SLOT_NAME);
		slot.add(offer);
		offer.store();
		this.store();
		return offer;
	}

	public void acceptOffer(final Offer offer, final Player acceptingPlayer) {
		if (getOffers().contains(offer)) {
			if (acceptingPlayer.drop("money", offer.getPrice().intValue())) {
				acceptingPlayer.equipOrPutOnGround(offer.getItem());
				final Earning earning = new Earning(offer.getItem(), offer.getPrice(), offer.getOffererName());
				this.getSlot(EARNINGS_SLOT_NAME).add(earning);
				offers.remove(offer);
				this.getSlot(OFFERS_SLOT_NAME).remove(offer.getID());
				this.store();
			}
		}
	}

	public void fetchEarnings(final Player earner) {
		Set<Earning> earningsToRemove = new HashSet<Earning>();
		for (RPObject earningRPObject : this.getSlot(EARNINGS_SLOT_NAME)) {
			Earning earning = (Earning) earningRPObject;
			if(earning.getSeller().equals(earner.getName())) {
				final StackableItem item = (StackableItem) SingletonRepository
				.getEntityManager().getItem("money");
				item.setQuantity(this.sumUpEarningsForPlayer(earner));
				earner.equipToInventoryOnly(item);
				earnings.remove(earning);
				earningsToRemove.add(earning);
			}
		}
		for(Earning earning : earningsToRemove) {
			this.getSlot(EARNINGS_SLOT_NAME).remove(earning.getID());
		}
	}
	
	public int countOffersOfPlayer(Player offerer) {
		int count = 0;
		for (Offer offer : this.offers) {
			if(offer.getOffererName().equals(offerer.getName())) {
				count = count + 1;
			}
		}
		return count;
	}

	private int sumUpEarningsForPlayer(final Player earner) {
		int sum = 0;
		for (final RPObject earningRPObject : this.getSlot(EARNINGS_SLOT_NAME)) {
			Earning earning = (Earning) earningRPObject;
			if(earning.getSeller().equals(earner.getName())) {
				sum += earning .getValue().intValue();
			}
		}
		return sum;
	}

	public List<Offer> getOffers() {
		return offers;
	}

	@Override
	protected void dropItemsOn(Corpse corpse) {
		// 
		
	}

	@Override
	public void logic() {
		//
	}

}
