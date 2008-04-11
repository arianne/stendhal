package games.stendhal.server.util;

import games.stendhal.common.Grammar;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * ItemCollection is a collection of items with associated amount.
 * It can be constructed from a semicolon-separated quest state string.
 * Item names and amounts are separated by equal signs, so the format is:
 * item1=5;item2=1;C=3;D=1
 *
 * @author Martin Fuchs
 */
@SuppressWarnings("serial")
public class ItemCollection extends TreeMap<String, Integer> {

    /**
     * Construct an empty ItemCollection.
     */
    public ItemCollection() {
    }

    /**
     * Construct an ItemCollection from a quest state string in
     * the form "item1=n1;item2=n2;...".
     * @param str
     */
    public void addFromQuestStateString(String str) {
	    if (str != null) {
	        List<String> items = Arrays.asList(str.split(";"));

    		for (String item : items) {
    			String[] pair = item.split("=");

    			if (pair.length == 2) {
        			addItem(pair[0], Integer.parseInt(pair[1]));
    			}
    		}
		}
	}

    /**
     * Return the items as quest state string.
     * @return
     */
    public String toStringForQuestState() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;

        for (Map.Entry<String, Integer> e : entrySet()) {
            if (first) {
                first = false;
            } else {
                sb.append(';');
            }

            sb.append(e.getKey() + "=" + e.getValue());
        }

        return sb.toString();
    }

	/**
	 * Remove the specified amount of items from the collection.
	 * @param itemName
	 * @param amount
	 * @return
	 */
	public boolean removeItem(String itemName, int amount) {
    	Integer curAmount = get(itemName);

    	if (curAmount != null) {
        	if (curAmount >= amount) {
        		curAmount -= amount;

        		if (curAmount > 0) {
                    put(itemName, curAmount);
        		} else {
        		    remove(itemName);
        		}

        		return true;
        	} else {
        		return false;
        	}
        }

    	return false;
	}

    /**
     * Add the specified amount of items to the collection.
     * @param itemName
     * @param amount
     */
    public void addItem(String itemName, int amount) {
        Integer curAmount = get(itemName);

        if (curAmount != null) {
            put(itemName, curAmount + amount);
        } else {
            put(itemName, amount);
        }
    }

    /**
     * Return a String list containing the items in the format "xxx=n".
     * @return
     */
    public List<String> toStringList() {
        List<String> result = new LinkedList<String>();

        for (Map.Entry<String, Integer> item : entrySet()) {
            result.add(item.getKey() + '=' + item.getValue());
        }

        return result;
    }

    /**
     * Return a String list containing the items in the format "n #xxx, ...".
     * @return
     */
    public List<String> toStringListWithHash() {
        List<String> result = new LinkedList<String>();

        for (Map.Entry<String, Integer> item : entrySet()) {
            result.add(Grammar.quantityplnounWithHash(item.getValue(), item.getKey()));
        }

        return result;
    }

}
