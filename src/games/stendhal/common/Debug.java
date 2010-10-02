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
 */
public class Debug {
	/** if this is enable tileset are loaded on demand. */
	public static final boolean VERY_FAST_CLIENT_START = true;

	/** enables cycling through the panel textures by clicking at the title bar. */
	public static final boolean CYCLE_PANEL_TEXTURES = false;

	/** version. */
	// Note: This line is updated by build.xml using a regexp so be sure to adjust it in case you modify this line.
	public static final String VERSION = "0.87.5";

	public static final String PRE_RELEASE_VERSION = null;

	/**
	 * Log list sizes to find the memory leak. It must be somewhere...
	 */
	public static final boolean SHOW_LIST_SIZES = false;
	
	/**
	 * it shows entity's view area
	 */
	public static final boolean SHOW_ENTITY_VIEW_AREA = false;
}
