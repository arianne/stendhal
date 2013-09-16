/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.util;

import games.stendhal.common.grammar.Grammar;

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
public class ItemCollection extends TreeMap<String, Integer> {

	private static final long serialVersionUID = 1L;

	/**
     * Construct an ItemCollection from a quest state string in
     * the form "item1=n1;item2=n2;...".
     * @param str
     */
	public void addFromQuestStateString(final String str) {
		addFromQuestStateString(str, 0);
	}
	
    public void addFromQuestStateString(final String str, final int position) {
	    if (str != null) {
	    	final String[] slots = str.split(",");
	    	if (slots[position] != null) {
		        final List<String> items = Arrays.asList(slots[position].split(";"));
	
	    		for (final String item : items) {
	    			final String[] pair = item.split("=");
	
	    			if (pair.length == 2) {
	        			addItem(pair[0], Integer.parseInt(pair[1]));
	    			}
	    		}
    		}
		}
	}

    /**
     * Return the items as quest state string.
     * @return semicolon separated states list
     */
    public String toStringForQuestState() {
        final StringBuilder sb = new StringBuilder();
        boolean first = true;

        for (final Map.Entry<String, Integer> e : entrySet()) {
            if (first) {
                first = false;
            } else {
                sb.append(';');
            }

            sb.append(e.getKey());
            sb.append("=");
            sb.append(e.getValue());
        }

        return sb.toString();
    }

	/**
	 * Remove the specified amount of items from the collection.
	 * @param itemName
	 * @param amount
	 * @return true if amount has been updated
	 */
	public boolean removeItem(final String itemName, final int amount) {
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
    public void addItem(final String itemName, final int amount) {
        final Integer curAmount = get(itemName);

        if (curAmount != null) {
            put(itemName, curAmount + amount);
        } else {
            put(itemName, amount);
        }
    }

    /**
     * @return a String list containing the items in the format "n item".
     */
    public List<String> toStringList() {
        final List<String> result = new LinkedList<String>();

        for (final Map.Entry<String, Integer> entry : entrySet()) {
            result.add(Grammar.quantityplnoun(entry.getValue(), entry.getKey()));
        }

        return result;
    }

    /**
     * @return a String list containing the items in the format "n #item, ...".
     */
    public List<String> toStringListWithHash() {
        final List<String> result = new LinkedList<String>();

        for (final Map.Entry<String, Integer> item : entrySet()) {
            result.add(Grammar.quantityplnounWithHash(item.getValue(), item.getKey()));
        }

        return result;
    }

}
