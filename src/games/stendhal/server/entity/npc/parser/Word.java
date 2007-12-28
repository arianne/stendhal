package games.stendhal.server.entity.npc.parser;

/**
 * A Word is part of a Sentence. It encapsulates the original, white space
 * trimmed text, the word type, a normalized lower case text string and the
 * integer amount.
 * 
 * @author Martin Fuchs
 */
public class Word {

	private String original;
	private WordType type;
	private String normalized;
	private Integer amount;
	private boolean breakFlag = false;

	/**
	 * create a Word from the given original string.
	 * 
	 * @param s
	 */
	public Word(String s) {
		original = s;
	}

	/**
	 * create a Word from the given strings.
	 * 
	 * @param s
	 * @param n
	 * @param typeString
	 */
	public Word(String s, String n, String typeString) {
		original = s;
		normalized = n;
		type = new WordType(typeString);
	}

	/**
	 * parse the given numeric expression and assign the value to 'amount'.
	 * 
	 * @param s
	 * @param parser
	 */
	public void parseAmount(String s, ConversationParser parser) {
		try {
			setAmount(new Integer(s));
			setType(new WordType(WordType.NUMERAL));
			normalized = amount.toString();
		} catch (NumberFormatException e) {
			parser.setError("illegal number format: '" + s + "'");
		}
	}

	/**
	 * merge the given preceding Word into this Word.
	 * 
	 * @param other
	 */
	public void mergeLeft(final Word other) {
		original = other.getOriginal() + ' ' + original;
		mergeType(other.getType());
		setAmount(mergeAmount(other.amount, amount));
	}

	/**
	 * merge the given following Word into this Word.
	 * 
	 * @param other
	 */
	public void mergeRight(final Word other) {
		original = original + ' ' + other.getOriginal();
		mergeType(other.getType());
		setAmount(mergeAmount(amount, other.amount));
		breakFlag = other.getBreakFlag();
	}

	/**
	 * merge two amounts into one number.
	 * 
	 * @param left
	 * @param right
	 * @return combined number
	 */
	private static Integer mergeAmount(Integer left, Integer right) {
		if (left != null) {
			if (right != null) {
				if (left <= right) {
					return left * right; // e.g. five hundred
				} else {
					return left + right; // e.g. hundred fifty
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
	 * return amount as integer value, default to 1.
	 * 
	 * @return
	 */
	public int getAmount() {
		return amount != null ? amount : 1;
	}

	/**
	 * set flag to separate different parts of the sentence.
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

	/**
	 * Merge word type with another one
	 * while handling null values.
	 *
	 * @param otherType
	 */
	public void mergeType(WordType otherType) {
		if (type != null) {
			if (otherType != null) {
				type = type.merge(otherType);
			}
		} else {
			type = otherType;
		}
	}

	public WordType getType() {
		return type;
	}

	public boolean getBreakFlag() {
		return breakFlag;
	}

	@Override
	public String toString() {
		return normalized != null ? normalized : getOriginal();
	}

	public void setNormalized(String normalized) {
		this.normalized = normalized;
	}

	public String getNormalized() {
		return normalized;
	}

}
