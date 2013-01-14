package games.stendhal.server.entity.mapstuff.block;

import games.stendhal.common.Direction;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.MovementListener;
import games.stendhal.server.core.events.ZoneEnterExitListener;
import games.stendhal.server.entity.ActiveEntity;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.mapstuff.area.AreaEntity;

import java.awt.geom.Rectangle2D;

import marauroa.common.game.Definition;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;

/**
 * A solid, movable block on a map
 *
 * @author madmetzger
 */
public class Block extends AreaEntity implements ZoneEnterExitListener, MovementListener{

	public static void generateRPClass() {
		RPClass clazz = new RPClass("block");
		clazz.isA("area");
		//start_* denotes the initial place of a block to be able resetting it to that position
		clazz.addAttribute("start_x", Type.INT, Definition.HIDDEN);
		clazz.addAttribute("start_y", Type.INT, Definition.HIDDEN);
		//flag denoting if this block is multiple times pushable
		clazz.addAttribute("multi", Type.FLAG, Definition.HIDDEN);
	}

	/**
	 * Create a new Block a (startX, startY)
	 *
	 * @param startX
 * 				initial x-coordinate
	 * @param startY
	 * 			initial y-coordinate
	 * @param multiPush
	 * 			is pushing multiple times allowed
	 */
	public Block(int startX, int startY, boolean multiPush) {
		super(1,1);
		this.put("start_x", startX);
		this.put("start_y", startY);
		this.put("multi", Boolean.valueOf(multiPush).toString());
		setRPClass("block");
		put("type", "block");
		// Count as collision for the client and pathfinder
		setResistance(100);
		this.reset();
	}

	/**
	 * Resets the block position to its initial state
	 */
	public void reset() {
		this.setPosition(this.getInt("start_x"), this.getInt("start_y"));
		this.notifyWorldAboutChanges();
	}

	/**
	 * Push this Block into a given direction
	 * @param d
	 * 			the direction, this block is pushed into
	 */
	public void push(Direction d) {
		if(this.mayBePushed(d)) {
			this.setPosition(getXAfterPush(d), getYAfterPush(d));
		}
	}

	protected int getYAfterPush(Direction d) {
		return this.getY() + d.getdy();
	}

	protected int getXAfterPush(Direction d) {
		return this.getX() + d.getdx();
	}

	private boolean wasPushed() {
		boolean xChanged = this.getInt("x") != this.getInt("start_x");
		boolean yChanged = this.getInt("y") != this.getInt("start_y");
		return xChanged || yChanged;
	}

	private boolean mayBePushed(Direction d) {
		boolean pushed = wasPushed();
		boolean multiPush = this.getBool("multi");

		if(multiPush) {
			// multi push always allows pushing
			// additional checks: new position must be free
			return true;
		} else {
			// if multiple pushing is not allowed and block was pushed once, return false
			return !pushed;
		}

	}

	@Override
	public void onEntered(ActiveEntity entity, StendhalRPZone zone, int newX,
			int newY) {
		// do nothing on enter
	}

	@Override
	public void onExited(ActiveEntity entity, StendhalRPZone zone, int oldX,
			int oldY) {
		resetInPlayerlessZone(zone);
	}

	@Override
	public void onMoved(ActiveEntity entity, StendhalRPZone zone, int oldX,
			int oldY, int newX, int newY) {
		// do nothing on move
	}

	@Override
	public void onEntered(RPObject object, StendhalRPZone zone) {
		// do nothing on enter
	}

	@Override
	public void onExited(RPObject object, StendhalRPZone zone) {
		resetInPlayerlessZone(zone);
	}

	private void resetInPlayerlessZone(StendhalRPZone zone) {
		// reset to initial position if zone gets empty of players
		final boolean zoneContainsPlayer = zone.containsPlayer();
		if(!zoneContainsPlayer) {
			this.reset();
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
		Rectangle2D oldA = new Rectangle2D.Double(oldX, oldY, entity.getWidth(), entity.getHeight());
		Rectangle2D newA  = new Rectangle2D.Double(newX, newY, entity.getWidth(), entity.getHeight());
		Direction d = Direction.getAreaDirectionTowardsArea(oldA, newA);
		this.push(d);
	}

}
