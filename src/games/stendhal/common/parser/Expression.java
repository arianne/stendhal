/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.common.parser;

import java.util.regex.Pattern;

import games.stendhal.common.ErrorDrain;

/**
 * An Expression is part of a Sentence. It encapsulates the original, white space trimmed text, the
 * expression type, a normalized lower case text string and the integer amount.
 *
 * @author Martin Fuchs
 */
public final class Expression {
	/** Instance of an empty Expression. */
    public static final Expression EMPTY_EXPRESSION = new Expression("", "");

    /** JOKER is a joker String used in pattern matches. */
    public static final String JOKER = "*";

    /** Original, un-normalized string expression. */
    private String original;

    /** Expression type. */
    private ExpressionType type;

    /** Normalized string representation of this Expression. */
    private String normalized = "";

    /** Main word of the Expression. */
    private String mainWord;

    /** Number of items. */
    private Integer amount;

    /** Break flag to define sentence part borders. */
    private boolean breakFlag = false;

    /** Expression matcher for comparing expressions in various modes. */
    private ExpressionMatcher matcher = null;


    /**
     * Create an Expression from the given original string. Normalized form, main word and type are
     * not yet defined.
     *
     * @param str
     */
    /*protected*/ Expression(final String str) {
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
     *  TODO mf - We may switch from Integer to Long
     * if we extend the column type in table 'words'
     *
     * @param str
     * @param errors
     */
    public void parseAmount(final String str, final ErrorDrain errors) {
        try {
            // replace commas by dots to recognize numbers like "1,5"
            final String numberString = str.replace(',', '.');

            // Parse as float number, then round to the next integer.
            setAmount((int) Math.round(Double.parseDouble(numberString)));

            setType(new ExpressionType(ExpressionType.NUMERAL));
            normalized = amount.toString();
        } catch (final NumberFormatException e) {
            errors.setError("illegal number format: '" + str + "'");
        }
    }

    /**
     * Merge the given preceding Expression into this Expression while leaving
     * mainWord unchanged.The members normalized and original are only touched
     * when negating the expression.
     *
     * @param other
     */
    public void mergeSimple(final Expression other) {
        mergeType(other.getType());
        setAmount(mergeAmount(other.amount, amount));
    }

    /**
     * Merge the given preceding Expression into this Expression, while leaving mainWord unchanged.
     *
     * @param prec
     * @param mergeNormalized
     */
    public void mergeLeft(final Expression prec, final boolean mergeNormalized) {
        original = prec.getOriginal() + ' ' + original;

        if (mergeNormalized) {
            normalized = prec.getNormalized() + ' ' + normalized;
        }

        mergeType(prec.getType());
        setAmount(mergeAmount(prec.amount, amount));
    }

    /**
     * Merge the given following Expression into this Expression, while leaving mainWord unchanged.
     *
     * @param other
     * @param mergeNormalized
     */
    public void mergeRight(final Expression other, final boolean mergeNormalized) {
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
                	// e.g. five hundred
                    return left * right;
                } else {
                	// e.g. hundred fifty
                    return left + right;
                }
            } else {
                return left;
            }
        } else {
            return right;
        }
    }

    /**
     * Merge the given following name component into this Expression.
     *
     * @param next
     * @param newType
     */
    public void mergeName(final Expression next, ExpressionType newType) {
        original = original + ' ' + next.getOriginal();

        if (newType.isName()) {
        	setNormalized(original.toLowerCase());
        } else {
        	setNormalized(normalized + ' ' + next.getNormalized());
        }

        setType(newType);
        setAmount(mergeAmount(amount, next.amount));
        breakFlag = next.getBreakFlag();
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
     * @return amount as integer value, default to 1.
     */
    public int getAmount() {
		if (amount == null) {
			return 1;
		} else {
			return amount.intValue();
		}
	}

    /**
     * @return true if there is an explicit amount.
     */
    public boolean hasAmount() {
		return amount != null;
	}

	/**
	 * @return amount as long integer value, default to 1.
	 */
	public long getAmountLong() {
		if (amount == null) {
			return 1;
		} else {
			return amount;
		}
	}

    /**
	 * Set the break flag to define sentence part borders.
	 */
    public void setBreakFlag() {
        breakFlag = true;
    }

    /**
     * Set Expression matcher.
     *
     * @param matcher
     */
    public void setMatcher(final ExpressionMatcher matcher) {
        this.matcher = matcher;
    }

    /**
     * Return matcher used for matching this expression.
     *
     * @return matcher
     */
    public ExpressionMatcher getMatcher() {
        return matcher;
    }

    /**
     *
     * @return the original, un-normalized string expression.
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
     * @return normalized form
     */
    public String getNormalized() {
        return normalized;
    }

    /**
     * @return the main word of the expression.
     */
    public String getMainWord() {
    	if (mainWord == null) {
    		return "";
    	} else {
    		return mainWord;
    	}
    }

    /**
     * @return the break flag to check for sentence part borders.
     */
    public boolean getBreakFlag() {
        return breakFlag;
    }

    /**
     * Set Expression type.
     *
     * @param type
     */
    public void setType(final ExpressionType type) {
        this.type = type;
    }

    /**
     *
     * @return  Expression type.
     */
    public ExpressionType getType() {
        return type;
    }

    /**
     *
     * @return string representation of Expression type
     */
    public String getTypeString() {
		if (type == null) {
			return "";
		} else {
			return type.getTypeString();
		}
	}

    /**
	 * Determine if the Expression consists of verbs.
	 *
	 * @return false if not a verb or null, true otherwise
	 */
    public boolean isVerb() {
        return (type != null) && type.isVerb();
    }

    /**
     * Determine if the Expression is an object. (a thing, not a person)
     *
     * @return false if not an object or null, true otherwise
     */
    public boolean isObject() {
        return (type != null) && type.isObject();
    }

    /**
     * Determine if the Expression represents a person.
     *
     * @return false if not a subject or null, true otherwise
     */
    public boolean isSubject() {
        return (type != null) && type.isSubject();
    }

    /**
     * Determine if the Expression is negated.
     *
     * @return false if not negated or null, true otherwise
     */
    public boolean isNegated() {
        return (type != null) && type.isNegated();
    }

    /**
     * Determine Expressions to ignore.
     *
     * @return false if not ignored or null, true otherwise
     */
    public boolean isIgnore() {
        return (type != null) && type.isIgnore();
    }

    /**
     * Determine if the Expression consists of question words.
     *
     * @return false if not a question or null, true otherwise
     */
    public boolean isQuestion() {
        return (type != null) && type.isQuestion();
    }

    /**
     * Determine if the Expression consists of prepositions.
     *
     * @return false if not a preposition or null, true otherwise
     */
    public boolean isPreposition() {
        return (type != null) && type.isPreposition();
    }

    /**
     * Determine if the Expression consists of numeral words.
     *
     * @return false if not a numeral or null, true otherwise
     */
    public boolean isNumeral() {
        return (type != null) && type.isNumeral();
    }

    /**
     * Determine if the Expression contains a dynamically defined word.
     *
     * @return true dynamically created
     */
	public boolean isDynamic() {
        return (type != null) && type.isDynamic();
	}

    /**
     * Merge Expression type with another one while handling null values.
     *
     * @param otherType
     */
    public void mergeType(final ExpressionType otherType) {
        if (type != null) {
            if (otherType != null) {
                type = type.merge(otherType);

                if (otherType.isNegated()) {
                	negateStrings();
                }
            }
        } else {
            type = otherType;
        }
    }

    /**
     * Return the normalized Expression with type.
     *
     * @return string in the format NORMALIZED/TYPE.
     */
    public String getNormalizedWithTypeString() {
        return normalized + "/" + getTypeString();
    }

    /**
     * Check if two Expressions match exactly.
     *
     * @param other
     *            Expression
     * @return true if two expression match, false otherwise
     */
    public boolean matches(final Expression other) {
        if (other != null) {
            // If there is no override by an ExpressionMatcher in 'other', use the
            // default rule and compare the original strings.
            if (other.matcher == null) {
                if (original.equals(other.original)) {
                    return true;
                }
            } else {
                // If both Expressions contain a matcher object, first compare this.
                if (matcher != null) {
                    if (!matcher.equals(other.matcher)) {
                        return false;
                    }
                }

                // Now call the matcher to look if the Expression matches the defined rule.
                return other.matcher.match(this, other);
            }
        }

        return false;
    }

    /**
     * Check if two Expressions match each other.
     *
     * @param other
     *            Expression
     * @return true if two expression match, false otherwise
     */
    public boolean matchesNormalized(final Expression other) {
        if (other != null) {
            // If there is no override by an ExpressionMatcher in 'other', use the
            // default rule and compare the normalised strings.
            if (other.matcher == null) {
                if (getNormalized().equals(other.getNormalized())) {
                    return true;
                }
            } else {
                // If both Expressions contain a matcher object, first compare this.
                if (matcher != null) {
                    if (!matcher.equals(other.matcher)) {
                        return false;
                    }
                }

                return other.matcher.match(this, other);
            }
        }

        return false;
    }

    /**
     * Check if the Expression is similar to another Expression.
     *
     * @param other
     *            Expression
     * @return true if two expression match, false otherwise
     */
    public boolean matchesNormalizedSimilar(final Expression other) {
        if (other != null) {
            // If there is no override by an ExpressionMatcher in 'other', use the
            // default rule and compare the normalised strings and main words.
            if (other.matcher == null) {
                if (SimilarExprMatcher.isSimilar(getNormalized(), other.getNormalized(), 0.1)) {
                    return true;
                } else if (SimilarExprMatcher.isSimilar(getMainWord(), other.getMainWord(), 0.1)) {
                    return true;
                }
            }

            // We don't use ExpressionMatcher here when searching only for matches at the Expression start.
        }

        return false;
    }

    /**
	 * Check if the Expression matches the given matching Expression.
	 *
	 * <p>
	 * The matching object can contain explicit expressions, which are compared
	 * after normalizing, or ExpressionType specifiers like "VER" or "SUB*" in
	 * upper case. This defines the joker matching algorithm for sentence
	 * matching, which chooses automatically between word and type matching,
	 * depending on which of word and word type string is given.
	 *
	 * @param other
	 * @return true if this expression matches the other, false otherwise
	 */
    boolean sentenceMatchExpression(final Expression other) {
        final String matchString = other.getNormalized();

        if (matchString.contains(JOKER)) {
            if (matchString.equals(JOKER)) {
                // Type string matching is identified by a single "*" as normalized string expression.
                if (!matchesJokerString(getTypeString(), other.getTypeString())) {
                    return false;
                }
            } else {
                // Look for a normalized string match against the string containing a joker character.
                if (!matchesJokerString(getNormalized(), matchString)) {
                    return false;
                }
            }
        } else if (other.getMatcher() != null) {
            // avoid endless recursion
            return original.equals(other.original);
        } else if (!matchesNormalized(other)) {
            return false;
        }

        return true;
    }

    /**
     * Match the given String against a pattern String containing JOKER characters.
     *
     * @param str
     * @param matchString
     * @return true if two expression match, false otherwise
     */
    public static boolean matchesJokerString(final String str, final String matchString) {
        if (str.equals(JOKER)) {
            // Empty strings do not match the "*" joker.
            return str.length() > 0;
        } else {
            // Convert the joker string into a regular expression and let the Pattern class do the work.
            return Pattern.compile(matchString.replace(JOKER, ".*")).matcher(str).find();
        }
    }

	/**
	 * Negate the expression.
	 * This is used in SentenceImplementation to normalize sentences containing "don't" expressions.
	 */
	public void negate() {
		type = getType().negate();

		negateStrings();
	}

	/**
	 * Negate original and normalized string, while leaving
	 * type untouched.
	 */
	private void negateStrings() {
		if (type.isNegated()) {
			original = original + " not";
			normalized = normalized + " not";
		} else {
			original = original.replaceFirst(" not", "");
			normalized = normalized.replaceFirst(" not", "");
		}
	}

    /**
     * Check for equality of two Expression objects.
     */
    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        } else if (other == null) {
            return false;
        } else if (other.getClass() == Expression.class) {
            final Expression o = (Expression) other;

            if (matcher != null) {
                if ((o.matcher == null) || !matcher.equals(o.matcher)) {
                    return false;
                }
            } else {
                if (o.matcher != null) {
                    return false;
                }
            }

            if (normalized.length() > 0) {
                return normalized.equals(o.normalized);
            } else {
                return original.equals(o.original);
            }
        } else {
            return false;
        }
    }

    /**
     * Returns a hash code for this Expression object.
     */
    @Override
    public int hashCode() {
        int hash;

        if (normalized.length() > 0) {
            hash = normalized.hashCode();
        } else {
            hash = original.hashCode();
        }

        if (matcher != null) {
            hash ^= matcher.hashCode();
        }

        return hash;
    }

    /**
     * Return a simple string representation of the Expression.
     */
    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();

        if (matcher != null) {
            b.append(matcher.toString());
            b.append(ExpressionMatcher.PM_SEPARATOR);
        }

        if (normalized.length() > 0) {
            b.append(normalized);
        } else {
            b.append(original);
        }

        return b.toString();
    }

}
