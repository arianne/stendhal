package games.stendhal.server.core.rule.defaultruleset;

import games.stendhal.server.entity.spell.Spell;

import org.apache.log4j.Logger;

public class DefaultSpell {
	
	private static final Logger logger = Logger.getLogger(DefaultSpell.class);
	
	private String name;
	
	private Class<? extends Spell> implementationClass;
	
	private int mana;
	
	private int cooldown;
	
	private int minimumLevel;
	
	private int range;
	
	private int atk;
	
	private int def;
	
	private int amount;
	
	private int regen;
	
	private int rate;
	
	private double lifesteal;

	/**
	 * Creates a new {@link DefaultSpell}
	 * @param name the name of that spell
	 */
	public DefaultSpell(String name, String clazzName) {
		try {
			this.name = name;
			this.implementationClass = (Class<? extends Spell>) Class.forName(clazzName);
		} catch (ClassNotFoundException e) {
			logger.error("Error while creating DefaultSpell", e);
		}
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	public Class<? extends Spell> getImplementationClass() {
		return implementationClass;
	}
	/**
	 * @return the mana
	 */
	public int getMana() {
		return mana;
	}

	/**
	 * @param mana the mana to set
	 */
	public void setMana(String mana) {
		this.mana = Integer.parseInt(mana);
	}

	/**
	 * @return the cooldown
	 */
	public int getCooldown() {
		return cooldown;
	}

	/**
	 * @param cooldown the cooldown to set
	 */
	public void setCooldown(String cooldown) {
		this.cooldown = Integer.parseInt(cooldown);
	}

	/**
	 * @return the minimumLevel
	 */
	public int getMinimumLevel() {
		return minimumLevel;
	}

	/**
	 * @param minimumLevel the minimumLevel to set
	 */
	public void setMinimumLevel(String minimumLevel) {
		this.minimumLevel = Integer.parseInt(minimumLevel);
	}

	/**
	 * @return the range
	 */
	public int getRange() {
		return range;
	}

	/**
	 * @param range the range to set
	 */
	public void setRange(String range) {
		this.range = Integer.parseInt(range);
	}

	/**
	 * @return the atk
	 */
	public int getAtk() {
		return atk;
	}

	/**
	 * @param atk the atk to set
	 */
	public void setAtk(String atk) {
		this.atk = Integer.parseInt(atk);
	}

	/**
	 * @return the def
	 */
	public int getDef() {
		return def;
	}

	/**
	 * @param def the def to set
	 */
	public void setDef(String def) {
		this.def = Integer.parseInt(def);
	}

	/**
	 * @return the amount
	 */
	public int getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(String amount) {
		this.amount = Integer.parseInt(amount);
	}

	/**
	 * @return the regen
	 */
	public int getRegen() {
		return regen;
	}

	/**
	 * @param regen the regen to set
	 */
	public void setRegen(String regen) {
		this.regen = Integer.parseInt(regen);
	}

	/**
	 * @return the rate
	 */
	public int getRate() {
		return rate;
	}

	/**
	 * @param rate the rate to set
	 */
	public void setRate(String rate) {
		this.rate = Integer.parseInt(rate);
	}

	/**
	 * @return the lifesteal
	 */
	public double getLifesteal() {
		return lifesteal;
	}

	/**
	 * @param lifesteal the lifesteal to set
	 */
	public void setLifesteal(String lifesteal) {
		this.lifesteal = Float.parseFloat(lifesteal);
	}

}
