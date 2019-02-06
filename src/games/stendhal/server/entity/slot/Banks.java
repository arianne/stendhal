/***************************************************************************
 *                   (C) Copyright 2003-2019 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.slot;

/**
 * List of banks.
 *
 * @author hendrik
 */
public enum Banks {
	/** bank in Semos. */
	SEMOS("bank"),
	/** bank in Ados. */
	ADOS("bank_ados"),
	/** bank in Deniran */
	DENIRAN("bank_deniran"),
	/** bank in Fado. */
	FADO("bank_fado"),
	/** bank in Nalwor. */
	NALWOR("bank_nalwor"),
	/** mini-bank in zaras house. */
	ZARAS("zaras_chest_ados");

	private String slotName;

	/**
	 * Creates a new Bank.
	 *
	 * @param slotName
	 *            the banks name
	 */
	private Banks(final String slotName) {
		this.slotName = slotName;
	}

	/**
	 * Gets the slot name.
	 *
	 * @return slotName
	 */
	public String getSlotName() {
		return slotName;
	}

	/**
	 * Returns the Banks instance for the specified slot name.
	 *
	 * @param slotName name of bank slot
	 * @return Banks enum instance or null
	 */
	public static Banks getBySlotName(final String slotName) {
		for (final Banks bank : values()) {
			if (bank.getSlotName().equals(slotName)) {
				return bank;
			}
		}
		return null;
	}
}
