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
package games.stendhal.server.core.reflectiondebugger;

import java.util.TreeMap;

/**
 * A tree map which does not override a value in <code>put</code>.
 *
 * @author hendrik
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 */
public class KeepFirstTreeMap<K, V> extends TreeMap<K, V> {
	private static final long serialVersionUID = -2688811366590604961L;

	@Override
	public V put(final K key, final V value) {
		V oldVal = super.get(key);
		if (oldVal != null) {
			return oldVal;
		}

		return super.put(key, value);
	}


}
