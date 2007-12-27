package games.stendhal.server.entity.npc.newparser;

import java.io.PrintWriter;

/**
 * Word list entry, used to categorise words
 * Nouns and verbs can be associated with their plural form.
 * 
 * @author Martin Fuchs
 */
public class WordEntry {
	public String	normalized;	/** normalised word */
	public WordType	type;		/** word type, e.g. VER, ADJ, NOU, NOU-ANI, ... */
	public String	plurSing;	/** pluralised word (or singular for entries of type ...-PLU */
	public Integer	value;		/** numeric value for words of type NUM */

	public void print(PrintWriter pw, String key) {
	    pw.printf("%s\t", key);

	    if (type != null) {
	    	pw.print(type);
	    }

	    if (plurSing != null) {
	    	pw.printf("\t%s", plurSing);
	    }

	    if (value != null) {
	    	pw.printf("\t%d", value);
	    }

		if (!normalized.equals(key)) {
			pw.printf("\t=%s", normalized);
		}
	}
}
