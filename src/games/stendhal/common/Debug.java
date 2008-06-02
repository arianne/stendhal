/*
 * Debug.java
 *
 * Created on 12. Oktober 2005, 21:28
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package games.stendhal.common;

/**
 * Gathers all Debug constants in one place.
 * 
 * @author mtotz
 * 
 * TODO: Replace with an XML configuration file?
 */
public interface Debug {

	/** server version. */
	// Note: This line is updated by build.xml using a regexp so be sure to adjust it in case you modify this line.
	String VERSION = "0.68";

	/**
	 * This emulates perception losses. Never make this true and commit it to
	 * CVS
	 */
	boolean EMULATE_PERCEPTION_LOSS = false;

	/**
	 * Log list sizes to find the memory leak. It must be somewhere...
	 */
	boolean SHOW_LIST_SIZES = false;

}
