/***************************************************************************
 *                   (C) Copyright 2003-2020 - Marauroa                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.mapstuff.spawner;

import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;

/**
 * A regenerative source of food that can be eaten by sheep.
 */
public class SheepFood extends PassiveEntityRespawnPoint {

	private int amount;

	private static final int MAX_NUMBER_OF_FRUITS = 5;

	/** How long it takes to grow one berry. originally 2000 tt */
	private static final int GROWING_RATE = 1200;

	public static void generateRPClass() {
		final RPClass food = new RPClass("food");
		food.isA("plant_grower");
		food.addAttribute("amount", Type.BYTE);
	}

	public SheepFood(final RPObject object) {
		super(object, "aeryberry", GROWING_RATE);
		setRPClass("food");
		put("type", "food");
		update();
	}

	public SheepFood() {
		super("aeryberry", GROWING_RATE);
		setRPClass("food");
		put("type", "food");
	}

	@Override
	public void update() {
		super.update();
		if (has("amount")) {
			amount = getInt("amount");
		}
	}

	@Override
	public void onFruitPicked(final Item picked) {
		super.onFruitPicked(picked);
		setAmount(amount - 1);
		notifyWorldAboutChanges();
	}

	private void setAmount(final int amount) {
			this.amount = amount;
			put("amount", amount);
	}

	/**
	 * Gets the number of ripe fruits that are on.
	 *
	 * @return number of ripe fruits
	 */
	public int getAmount() {
		return amount;
	}

	@Override
	protected void growNewFruit() {
		setAmount(amount + 1);
		notifyWorldAboutChanges();
	}

	@Override
	public String describe() {
		final String text = "You see an aeryberry bush, with "
				+ Grammar.quantityplnoun(getAmount(), "berry", "one")
				+ " on it. Only sheep can eat aeryberries.";
		return text;
	}

	@Override
	public void setToFullGrowth() {
		setAmount(MAX_NUMBER_OF_FRUITS);
		// don't grow anything new until someone picks a fruit
		SingletonRepository.getTurnNotifier().dontNotify(this);
	}

	@Override
	public void setStartState() {
		setToFullGrowth();
	}

	@Override
	public void onItemPickedUp(Player player) {
		// do nothing, players cannot pick up sheep food
	}
}
