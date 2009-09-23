package games.stendhal.server.entity;

import games.stendhal.common.Outfits;
import games.stendhal.common.Rand;

import org.apache.log4j.Logger;
/**
 * A data structure that represents the outfit of an RPEntity. This RPEntity can
 * either be an NPC which uses the outfit sprite system, or of a player.
 * 
 * You can use this data structure so that you don't have to deal with the way
 * outfits are stored internally.
 * 
 * An outfit can contain of up to four parts: hair, head, dress, and base.
 * 
 * Note, however, that you can create outfit objects that consist of less than
 * four parts by setting the other parts to <code>null</code>. For example,
 * you can create a dress outfit that you can combine with the player's current
 * so that the player gets the dress, but keeps his hair, head, and base.
 * 
 * @author daniel
 * 
 */
public class Outfit {

	/** the logger instance. */
	private static final Logger LOGGER = Logger.getLogger(Outfit.class);

	/** The hair index, as a value between 0 and 99, or null. */
	private Integer hair;

	/** The head index, as a value between 0 and 99, or null. */
	private Integer head;

	/** The dress index, as a value between 0 and 99, or null. */
	private Integer dress;

	/** The base index, as a value between 0 and 99, or null. */
	private Integer base;

	/**
	 * Creates a new default outfit (naked person).
	 */
	public Outfit() {
		this(0, 0, 0, 0);
	}

	/**
	 * Creates a new outfit. Set some of the parameters to null if you want an
	 * entity that put on this outfit to keep on the corresponding parts of its
	 * current outfit.
	 * 
	 * @param hair
	 *            The index of the hair style, or null
	 * @param head
	 *            The index of the head style, or null
	 * @param dress
	 *            The index of the dress style, or null
	 * @param base
	 *            The index of the base style, or null
	 */
	public Outfit(final Integer hair, final Integer head, final Integer dress, final Integer base) {
		this.hair = hair;
		this.head = head;
		this.dress = dress;
		this.base = base;
	}

	/**
	 * Creates a new outfit based on a numeric code.
	 * 
	 * @param code
	 *            A 8-digit decimal number where the first pair (from the left)
	 *            of digits stand for hair, the second pair for head, the third
	 *            pair for dress, and the fourth pair for base.
	 */
	public Outfit(final int code) {
		
		this.base = code % 100;
		
		this.dress = code / 100 % 100;
		
		this.head = code / 10000 % 100;
		
		this.hair = code / 1000000 % 100;
	}

	/**
	 * Gets the index of this outfit's base style.
	 * 
	 * @return The index, or null if this outfit doesn't contain a base.
	 */
	public Integer getBase() {
		return base;
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
	 * Represents this outfit in a numeric code.
	 * 
	 * @return A 8-digit decimal number where the first pair of digits stand for
	 *         hair, the second pair for head, the third pair for dress, and the
	 *         fourth pair for base.
	 */
	public int getCode() {
		return hair * 1000000 + head * 10000 + dress * 100 + base;
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
		int newHair;
		int newHead;
		int newDress;
		int newBase;
		// wear the this outfit 'over' the other outfit;
		// use the other outfit for parts that are not defined for this outfit.
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
		if (this.base == null) {
			newBase = other.base;
		} else {
			newBase = this.base;
		}
		return new Outfit(newHair, newHead, newDress, newBase);
	}

	/**
	 * Checks whether this outfit is equal to or part of another outfit.
	 * 
	 * @param other
	 *            Another outfit.
	 * @return true iff this outfit is part of the given outfit.
	 */
	public boolean isPartOf(final Outfit other) {
		return ((hair == null) || hair.equals(other.hair))
				&& ((head == null) || head.equals(other.head))
				&& ((dress == null) || dress.equals(other.dress))
				&& ((base == null) || base.equals(other.base));
	}

	/**
	 * Checks whether this outfit may be selected by a normal player as normal
	 * outfit. It returns false for special event and GM outfits.
	 * 
	 * @return true if it is a normal outfit
	 */
	public boolean isChoosableByPlayers() {
		return (hair < Outfits.HAIR_OUTFITS) && (hair >= 0) 
		    && (head < Outfits.HEAD_OUTFITS) && (head >= 0)
			&& (dress < Outfits.CLOTHES_OUTFITS) && (dress >= 0) 
			&& (base < Outfits.BODY_OUTFITS) && (base >= 0);
	}

	/**
	 * Is outfit missing a dress?
	 * 
	 * @return true if naked, false if dressed
	 */
	public boolean isNaked() {
		return (dress == null) || dress.equals(0);
	}

	/**
	 * Create a random unisex outfit, with a 'normal' face and unisex base
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
		final int newHead = Rand.randUniform(1, 15);
		final int newDress = Rand.randUniform(1, 16);
		final int newBase = Rand.randUniform(1, 5);
		LOGGER.debug("chose random outfit: "  + newHair + " " + newHead + " " + newDress + " " + newBase);
		return new Outfit(newHair, newHead, newDress, newBase);
	}
}
