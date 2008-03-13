package games.stendhal.server.util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * CountedItemList is a list of items with associated amount.
 * It can be constructed from a semicolon-separated quest state string.
 * Item names and amounts are separated by equal signs, so the format is:
 * item1=5;item2=1;C=3;D=1
 *
 * @author Martin Fuchs
 */
@SuppressWarnings("serial")
public class CountedItemList extends LinkedList<ItemEntry> {

	public CountedItemList(String str) {
		List<String> missing = Arrays.asList(str.split(";"));

		for (String item : missing) {
			ItemEntry entry = new ItemEntry();
			
			String[] pair = item.split("=");

			entry.itemName = pair[0];
			entry.amount = Integer.parseInt(pair[1]);

			add(entry);
		}
	}

	public boolean removeItem(String itemName, int amount) {
    	for (ItemEntry e : this) {
            if (e.itemName.equals(itemName)) {
            	if (e.amount >= amount) {
            		e.amount -= amount;

            		if (e.amount == 0) {
            			remove(e);
            		}

            		return true;
            	} else {
            		return false;
            	}
            }
    	}

    	return false;
	}

    public String stringForQuestState() {
    	StringBuilder sb = new StringBuilder();
    	boolean first = true;

    	for (ItemEntry e : this) {
    		if (first) {
    			first = false;
    		} else {
    			sb.append(';');
    		}

    		sb.append(e.stringForQuestState());
    	}

		return sb.toString();
	}

}
