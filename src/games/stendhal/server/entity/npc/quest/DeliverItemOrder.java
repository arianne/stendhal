/***************************************************************************
 *                   (C) Copyright 2003-2023 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
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


	/** The style the customer likes. */
	private String flavor;

	/** The time until the item should be delivered. */
	private int expectedMinutes;

	/** The money the player should get on fast delivery. */
	private int tip;

	/**
	 * The experience the player should gain for delivery. When the item
	 * has already become old, the player will gain half of this amount.
	 */
	private int xp;

	/**
	 * The text that the customer should say upon quick delivery. It should
	 * contain %d as a placeholder for the tip, and can optionally contain
	 * %s as a placeholder for the item flavor.
	 */
	private String respondToFastDelivery;

	/**
	 * The text that the customer should say upon quick delivery. It can
	 * optionally contain %s as a placeholder for the item flavor.
	 */
	private String respondToSlowDelivery;

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

	String getRespondToFastDelivery() {
		return respondToFastDelivery;
	}

	String getRespondToSlowDelivery() {
		return respondToSlowDelivery;
	}

	// hide constructor
	DeliverItemOrder() {
		super();
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

	public DeliverItemOrder respondToFastDelivery(String respondToFastDelivery) {
		this.respondToFastDelivery = respondToFastDelivery;
		return this;
	}

	public DeliverItemOrder respondToSlowDelivery(String respondToSlowDelivery) {
		this.respondToSlowDelivery = respondToSlowDelivery;
		return this;
	}

	public DeliverItemOrder playerMinLevel(int minLevel) {
		this.level = minLevel;
		return this;
	}


}
