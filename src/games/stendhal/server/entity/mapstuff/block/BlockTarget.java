/**
 * 
 */
package games.stendhal.server.entity.mapstuff.block;

import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.MovementListener;
import games.stendhal.server.entity.ActiveEntity;
import games.stendhal.server.entity.mapstuff.area.AreaEntity;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;

import org.apache.log4j.Logger;

/**
 * An entity representing a target for a pushable block
 * 
 * @author madmetzger
 */
public class BlockTarget extends AreaEntity implements MovementListener {
	
	private static final Logger logger = Logger.getLogger(BlockTarget.class);
	
	private ChatAction action;
	
	private ChatCondition condition;
	
	/**
	 * Generate the RPClass
	 */
	public static void generateRPClass() {
		RPClass clazz = new RPClass("blocktarget");
		clazz.isA("area");
		clazz.addAttribute("x", Type.INT);
		clazz.addAttribute("y", Type.INT);
		clazz.addAttribute("shape", Type.STRING);
	}
	
	/**
	 * Create a BlockTarget accepting any Block
	 * 
	 * @param x x-coordinate
	 * @param y y-coordinate
	 */
	public BlockTarget(int x, int y) {
		this.setPosition(x, y);
	}
	
	/**
	 * Create a shaped BlockTarget, that only accepts Blocks of a certain shape
	 * 
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param shape accepted shape
	 */
	public BlockTarget(int x, int y, String shape) {
		this(x, y);
		this.put("shape", shape);
	}
	
	/**
	 * Check if a Block would trigger this BlockTarget
	 * 
	 * @param b the Block to check
	 * @return true iff the given Block would trigger this target
	 */
	public boolean doesTrigger(Block b, Player p) {
		String blockShape = b.getShape();
		String targetShape = this.getShape();
		boolean shapeFits = true;
		boolean conditionMet = true;
		
		if(targetShape != null) {
			shapeFits = targetShape.equals(blockShape);
		}
		
		if(this.condition != null) {
			conditionMet = this.condition.fire(p, null, null);
		}
		
		return conditionMet && shapeFits;
	}
	
	/**
	 * Trigger this BlockTarget
	 * 
	 * @param p The Player who has pushed the triggering Block on this target
	 */
	public void trigger(Player p) {
		if(this.action != null) {
			this.action.fire(p, null, null);
		}
	}
	
	/**
	 * Get the shape of this BlockTarget
	 * 
	 * @return the shape or null if this BlockTarget has no shape
	 */
	public String getShape() {
		if(this.has("shape")) {
			return this.get("shape");
		}
		return null;
	}

    @Override
    public void onEntered(ActiveEntity entity, StendhalRPZone zone, int newX, int newY) {
            //TODO implement condition checking and action execution here
    	if(entity instanceof Block) {
    		logger.info("Block entered target: " + entity);
    	}
    }

    @Override
    public void onExited(ActiveEntity entity, StendhalRPZone zone, int oldX, int oldY) {
        // nothing to do        
    }

    @Override
    public void beforeMove(ActiveEntity entity, StendhalRPZone zone, int oldX, int oldY, int newX, int newY) {
        // nothing to do
    }

    @Override
    public void onMoved(ActiveEntity entity, StendhalRPZone zone, int oldX, int oldY, int newX, int newY) {
        // nothing to do
    }

	/**
	 * @param action the action to set
	 */
	public void setAction(ChatAction action) {
		this.action = action;
	}

	/**
	 * Set the ChatCondition to check
	 * 
	 * @param condition the condition to set
	 */
	public void setCondition(ChatCondition condition) {
		this.condition = condition;
	}


}
