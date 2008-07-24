package games.stendhal.server.entity.npc.behaviour.impl;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;

import java.util.HashMap;
import java.util.Map;

public class SeedSellerBehaviour extends SellerBehaviour {

	private static Map<String, Integer> pricelist = new HashMap<String, Integer>();
	static {
		pricelist.put("lilia seed", 10);
		pricelist.put("daisies seed", 20);
	}
	

	public SeedSellerBehaviour() {
		
		
		this(pricelist);
	}

	public SeedSellerBehaviour(final Map<String, Integer> priceList) {
		super(priceList);
	}

	@Override
	protected Item getAskedItem(final String askedItem) {
		final String[] tokens = askedItem.split(" ");
		final StackableItem item = (StackableItem) SingletonRepository.getEntityManager().getItem("seed");
		item.setInfoString(tokens[0]);
		return item;

	}
}
