package games.stendhal.server.entity.npc.parser;

import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("serial")

/**
 * TriggerList can be used to create a list of Expressions from Strings
 * and search for Expressions in this list.
 */
public class TriggerList extends LinkedList<Expression> {

	/**
	 * Create a list of normalized trigger Words from a String list.
	 *
	 * @param String list
	 */
	public TriggerList(List<String> strings) {
		for (String item : strings) {
			add(ConversationParser.createTriggerExpression(item));
		}
    }

	/**
	 * Search for the given expression in the list.
	 *
	 * @param expr
	 * @return matching expression in the list
	 */
	public Expression find(Expression expr) {
		int idx = indexOf(expr);

		if (idx != -1) {
			return get(idx);
		} else {
			return null;
		}
	}

}
