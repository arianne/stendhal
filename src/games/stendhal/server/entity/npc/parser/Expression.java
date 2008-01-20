package games.stendhal.server.entity.npc.parser;

import java.util.Iterator;

/**
 * An Expression is part of a Sentence. It encapsulates the original, white space
 * trimmed text, the expression type, a normalized lower case text string and the
 * integer amount.
 * 
 * @author Martin Fuchs
 */
public class Expression {

	/** original, un-normalized string expression.*/
	private String original;

	/** Expression type. */
	private ExpressionType type;

	/** normalized string representation of this Expression. */
	private String normalized;

	/** main word of the Expression. */
	private String mainWord;

	/** number of items. */
	private Integer amount;

	/** break flag to define sentence part borders. */
	private boolean breakFlag = false;

	/** instance of an empty Expression. */
	public static final Expression emptyExpression = new Expression("", "");

	/**
	 * Create an Expression from the given original string.
	 * Normalized form, main word and type are not yet defined.
	 * 
	 * @param str
	 */
	public Expression(final String str) {
		original = str;
	}

	/**
	 * Create an Expression from a single word and a type string.
	 * 
	 * @param word
	 * @param typeString
	 */
	public Expression(final String word, final String typeString) {
		original = word;
		normalized = word;
		mainWord = word;
		type = new ExpressionType(typeString);
	}

	/**
	 * Parse the given numeric expression and assign the value to 'amount'.
	 * TODO mf - We may switch from Integer to Long if we extend the column type in table 'words'
	 *
	 * @param str
	 * @param parser
	 */
	public void parseAmount(final String str, ConversationParser parser) {
		try {
			// replace commas by dots to recognize numbers like "1,5"
			String numberString = str.replace(',', '.');

			// Parse as float number, then round to the next integer.
			setAmount((int) Math.round(Double.parseDouble(numberString)));

			setType(new ExpressionType(ExpressionType.NUMERAL));
			normalized = amount.toString();
		} catch (NumberFormatException e) {
			parser.setError("illegal number format: '" + str + "'");
		}
	}

	/**
	 * Merge the given preceding Expression into this Expression,
	 * while leaving mainWord unchanged.
	 * 
	 * @param other
	 * @param mergeNormalized
	 */
	public void mergeLeft(final Expression other, boolean mergeNormalized) {
		original = other.getOriginal() + ' ' + original;

		if (mergeNormalized) {
			normalized = other.getNormalized() + ' ' + normalized;
		}

		mergeType(other.getType());
		setAmount(mergeAmount(other.amount, amount));
	}

	/**
	 * Merge the given following Expression into this Expression,
	 * while leaving mainWord unchanged.
	 * 
	 * @param other
	 * @param mergeNormalized
	 */
	public void mergeRight(final Expression other, boolean mergeNormalized) {
		original = original + ' ' + other.getOriginal();

		if (mergeNormalized) {
			normalized = normalized + ' ' + other.getNormalized();
		}

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
	private static Integer mergeAmount(final Integer left, final Integer right) {
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

	/**
	 * Set item count.
	 *
	 * @param amount
	 */
	public void setAmount(final Integer amount) {
		this.amount = amount;
	}

	/**
	 * Return amount as integer value, default to 1.
	 * 
	 * @return
	 */
	public int getAmount() {
		return amount != null ? amount.intValue() : 1;
	}

	/**
	 * Return amount as integer value, default to 1.
	 * 
	 * @return
	 */
	public long getAmountLong() {
		return amount != null ? amount : 1;
	}

	/**
	 * Set the break flag to define sentence part borders.
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

	/**
	 * Set the normalized form of the expression.
	 *
	 * @param normalized
	 */
	public void setNormalized(final String normalized) {
		this.normalized = normalized;
		this.mainWord = normalized;
	}

	/**
	 * Return the normalized form of the Expression.
	 *
	 * @return
	 */
	public String getNormalized() {
		return normalized;
	}

	/**
	 * Return the main word of the expression.
	 *
	 * @return
	 */
	public String getMainWord() {
		return mainWord;
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
	 * Set Expression type.
	 *
	 * @param type
	 */
	public void setType(ExpressionType type) {
		this.type = type;
	}

	/**
	 * Return the Expression type.
	 *
	 * @return
	 */
	public ExpressionType getType() {
		return type;
	}

	/**
	 * Return type Expression type string.
	 * 
	 * @return
	 */
	public String getTypeString() {
		return type != null ? type.getTypeString() : "";
	}

	/**
	 * Determine if the Expression consists of verbs.
	 * 
	 * @return
	 */
	public boolean isVerb() {
	    return type != null && type.isVerb();
    }

	/**
	 * Determine if the Expression is an object. (a thing, not a person)
	 * 
	 * @return
	 */
	public boolean isObject() {
		return type != null && type.isObject();
	}

	/**
	 * Determine if the Expression represents a person.
	 * 
	 * @return
	 */
	public boolean isSubject() {
		return type != null && type.isSubject();
	}

	/**
	 * Determine if the Expression is negated.
	 * 
	 * @return
	 */
	public boolean isNegated() {
		return type != null && type.isNegated();
	}

	/**
	 * Determine Expressions to ignore.
	 * 
	 * @return
	 */
	public boolean isIgnore() {
		return type != null && type.isIgnore();
    }

	/**
	 * Determine if the Expression consists of question words.
	 * 
	 * @return
	 */
	public boolean isQuestion() {
		return type != null && type.isQuestion();
    }

	/**
	 * Determine if the Expression consists of prepositions.
	 * 
	 * @return
	 */
	public boolean isPreposition() {
		return type != null && type.isPreposition();
    }

	/**
	 * Determine if the Expression consists of numeral words.
	 * 
	 * @return
	 */
	public boolean isNumeral() {
		return type != null && type.isNumeral();
    }

	/**
	 * Merge Expression type with another one while handling null values.
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
	 * Return the normalized Expression with type string
	 * in the format NORMALIZED/TYPE.
	 *
	 * @return
	 */
	public String getNormalizedWithTypeString() {
		return normalized + "/" + getTypeString();
    }

	/**
	 * Check if two Expressions match exactly.
	 * 
	 * @param other Expression
	 * @return
	 */
	public boolean matches(final Expression other) {
		if (other != null) {
			return original.equals(other.original);
		} else {
			return false;
		}
	}

	/**
	 * Return the string expression to be used for matching.
	 *
	 * @return
	 */
	String getNormalizedMatchString() {
		// special case for numeric expressions to disambiguate "no" from "0"
		if (type != null && type.isNumeral()) {
			return original.toLowerCase();
		} else {
			return normalized;
		}
    }

	/**
	 * Check if two Expressions match.
	 * 
	 * @param other Expression
	 * @return
	 */
	public boolean matchesNormalized(final Expression other) {
		if (other != null) {
			return getNormalizedMatchString().equals(other.getNormalizedMatchString());
		} else {
			return false;
		}
	}

	/**
	 * Check if the Expression beginning matches another Expression.
	 * 
	 * @param other Expression
	 * @return
	 */
	public boolean matchesNormalizedBeginning(final Expression other) {
		if (other != null) {
			return getNormalizedMatchString().startsWith(other.getNormalizedMatchString());
		} else {
			return false;
		}
    }

	/**
	 * Advance the iterator and return the next non-ignorable Expression.
	 * 
	 * @param it
	 * @return
	 */
	public static Expression nextValid(Iterator<Expression> it) {
		while (it.hasNext()) {
			Expression expr = it.next();

			if (!expr.isIgnore()) {
				return expr;
			}
		}

	    return null;
    }

	/**
	 * Check for equality of two Expression objects.
	 */
	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		} else if (other == null) {
			return false;
		} else if (other instanceof Expression) {
	        return original.equals(((Expression) other).original);
        } else {
        	return toString().equals(other.toString());
        }
	}

    /**
     * Returns a hash code for this Expression object.
     */
	@Override
	public int hashCode() {
		return original.hashCode();
	}

	/**
	 * Return a simple string representation of the Expression.
	 */
	@Override
	public String toString() {
		return normalized != null ? normalized : original;
	}

}
