/***************************************************************************
 *                    Copyright © 2003-2022 - Arianne                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.common.constants;


/**
 * Defines the range at which an entity can be heard speaking.
 */
public enum VoiceRange {
	NORMAL(15),
	MAP(500);

	private final int range;


	private VoiceRange(final int range) {
		this.range = range;
	}

	/**
	 * Retrieves a <code>VoiceRange</code> using an integer value. Defaults to
	 * <code>VoiceRange.NORMAL</code>.
	 *
	 * @param value
	 *     The value used to find a <code>VoiceRange</code>.
	 * @return
	 *     The <code>VoiceRange</code> matching the <code>value</code> or
	 *     <code>VoiceRange.NORMAL</code> if not match is found.
	 */
	public static VoiceRange fromInt(final int value) {
		for (final VoiceRange vr: VoiceRange.values()) {
			if (value == vr.getValue()) {
				return vr;
			}
		}

		return VoiceRange.NORMAL;
	}

	/**
	 * Retrieves the range of the <code>VoiceRange</code>.
	 *
	 * @return
	 *     Range.
	 */
	public int getValue() {
		return range;
	}
}
