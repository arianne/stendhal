/**
 * @(#) src/games/stendhal/common/FeatureList.java
 *
 * $Id$
 */

package games.stendhal.common;

//
//

import java.util.HashMap;
import java.util.Iterator;

/**
 * A list of [enabled] features.
 * TODO currently effectively not used
 */
public class FeatureList implements Iterable<String>
{
	protected HashMap<String, String> list;

	/**
	 * Create a list of [enabled] features.
	 */
	public FeatureList() {
		list = new HashMap<String, String>();
	}

	//
	// FeatureList
	//

	/**
	 * Clear the list of features.
	 */
	public void clear() {
		list.clear();
	}

	/**
	 * Read an encoded features list.
	 *
	 * Encoded features are in the form of:<br>
	 * <em>name</em>[<code>=</code><em>value</em>][<code>:</code><em>name</em>[<code>=</code><em>value</em>]...]
	 */
	public void decode(String encoded) {
		int len;
		int pos;
		int epos;
		int cpos;
		String name;
		String value;

		list.clear();

		len = encoded.length();
		pos = 0;

		while (pos < len) {
			cpos = encoded.indexOf(':', pos);
			if (cpos == -1) {
				cpos = len;
			}
			epos = encoded.indexOf('=', pos);
			if ((epos == -1) || (epos > cpos)) {
				epos = cpos;
			}

			name = encoded.substring(pos, epos);

			if (epos < cpos) {
				value = encoded.substring(epos + 1, cpos);
			} else {
				value = "";
			}

			list.put(name, value);

			pos = cpos + 1;
		}
	}

	/**
	 * Build an encoded features list.
	 *
	 * Encoded features are in the form of:<br>
	 * <em>name</em>[<code>=</code><em>value</em>][<code>:</code><em>name</em>[<code>=</code><em>value</em>]...]
	 */
	/*TODO remove unused code
	public String encode() {
		StringBuffer sbuf;

		sbuf = new StringBuffer();

		for (String name : list.keySet()) {
			String value = list.get(name);

			if (sbuf.length() != 0) {
				sbuf.append(':');
			}

			sbuf.append(name);

			if (value.length() != 0) {
				sbuf.append('=');
				sbuf.append(value);
			}
		}

		return sbuf.toString();
	} */

	/**
	 * Get a feature value.
	 *
	 * @return A feature value, or <code>null</code> if not-enabled.
	 */
	public String get(String name) {
		return list.get(name);
	}

	/**
	 * Determine if a feature is enabled.
	 *
	 * @return <code>true</code> is a feature is enabled.
	 */
	/*TODO remove unused code
	public boolean has(String name) {
		return list.containsKey(name);
	} */


	/**
	 * Enable/disable a feature.
	 *
	 * @param name
	 *            The feature mnemonic.
	 * @param enabled
	 *            Flag indicating if enabled.
	 *
	 * @return <code>true</code> if the list changed,
	 *         <code>false<code> otherwise.
	 */
	/*TODO remove unused code
	public boolean set(String name, boolean enabled) {
		return set(name, enabled ? "" : null);
	} */

	/**
	 * Set/remove a feature. <strong>NOTE: The names and values MUST NOT contain
	 * <code>=</code> (equals), or <code>:</code> (colon).
	 *
	 * @param name
	 *            The feature mnemonic.
	 * @param value
	 *            The feature value, or <code>null</code> to disable.
	 *
	 * @return <code>true</code> if the list changed,
	 *         <code>false<code> otherwise.
	 */
	/*TODO remove unused code
	public boolean set(String name, String value) {
		if (value != null) {
			list.put(name, value);
		} else {
			if (list.remove(name) == null) {
				return false;
			}
		}

		return true;
	} */


	//
	// Iterable
	//

	/**
	 * Get the feature names.
	 */
	public Iterator<String> iterator() {
		return list.keySet().iterator();
	}
}
