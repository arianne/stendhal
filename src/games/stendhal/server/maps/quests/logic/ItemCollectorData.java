package games.stendhal.server.maps.quests.logic;

import games.stendhal.common.grammar.Grammar;

public class ItemCollectorData implements ItemCollectorSetters {

	private String itemName;
	private int requiredAmount = 1;
	private String message;

	private int stillNeededAmount = 1;

	@Override
	public ItemCollectorSetters item(String itemName) {
		this.itemName = itemName;
		return this;
	}

	@Override
	public ItemCollectorSetters pieces(int count) {
		this.requiredAmount = count;
		this.stillNeededAmount = count;
		return this;
	}

	@Override
	public ItemCollectorSetters bySaying(String message) {
		this.message = message;
		return this;
	}

	public void subtractAmount(final int amount) {
		stillNeededAmount -= amount;
	}

	public void subtractAmount(final String string) {
		subtractAmount(Integer.parseInt(string));
	}

	public void resetAmount() {
		stillNeededAmount = requiredAmount;
	}

	public int getAlreadyBrought() {
		return requiredAmount - stillNeededAmount;
	}

	public int getStillNeeded() {
		return stillNeededAmount;
	}

	public int getRequiredAmount() {
		return requiredAmount;
	}

	public String getName() {
		return itemName;
	}

	public String getAnswer() {
		String neededItems = Grammar.quantityplnoun(stillNeededAmount, itemName, "a");
		return String.format(message, neededItems);
	}
}
