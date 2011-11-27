package games.stendhal.server.entity.modifier;

import games.stendhal.common.constants.Nature;

import java.util.Date;

import marauroa.common.Pair;

/**
 * Container for modifiers of attributes
 * 
 * @author madmetzger
 */
public class AttributeModifier implements Comparable<AttributeModifier> {
	
	private final Date expireTimestamp;
	
	private final double speedModifier;
	
	private final double atkModifier;
	
	private final double defModifier;
	
	private final double hpModifier;
	
	private final double manaModifier;
	
	private final Pair<Nature, Double> susceptibilitiesModifier;
	
	/**
	 * Create a AttributeModifier only affecting speed
	 * 
	 * @param expireTimestamp
	 * @param speedModifier
	 * @return an AttributeModifier only affecting speed
	 */
	public static AttributeModifier createSpeedModifier(Date expireTimestamp, double speedModifier) {
		return new AttributeModifier(expireTimestamp, speedModifier, 0d, 0d, 0d, 0d, new Pair<Nature, Double>(Nature.CUT, 0d));
	}
	
	/**
	 * Create a AttributeModifier only affecting ATK
	 * 
	 * @param expireTimestamp
	 * @param atkModifier
	 * @return an AttributeModifier only affecting ATK
	 */
	public static AttributeModifier createAtkModifier(Date expireTimestamp, double atkModifier) {
		return new AttributeModifier(expireTimestamp, 0d, atkModifier, 0d, 0d, 0d, new Pair<Nature, Double>(Nature.CUT, 0d));
	}
	
	/**
	 * Create a AttributeModifier only affecting DEF
	 * 
	 * @param expireTimestamp
	 * @param defModifier
	 * @return an AttributeModifier only affecting DEF
	 */
	public static AttributeModifier createDefModifier(Date expireTimestamp, double defModifier) {
		return new AttributeModifier(expireTimestamp, 0d, 0d, defModifier, 0d, 0d, new Pair<Nature, Double>(Nature.CUT, 0d));
	}
	
	/**
	 * Create a AttributeModifier only affecting HP
	 * 
	 * @param expireTimestamp
	 * @param hpModifier
	 * @return an AttributeModifier only affecting HP
	 */
	public static AttributeModifier createHpModifier(Date expireTimestamp, double hpModifier) {
		return new AttributeModifier(expireTimestamp, 0d, 0d, 0d, hpModifier, 0d, new Pair<Nature, Double>(Nature.CUT, 0d));
	}
	
	/**
	 * Create a AttributeModifier only affecting mana
	 * 
	 * @param expireTimestamp
	 * @param manaModifier
	 * @return an AttributeModifier only affecting mana
	 */
	public static AttributeModifier createManaModifier(Date expireTimestamp, double manaModifier) {
		return new AttributeModifier(expireTimestamp, 0d, 0d, 0d, 0d, manaModifier, new Pair<Nature, Double>(Nature.CUT, 0d));
	}
	
	/**
	 * Create a AttributeModifier only affecting the susceptibilities
	 * 
	 * @param expireTimestamp
	 * @param manaModifier
	 * @return an AttributeModifier only affecting mana
	 */
	public static AttributeModifier createSusceptibilityModifier(Date expireTimestamp, Pair<Nature, Double> susceptibilityModifier) {
		return new AttributeModifier(expireTimestamp, 0d, 0d, 0d, 0d, 0d, susceptibilityModifier);
	}
	
	/**
	 * Create a new general purpose AttributeModifier
	 * 
	 * @param expireTimestamp
	 * @param speedModifier
	 * @param atkModifier
	 * @param defModifier
	 * @param hpModifier
	 * @param manaModifier
	 * @param susceptibility
	 */
	private AttributeModifier(Date expireTimestamp, double speedModifier,
			double atkModifier, double defModifier, double hpModifier,
			double manaModifier, Pair<Nature, Double> susceptibility) {
		this.expireTimestamp = expireTimestamp;
		this.speedModifier = speedModifier;
		this.atkModifier = atkModifier;
		this.defModifier = defModifier;
		this.hpModifier = hpModifier;
		this.manaModifier = manaModifier;
		this.susceptibilitiesModifier = susceptibility;
	}

	/**
	 * @return true iff current time is after the expiry timestamp
	 */
	public boolean isExpired() {
		return System.currentTimeMillis() >= getExpireTimestamp().getTime();
	}

	/**
	 * @return the timestamp when this modifier is expired
	 */
	private Date getExpireTimestamp() {
		return expireTimestamp;
	}
	
	/**
	 * @return the number of milliseconds till expiry of this attribute modifier
	 */
	public long getMillisecondsTillExpire() {
		return this.getExpireTimestamp().getTime() - System.currentTimeMillis();
	}

	/**
	 * @return the modifying factor for base_speed if not this.isExpired() == true, 0 otherwise
	 */
	public double getSpeedModifier() {
		if(!this.isExpired()) {
			return speedModifier;
		}
		return 0d;
	}

	/**
	 * @return the modifying factor for ATK if not this.isExpired() == true, 0 otherwise
	 */
	public double getAtkModifier() {
		if(!this.isExpired()) {
			return atkModifier;
		}
		return 0d;
	}

	/**
	 * @return the modifying factor for DEF if not this.isExpired() == true, 0 otherwise
	 */
	public double getDefModifier() {
		if(!this.isExpired()) {
			return defModifier;
		}
		return 0d;
	}

	/**
	 * @return the modifying factor for  base_hp if not this.isExpired() == true, 0 otherwise
	 */
	public double getHpModifier() {
		if(!this.isExpired()) {
			return hpModifier;
		}
		return 0d;
	}

	/**
	 * @return the modifying factor for mana if not this.isExpired() == true, 0 otherwise
	 */
	public double getManaModifier() {
		if(!this.isExpired()) {
			return manaModifier;
		}
		return 0d;
	}

	/**
	 * @param n the nature to get the susceptibility for
	 * @return the modifying factor for the susceptibility if not this.isExpired() == true, 0 otherwise
	 */
	public double getSusceptibilitiesModifier(Nature n) {
		if(susceptibilitiesModifier.first().equals(n) && !this.isExpired()) {
			return susceptibilitiesModifier.second();
		}
		return 0d;
	}

	public int compareTo(AttributeModifier other) {
		return Long.valueOf(this.getExpireTimestamp().getTime() - other.getExpireTimestamp().getTime()).intValue();
	}

	/**
	 * @return the susceptibilitiesModifier
	 */
	public Pair<Nature, Double> getSusceptibilitiesModifier() {
		return susceptibilitiesModifier;
	}

}
