package games.stendhal.server.util;

/**
 * Item entry with amount for CountedItemList.
 *
 * @author Martin Fuchs
 */
public class ItemEntry {
	public String	itemName;
	public int		amount;

    public String stringForQuestState() {
		return itemName + "=" + amount;
	}

	@Override
    public String toString() {
		return "" + amount + " " + itemName;
	}
}
