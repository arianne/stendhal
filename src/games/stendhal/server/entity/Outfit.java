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
 * four parts by setting the other parts to NONE. For example, you can create
 * a dress outfit that you can combine with the player's current so that the
 * player gets the dress, but keeps his hair, head, and base.  
 * 
 * @author daniel
 *
 */
public class Outfit {

	/**
	 * State that a part of an outfit should not be used
	 * when combining with another outfit. 
	 */
	public static final int NONE = -1;

	/** The hair index, as a value between 0 and 99. */
	private int hair;
	/** The head index, as a value between 0 and 99. */
	private int head;
	/** The dress index, as a value between 0 and 99. */
	private int dress;
	/** The base index, as a value between 0 and 99. */
	private int base;
	
	/**
	 * Creates a new default outfit (naked person). 
	 */
	public Outfit() {
		this(0, 0, 0, 0);
	}
	
	/**
	 * Creates a new outfit.
	 * @param hair
	 * @param head
	 * @param dress
	 * @param base
	 */
	public Outfit(int hair, int head, int dress, int base) {
		this.hair = hair;
		this.head = head;
		this.dress = dress;
		this.base = base;
	}
	
	/**
	 * Creates a new outfit based on a numeric code.
	 * @param code A 8-digit decimal number where the first pair of digits
	 * stand for hair, the second pair for head, the third pair for dress,
	 * and the fourth pair for base.  
	 */
	public Outfit(int code) {
		this.base = code % 100;
		code /= 100;
		this.dress = code % 100;
		code /= 100;
		this.head  = code % 100;
		code /= 100;
		this.hair  = code;
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
		if (this.hair == NONE) {
			newHair = other.hair;
		} else {
			newHair = this.hair;
		}
		if (this.head == NONE) {
			newHead = other.head;
		} else {
			newHead = this.head;
		}
		if (this.dress == NONE) {
			newDress = other.dress;
		} else {
			newDress = this.dress;
		}
		if (this.base == NONE) {
			newBase = other.base;
		} else {
			newBase = this.base;
		}
		return new Outfit(newHair, newHead, newDress, newBase);
	}
	
	/**
	 * Checks whether this outfit is equal to or part of another outfit.
	 * @param other Another outfit.
	 * @return
	 */
	public boolean isPartOf(Outfit other) {
		return (hair == Outfit.NONE || hair == other.hair)
				&& (head == Outfit.NONE || head == other.head)
				&& (dress == Outfit.NONE || dress == other.dress)
				&& (base == Outfit.NONE || base == other.base);
	}
}
