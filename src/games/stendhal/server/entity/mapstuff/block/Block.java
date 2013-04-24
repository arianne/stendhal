package games.stendhal.server.entity.mapstuff.block;

import games.stendhal.common.Direction;
import games.stendhal.common.Rand;
import games.stendhal.common.constants.SoundLayer;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.MovementListener;
import games.stendhal.server.core.events.ZoneEnterExitListener;
import games.stendhal.server.entity.ActiveEntity;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.SoundEvent;

import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import marauroa.common.game.Definition;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;

import org.apache.log4j.Logger;

/**
 * A solid, movable block on a map
 *
 * @author madmetzger
 */
public class Block extends ActiveEntity implements ZoneEnterExitListener, MovementListener {
	
    private static final String Z_ORDER = "z";

    private static final String START_Y = "start-y";

    private static final String START_X = "start-x";
    
    private final List<String> sounds;

    private static final Logger logger = Logger.getLogger(Block.class);

	public static void generateRPClass() {
		RPClass clazz = new RPClass("block");
		clazz.isA("area");
		//start_* denotes the initial place of a block to be able resetting it to that position
		clazz.addAttribute(START_X, Type.INT, Definition.HIDDEN);
		clazz.addAttribute(START_Y, Type.INT, Definition.HIDDEN);
		//flag denoting if this block is multiple times pushable
		clazz.addAttribute("multi", Type.FLAG, Definition.HIDDEN);
        // z order to control client side drawing
        clazz.addAttribute(Z_ORDER, Type.INT);
        clazz.addAttribute("class", Type.STRING);
        clazz.addAttribute("shape", Type.STRING);
	}

	/**
	 * Create a new Block with default style at (startX, startY)
	 *
	 * @param startX
	 *			initial x-coordinate
	 * @param startY
	 * 			initial y-coordinate
	 * @param multiPush
	 * 			is pushing multiple times allowed
	 */
	public Block(int startX, int startY, boolean multiPush) {
		this(startX, startY, multiPush, "block", null, Arrays.asList("scrape-1", "scrape-2"));
	}
	
	/**
	 * 
	 * @param startX
	 * @param startY
	 * @param multiPush
	 * @param style
	 */
	public Block(int startX, int startY, boolean multiPush, String style) {
		this(startX, startY, multiPush, style, null, Collections.<String> emptyList());
	}
	
	public Block(int startX, int startY, boolean multiPush, String style, String shape) {
		this(startX, startY, multiPush, style, shape, Collections.<String> emptyList());
	}

	/**
	 * Create a new block at startX, startY with a different style at client side
	 * 
	 * @param startX
	 *			initial x-coordinate
	 * @param startY
	 * 			initial y-coordinate
	 * @param multiPush
	 * 			is pushing multiple times allowed
	 * @param style
	 * 			what style should the client use?
	 * @param shape 
	 * @param sounds
	 * 			what sounds should be played on push?
	 */
	public Block(int startX, int startY, boolean multiPush, String style, String shape, List<String> sounds) {
		super();
		this.put(START_X, startX);
		this.put(START_Y, startY);
        this.put(Z_ORDER, 8000);
		this.put("multi", Boolean.valueOf(multiPush).toString());
		setRPClass("block");
		put("type", "block");
		put("class", "block");
		this.sounds = sounds;
		// Count as collision for the client and pathfinder
		setResistance(100);
        setDescription("You see a solid block of rock. Are you strong enough to push it away?");
		if(style != null) {
			put("name", style);
		} else {
			put("name", "block");
		}
		if(shape != null) {
			put("shape", shape);
		}
		this.reset();
	}
	

	/**
	 * Resets the block position to its initial state
	 */
	public void reset() {
		this.setPosition(this.getInt(START_X), this.getInt(START_Y));
		this.notifyWorldAboutChanges();
	}

	/**
	 * Push this Block into a given direction
	 * @param p 
	 * @param d
	 * 			the direction, this block is pushed into
	 */
	public void push(Player p, Direction d) {
		if(this.mayBePushed(d)) {
			int x = getXAfterPush(d);
			int y = getYAfterPush(d);
			this.setPosition(x, y);
            List<Entity> entitiesAt = this.getZone().getEntitiesAt(x, y);
            for (Entity entity : entitiesAt) {
				if(entity instanceof BlockTarget) {
					BlockTarget t = (BlockTarget) entity;
					if(t.doesTrigger(this, p)) {
						t.trigger(this, p);
					}
				}
			}
            this.sendSound();
            this.notifyWorldAboutChanges();
			logger.debug("Block ["+this.getID().toString()+"] pushed to ("+this.getX()+","+this.getY()+").");
		}
	}
	
	
	private void sendSound() {
		if(!this.sounds.isEmpty()) {
			SoundEvent e = new SoundEvent(Rand.rand(sounds), SoundLayer.AMBIENT_SOUND);
			this.addEvent(e);
		}
	}

	protected int getYAfterPush(Direction d) {
		return this.getY() + d.getdy();
	}

	protected int getXAfterPush(Direction d) {
		return this.getX() + d.getdx();
	}

	private boolean wasPushed() {
		boolean xChanged = this.getInt("x") != this.getInt(START_X);
		boolean yChanged = this.getInt("y") != this.getInt(START_Y);
		return xChanged || yChanged;
	}

	private boolean mayBePushed(Direction d) {
		boolean pushed = wasPushed();
		boolean multiPush = this.getBool("multi");
		int newX = this.getXAfterPush(d);
		int newY = this.getYAfterPush(d);
		
		if(!multiPush && pushed) {
			return false;
		}

		// additional checks: new position must be free
		boolean collision = this.getZone().collides(this, newX, newY);
		
		return !collision;
	}
	
	/**
	 * Get the shape of this Block
	 * 
	 * @return the shape or null if this Block has no shape
	 */
	public String getShape() {
		if(this.has("shape")) {
			return this.get("shape");
		}
		return null;
	}

	@Override
	public void onEntered(ActiveEntity entity, StendhalRPZone zone, int newX,
			int newY) {
		// do nothing
	}

	@Override
	public void onExited(ActiveEntity entity, StendhalRPZone zone, int oldX,
			int oldY) {
        logger.debug("Block [" + this.getID().toString() + "] notified about entity [" + entity + "] exiting [" + zone.getName() + "].");
		resetInPlayerlessZone(zone, entity);
	}

	@Override
	public void onMoved(ActiveEntity entity, StendhalRPZone zone, int oldX,
			int oldY, int newX, int newY) {
		// do nothing on move
	}

	@Override
	public void onEntered(RPObject object, StendhalRPZone zone) {
		// do nothing
	}

	@Override
	public void onExited(RPObject object, StendhalRPZone zone) {
        logger.debug("Block [" + this.getID().toString() + "] notified about object [" + object + "] exiting [" + zone.getName() + "].");
		resetInPlayerlessZone(zone, object);
	}

	private void resetInPlayerlessZone(StendhalRPZone zone, RPObject object) {
		// reset to initial position if zone gets empty of players
		final List<Player> playersInZone = zone.getPlayers();
		int numberOfPlayersInZone = playersInZone.size();
        if (numberOfPlayersInZone == 0 || numberOfPlayersInZone == 1 && playersInZone.contains(object)) {
			this.reset();
            logger.debug("Block [" + this.getID().toString() + "] reset to (" + this.getX() + "," + this.getY() + ").");
		}
	}

	@Override
	public boolean isObstacle(Entity entity) {
		if (entity instanceof RPEntity) {
			return true;
		}

		return super.isObstacle(entity);
	}

	@Override
	public void beforeMove(ActiveEntity entity, StendhalRPZone zone, int oldX,
			int oldY, int newX, int newY) {
        if (entity instanceof Player) {
            Rectangle2D oldA = new Rectangle2D.Double(oldX, oldY, entity.getWidth(), entity.getHeight());
            Rectangle2D newA = new Rectangle2D.Double(newX, newY, entity.getWidth(), entity.getHeight());
            Direction d = Direction.getAreaDirectionTowardsArea(oldA, newA);
            this.push((Player) entity, d);
        }
	}

}
