package games.stendhal.server.entity.trade;

import games.stendhal.server.entity.PassiveEntity;
import games.stendhal.server.entity.item.Item;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.Definition.Type;

public class Earning extends PassiveEntity {
	
	public static final String EARNING_RPCLASS_NAME = "earning";

	private final String item;
	
	private final Integer value;
	
	private final String sellerName;
	
	public static void generateRPClass() {
		final RPClass earningClass = new RPClass(EARNING_RPCLASS_NAME);
		earningClass.isA("entity");
		earningClass.addAttribute("value", Type.INT);
		earningClass.addAttribute("item", Type.STRING);
		earningClass.addAttribute("sellerName",Type.STRING);
	}

	/**
	 * constructs Earning from sold {@link Item} and price.
	 * @param item the sold item
	 * @param value the earned money
	 */
	public Earning(final String item, final Integer value, final String sellerName) {
		super();
		setRPClass(EARNING_RPCLASS_NAME);
		hide();
		put("item", item);
		this.item = item;
		put("value", value);
		this.value = value;
		this.sellerName = sellerName;
		put("sellerName",sellerName);
	}
	
	public Earning(final RPObject object) {
		this(object.get("item"), Integer.valueOf(object.getInt("value")),object.get("sellerName"));
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
	public String getItem() {
		return item;
	}

	public String getSeller() {
		return this.sellerName;
	}

}
