package games.stendhal.server.trade;

import games.stendhal.server.entity.item.Item;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.Definition.Type;

public class Earning extends RPObject {
	
	public static final String EARNING_RPCLASS_NAME = "earning";

	private final Item item;
	
	private final Integer value;
	
	private final String sellerName;
	
	public static void generateRPClass() {
		final RPClass earningClass = new RPClass(EARNING_RPCLASS_NAME);
		earningClass.isA("entity");
		earningClass.addAttribute("value", Type.INT);
		earningClass.addRPSlot("item", 1);
		earningClass.addAttribute("sellerName",Type.STRING);
	}

	/**
	 * constructs Earning from sold {@link Item} and price.
	 * @param item the sold item
	 * @param value the earned money
	 */
	public Earning(final Item item, final Integer value, final String sellerName) {
		super();
		setRPClass(EARNING_RPCLASS_NAME);
		addSlot("item");
		getSlot("item").add(item);
		this.item = item;
		put("value", value);
		this.value = value;
		this.sellerName = sellerName;
		put("sellerName",sellerName);
		store();
	}
	
	public Earning(final RPObject object) {
		this((Item) object.getSlot("item").getFirst(), Integer.valueOf(object.getInt("value")),object.get("sellerName"));
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

	public String getSeller() {
		return this.sellerName;
	}

}
