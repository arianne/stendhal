package games.stendhal.server.entity.npc.newparser;

/**
 * A Word is part of a Sentence.
 * It encapsulates the original, white space trimmed text,
 * the word type, a normalised lower case text string
 * and the integer amount.
 *
 * @author Martin Fuchs
 */
public class Word {
	public String	original;
	public WordType	type;
	public String	normalized;
	public Integer	amount;

	/**
	 * create a Word from the given original string
	 * @param s
	 */
	public Word(String s) {
		original = s;
	}

	/**
	 * parse the given numeric expression and assign the value to 'amount'
	 * @param s
	 * @param parser
	 */
	public void parseAmount(String s, ConversationParser parser) {
		try {
			int n = Integer.parseInt(s);

			type = new WordType("NUM");
			amount = n;
			normalized = amount.toString();
		} catch(NumberFormatException e) {
			parser.setError("illegal number format: '" + s + "'");
		}
    }

	/**
	 * merge the given preceding Word into this Word
	 * @param other
	 */
	public void mergeLeft(final Word other) {
		original = other.original + ' ' + original;
		type = type.merge(other.type);
		amount = mergeAmount(other.amount, amount);
	}

	/**
	 * merge the given following Word into this Word
	 * @param other
	 */
	public void mergeRight(final Word other) {
		original = original + ' ' + other.original;
		type = type.merge(other.type);
		amount = mergeAmount(amount, other.amount);
	}

	/**
	 * merge two amounts into one number
	 * @param left
	 * @param right
	 * @return combined number
	 */
	private static Integer mergeAmount(Integer left, Integer right) {
		if (left != null) {
			if (right != null) {
				if (left <= right) {
					return left * right;	// e.g. five hundred
				} else {
					return left + right;	// e.g. hundred fifty
				}
			} else {
				return left;
			}
		} else {
			return right;
		}
    }

	@Override
	public String toString() {
		return normalized!=null? normalized: original;
	}
}
