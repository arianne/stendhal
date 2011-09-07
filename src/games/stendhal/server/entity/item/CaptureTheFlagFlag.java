/*
 * CaptureTheFlagFlag.java
 * 
 * TODO:
 *   - move to new package - games.stendhal.server.games.capturetheflag.item,
 *     as soon as i figure out how to do that in eclipse
 */

package games.stendhal.server.entity.item;

import java.util.Map;
import java.util.HashMap;

import games.stendhal.common.MathHelper;

import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.Item;

import marauroa.common.game.SlotOwner;

import java.awt.Color;


// XXX obviously not about NPC
// import games.stendhal.server.entity.npc.NPCAttrUtils;

/**
 * represents the flag in Capture the Flag games - when player
 * holds flag in hand, outfit diplays flag.  when player
 * drops flag, outfit reverts to previous.
 * 
 * @author sjtsp2008
 *
 */
public class CaptureTheFlagFlag extends Item {

	// this is the detail that you set for the outfit overlay
	// (and then you have to set a color)
	int     detailValue = 5;
	String  color;
	
	int colorValue = new Color(0, 255, 0).getRGB();
	
	// String droppable;
	
	public CaptureTheFlagFlag(final String name, 
							  final String clazz, 
							  final String subclass,
							  final Map<String,String> attributes) {
		super(name, clazz, subclass, attributes);
		
		// XXX get a string, map to integer
		// this.colorValue = NPCAttrUtils.getAttrInt("color", attributes, 3);
		String colorStr = attributes.get("color");
		if (colorStr != null) {
		  this.colorValue = MathHelper.parseIntDefault(colorStr, 5);
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

	public int getColorValue() {
		return this.colorValue;
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

		boolean result = super.onEquipped(equipper, slot);

		// System.out.println("flag.onEquipped() - super result: " + result);
		
		// TODO: should we double-check that slot is hand?
		//         currently, xml definition restricts
		
		// System.out.println("CaptureTheFlagFlag.onEquipped(): " + this.get("name") + " -> " + equipper);

		Outfit flagOutfit  = new Outfit(this.detailValue, null, null, null, null);
				
		equipper.setOutfit(flagOutfit.putOver(equipper.getOutfit()));
		

		// XXX i think the color needs to be the int for an rgb value
		equipper.put("outfit_colors", "detail", colorValue);
		
		// TODO: update player to establish chance of dropping
		//       flag every time hit (either hit at all, or hit with special snowball)
		
		return true;
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

		boolean result      = super.onUnequipped();

		SlotOwner owner     = this.getContainerOwner();
		
		if (owner == null) {
			return false;
		}

		//
		// note that we just replace the outfit detail, versus
		// trying to replace what was there originally.
		//
		Outfit   noFlagOutfit = new Outfit(0, null, null, null, null);
		RPEntity entity       = (RPEntity) owner;
		
		entity.setOutfit(noFlagOutfit.putOver(entity.getOutfit()));

		return true;
	}
	
}
