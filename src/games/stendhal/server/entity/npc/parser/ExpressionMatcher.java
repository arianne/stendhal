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
	final static String PM_NOCASE_MATCH = "NOCASE";

	final static String PM_MATCH_SEPARATOR = "|";

	/** Flag to enable type string matching. */
	private boolean typeMatching = false;

	/** Flag to enforce exact expression matching. */
	private boolean exactMatching = false;

	/** Flag to enable case insensitive matching. */
	private boolean caseInsensitive = false;

	/** Reset all matching flags. */
	public void clear() {
		typeMatching = false;
		exactMatching = false;
		caseInsensitive = false;
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
	 * Return true if any of the available matching flags is set.
	 *
	 * @return
	 */
	public boolean isAnyFlagSet() {
	    return typeMatching || exactMatching || caseInsensitive;
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

		if (text.startsWith(PM_MATCH_SEPARATOR)) {
			StringTokenizer tok = new StringTokenizer(text, PM_MATCH_SEPARATOR);

    		while (tok.hasMoreTokens()) {
    			String flag = tok.nextToken();

    			if (flag.equals(PM_TYPE_MATCH)) {
    				typeMatching = true;
				} else if (flag.equals(PM_EXACT_MATCH)) {
					exactMatching = true;
				} else if (flag.equals(PM_NOCASE_MATCH)) {
					caseInsensitive = true;
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
	public Sentence parseSentence(String text, ConversationContext ctx) {
		if (typeMatching) {
    		return readTypeMatchExpressions(text, ctx);
        } else if (exactMatching) {
			return readExactExpressions(text, ctx);
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
				e.printStackTrace();
				typeStr = "???";
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
	private Sentence readExactExpressions(String text, ConversationContext ctx) {
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

		// If no exact match is required, compare the normalized expressions.
		if (!exactMatching) {
			if (expr1.getNormalizedMatchString().equals(expr2.getNormalizedMatchString())) {
				return true;
			}

			if (caseInsensitive) {
				if (expr1.getNormalizedMatchString().equalsIgnoreCase(expr2.getNormalizedMatchString())) {
	    			return true;
	    		}
			}
		}

		return false;
    }

}
