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
	static final String VERSION = "0.67";

	/**
	 * This emulates perception losses. Never make this true and commit it to
	 * CVS
	 */
	static final boolean EMULATE_PERCEPTION_LOSS = false;

	/**
	 * Log list sizes to find the memory leak. It must be somewhere...
	 */
	static final boolean SHOW_LIST_SIZES = false;

}
