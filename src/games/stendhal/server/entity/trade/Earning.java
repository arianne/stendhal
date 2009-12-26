package games.stendhal.server.entity.trade;

import games.stendhal.server.entity.PassiveEntity;
import games.stendhal.server.entity.item.Item;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.Definition.Type;

public class Earning extends PassiveEntity {
	
	public static final String EARNING_RPCLASS_NAME = "earning";
	
	private static final String VALUE_ATTRIBUTE = "value";
	private static final String REWARD_ATTRIBUTE = "reward";
	private static final String NAME_ATTRIBUTE = "sellerName";
	
	private final Integer value;
	private final String sellerName;
	
	public static void generateRPClass() {
		final RPClass earningClass = new RPClass(EARNING_RPCLASS_NAME);
		earningClass.isA("entity");
		earningClass.addAttribute(VALUE_ATTRIBUTE, Type.INT);
		earningClass.addAttribute(NAME_ATTRIBUTE, Type.STRING);
		earningClass.addAttribute(REWARD_ATTRIBUTE, Type.INT);
	}

	/**
	 * constructs Earning from sold {@link Item} and price.
	 * @param item the sold item
	 * @param value the earned money
	 */
	public Earning(final Integer value, final String sellerName, final boolean shouldReward) {
		super();
		setRPClass(EARNING_RPCLASS_NAME);
		hide();
		put(VALUE_ATTRIBUTE, value);
		this.value = value;
		this.sellerName = sellerName;
		put(NAME_ATTRIBUTE, sellerName);
		put(REWARD_ATTRIBUTE, shouldReward ? 1 : 0);
	}
	
	public Earning(final RPObject object) {
		this(Integer.valueOf(object.getInt(VALUE_ATTRIBUTE)),object.get(NAME_ATTRIBUTE), object.getInt(REWARD_ATTRIBUTE) != 0);
	}

	/**
	 * @return the earned money
	 */
	public Integer getValue() {
		return this.value;
	}

	public String getSeller() {
		return this.sellerName;
	}

	public boolean shouldReward() {
		return (getInt(REWARD_ATTRIBUTE) != 0);
	}
}
