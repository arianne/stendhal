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
package games.stendhal.server.core.rule.defaultruleset;

import java.lang.reflect.Constructor;

import org.apache.log4j.Logger;

import games.stendhal.common.constants.Nature;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.core.rule.defaultruleset.creator.AbstractCreator;
import games.stendhal.server.core.rule.defaultruleset.creator.FullSpellCreator;
import games.stendhal.server.entity.spell.Spell;
/**
 * Spell information are loaded from XML into a {@link DefaultSpell}.
 * The {@link EntityManager} uses this information to instantiate objects of
 * the right type.
 *
 * @author madmetzger
 */
public class DefaultSpell {

	private static final Logger logger = Logger.getLogger(DefaultSpell.class);

	private AbstractCreator<Spell> creator;

	private String name;

	private Nature nature;

	private Class<?> implementationClass;

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

	private double modifier;

	/**
	 * Creates a new {@link DefaultSpell}
	 * @param name the name of that spell
	 * @param clazzName class name
	 */
	public DefaultSpell(String name, String clazzName) {
		try {
			this.name = name;
			this.implementationClass = Class.forName(clazzName);
			this.buildCreator(implementationClass);
		} catch (ClassNotFoundException e) {
			logger.error("Error while creating DefaultSpell", e);
		}
	}

	private void buildCreator(final Class< ? > implementation) {
		try {
			Constructor< ? > construct;
			construct = implementation.getConstructor(new Class[] {
					String.class, Nature.class, int.class, int.class, int.class,
					int.class, double.class, int.class, int.class,
					int.class, int.class, int.class, double.class});

			this.creator = new FullSpellCreator(this, construct);
		} catch (final NoSuchMethodException ex) {
			logger.error("No matching full constructor for Spell found.", ex);
		}

	}

	/**
	 * Creates a new instance using the configured implementation class of that spell
	 *
	 * @return an instance of the specified implementation class
	 */
	public Spell getSpell() {
		if (creator == null) {
			return null;
		}
		return creator.create();
	}


	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the class object specified in the xml configuration
	 */
	public Class<?> getImplementationClass() {
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

	/**
	 * sets the nature for the spell
	 * @param nature the spell's nature
	 */
	public void setNature(String nature) {
		this.nature = Nature.parse(nature);
	}

	/**
	 * @return the spell's nature
	 */
	public Nature getNature() {
		return nature;
	}

	/**
	 * @return the modifier value
	 */
	public double getModifier() {
		return this.modifier;
	}
	/**
	 * @param modifier the modifier to set
	 */
	public void setModifier(String modifier) {
		this.modifier = Double.parseDouble(modifier);
	}

}
