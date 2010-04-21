package games.stendhal.server.entity.spell.effect;

import games.stendhal.common.constants.Nature;

/**
 * abstract super class for all effects to encapsulate common attributes here
 * 
 * @author madmetzger
 */
public abstract class AbstractEffect implements Effect {

	private final int amount;
	
	private final int atk;
	
	private final int def;
	
	private final double lifesteal;
	
	private final Nature nature;
	
	private final int rate;
	
	private final int regen;

	/**
	 * @param amount
	 * @param atk
	 * @param def
	 * @param lifesteal
	 * @param rate
	 * @param regen
	 */
	public AbstractEffect(Nature nature, int amount, int atk, int def, double lifesteal,
			int rate, int regen) {
		super();
		this.amount = amount;
		this.atk = atk;
		this.def = def;
		this.lifesteal = lifesteal;
		this.rate = rate;
		this.regen = regen;
		this.nature = nature;
	}

	/**
	 * @return the atk
	 */
	protected int getAtk() {
		return atk;
	}

	/**
	 * @return the def
	 */
	protected int getDef() {
		return def;
	}

	/**
	 * @return the lifesteal
	 */
	protected double getLifesteal() {
		return lifesteal;
	}

	/**
	 * @return the rate
	 */
	protected int getRate() {
		return rate;
	}

	/**
	 * @return the regen
	 */
	protected int getRegen() {
		return regen;
	}

	protected int getAmount() {
		return this.amount;
	}

	public Nature getNature() {
		return nature;
	}

}
