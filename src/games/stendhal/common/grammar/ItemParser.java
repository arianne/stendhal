package games.stendhal.common.grammar;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import games.stendhal.common.parser.Expression;
import games.stendhal.common.parser.NameSearch;
import games.stendhal.common.parser.Sentence;

public class ItemParser {

	/** ItemNames contains all valid item names. */
	protected Set<String> itemNames;

	public ItemParser() {
	    this.itemNames = new HashSet<String>();
    }

	public ItemParser(final Set<String> itemNames) {
	    this.itemNames = itemNames;
    }

	public ItemParser(final String itemName) {
	    itemNames = new HashSet<String>();
	    itemNames.add(itemName);
    }

	/**
	 * @return the recognized item names
	 */
	public Set<String> getItemNames() {
		return itemNames;
	}

	/**
	 * Search for a matching name in the available names.
	 *
	 * @param sentence
	 * @return parsing result
	 */
	public ItemParserResult parse(final Sentence sentence) {
		if (itemNames.isEmpty()) {
    		List<Expression> expressions = sentence.getExpressions();
    		String chosenName;
    		int amount;

    		if (expressions.size() == 1) {
				Expression expr = expressions.get(0);
				amount = expr.getAmount();
				chosenName = expr.getNormalized();
			} else {
				final Expression numeral = sentence.getNumeral();
	    		amount = numeral!=null? numeral.getAmount(): 1;
	    		chosenName = sentence.getExpressionStringAfterVerb();
			}

			return new ItemParserResult(false, chosenName, amount, null);
		}

		NameSearch search = sentence.findMatchingName(itemNames);

		boolean found = search.found();

		// Store found name.
		String chosenName = search.getName();
		int amount = search.getAmount();
		Set<String> mayBeItems = null;

		if (!found) {
			if ((sentence.getNumeralCount() == 1)
					&& (sentence.getUnknownTypeCount() == 0)
					&& (sentence.getObjectCount() == 0)) {
				final Expression number = sentence.getNumeral();

    			// If there is given only a number, return this as amount.
        		amount = number.getAmount();
    		} else {
    			// If there was no match, return the given object name instead
    			// and set amount to 1.
        		chosenName = sentence.getExpressionStringAfterVerb();
        		amount = 1;
    		}

			if (chosenName == null && itemNames.size() == 1) {
    			// The NPC only offers one type of ware, so
    			// it's clear what the player wants.
				chosenName = itemNames.iterator().next();
				found = true;
			} else if (chosenName != null) {
				mayBeItems = new HashSet<String>();

    			// search for items to sell with compound names, ending with the given expression
    			for(String name : itemNames) {
    				if (name.endsWith(" "+chosenName)||(name.startsWith(chosenName+" "))) {
    					mayBeItems.add(name);
    				}
    			}
    		}
		}

		return new ItemParserResult(found, chosenName, amount, mayBeItems);
    }

	/**
	 * Answer with an error message in case the request could not be fulfilled.
	 *
	 * @param res
	 * @param userAction
	 * @param npcAction
	 * @return error message
	 */
	public String getErrormessage(final ItemParserResult res, final String userAction, final String npcAction) {
		String chosenItemName = res.getChosenItemName();
		Set<String> mayBeItems = res.getMayBeItems();

		if (chosenItemName == null) {
			return "Please tell me what you want to " + userAction + ".";
		} else if (mayBeItems!=null && mayBeItems.size()>1) {
			return "There is more than one " + chosenItemName + ". " +
					"Please specify which sort of "
					+ chosenItemName + " you want to " + userAction + ".";
		} else if (mayBeItems!=null && !mayBeItems.isEmpty()) {
			return "Please specify which sort of "
					+ chosenItemName + " you want to " + userAction + ".";
		} else if (npcAction != null) {
			return "Sorry, I don't " + npcAction + " "
					+ Grammar.plural(chosenItemName) + ".";
		} else {
			return null;
		}
	}

}
