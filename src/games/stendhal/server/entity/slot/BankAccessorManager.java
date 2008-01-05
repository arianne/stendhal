package games.stendhal.server.entity.slot;

import games.stendhal.server.entity.Entity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Keeps track of "personal chests" which are an interface to the bank slots.
 * 
 * @author hendrik
 */
public class BankAccessorManager {
	private static BankAccessorManager instance;
	private HashMap<Banks, List<Entity>> accessors;

	private BankAccessorManager() {
		// hide constructor; Singleton patern
		accessors = new HashMap<Banks, List<Entity>>();
	}

	/**
	 * Gets the BankAccessorManager.
	 * 
	 * @return BankAccessorManager
	 */
	public static BankAccessorManager get() {
		if (instance == null) {
			instance = new BankAccessorManager();
		}
		return instance;
	}

	/**
	 * Adds an accessor for this bank.
	 * 
	 * @param bank
	 *            Banks
	 * @param entity
	 *            Accessor
	 */
	public void add(Banks bank, Entity entity) {
		List<Entity> bankAccess = getListAddingUnkownBanks(bank);
		if (!bankAccess.contains(entity)) {
			bankAccess.add(entity);
		}
	}

	/**
	 * Gets the list of accessors for the specified bank. 
	 * <p> In case the bank is unkown, an empty list is automatically created
	 * 
	 * @param bank
	 *            Banks
	 * @return list of accessors
	 */
	private List<Entity> getListAddingUnkownBanks(Banks bank) {
		List<Entity> bankAccess = accessors.get(bank);
		if (bankAccess == null) {
			bankAccess = new LinkedList<Entity>();
			accessors.put(bank, bankAccess);
		}
		return bankAccess;
	}

	/**
	 * gets the list of accessors for think bank.
	 * 
	 * @param bank
	 *            Banks
	 * @return list of accessors or an empty list if this bank is unkown
	 */
	protected List<Entity> get(Banks bank) {

		// If the visibilty of this method should be raised,
		// please return only a copy of this list
		return getListAddingUnkownBanks(bank);
	}
}
