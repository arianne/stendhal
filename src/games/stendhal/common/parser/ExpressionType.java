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

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * An ExpressionType defines the type of an Expression object.
 * It uses a human readable string representation like "VER" (verb)
 * or "OBJ" (object). Derived types like negative verbs are
 * written in a concatenated form like "VER-NEG".
 * ExpressionType objects are immutable. To alter expression types,
 * there is always created a new ExpressionType object (similar
 * to the immutable Java String class).
 *
 * @author Martin Fuchs
 */
public final class ExpressionType implements Serializable {

	private static final long serialVersionUID = -8694964043717271498L;

    // Expression type string constants

	/** String constant representing verb form. */
    public static final String VERB = "VER";

    /** String constant representing gerund form. */
    public static final String GERUND = "GER";

    /** String constant representing conditional form. */
    public static final String CONDITIONAL = "CON";
    /** String constant representing negated form. */
    public static final String NEGATED = "NEG";
    /** String constant representing pronoun. */
    public static final String PRONOUN = "PRO";

    /** String constant representing an object (syntax).*/
    public static final String OBJECT = "OBJ";

    /** String constant representing a fluid. */
    public static final String FLUID = "FLU";

    /** String constant representing food. */
    public static final String FOOD = "FOO";

    /** String constant representing an obsessional word. */
    public static final String OBSESSIONAL = "OBS";


    /** String constant representing a subject. */
    public static final String SUBJECT = "SUB";

    /** String constant representing an animal. */
    public static final String ANIMAL = "ANI";

    /** String constant representing a person's name. */
    public static final String NAME = "NAM";

    /** String constant representing an adjective or adverb. */
    public static final String ADJECTIVE = "ADJ";

    /** String constant representing a color. */
    public static final String COLOR = "COL";

    /** String constant representing a numeral. */
    public static final String NUMERAL = "NUM";

    /** String constant representing a preposition. */
    public static final String PREPOSITION = "PRE";

    /** String constant representing  a question word. */
    public static final String QUESTION = "QUE";

    /** String constant representing an expression which is to be ignored. */
    public static final String IGNORE = "IGN";

    /** String constant representing a suffix. */
    public static final String SUFFIX = "-";

    /** String constant representing plural form. */
    public static final String PLURAL = "PLU";

    /** String constant representing a n expression dynamically defined at runtime. */
    public static final String DYNAMIC = "DYN";

    /** String constant representing a type less expression. */
    public static final String UNKNOWN = "";

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
    public static final List<String> TYPESTRINGS = Arrays.asList(
		VERB, OBJECT, SUBJECT, ADJECTIVE, NUMERAL,
		PREPOSITION, QUESTION, IGNORE,

		SUFFIX_GERUND, SUFFIX_COLOR, SUFFIX_CONDITIONAL, SUFFIX_NEGATED, SUFFIX_PRONOUN, SUFFIX_FOOD,
		SUFFIX_OBSESSIONAL, SUFFIX_FLUID, SUFFIX_ANIMAL, SUFFIX_NAME, SUFFIX_PLURAL
    );

    private final String typeString;

    public ExpressionType(final String s) {
        typeString = s;
    }

    /**
     * @return type string Note: There is no setTypeString to make ExpressionType objects immutable.
     *

     */
    public String getTypeString() {
        return typeString;
    }

    /**
     * @return main Expression type string (first 3 letters).
     *
     */
    public String getMainType() {
        if (typeString.length() >= 3) {
            return typeString.substring(0, 3);
        } else {
            return typeString;
        }
    }

    /**
	 * Returns true if, and only if, length() is 0.
	 *
	 * @return true if length() is 0, otherwise false
	 */
    public boolean isEmpty() {
    	return typeString.length() == 0;
    }

    /**
     * Determine if the Expression consists of verbs.
     *
     * @return true if the typeString starts with the verb-constant
     */
    public boolean isVerb() {
        return typeString.startsWith(VERB);
    }

    /**
     * Determine if the Expression contains a verb in gerund form.
     * @return true if so
     *
     */
    public boolean isGerund() {
        return typeString.startsWith(VERB_GERUND);
    }

    /**
     * Determine if the Expression is an object. (a thing, not a person)
     *
     * @return false if not an object or null, true otherwise
     */
    public boolean isObject() {
        return typeString.startsWith(OBJECT);
    }

    /**
     * Determine if the Expression is an subject. (a thing, not a person)
     *
     * @return false if not a subject, true otherwise
     */
    public boolean isSubject() {
        return typeString.startsWith(SUBJECT);
    }

    /**
     * Determine if the Expression consists of numeral words.
     * @return true if typeString start with 'NUM'; false otherwise.
     */
    public boolean isNumeral() {
        return typeString.startsWith(NUMERAL);
    }

    /**
     * Determine if the Expression consists of adjectives or adverbs.
     * @return true if typeString start with 'ADJ'; false otherwise.
     */
    public boolean isAdjective() {
        return typeString.startsWith(ADJECTIVE);
    }

    /**
     * Determine if the Expression consists of prepositions.
     * @return true if typeString start with 'PRE'; false otherwise.

     */
    public boolean isPreposition() {
        return typeString.startsWith(PREPOSITION);
    }

    /**
     * Determine Expressions to ignore.
     * @return true if typeString start with 'IGN'; false otherwise.
     */
    public boolean isIgnore() {
        return typeString.startsWith(IGNORE);
    }

    /**
     * Determine if the Expression is in plural form.
     * @return true if typeString contains '-PLU'; false otherwise.
     */
    public boolean isPlural() {
        return typeString.contains(SUFFIX_PLURAL);
    }

    /**
     * Determine if the Expression is a creature name.
     * @return true if typeString contains '-NAM'; false otherwise.
     */
    public boolean isName() {
        return typeString.contains(SUFFIX_NAME);
    }

    /**
     * Determine if the Expression is an animal.
     * @return true if typeString contains '-ANI'; false otherwise.
     */
    public boolean isAnimal() {
        return typeString.contains(SUFFIX_ANIMAL);
    }

    /**
     * Determine if the Expression is some food.
     * @return true if typeString contains '-FOO'; false otherwise.
     */
    public boolean isFood() {
        return typeString.contains(SUFFIX_FOOD);
    }

    /**
     * Determine if the Expression is some fluid.
     * @return true if typeString contains '-FLU'; false otherwise.
     */
    public boolean isFluid() {
        return typeString.contains(SUFFIX_FLUID);
    }

    /**
     * Determine if the Expression consists of question words.
     * @return true if typeString start with 'QUE'; false otherwise.
     */
    public boolean isQuestion() {
        return typeString.startsWith(QUESTION);
    }

    /**
     * Determine if the Expression is a or is merged with a question word.
     * @return true if typeString contains 'QUE'; false otherwise.
     */
    public boolean hasQuestion() {
        return typeString.contains(QUESTION);
    }

    /**
     * Determine if the Expression is a obsessional one.
     * @return true if typeString contains '-OBS'; false otherwise.
     */
    public boolean isObsessional() {
        return typeString.contains(SUFFIX_OBSESSIONAL);
    }

    /**
     * Determine if the Expression specifies a color.
     * @return true if typeString contains 'COL'; false otherwise.
     */
    public boolean hasColor() {
        return typeString.contains(COLOR);
    }

    /**
     * Determine if the Expression is a pronoun.
     * @return true if typeString contains '-PRO'; false otherwise.
     */
    public boolean isPronoun() {
        return typeString.contains(SUFFIX_PRONOUN);
    }

    /**
     * Determine if the Expression is in conditional form.
     * @return true if typeString contains '-CON'; false otherwise.
     */
    public boolean isConditional() {
        return typeString.contains(SUFFIX_CONDITIONAL);
    }

    /**
     * Determine if the Expression is negated.
     * @return true if typeString contains '-NEG'; false otherwise.
     */
    public boolean isNegated() {
        return typeString.contains(SUFFIX_NEGATED);
    }

    /**
     * Determine if the Expression contains a dynamically defined word.
     * @return true if typeString contains '-DYN'; false otherwise.
     */
    public boolean isDynamic() {
        return typeString.contains(SUFFIX_DYNAMIC);
    }

    /**
     * Check if the given String contains a type string specifier.
     *
     * @param str
     *
     * @return true if first letter is upper case and contains any of the predefined TYPESTRINGs
     * */
    public static boolean isTypeString(final String str) {
        if (str.length() > 0) {
            final char first = str.charAt(0);

            // All type strings must start with an upper case letter,
            // even the SUFFIX character '-' is not allowed.
            if (Character.isUpperCase(first)) {
                for (final String ts : TYPESTRINGS) {
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

    /**
     * Negate the type.
     * This are the two typical cases:
     * VER -> VER-NEG
     * VER-NEG -> VER
     * @return negated expression
     */
    public ExpressionType negate()
    {
    	if (isNegated()) {
			return new ExpressionType(typeString.replace(SUFFIX_NEGATED, ""));
		} else {
			return new ExpressionType(typeString + SUFFIX_NEGATED);
		}
    }

    @Override
    public String toString() {
        return typeString;
    }

}
