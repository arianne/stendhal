/*
 * @(#) src/games/stendhal/common/ConfigurableFactoryContext.java
 *
 * $Id$
 */

package games.stendhal.common;

//
//

/**
 * A configuration context for general object factories. This will allow for
 * adding things like hierarchal configurations later without breaking
 * everything.
 */
public interface ConfigurableFactoryContext {
	/**
	 * Get an attribute.
	 *
	 * @param	name		The attribute name.
	 *
	 * @return	The value of the attribute, or <code>null</code> if
	 *		not set.
	 */
	public String getAttribute(String name);
}
