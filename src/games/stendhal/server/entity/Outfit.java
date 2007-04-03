package games.stendhal.server.entity;

/**
 * A data structure that represents the outfit of an RPEntity. This RPEntity
 * can either be an NPC which uses the outfit sprite system, or of a player.
 * 
 * You can use this data structure so that you don't have to deal with the
 * way outfits are stored internally.
 * 
 * An outfit can contain of up to four parts: hair, head,
 * dress, and base.
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

	/** The hair index, as a value between 0 and 99. */
	private Integer hair;

	/** The head index, as a value between 0 and 99. */
	private Integer head;

	/** The dress index, as a value between 0 and 99. */
	private Integer dress;

	/** The base index, as a value between 0 and 99. */
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
	 * @param hair The index of the hair style, or null 
	 * @param head The index of the head style, or null
	 * @param dress The index of the dress style, or null
	 * @param base The index of the base style, or null
	 */
	public Outfit(Integer hair, Integer head, Integer dress, Integer base) {
		this.hair = hair;
		this.head = head;
		this.dress = dress;
		this.base = base;
	}

	/**
	 * Creates a new outfit based on a numeric code.
	 * @param code A 8-digit decimal number where the first pair (from the
	 * left) of digits stand for hair, the second pair for head, the third
	 * pair for dress, and the fourth pair for base.  
	 */
	public Outfit(int code) {
		this.base = code % 100;
		code /= 100;
		this.dress = code % 100;
		code /= 100;
		this.head = code % 100;
		code /= 100;
		this.hair = code;
	}

	public int getBase() {
		return base;
	}

	public void setBase(int base) {
		this.base = base;
	}

	public int getDress() {
		return dress;
	}

	public void setDress(int dress) {
		this.dress = dress;
	}

	public int getHair() {
		return hair;
	}

	public void setHair(int hair) {
		this.hair = hair;
	}

	public int getHead() {
		return head;
	}

	public void setHead(int head) {
		this.head = head;
	}

	/**
	 * Represents this outfit in a numeric code.
	 * @return A 8-digit decimal number where the first pair of digits
	 * stand for hair, the second pair for head, the third pair for dress,
	 * and the fourth pair for base.  
	 */
	public int getCode() {
		return hair * 1000000 + head * 10000 + dress * 100 + base;
	}

	/**
	 * Gets the result that you get when you wear this outfit over another
	 * outfit. Note that this new outfit can contain parts that are marked
	 * as NONE; in this case, the parts from the other outfit will be used.
	 * @param other the outfit that should be worn 'under' the current one
	 * @return the combined outfit
	 */
	public Outfit putOver(Outfit other) {
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
	 * @param other Another outfit.
	 * @return true iff this outfit is part of the given outfit.
	 */
	public boolean isPartOf(Outfit other) {
		return (hair == null || hair == other.hair) && (head == null || head == other.head)
		        && (dress == null || dress == other.dress) && (base == null || base == other.base);
	}
}
