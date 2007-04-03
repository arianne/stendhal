package games.stendhal.server.entity.slot;

import games.stendhal.server.entity.Entity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Keeps track of "personal chests" which are an interface to the bank slots
 *
 * @author hendrik
 */
public class BankAccessorManager {
	private static BankAccessorManager instance = null;
	private HashMap<Banks, List<Entity>> accessors = null;
	
	private BankAccessorManager() {
		// hide constructor; Singleton patern
		accessors = new HashMap<Banks, List<Entity>>();
	}

	/**
	 * get the BankAccessorManager
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
	 * adds an accessor for this bank
	 *
	 * @param bank   Banks
	 * @param entity Accessor
	 */
	public void add(Banks bank, Entity entity) {
		List<Entity> bankAccess = accessors.get(bank);
		if (bankAccess == null) {
			bankAccess = new LinkedList<Entity>();
			accessors.put(bank, bankAccess);
		}
		if (!bankAccess.contains(entity)) {
			bankAccess.add(entity);
		}
	}

	/**
	 * gets the list of accessors for think bank
	 *
	 * @param bank Banks
	 * @return list of accessors
	 */
	protected List<Entity> get(Banks bank) {

		// If the visibilty of this method should be raised,
		// please return only a copy of this list
		return accessors.get(bank);
	}
}
