package games.stendhal.server.entity.npc.parser;

/**
 * An Expression is part of a Sentence. It encapsulates the original, white space
 * trimmed text, the expression type, a normalized lower case text string and the
 * integer amount.
 * 
 * @author Martin Fuchs
 */
public class Expression {

	/** original, un-normalized string expression */
	private String original;

	/** expression type */
	private ExpressionType type;

	/** normalized string representation of this Expression */
	private String normalized;

	/** number of items */
	private Integer amount;

	/** break flag to define sentence part borders */
	private boolean breakFlag = false;

	/** instance of an empty Expression */
	public static final Expression emptyExpression = new Expression("", "", "");

	/**
	 * Create a Expression from the given original string.
	 * 
	 * @param s
	 */
	public Expression(String s) {
		original = s;
	}

	/**
	 * Create a Expression from the given strings.
	 * 
	 * @param s
	 * @param n
	 * @param typeString
	 */
	public Expression(String s, String n, String typeString) {
		original = s;
		normalized = n;
		type = new ExpressionType(typeString);
	}

	/**
	 * Parse the given numeric expression and assign the value to 'amount'.
	 * 
	 * @param s
	 * @param parser
	 */
	public void parseAmount(String s, ConversationParser parser) {
		try {
			setAmount(new Integer(s));
			setType(new ExpressionType(ExpressionType.NUMERAL));
			normalized = amount.toString();
		} catch (NumberFormatException e) {
			parser.setError("illegal number format: '" + s + "'");
		}
	}

	/**
	 * Merge the given preceding Expression into this Expression.
	 * 
	 * @param other
	 */
	public void mergeLeft(final Expression other) {
		original = other.getOriginal() + ' ' + original;
		mergeType(other.getType());
		setAmount(mergeAmount(other.amount, amount));
	}

	/**
	 * Merge the given following Expression into this Expression.
	 * 
	 * @param other
	 */
	public void mergeRight(final Expression other) {
		original = original + ' ' + other.getOriginal();
		mergeType(other.getType());
		setAmount(mergeAmount(amount, other.amount));
		breakFlag = other.getBreakFlag();
	}

	/**
	 * Merge two amounts into one number.
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
	 * Return amount as integer value, default to 1.
	 * 
	 * @return
	 */
	public int getAmount() {
		return amount != null ? amount : 1;
	}

	/**
	 * Set flag to separate different parts of the sentence.
	 */
	public void setBreakFlag() {
		breakFlag = true;
	}

	/**
	 * Return the original, un-normalized string expression.
	 *
	 * @return
	 */
	public String getOriginal() {
		return original;
	}

	public void setType(ExpressionType type) {
		this.type = type;
	}

	/**
	 * Return the expression type.
	 *
	 * @return
	 */
	public ExpressionType getType() {
		return type;
	}

	/**
	 * Return the break flag to check for sentence part borders.
	 *
	 * @return
	 */
	public boolean getBreakFlag() {
		return breakFlag;
	}

	/**
	 * Set the break flag to define sentence part borders.
	 *
	 * @param normalized
	 */
	public void setNormalized(String normalized) {
		this.normalized = normalized;
	}

	/**
	 * Return the normalized form of the word.
	 *
	 * @return
	 */
	public String getNormalized() {
		return normalized;
	}

	/**
	 * Return type expression type string.
	 * 
	 * @return
	 */
	public String getTypeString() {
		return type!=null? type.getTypeString(): "";
	}

	/**
	 * Determine if the word is a verb.
	 * 
	 * @return
	 */
	public boolean isVerb() {
	    return type!=null && type.isVerb();
    }

	/**
	 * Determine if the word is an object. (a thing, not a person)
	 * 
	 * @return
	 */
	public boolean isObject() {
	    return type!=null && type.isObject();
    }

	/**
	 * Determine if the word is a person.
	 * 
	 * @return
	 */
	public boolean isSubject() {
	    return type!=null && type.isSubject();
    }

	/**
	 * Merge expression type with another one
	 * while handling null values.
	 *
	 * @param otherType
	 */
	public void mergeType(ExpressionType otherType) {
		if (type != null) {
			if (otherType != null) {
				type = type.merge(otherType);
			}
		} else {
			type = otherType;
		}
	}

	/**
	 * Return the normalized word with type string
	 * in the format NORMALIZED/TYPE.
	 *
	 * @return
	 */
	public String getNormalizedWithTypeString() {
		return normalized + "/" + getTypeString();
    }

	/**
	 * Return the string expression to be used for matching.
	 *
	 * @return
	 */
	private String getMatchString() {
		// special case for numeric expressions to disambiguate "no" from "0"
		if (type!=null && type.isNumeral()) {
			return original.toLowerCase();
		} else {
			return normalized;
		}
    }

	/**
	 * Check if two words match.
	 * 
	 * @param other word
	 * @return
	 */
	public boolean matches(Expression other) {
		if (other != null) {
			return getMatchString().equals(other.getMatchString());
		} else {
			return false;
		}
	}

	/**
	 * Check if the word beginning matches another word.
	 * 
	 * @param other word
	 * @return
	 */
	public boolean matchesBeginning(Expression other) {
		return getMatchString().startsWith(other.getMatchString());
    }

	/**
	 * Check for equality of two Expression objects.
	 */
	@Override
	public boolean equals(Object other) {
		return toString().equals(other.toString());
	}

	/**
	 * Return a simple string representation of the Expression.
	 */
	@Override
	public String toString() {
		return normalized != null ? normalized : getOriginal();
	}

}
