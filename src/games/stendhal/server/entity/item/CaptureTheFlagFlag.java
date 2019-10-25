/*
 * CaptureTheFlagFlag.java
 *
 * TODO:
 *   - move to new package - games.stendhal.server.games.capturetheflag.item,
 *     as soon as i figure out how to do that in eclipse
 */

package games.stendhal.server.entity.item;

import java.util.HashMap;
import java.util.Map;

import games.stendhal.server.entity.DressedEntity;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.RPEntity;
import marauroa.common.game.SlotOwner;


// XXX obviously not about NPC
// import games.stendhal.server.entity.npc.NPCAttrUtils;

/**
 * represents the flag in Capture the Flag games - when player
 * holds flag in hand, outfit displays flag.  when player
 * drops flag, outfit reverts to previous.
 *
 * @author sjtsp2008
 *
 */
public class CaptureTheFlagFlag extends Item {

	// this is the detail that you set for the outfit overlay
	// (and then you have to set a color)
	int     detailValue = 5;

	String colorValue   = "0x00ff00";

	// boolean droppable;

	public CaptureTheFlagFlag(final String name,
							  final String clazz,
							  final String subclass,
							  final Map<String,String> attributes) {
		super(name, clazz, subclass, attributes);

		// XXX get a string, map to integer
		// this.colorValue = NPCAttrUtils.getAttrInt("color", attributes, 3);
		String colorStr = attributes.get("color");
		if (colorStr != null) {
		    this.colorValue = colorStr;
		}

		// this.droppable = NPCAttrUtils.getAttr("droppable", attributes);

	}

	/**
	 * default constructor.  maybe just for test harness, maybe for simpler xml
	 */
	public CaptureTheFlagFlag() {
		// XXX
		this("flag", "token", "flag", new HashMap<String,String>());
	}

	public String getColorValue() {
		return this.colorValue;
	}

	/**
	 * return the detail id for flag
	 * @return detail id
	 */
	public int getDetailValue() {
		return this.detailValue;
	}

	// XXX lift in to some superclass - currently, this is the *only* thing
	//     that is droppable
	public boolean isDroppable() {
		return true;
	}

	/**
	 * if flag is held, update player's outfit.
	 *
	 * XXX current api returns boolean, but that has little value
	 */
	@Override
	public boolean onEquipped(RPEntity equipper, String slot) {

		super.onEquipped(equipper, slot);

		// System.out.println("flag.onEquipped() - super result: " + result);

		// TODO: should we double-check that slot is hand?
		//         currently, xml definition restricts

		// System.out.println("CaptureTheFlagFlag.onEquipped(): " + this.get("name") + " -> " + equipper);

		if (equipper instanceof DressedEntity) {
			DressedEntity dressed = (DressedEntity) equipper;

			Outfit flagOutfit;
			flagOutfit  = new Outfit(null, null, null, null, null, null, null, null, this.detailValue);

			dressed.put("outfit_colors", "detail", this.colorValue);
			dressed.setOutfit(flagOutfit.putOver(dressed.getOutfit()));

			// dressed.put("outfit_colors", "detail", colorValue);
			// dressed.put("outfit_colors", "detail", 0x00ff00);

			return true;
		}

		return false;
	}

	/**
	 * item is being removed from owner - remove flag from players outfit
	 *
	 * this is called in the process of removing item from
	 * original owner, perhaps just before giving it to a
	 * new owner (or maybe just dropping it).  at this point,
	 * the item still "belongs" to the original owner.
	 */
	@Override
	public boolean onUnequipped() {

		// System.out.println("CaptureTheFlagFlag.onUnequipped(): " + this.get("name"));

		super.onUnequipped();

		SlotOwner owner     = this.getContainerOwner();

		if (owner == null) {
			return false;
		}

		if (owner instanceof DressedEntity) {
			//
			// note that we just replace the outfit detail, versus
			// trying to replace what was there originally.
			//
			Outfit   noFlagOutfit = new Outfit(null, null, null, null, null, null, null, null, 0);
			DressedEntity entity       = (DressedEntity) owner;

			entity.setOutfit(noFlagOutfit.putOver(entity.getOutfit()));

			return true;
		}

		return false;
	}

}
