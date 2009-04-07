package games.stendhal.server.trade;

import games.stendhal.server.entity.item.Item;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.Definition.Type;

public class Earning extends RPObject {
	
	private final Item item;
	
	private final Integer value;
	
	public static void generateRPClass() {
		final RPClass earningClass = new RPClass("earning");
		earningClass.isA("entity");
		earningClass.addAttribute("value",Type.INT);
		earningClass.addRPSlot("item",1);
	}

	/**
	 * standard constructor from sold {@link Item} and price
	 * @param item the sold item
	 * @param value the earned money
	 */
	public Earning(final Item item, final Integer value) {
		super();
		setRPClass("earning");
		addSlot("item");
		getSlot("item").add(item);
		this.item = item;
		put("value",value);
		this.value = value;
		store();
	}
	
	public Earning (final RPObject object) {
		this((Item) object.getSlot("item").getFirst(),Integer.valueOf(object.getInt("value")));
	}

	/**
	 * @return the earned money
	 */
	public Integer getValue() {
		return this.value;
	}

	/**
	 * @return the item
	 */
	public Item getItem() {
		return item;
	}

}
