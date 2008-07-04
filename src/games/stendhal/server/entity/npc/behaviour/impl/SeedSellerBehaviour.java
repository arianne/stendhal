package games.stendhal.server.entity.npc.behaviour.impl;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;

import java.util.Map;

public class SeedSellerBehaviour extends SellerBehaviour {

	public SeedSellerBehaviour() {
		super();
	}

	public SeedSellerBehaviour(Map<String, Integer> priceList) {
		super(priceList);
	}

	@Override
	protected Item getAskedItem(String askedItem) {
		String[] tokens = askedItem.split(" ");
		Item item = SingletonRepository.getEntityManager().getItem("seed");
		item.setInfoString(tokens[0]);
		return item;

	}
}
