package games.stendhal.server.entity.player;

/**
 * A datastructure that represents the outfit of a player.
 * You can use it so that you don't have to deal with the
 * way outfits are stored internally.
 * 
 * @author daniel
 *
 */
public class Outfit {

	/**
	 * State that a part of an outfit should not be used
	 * when combining with another outfit. 
	 */
	public static final int NO_CHANGE = -1;

	/** The hair index, as a value between 0 and 99. */
	private int hair;
	/** The head index, as a value between 0 and 99. */
	private int head;
	/** The dress index, as a value between 0 and 99. */
	private int dress;
	/** The base index, as a value between 0 and 99. */
	private int base;
	
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
	 * Gets the result that you get when, based on <code>this</code> outfit,
	 * you put on a new outfit. Note that the new outfit can contain parts
	 * that are marked as NO_CHANGE; in this case, the parts from
	 * <code>this</code> outfit will be used.
	 * @param newOutfit the outfit that should be worn 'over' the current one
	 * @return the combined outfit
	 */
	public Outfit combineWith(Outfit newOutfit) {
		// start with this outfit
		int newHair;
		int newHead;
		int newDress;
		int newBase;
		// wear the new outfit 'over' this outfit
		if (newOutfit.hair != NO_CHANGE) {
			newHair = newOutfit.hair;
		} else {
			newHair = this.hair;
		}
		if (newOutfit.head != NO_CHANGE) {
			newHead = newOutfit.head;
		} else {
			newHead = this.head;
		}
		if (newOutfit.dress != NO_CHANGE) {
			newDress = newOutfit.dress;
		} else {
			newDress = this.dress;
		}
		if (newOutfit.base != NO_CHANGE) {
			newBase = newOutfit.base;
		} else {
			newBase = this.base;
		}
		return new Outfit(newHair, newHead, newDress, newBase);
	}
}
