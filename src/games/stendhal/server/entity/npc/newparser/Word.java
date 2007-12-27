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

	private String original;
	private WordType type;
	private String	normalized;
	private Integer	amount;
	private boolean breakFlag = false;

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
			setAmount(new Integer(s));
			setType(new WordType("NUM"));
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
		original = other.getOriginal() + ' ' + original;
		setType(getType().merge(other.getType()));
		setAmount(mergeAmount(other.amount, amount));
	}

	/**
	 * merge the given following Word into this Word
	 * @param other
	 */
	public void mergeRight(final Word other) {
		original = original + ' ' + other.getOriginal();
		setType(getType().merge(other.getType()));
		setAmount(mergeAmount(amount, other.amount));
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

	public void setAmount(Integer amount) {
	    this.amount = amount;
    }

	/**
	 * return amount as integer value, default to 1
	 * @return
	 */
	public int getAmount() {
		return amount!=null? amount: 1;
	}

	/**
	 * set flag to separate different sentence parts
	 */
	public void setBreakFlag() {
	    breakFlag = true;	    
    }

	public String getOriginal() {
	    return original;
    }

	public void setType(WordType type) {
	    this.type = type;
    }

	public WordType getType() {
	    return type;
    }

	public boolean getBreakFlag() {
	    return breakFlag;	    
    }

	@Override
	public String toString() {
		return normalized!=null? normalized: getOriginal();
	}

	public void setNormalized(String normalized) {
	    this.normalized = normalized;
    }

	public String getNormalized() {
	    return normalized;
    }

}
