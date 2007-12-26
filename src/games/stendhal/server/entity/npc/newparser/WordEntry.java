package games.stendhal.server.entity.npc.newparser;

import java.io.PrintWriter;

/**
 * Word list entry, used to categorize words
 * Nouns and verbs can be associated with their plural form.
 * 
 * @author Martin Fuchs
 */
public class WordEntry {
	public String	word;	/** word */
	public String	type;	/** word type, e.g. VER, ADJ, PLU, PLU-ANI, ... */
	public String	plural;	/** pluralised word (or singular for entries of type ...-PLU */
	public Integer	value;	/** numeric value for words of type NUM */

	public void print(PrintWriter pw) {
	    pw.printf("%s\t", word);

	    if (type != null) {
	    	pw.print(type);
	    }

	    if (plural != null) {
	    	pw.printf("\t%s", plural);
	    }

	    if (value != null) {
	    	pw.printf("\t%d", value);
	    }
    } 
}
