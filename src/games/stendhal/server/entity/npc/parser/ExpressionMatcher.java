package games.stendhal.server.entity.npc.parser;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * ExpressionMatcher is used to compare Expression in various matching modes. 
 *
 * @author Martin Fuchs
 */
public class ExpressionMatcher {

	// There are some patterns that can be used as leading flags in expression strings, e.g. "|EXACT|":
	final static String PM_TYPE_MATCH = "TYPE";
	final static String PM_EXACT_MATCH = "EXACT";
	final static String PM_SIMILAR_MATCH = "SIMILAR";
	final static String PM_ICASE_MATCH = "ICASE";
	final static String PM_JOKER_MATCH = "JOKER";

	public final static String PM_SEPARATOR = "|";

	/** Flag to enable type string matching. */
	protected boolean typeMatching = false;

	/** Flag to enforce exact expression matching. */
	protected boolean exactMatching = false;

	/** Flag to use similarity matching. */
	protected boolean similarMatching = false;

	/** Flag to enable case insensitive matching. */
	protected boolean caseInsensitive = false;

	/** Flag to enable joker matching. */
	protected boolean jokerMatching = false;

	/** Reset all matching flags. */
	public void clear() {
		typeMatching = false;
		exactMatching = false;
		similarMatching = false;
		caseInsensitive = false;
		jokerMatching = false;
	}

	/**
     * @param typeMatching the typeMatching to set
     */
	public void setTypeMatching(boolean typeMatching) {
	    this.typeMatching = typeMatching;
    }

	/**
     * @return the typeMatching
     */
    public boolean getTypeMatching() {
	    return typeMatching;
    }

	/**
     * @param exactMatching the exactMatching to set
     */
    public void setExactMatching(boolean exactMatching) {
	    this.exactMatching = exactMatching;
    }

	/**
     * @return the exactMatching
     */
    public boolean getExactMatching() {
	    return exactMatching;
    }

	/**
     * @param similarMatching the similarMatching to set
     */
    public void setSimilarMatching(boolean similarMatching) {
	    this.similarMatching = similarMatching;
    }

	/**
     * @return the similarMatching
     */
    public boolean getSimilarMatching() {
	    return similarMatching;
    }

	/**
     * @param caseInsensitive the caseInsensitive to set
     */
    public void setCaseInsensitive(boolean caseInsensitive) {
	    this.caseInsensitive = caseInsensitive;
    }

	/**
     * @return the caseInsensitive
     */
    public boolean isCaseInsensitive() {
	    return caseInsensitive;
    }

	/**
     * @return the jokerMatching
     */
    public boolean isJokerMatching() {
	    return jokerMatching;
    }

	/**
     * @param jokerMatching the jokerMatching to set
     */
    public void setjokerMatching(boolean jokerMatching) {
	    this.jokerMatching = jokerMatching;
    }

	/**
	 * Return true if any of the available matching flags is set.
	 *
	 * @return
	 */
	public boolean isAnyFlagSet() {
	    return typeMatching || exactMatching || similarMatching || caseInsensitive || jokerMatching;
    }

	/**
	 * Return true if none of the available matching flags is set.
	 *
	 * @return
	 */
	public boolean isEmpty() {
	    return !isAnyFlagSet();
    }

	/**
	 * Read leading matching flags from the given text string and return the remaining text.
	 *
	 * @param text
	 * @return text without leading flags
	 */
	public String readMatchingFlags(String text) {
		clear();

		if (text.startsWith(PM_SEPARATOR)) {
			StringTokenizer tok = new StringTokenizer(text, PM_SEPARATOR);

    		while (tok.hasMoreTokens()) {
    			String flag = tok.nextToken();

    			if (flag.equals(PM_TYPE_MATCH)) {
    				typeMatching = true;
				} else if (flag.equals(PM_EXACT_MATCH)) {
					exactMatching = true;
				} else if (flag.equals(PM_SIMILAR_MATCH)) {
					similarMatching = true;
				} else if (flag.equals(PM_ICASE_MATCH)) {
					caseInsensitive = true;
				} else if (flag.equals(PM_JOKER_MATCH)) {
					jokerMatching = true;
				} else {
					break;
				}

				text = text.substring(flag.length()+1);
    		}

    		if (isAnyFlagSet()) {
    			// strip the last separator character
    			text = text.substring(1);
    		}
		}

	    return text;
    }

	/**
	 * Parse the given text string and create a Sentence object using the current matching flags.
	 *
	 * @param text
	 * @param ctx
	 * @return
	 */
	Sentence parseSentence(String text, ConversationContext ctx) {
		if (typeMatching) {
    		return readTypeMatchExpressions(text, ctx);
        } else if (exactMatching) {
			return readSimpleExpressions(text, ctx);
        } else if (similarMatching) {
			return readSimpleExpressions(text, ctx);
        } else if (jokerMatching) {
			return readJokerExpressions(text, ctx);
        } else if (caseInsensitive) {
			return readSimpleExpressions(text, ctx);
    	} else {
    		return ConversationParser.parse(text, ctx);
    	}
	}

	/**
	 * Read in the expressions from the given string in prepared form. The given text
	 * should be in the format: "<expression>/<TYPESTRING> <expression>/<TYPESTRING> ..."
	 *
	 * @param text: Text to be parsed
	 * @return Sentence
	 */
	private Sentence readTypeMatchExpressions(String text, ConversationContext ctx) {
		SentenceImplementation sentence = new SentenceImplementation(ctx);

		StringTokenizer tok = new StringTokenizer(text, "/");
		while (tok.hasMoreTokens()) {
			String str = tok.nextToken();
			String typeStr;

			try {
    			// remove the leading slash from the type string
    			typeStr = tok.nextToken(" \t\n\r\f").substring(1);
			} catch(NoSuchElementException e) {
				typeStr = "*";
			}

			Expression expr = new Expression(str, typeStr);
			expr.setMatcher(this);
			sentence.expressions.add(expr);
		}

		return sentence;
    }

	/**
	 * Read in the words from the given string and create the sentence using this unchanged expressions.
	 *
	 * @param text: Text to be parsed
	 * @return Sentence
	 */
	private Sentence readSimpleExpressions(String text, ConversationContext ctx) {
		SentenceImplementation sentence = new SentenceImplementation(ctx);

		StringTokenizer tok = new StringTokenizer(text);
		while (tok.hasMoreTokens()) {
			String str = tok.nextToken();

			Expression expr = new Expression(str);
			expr.setMatcher(this);
			sentence.expressions.add(expr);
		}

		return sentence;
    }

	/**
	 * Read in the words from the given string and create the sentence using the same rules as
	 * in SentenceImplementation with activated 'forMatching' flag.
	 *
	 * @param text: Text to be parsed
	 * @return Sentence
	 */
	private Sentence readJokerExpressions(String text, ConversationContext ctx) {
		SentenceImplementation sentence = new SentenceImplementation(ctx);

		StringTokenizer tok = new StringTokenizer(text);
		while (tok.hasMoreTokens()) {
			String str = tok.nextToken();

			Expression expr = new Expression(str);

			if (ExpressionType.isTypeString(str)) {
				expr.setType(new ExpressionType(str));
				expr.setNormalized(Expression.JOKER);
			}

			expr.setMatcher(this);
			sentence.expressions.add(expr);
		}

		return sentence;
    }

	/**
	 * Match two Expressions using the mode in matchingFlags.
	 *
	 * @param expr1
	 * @param expr2
	 * @return
	 */
	public boolean match(Expression expr1, Expression expr2) {
		// In type matching mode, the word type has to match exactly.
		if (typeMatching) {
			if (!expr1.getTypeString().equals(expr2.getTypeString())) {
				return false;
			}
		}

		// If the original expression matches, return true.
		if (expr1.getOriginal().equals(expr2.getOriginal())) {
			return true;
		}

		if (caseInsensitive) {
    		if (expr1.getOriginal().equalsIgnoreCase(expr2.getOriginal())) {
    			return true;
    		}
		}

		if (jokerMatching) {
			return expr1.sentenceMatchExpression(expr2);
		}

		if (similarMatching) {
    		if (SimilarExprMatcher.isSimilar(expr1.getOriginal(), expr2.getOriginal(), 0.1)) {
    			return true;
    		}
		}

		// If no exact match is required, compare the normalized expressions.
		if (!exactMatching) {
			if (expr2.getNormalized().equals(Expression.JOKER)) {
				return true;
			}

			if (expr1.getNormalized().equals(expr2.getNormalized())) {
				return true;
			}

			if (caseInsensitive) {
				if (expr1.getNormalized().equalsIgnoreCase(expr2.getNormalized())) {
	    			return true;
	    		}
			}
		}

		return false;
    }

	/**
	 * Check for equality of two ExpressionMatcher objects.
	 */
	@Override
    public boolean equals(Object other) {
		if (this == other) {
			return true;
		} else if (other == null) {
			return false;
		} else if (other.getClass() == ExpressionMatcher.class) {
			ExpressionMatcher o = (ExpressionMatcher)other;

    		if (typeMatching != o.typeMatching) {
    			return false;
    		} else if (exactMatching != o.exactMatching) {
    			return false;
    		} else if (similarMatching != o.similarMatching) {
    			return false;
    		} else if (caseInsensitive != o.caseInsensitive) {
    			return false;
    		} else if (jokerMatching != o.jokerMatching) {
    			return false;
    		} else {
    			return true;
    		}
		} else {
			return false;
		}
	}

    /**
     * Returns a hash code for this ExpressionMatcher object.
     */
	@Override
	public int hashCode() {
		int hash = 0;

		if (typeMatching) {
			hash |= 1;
		}

		if (exactMatching) {
			hash |= 2;
		}

		if (similarMatching) {
			hash |= 4;
		}

		if (caseInsensitive) {
			hash |= 8;
		}

		if (jokerMatching) {
			hash |= 0x10;
		}

		return hash;
	}

	/**
	 * Return a simple string representation.
	 */
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();

		if (typeMatching) {
			b.append(PM_SEPARATOR);
			b.append(PM_TYPE_MATCH);
		}

		if (exactMatching) {
			b.append(PM_SEPARATOR);
			b.append(PM_EXACT_MATCH);
		}

		if (similarMatching) {
			b.append(PM_SEPARATOR);
			b.append(PM_SIMILAR_MATCH);
		}

		if (caseInsensitive) {
			b.append(PM_SEPARATOR);
			b.append(PM_ICASE_MATCH);
		}

		if (jokerMatching) {
			b.append(PM_SEPARATOR);
			b.append(PM_JOKER_MATCH);
		}

		return b.toString();
	}

}
