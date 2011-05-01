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

import java.io.PrintWriter;

/**
 * Word list entry, used to categorise words. Nouns and verbs can be associated with their plural form.
 *
 * @author Martin Fuchs
 */
public final class WordEntry {

    // normalised word
    private String normalized = "";

    // pluralised word (or singular for entries of type ...-PLU
    private String plurSing;

    // Expression type, e.g. VER, ADJ, OBJ, OBJ-FOO, SUB, SUB-ANI, ...
    private ExpressionType type;

    // numeric value for words of type NUM
    private Integer value;

    // database ID
    private int id;

    /**
     * Write word entry to the given print writer.
     *
     * @param pw
     * @param key
     */
    public void print(final PrintWriter pw, final String key) {
        pw.printf("%s\t", key);

        if (type != null) {
            pw.print(type);
        }

        if (!normalized.equals(key)) {
            pw.printf("\t=%s", normalized);
        }

        if (value != null) {
            pw.printf("\t%d", value);
        }

        if (plurSing != null) {
            pw.printf("\t%s", plurSing);
        }
    }

    void setNormalized(final String normalized) {
        this.normalized = normalized;
    }

    public String getNormalized() {
        return normalized;
    }

    void setType(final ExpressionType type) {
        this.type = type;
    }

    public ExpressionType getType() {
        return type;
    }

    public String getTypeString() {
        if (type != null) {
			return type.getTypeString();
		} else {
			return "";
		}
    }

    public String getNormalizedWithTypeString() {
        return normalized + "/" + getTypeString();
    }

    void setPlurSing(final String plurSing) {
        this.plurSing = plurSing;
    }

    public String getPlurSing() {
        return plurSing;
    }

    public boolean isPlural() {
        return (type != null) && type.isPlural();
    }

    public boolean isVerb() {
        return (type != null) && type.isVerb();
    }

    public boolean isObject() {
        return (type != null) && type.isObject();
    }

    public boolean isSubject() {
        return (type != null) && type.isSubject();
    }

	public boolean isName() {
        return (type != null) && type.isName();
	}

	public boolean isNumeral() {
        return (type != null) && type.isNumeral();
	}

    public boolean isDynamic() {
        return (type != null) && type.isDynamic();
    }

	public boolean isPronoun() {
        return (type != null) && type.isPronoun();
	}

	public boolean isObsessional() {
        return (type != null) && type.isObsessional();
	}

    void setValue(final Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    void setId(final int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    /**
     * Return a simple string representation of the Expression.
     */
    @Override
    public String toString() {
        return getNormalizedWithTypeString();
    }

}
