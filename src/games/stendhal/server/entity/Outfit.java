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
package games.stendhal.server.entity;

import games.stendhal.common.Outfits;
import games.stendhal.common.Rand;
import games.stendhal.common.constants.Testing;

import org.apache.log4j.Logger;
/**
 * A data structure that represents the outfit of an RPEntity. This RPEntity can
 * either be an NPC which uses the outfit sprite system, or of a player.
 *
 * You can use this data structure so that you don't have to deal with the way
 * outfits are stored internally.
 *
 * An outfit can contain up to five parts: detail, hair, head, dress, and body.
 *
 * Note, however, that you can create outfit objects that consist of less than
 * five parts by setting the other parts to <code>null</code>. For example,
 * you can create a dress outfit that you can combine with the player's current
 * so that the player gets the dress, but keeps his hair, head, and body.
 *
 * Not all outfits can be chosen by players.
 *
 * @author daniel
 *
 */
public class Outfit {

	/** the logger instance. */
	private static final Logger LOGGER = Logger.getLogger(Outfit.class);

	/** The detail index, as a value between 0 and 99, or null. */
	private Integer detail;

	/** The hair index, as a value between 0 and 99, or null. */
	private final Integer hair;

	/** The head index, as a value between 0 and 99, or null. */
	private final Integer head;

	/** The dress index, as a value between 0 and 99, or null. */
	private final Integer dress;

	/** The body index, as a value between 0 and 99, or null. */
	private final Integer body;
	
	/** The eyes index, as a value between 0 and 99, or null. */
	private final Integer eyes;

	/** The mouth index, as a value between 0 and 99, or null. */
	private final Integer mouth;

	/**
	 * Creates a new default outfit (naked person).
	 */
	public Outfit() {
		this(0, 0, 0, 0, 0, 0, 0);
	}

	/**
	 * Creates a new outfit. Set some of the parameters to null if you want an
	 * entity that put on this outfit to keep on the corresponding parts of its
	 * current outfit.
	 *
	 * @param detail
	 *            The index of the detail style, or null
	 * @param hair
	 *            The index of the hair style, or null
	 * @param head
	 *            The index of the head style, or null
	 * @param dress
	 *            The index of the dress style, or null
	 * @param body
	 *            The index of the body style, or null
	 * 
	 * TODO: Remove when new outfit system implemented.
	 */
	public Outfit(final Integer detail, final Integer hair, final Integer head,
			final Integer dress, final Integer body) {
		this.detail = detail;
		this.hair = hair;
		this.head = head;
		this.dress = dress;
		this.body = body;
		this.mouth = null;
		this.eyes = null;
	}

	/**
	 * Creates a new outfit. Set some of the parameters to null if you want an
	 * entity that put on this outfit to keep on the corresponding parts of its
	 * current outfit.
	 *
	 * @param detail
	 *            The index of the detail style, or null
	 * @param hair
	 *            The index of the hair style, or null
	 * @param head
	 *            The index of the head style, or null
	 * @param dress
	 *            The index of the dress style, or null
	 * @param body
	 *            The index of the body style, or null
	 * @param mouth
	 *            The index of the mouth style, or null
	 * @param eyes
	 *            The index of the eyes style, or null
	 */
	public Outfit(final Integer detail, final Integer hair, final Integer head,
			final Integer dress, final Integer body, final Integer mouth,
			final Integer eyes) {
		this.detail = detail;
		this.hair = hair;
		this.head = head;
		this.dress = dress;
		this.body = body;
		this.mouth = mouth;
		this.eyes = eyes;
	}

	/**
	 * Creates a new outfit based on a numeric code.
	 *
	 * @param code
	 *            A 10-digit decimal number where the last part (from the right)
	 *            stands for body, the next for dress, then head, then hair,
	 *            then detail.
	 */
	public Outfit(final double code) {
		
		this.body = (int) (code % 100);

		this.dress = (int) (code / 100 % 100);

		this.head = (int) (code / Math.pow(100, 2) % 100);

		this.hair = (int) (code / Math.pow(100, 3) % 100);

		this.detail = (int) (code / Math.pow(100, 4) % 100);
		
		if (Testing.enabled(Testing.OUTFITS)) {
			this.mouth = (int) (code / Math.pow(100, 5) % 100);
			
			this.eyes = (int) (code / Math.pow(100, 6) % 100);
		} else {
			this.mouth = null;
			
			this.eyes = null;
		}
	}
	
	/**
	 * Gets the index of this outfit's body style.
	 * 
	 * @return The index, or null if this outfit doesn't contain a body.
	 */
	public Integer getBody() {
		return body;
	}
	
	/**
	 * Gets the index of this outfit's dress style.
	 *
	 * @return The index, or null if this outfit doesn't contain a dress.
	 */
	public Integer getDress() {
		return dress;
	}
	
	/**
	 * Gets the index of this outfit's hair style.
	 *
	 * @return The index, or null if this outfit doesn't contain hair.
	 */
	public Integer getHair() {
		return hair;
	}
	
	/**
	 * Gets the index of this outfit's head style.
	 *
	 * @return The index, or null if this outfit doesn't contain a head.
	 */
	public Integer getHead() {
		return head;
	}
	
	/**
	 * Gets the index of this outfit's mouth style.
	 *
	 * @return The index, or null if this outfit doesn't contain a mouth.
	 */
	public Integer getMouth() {
		return mouth;
	}
	
	/**
	 * Gets the index of this outfit's eyes style.
	 *
	 * @return The index, or null if this outfit doesn't contain eyes.
	 */
	public Integer getEyes() {
		return eyes;
	}
	
	/**
	 * Gets the index of this outfit's detail style.
	 *
	 * @return The index, or null if this outfit doesn't contain a detail.
	 */
	public Integer getDetail() {
		return detail;
	}
	
	/**
	 * Represents this outfit in a numeric code.
	 *
	 * @return A 10-digit decimal number where the first pair of digits stand for
	 *         detail, the second pair for hair, the third pair for head, the
	 *         fourth pair for dress, and the fifth pair for body
	 */
	public int getCode() {
		int de = 0;
		int ha = 0;
		int he = 0;
		int dr = 0;
		int bo = 0;
		if (detail != null) {
			de = detail.intValue();
		}
		if (hair != null) {
			ha = hair.intValue();
		}
		if (head != null) {
			he = head.intValue();
		}
		if (dress != null) {
			dr = dress.intValue();
		}
		if (body != null) {
			bo = body.intValue();
		}
		
		return de * 100000000 + ha * 1000000 + he * 10000 + dr * 100 + bo;
	}
	
	/**
	 * Gets the code for mouth and eyes.
	 * 
	 * @return
	 * 		Numerial value for mouth and eyes parts
	 */
	public int getSecondaryCode() {
		int mo = 0;
		int ey = 0;
		if (mouth != null) {
			mo = mouth.intValue();
		}
		if (eyes != null) {
			ey = eyes.intValue();
		}
		
		return ey * 100 + mo;
	}
	
	/**
	 * Gets the result that you get when you wear this outfit over another
	 * outfit. Note that this new outfit can contain parts that are marked as
	 * NONE; in this case, the parts from the other outfit will be used.
	 *
	 * @param other
	 *            the outfit that should be worn 'under' the current one
	 * @return the combined outfit
	 */
	public Outfit putOver(final Outfit other) {
		int newDetail;
		int newHair;
		int newHead;
		int newDress;
		int newBody;
		// wear the this outfit 'over' the other outfit;
		// use the other outfit for parts that are not defined for this outfit.
		if (this.detail == null) {
			newDetail = other.detail;
		} else {
			newDetail = this.detail;
		}
		if (this.hair == null) {
			newHair = other.hair;
		} else {
			newHair = this.hair;
		}
		if (this.head == null) {
			newHead = other.head;
		} else {
			newHead = this.head;
		}
		if (this.dress == null) {
			newDress = other.dress;
		} else {
			newDress = this.dress;
		}
		if (this.body == null) {
			newBody = other.body;
		} else {
			newBody = this.body;
		}
		
		if (Testing.enabled(Testing.OUTFITS)) {
			//int newMouth;
			//int newEyes;
			Integer newMouth;
			Integer newEyes;
			
			if (this.mouth == null) {
				newMouth = other.mouth;
			} else {
				newMouth = this.mouth;
			}
			if (this.eyes == null) {
				newEyes = other.eyes;
			} else {
				newEyes = this.eyes;
			}
			
			return new Outfit(newDetail, newHair, newHead, newDress, newBody,
					newMouth, newEyes);
		} else {
			return new Outfit(newDetail, newHair, newHead, newDress, newBody,
					null, null);
		}
	}

	/**
	 * Gets the result that you get when you remove (parts of) an outfit.
	 * Removes the parts in the parameter, from the current outfit.
	 * NOTE: If a part does not match, the current outfit part will remain the same.
	 *
	 * @param other
	 *            the outfit that should be removed from the current one
	 * @return the new outfit, with the parameter-outfit removed
	 */
	public Outfit removeOutfit(final Outfit other) {
		int newDetail;
		int newHair;
		int newHead;
		int newDress;
		int newBody;
		// wear the this outfit 'over' the other outfit;
		// use the other outfit for parts that are not defined for this outfit.
		if ((detail == null) || detail.equals(other.detail)) {
			newDetail = 0;
		} else {
			newDetail = detail;
		}
		if ((hair == null) || hair.equals(other.hair)) {
			newHair = 0;
		} else {
			newHair = hair;
		}
		if ((head == null) || head.equals(other.head)) {
			newHead = 0;
		} else {
			newHead = head;
		}
		if ((dress == null) || dress.equals(other.dress)) {
			newDress = 0;
		} else {
			newDress = dress;
		}
		if ((body == null) || body.equals(other.body)) {
			newBody = 0;
		} else {
			newBody = body;
		}
		
		if (Testing.enabled(Testing.OUTFITS)) {
			int newMouth;
			int newEyes;
			
			if ((mouth == null) || mouth.equals(other.mouth)) {
				newMouth = 0;
			} else {
				newMouth = mouth;
			}
			if ((eyes == null) || eyes.equals(other.eyes)) {
				newEyes = 0;
			} else {
				newEyes = eyes;
			}
			
			return new Outfit(newDetail, newHair, newHead, newDress, newBody,
					newMouth, newEyes);
		} else {
			return new Outfit(newDetail, newHair, newHead, newDress, newBody,
					null, null);
		}
	}

	/**
	 * removes the details
	 */
	public void removeDetail() {
		detail = 0;
	}

	/**
	 * Checks whether this outfit is equal to or part of another outfit.
	 *
	 * @param other
	 *            Another outfit.
	 * @return true iff this outfit is part of the given outfit.
	 */
	public boolean isPartOf(final Outfit other) {
		return  ((detail == null) || detail.equals(other.detail))
				&& ((hair == null) || hair.equals(other.hair))
				&& ((head == null) || head.equals(other.head))
				&& ((dress == null) || dress.equals(other.dress))
				&& ((body == null) || body.equals(other.body));
	}

	/**
	 * Checks whether this outfit may be selected by a normal player as normal
	 * outfit. It returns false for special event and GM outfits.
	 *
	 * @return true if it is a normal outfit
	 */
	public boolean isChoosableByPlayers() {
		return (detail == null || detail == 0)
			&& (hair < Outfits.HAIR_OUTFITS) && (hair >= 0)
		    && (head < Outfits.HEAD_OUTFITS) && (head >= 0)
			&& (dress < Outfits.CLOTHES_OUTFITS) && (dress >= 0)
			&& (body < Outfits.BODY_OUTFITS) && (body >= 0);
	}

	/**
	 * Is outfit missing a dress?
	 *
	 * @return true if naked, false if dressed
	 */
	public boolean isNaked() {
		if (isCompatibleWithClothes()) {
			return (dress == null) || dress.equals(0);
		} else {
			return false;
		} 
		
	}

	/**
	 * Create a random unisex outfit, with a 'normal' face and unisex body
	 *
	 * <ul>
	 * <li>hair number (1 to 26) selection of hairs which look ok with both goblin
	 *     face and cute face (later hairs only look right with cute face)</li>
	 * <li>head numbers (1 to 15) to avoid the cut eye, pink eyes, weird green eyeshadow etc</li>
	 * <li>dress numbers (1 to 16) from the early outfits before lady player base got introduced i.e. they are all unisex</li>
	 * <li>base numbers ( 1 to 15), these are the early bodies which were unisex</li>
	 * </ul>
	 * @return the new random outfit
	 */

	public static Outfit getRandomOutfit() {
		final int newHair = Rand.randUniform(1, 26);
		final int newHead;
		final int newDress = Rand.randUniform(1, 16);
		final int newBody = Rand.randUniform(1, 5);
		
		if (Testing.enabled(Testing.OUTFITS)) {
			newHead = Rand.randUniform(1, Outfits.HEAD_OUTFITS_TESTING);
			final int newMouth = Rand.randUniform(1, Outfits.MOUTH_OUTFITS);
			final int newEyes = Rand.randUniform(1, Outfits.EYES_OUTFITS);
			LOGGER.debug("chose random outfit: "  + newHair + " " + newHead
					+ " " + newDress + " " + newBody + " " + newMouth + " "
					+ newEyes);
			return new Outfit(0, newHair, newHead, newDress, newBody,
					newMouth, newEyes);
		} else {
			newHead = Rand.randUniform(1, 15);
			LOGGER.debug("chose random outfit: "  + newHair + " " + newHead
					+ " " + newDress + " " + newBody);
			return new Outfit(0, newHair, newHead, newDress, newBody,
					null, null);
		}
	}
	
	/**
	 * Can this outfit be worn with normal clothes
	 *
	 * @return true if the outfit is compatible with clothes, false otherwise
	 */
	public boolean isCompatibleWithClothes() {
		if (body > 80 && body < 99) {
			return false;
		}
		return true;
	}

	@Override
	public boolean equals(Object other) {

		if (!(other instanceof Outfit)) {
			return false;
		}
		else {
			Outfit outfit = (Outfit)other;
			return this.getCode() == outfit.getCode();
		}

	}

	@Override
	public int hashCode() {
		return this.getCode();
	}

}
