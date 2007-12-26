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
	public String	type;	/** word type, e.g. VER, ADJ, NOU, NOU-ANI, ... */
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

	/**
	 * determine is the word is a verb
	 * @return
	 */
	public boolean isVerb() {
	    return type.startsWith("VER");
    } 

	/**
	 * determine is the word is a noun
	 * @return
	 */
	public boolean isNoun() {
	    return type.startsWith("NOU");
    } 

	/**
	 * determine is the word is a numeral
	 * @return
	 */
	public boolean isNumeral() {
	    return type.startsWith("NUM");
    } 

	/**
	 * determine is the word is a preposition
	 * @return
	 */
	public boolean isPreposition() {
	    return type.startsWith("PRE");
    } 

	/**
	 * determine is the word is in plural form
	 * @return
	 */
	public boolean isPlural() {
	    return type.endsWith("-PLU");
    } 
}
