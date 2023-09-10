package games.stendhal.server.entity.npc.quest;

/**
 * An Order object.
 */
public class DeliverItemOrder {

	private DeliverItemTask deliverItemTask;

	/** Name of NPC. */
	private String npc;

	/** A hint where to find the customer. */
	private String npcDescription;
	

	/** The pizza style the customer likes. */
	private String flavor;

	/** The time until the pizza should be delivered. */
	private int expectedMinutes;

	/** The money the player should get on fast delivery. */
	private int tip;

	/**
	 * The experience the player should gain for delivery. When the pizza
	 * has already become cold, the player will gain half of this amount.
	 */
	private int xp;

	/**
	 * The text that the customer should say upon quick delivery. It should
	 * contain %d as a placeholder for the tip, and can optionally contain
	 * %s as a placeholder for the pizza flavor.
	 */
	private String messageOnHotPizza;

	/**
	 * The text that the customer should say upon quick delivery. It can
	 * optionally contain %s as a placeholder for the pizza flavor.
	 */
	private String messageOnColdPizza;

	/**
	 * The min level player who can get to this NPC
	 */
	private int level;

	DeliverItemOrder(DeliverItemTask deliverItemTask) {
		this.deliverItemTask = deliverItemTask;
	}

	/**
	 * Get the minimum level needed for the NPC
	 *
	 * @return minimum level
	 */
	int getLevel() {
		return level;
	}

	String getNpc() {
		return npc;
	}

	String getNpcDescription() {
		return npcDescription;
	}

	String getFlavor() {
		return flavor;
	}

	int getExpectedMinutes() {
		return expectedMinutes;
	}

	int getTip() {
		return tip;
	}

	int getXp() {
		return xp;
	}

	String getMessageOnHotPizza() {
		return messageOnHotPizza;
	}

	String getMessageOnColdPizza() {
		return messageOnColdPizza;
	}

	public DeliverItemOrder customerNpc(String npc) {
		deliverItemTask.getOrders().remove(this.npc);
		this.npc = npc;
		deliverItemTask.getOrders().put(npc,  this);
		return this;
	}

	public DeliverItemOrder customerDescription(String npcDescription) {
		this.npcDescription = npcDescription;
		return this;
	}

	public DeliverItemOrder itemDescription(String flavor) {
		this.flavor = flavor;
		return this;
	}

	public DeliverItemOrder minutesToDeliver(int minutesToDeliver) {
		this.expectedMinutes = minutesToDeliver;
		return this;
	}

	public DeliverItemOrder tipOnFastDelivery(int tip) {
		this.tip = tip;
		return this;
	}

	public DeliverItemOrder xpReward(int xp) {
		this.xp = xp;
		return this;
	}

	public DeliverItemOrder respondToFastDelivery(String responseToFastDelivery) {
		this.messageOnHotPizza = responseToFastDelivery;
		return this;
	}

	public DeliverItemOrder respondToSlowDelivery(String responseToSlowDelivery) {
		this.messageOnColdPizza = responseToSlowDelivery;
		return this;
	}

	public DeliverItemOrder playerMinLevel(int minLevel) {
		this.level = minLevel;
		return this;
	}


}