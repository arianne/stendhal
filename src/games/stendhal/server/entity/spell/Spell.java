/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.spell;

import games.stendhal.common.constants.Nature;
import games.stendhal.server.core.events.EquipListener;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.PassiveEntity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.trade.Dateable;
import games.stendhal.server.entity.trade.Earning;

import java.util.Arrays;
import java.util.List;

import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.Definition.Type;

import org.apache.log4j.Logger;

/**
 * The base spell class
 * 
 * @author timothyb89, madmetzger
 */
public abstract class Spell extends PassiveEntity implements EquipListener, Dateable {
	
	private static final String RPCLASS_SPELL = "spell";

	/**The spell name attribute name.*/
	private static final String ATTR_NAME = "name";

	private static final String ATTR_ATK = "atk";

	private static final String ATTR_COOLDOWN = "cooldown";

	private static final String ATTR_DEF = "def";

	private static final String ATTR_LIFESTEAL = "lifesteal";

	private static final String ATTR_MANA = "mana";

	private static final String ATTR_MINIMUMLEVEL = "minimumlevel";

	private static final String ATTR_RANGE = "range";

	private static final String ATTR_RATE = "rate";

	private static final String ATTR_REGEN = "regen";

	private static final String ATTR_AMOUNT = "amount";

	private static final String ATTR_TIMESTAMP = "timestamp";

	private static final String ATTR_NATURE = "nature";

	/** list of possible slots for this item. */
	private final List<String> possibleSlots = Arrays.asList("spells");
	
	/**
	 * Casts this spell if all preconditions are fulfilled:
	 *  - caster has enough mana
	 *  - cooldown time expired
	 *  - caster has the minimum level
	 *  - target is valid for the spell
	 *  
	 * @param caster the player who tries to cast this spell
	 * @param target the entity the spell is aimed at
	 */
	public void cast(Player caster, Entity target) {
		//check for sufficient mana
		int currentMana = caster.getMana();
		if (currentMana < getMana() ) {
			caster.sendPrivateText("You have not sufficent mana to cast your spell \""+getName()+"\".");
			return;
		}
		//check minimum level
		if (caster.getLevel() < getMinimumLevel()) {
			caster.sendPrivateText("You did not reach the minimum level for your spell \""+getName()+"\" yet.");
			return;
		}
		long earliestPossibleNextCastingTime = getTimestamp() + getCooldown(); 
		if(System.currentTimeMillis() < earliestPossibleNextCastingTime) {
			caster.sendPrivateText("Your spell \""+getName()+"\" did not yet cool down.");
			return;
		}
		//check if target is valid for spell?
		if (!isTargetValid(caster, target)) {
			caster.sendPrivateText("The target is not valid for your spell \""+getName()+"\".");
			return;
		}
		//check other preconditions like having learned that school?
		//check for right equipment
		//deduct mana
		caster.setMana(currentMana - getMana());
		doEffects(caster, target);
		//set last casting time for calculation of cooldown
		setTimestamp(System.currentTimeMillis());
		//log gameEvent
	}
	
	/**
	 * Provides the concrete behaviour of each concrete spell, i.e. a healing effect should done here
	 * 
	 * @param caster
	 * @param target
	 */
	protected abstract void doEffects(Player caster, Entity target);
	
	/**
	 * Checks if the target Entity is applicable for this spell. Basically each Entity can target of a spell.
	 * Subclasses have to override this method if they want to be more strict in the choice of the target.
	 * 
	 * @param caster the user of the spell
	 * @param target the target Entity to check the applicability for 
	 * @return true iff target is applicable to this spell
	 */
	protected boolean isTargetValid(Entity caster, Entity target) {
		return true;
	}

	/**
	 * Generate the RPClass for spells
	 */
	public static void generateRPClass() {
		final RPClass entity = new RPClass(RPCLASS_SPELL);
		entity.isA("entity");
		entity.addAttribute(ATTR_NAME, Type.STRING);
		entity.addAttribute(ATTR_AMOUNT, Type.INT);
		entity.addAttribute(ATTR_ATK, Type.INT);
		entity.addAttribute(ATTR_COOLDOWN, Type.INT);
		entity.addAttribute(ATTR_DEF, Type.INT);
		entity.addAttribute(ATTR_LIFESTEAL, Type.FLOAT);
		entity.addAttribute(ATTR_MANA, Type.INT);
		entity.addAttribute(ATTR_MINIMUMLEVEL, Type.INT);
		entity.addAttribute(ATTR_NATURE, Type.STRING);
		entity.addAttribute(ATTR_RANGE, Type.INT);
		entity.addAttribute(ATTR_RATE, Type.INT);
		entity.addAttribute(ATTR_REGEN, Type.INT);
		// class = nature
		entity.addAttribute("class", Type.STRING);
		entity.addAttribute("subclass", Type.STRING);
	}
	
	/**
	 * Creates a spell from an RPObject
	 * 
	 * @param object the RPObject to create the spell from
	 */
	public Spell(final RPObject object) {
		super(object);
		setNature(Nature.parse(object.get(ATTR_NATURE)));
		setRPClass(RPCLASS_SPELL);
	}
	
	/**
	 * Creates a new {@link Spell}
	 * Sub classes of {@link Spell} *have to* provide a constructor with this order of parameters!
	 * 
	 * @param name the name of the spell
	 * @param nature the nature of the spell
	 * @param amount the amount of the effect of this spell
	 * @param atk the atk value of the spell
	 * @param cooldown the time the spell needs to cool down before casting it again
	 * @param def the def value of the spell
	 * @param lifesteal the percentage of lifesteal for this spell
	 * @param mana the amount of mana this spell uses when casting it
	 * @param minimumlevel the required minimum level for this spell
	 * @param range the max distance for the spell target
	 * @param rate the frequency of the effect of this spell
	 * @param regen the amount to regen with each effect turn
	 */
	public Spell(	final String name, final Nature nature, final int amount, final int atk, final int cooldown,
					final int def, final double lifesteal, final int mana, final int minimumlevel,
					final int range, final int rate, final int regen) {
		setRPClass(RPCLASS_SPELL);
		put(ATTR_NAME, name);
		put("subclass", name);
		put("class", nature.toString().toLowerCase());
		put(ATTR_AMOUNT, amount);
		put(ATTR_ATK, atk);
		put(ATTR_COOLDOWN, cooldown);
		put(ATTR_DEF, def);
		put(ATTR_LIFESTEAL, lifesteal);
		put(ATTR_MANA, mana);
		put(ATTR_MINIMUMLEVEL, minimumlevel);
		put(ATTR_RANGE, range);
		put(ATTR_RATE, rate);
		put(ATTR_NATURE, nature.name());
		put("type", "spell");
	}

	public boolean canBeEquippedIn(final String slot) {
		return possibleSlots.contains(slot);
	}

	/**
	 * Get the spell name.
	 * 
	 * @return The spell's name, or <code>null</code> if undefined.
	 */
	public String getName() {
		if (has(ATTR_NAME)) {
			return get(ATTR_NAME);
		} else {
			return null;
		}
	}
	
	/**
	 * Get the spell amount.
	 * 
	 * @return The spell's amount, or <code>0</code> if undefined.
	 */
	public int getAmount() {
		if (has(ATTR_AMOUNT)) {
			return getInt(ATTR_AMOUNT);
		} else {
			return 0;
		}
	}
	
	/**
	 * Get the spell atk.
	 * 
	 * @return The spell's atk, or <code>0</code> if undefined.
	 */
	public int getAtk() {
		if (has(ATTR_ATK)) {
			return getInt(ATTR_ATK);
		} else {
			return 0;
		}
	}
	
	/**
	 * Get the spell cooldown.
	 * 
	 * @return The spell's cooldown, or <code>0</code> if undefined.
	 */
	public int getCooldown() {
		if (has(ATTR_COOLDOWN)) {
			return getInt(ATTR_COOLDOWN);
		} else {
			return 0;
		}
	}
	
	/**
	 * Get the spell def.
	 * 
	 * @return The spell's def, or <code>0</code> if undefined.
	 */
	public int getDef() {
		if (has(ATTR_DEF)) {
			return getInt(ATTR_DEF);
		} else {
			return 0;
		}
	}
	
	/**
	 * Get the spell lifesteal.
	 * 
	 * @return The spell's lifesteal, or <code>0</code> if undefined.
	 */
	public double getLifesteal() {
		if (has(ATTR_LIFESTEAL)) {
			return getDouble(ATTR_LIFESTEAL);
		} else {
			return 0;
		}
	}
	
	/**
	 * Get the spell mana.
	 * 
	 * @return The spell's mana, or <code>0</code> if undefined.
	 */
	public int getMana() {
		if (has(ATTR_MANA)) {
			return getInt(ATTR_MANA);
		} else {
			return 0;
		}
	}
	
	/**
	 * Get the spell minimum level.
	 * 
	 * @return The spell's minimum level, or <code>0</code> if undefined.
	 */
	public int getMinimumLevel() {
		if (has(ATTR_MINIMUMLEVEL)) {
			return getInt(ATTR_MINIMUMLEVEL);
		} else {
			return 0;
		}
	}
	
	/**
	 * Get the spell range.
	 * 
	 * @return The spell's range, or <code>0</code> if undefined.
	 */
	public int getRange() {
		if (has(ATTR_RANGE)) {
			return getInt(ATTR_RANGE);
		} else {
			return 0;
		}
	}
	
	/**
	 * Get the spell rate.
	 * 
	 * @return The spell's rate, or <code>0</code> if undefined.
	 */
	public int getRate() {
		if (has(ATTR_RATE)) {
			return getInt(ATTR_RATE);
		} else {
			return 0;
		}
	}
	
	/**
	 * Get the spell regen.
	 * 
	 * @return The spell's regen, or <code>0</code> if undefined.
	 */
	public int getRegen() {
		if (has(ATTR_REGEN)) {
			return getInt(ATTR_REGEN);
		} else {
			return 0;
		}
	}

	public long getTimestamp() {
		long timeStamp = 0;
		try {
			timeStamp = Long.parseLong(get(ATTR_TIMESTAMP));
		} catch (final NumberFormatException e) {
			Logger.getLogger(Earning.class).error("Invalid timestamp: " + get(ATTR_TIMESTAMP), e);
		}
		return timeStamp;
	}
	
	public void setTimestamp(long time) {
		put(ATTR_TIMESTAMP, Long.toString(time));
	}

	public void setNature(Nature nature) {
		put(ATTR_NATURE, nature.name());
	}

	public Nature getNature() {
		return Nature.parse(get(ATTR_NATURE));
	}
	
}
