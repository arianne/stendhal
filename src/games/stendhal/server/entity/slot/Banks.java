package games.stendhal.server.entity.slot;

/**
 * List of banks
 * 
 * @author hendrik
 */
public enum Banks {
	/** bank in Semos */
	SEMOS("bank"),
	/** bank in Ados */
	ADOS("bank_ados"),
	/** bank in Fado */
	FADO("bank_fado"),
	/** bank in Nalwor */
	NALWOR("bank_nalwor"),
	/** mini-bank in zaras house */
	ZARAS("zaras_chest_ados");

	private String slotName;

	/**
	 * create a new TutorialEventType
	 * 
	 * @param message
	 *            human readable message
	 */
	private Banks(String slotName) {
		this.slotName = slotName;
	}

	/**
	 * get the slot name
	 * 
	 * @return slotName
	 */
	public String getSlotName() {
		return slotName;
	}

	/**
	 * returns the Banks enum instance for the specified slot name
	 *
	 * @param slotName name of bank slot
	 * @return Banks enum instance or null
	 */
	public static Banks getBySlotName(String slotName) {
		for (Banks bank : values()) {
			if (bank.getSlotName().equals(slotName)) {
				return bank;
			}
		}
		return null;
	}
}
