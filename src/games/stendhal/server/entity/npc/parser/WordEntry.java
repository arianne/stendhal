package games.stendhal.server.entity.npc.parser;

import java.io.PrintWriter;

/**
 * Word list entry, used to categorise words
 * Nouns and verbs can be associated with their plural form.
 * 
 * @author Martin Fuchs
 */
public class WordEntry {
	private WordType type;		/** word type, e.g. VER, ADJ, OBJ, OBJ-FOO, SUB, SUB-ANI, ... */
	private String	normalized;	/** normalised word */
	private String	plurSing;	/** pluralised word (or singular for entries of type ...-PLU */
	private Integer	value;		/** numeric value for words of type NUM */

	public void print(PrintWriter pw, String key) {
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

	public void setNormalized(String normalized) {
	    this.normalized = normalized;
    }

	public String getNormalized() {
	    return normalized;
    }

	public void setType(WordType type) {
	    this.type = type;
    }

	public WordType getType() {
	    return type;
    }

	public void setPlurSing(String plurSing) {
	    this.plurSing = plurSing;
    }

	public String getPlurSing() {
	    return plurSing;
    }

	public void setValue(Integer value) {
	    this.value = value;
    }

	public Integer getValue() {
	    return value;
    }
}
