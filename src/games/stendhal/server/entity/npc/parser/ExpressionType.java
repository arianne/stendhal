package games.stendhal.server.entity.npc.parser;

import java.util.Arrays;
import java.util.List;

public final class ExpressionType {

    private final String typeString;

    // Expression type string constants
    public static final String VERB = "VER"; // verb
    public static final String GERUND = "GER"; // gerund form
    public static final String CONDITIONAL = "CON"; // conditional form
    public static final String NEGATED = "NEG"; // negated form
    public static final String PRONOUN = "PRO"; // pronoun

    public static final String OBJECT = "OBJ"; // object
    public static final String AMOUNT = "AMO"; // amount
    public static final String FLUID = "FLU"; // fluid
    public static final String FOOD = "FOO"; // food
    public static final String OBSESSIONAL = "OBS"; // obsessional word

    public static final String SUBJECT = "SUB"; // subject
    public static final String ANIMAL = "ANI"; // animal
    public static final String NAME = "NAM"; // person name

    public static final String ADJECTIVE = "ADJ"; // adjective/adverb
    public static final String COLOR = "COL"; // color expression

    public static final String NUMERAL = "NUM"; // numeral
    public static final String PREPOSITION = "PRE"; // preposition

    public static final String QUESTION = "QUE"; // question word

    public static final String IGNORE = "IGN"; // expression to ignore

    public static final String SUFFIX = "-";

    public static final String PLURAL = "PLU"; // plural form

    public static final String DYNAMIC = "DYN"; // expression dynamically defined at runtime

    public static final String UNKNOWN = ""; // expression without type

    // derived string type constants
    public static final String SUFFIX_GERUND = SUFFIX + GERUND;
    public static final String SUFFIX_COLOR = SUFFIX + COLOR;
    public static final String SUFFIX_CONDITIONAL = SUFFIX + CONDITIONAL;
    public static final String SUFFIX_NEGATED = SUFFIX + NEGATED;
    public static final String SUFFIX_PRONOUN = SUFFIX + PRONOUN;
    public static final String SUFFIX_FOOD = SUFFIX + FOOD;
    public static final String SUFFIX_OBSESSIONAL = SUFFIX + OBSESSIONAL;
    public static final String SUFFIX_FLUID = SUFFIX + FLUID;
    public static final String SUFFIX_ANIMAL = SUFFIX + ANIMAL;
    public static final String SUFFIX_NAME = SUFFIX + NAME;
    public static final String SUBJECT_NAME = SUBJECT + SUFFIX_NAME;
    public static final String SUFFIX_PLURAL = SUFFIX + PLURAL;
    public static final String SUFFIX_QUESTION = SUFFIX + QUESTION;
    public static final String VERB_GERUND = VERB + SUFFIX_GERUND;
    public static final String SUFFIX_DYNAMIC = SUFFIX + DYNAMIC;

    /** Type string specifiers, which can be used in sentence matching. */
    public static final List<String> TYPESTRINGS = Arrays.asList(VERB, OBJECT, AMOUNT, SUBJECT, ADJECTIVE, NUMERAL,
            PREPOSITION, QUESTION, IGNORE,

            SUFFIX_GERUND, SUFFIX_COLOR, SUFFIX_CONDITIONAL, SUFFIX_NEGATED, SUFFIX_PRONOUN, SUFFIX_FOOD,
            SUFFIX_OBSESSIONAL, SUFFIX_FLUID, SUFFIX_ANIMAL, SUFFIX_NAME, SUFFIX_PLURAL);

    public ExpressionType(final String s) {
        typeString = s;
    }

    /**
     * Return type string Note: There is no setTypeString to make ExpressionType objects immutable.
     *
     * @return
     */
    public String getTypeString() {
        return typeString;
    }

    /**
     * Return main Expression type string (first 3 letters).
     *
     * @return
     */
    public String getMainType() {
        if (typeString.length() >= 3) {
            return typeString.substring(0, 3);
        } else {
            return typeString;
        }
    }

    /**
     * Look for empty type strings.
     *
     * @return
     */
    public boolean isEmpty() {
        return typeString.length() == 0;
    }

    /**
     * Determine if the Expression consists of verbs.
     *
     * @return
     */
    public boolean isVerb() {
        return typeString.startsWith(VERB);
    }

    /**
     * Determine if the Expression contains a verb in gerund form.
     *
     * @return
     */
    public boolean isGerund() {
        return typeString.startsWith(VERB_GERUND);
    }

    /**
     * Determine if the Expression is an object (a thing, not a person).
     *
     * @return
     */
    public boolean isObject() {
        return typeString.startsWith(OBJECT);
    }

    /**
     * Determine if the Expression represents a person.
     *
     * @return
     */
    public boolean isSubject() {
        return typeString.startsWith(SUBJECT);
    }

    /**
     * Determine if the Expression consists of numeral words.
     *
     * @return
     */
    public boolean isNumeral() {
        return typeString.startsWith(NUMERAL);
    }

    /**
     * Determine if the Expression consists of adjectives or adverbs.
     *
     * @return
     */
    public boolean isAdjective() {
        return typeString.startsWith(ADJECTIVE);
    }

    /**
     * Determine if the Expression consists of prepositions.
     *
     * @return
     */
    public boolean isPreposition() {
        return typeString.startsWith(PREPOSITION);
    }

    /**
     * Determine Expressions to ignore.
     *
     * @return
     */
    public boolean isIgnore() {
        return typeString.startsWith(IGNORE);
    }

    /**
     * Determine if the Expression is in plural form.
     *
     * @return
     */
    public boolean isPlural() {
        return typeString.contains(SUFFIX_PLURAL);
    }

    /**
     * Determine if the Expression is a creature name.
     *
     * @return
     */
    public boolean isName() {
        return typeString.contains(SUFFIX_NAME);
    }

    /**
     * Determine if the Expression consists of question words.
     *
     * @return
     */
    public boolean isQuestion() {
        return typeString.startsWith(QUESTION);
    }

    /**
     * Determine if the Expression is a or is merged with a question word.
     *
     * @return
     */
    public boolean hasQuestion() {
        return typeString.contains(QUESTION);
    }

    /**
     * Determine if the Expression specifies a color.
     *
     * @return
     */
    public boolean hasColor() {
        return typeString.contains(COLOR);
    }

    /**
     * Determine if the Expression is a pronoun.
     *
     * @return
     */
    public boolean isPronoun() {
        return typeString.contains(SUFFIX_PRONOUN);
    }

    /**
     * Determine if the Expression is in conditional form.
     *
     * @return
     */
    public boolean isConditional() {
        return typeString.contains(SUFFIX_CONDITIONAL);
    }

    /**
     * Determine if the Expression is negated.
     *
     * @return
     */
    public boolean isNegated() {
        return typeString.contains(SUFFIX_NEGATED);
    }

    /**
     * Determine if the Expression contains a dynamically defined word.
     *
     * @return
     */
    public boolean isDynamic() {
        return typeString.contains(SUFFIX_DYNAMIC);
    }

    /**
     * Check if the given String contains a type string specifier.
     *
     * @param str
     * @return
     */
    public static boolean isTypeString(final String str) {
        if (str.length() > 0) {
            char first = str.charAt(0);

            // All type strings must start with an upper case letter,
            // even the SUFFIX character '-' is not allowed.
            if (Character.isUpperCase(first)) {
                for (String ts : TYPESTRINGS) {
                    if (str.contains(ts)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Merge with another ExpressionType.
     *
     * @param other
     * @return new ExpressionType object or this
     */
    public ExpressionType merge(final ExpressionType other) {
        String newTypeString = typeString;
        boolean modified = false;

        if (other.hasColor() && !hasColor()) {
            newTypeString += SUFFIX_COLOR;
            modified = true;
        }

        if (other.hasQuestion() && !hasQuestion()) {
            newTypeString += SUFFIX_QUESTION;
            modified = true;
        }

        if (other.isConditional() && !isConditional()) {
            newTypeString += SUFFIX_CONDITIONAL;
            modified = true;
        }

        if (other.isPronoun() && !isPronoun()) {
            newTypeString += SUFFIX_PRONOUN;
            modified = true;
        }

        if (other.isNegated() != isNegated()) {
            newTypeString += SUFFIX_NEGATED;
            modified = true;
        }

        if (other.isDynamic() && !isDynamic()) {
            newTypeString += SUFFIX_DYNAMIC;
            modified = true;
        }

        // Check if there was any change of the type, then we must create and return
        // a new ExpressionType object, as typeString is immutable.
        if (modified) {
            return new ExpressionType(newTypeString);
        } else {
            return this;
        }
    }

    @Override
    public String toString() {
        return typeString;
    }

}
