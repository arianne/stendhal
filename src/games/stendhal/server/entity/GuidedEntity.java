/*
 * @(#) src/games/stendhal/server/entity/GuidedEntity.java
 *
 * $Id$
 */

package games.stendhal.server.entity;

//
//

import static games.stendhal.common.constants.Common.PATHSET;
import games.stendhal.common.Direction;
import games.stendhal.common.Rand;
import games.stendhal.server.core.pathfinder.EntityGuide;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.core.pathfinder.Path;

import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import marauroa.common.game.RPObject;

/**
 * An entity that has speed/direction and is guided via a Path.
 */
public abstract class GuidedEntity extends ActiveEntity {
	
	/** The entity's default speed value */
	protected double baseSpeed;

	private final EntityGuide guide = new EntityGuide();

	public Registrator pathnotifier = new Registrator();
	
	/** Action entity will take after collision */
    private CollisionAction collisionAction;
	
    /**
     * The entity is using a random path
     */
    private boolean randomPath = false;
    private boolean returnToOrigin = false;
    
    /**
     * The radius at which the entity will walk
     */
    private int movementRadius = 0;
    
	/**
	 * Create a guided entity.
	 */
	public GuidedEntity() {
		baseSpeed = 0;
		guide.guideMe(this);
	}

	/**
	 * Create a guided entity.
	 *
	 * @param object
	 *            The source object.
	 */
	public GuidedEntity(final RPObject object) {
		super(object);
		baseSpeed = 0;
		guide.guideMe(this);
		update();
	}

	//
	// TEMP for Transition
	//
	
	/**
	 * Get the normal movement speed.
	 *
	 * @return The normal speed when moving.
	 */
	public final double getBaseSpeed() {
		return this.baseSpeed;
	}
	
	/**
	 * Set the normal movement speed.
	 *
	 * @param bs - New normal speed for moving.
	 */
	public final void setBaseSpeed(final double bs) {
		this.baseSpeed = bs;
	}
	
	//
	// GuidedEntity
	//

	/**
	 * Set a path for this entity to follow. Any previous path is cleared and
	 * the entity starts at the first node (so the first node should be its
	 * position, of course). The speed will be set to the default for the
	 * entity.
	 *
	 * @param path
	 *            The path.
	 */
	public final void setPath(final FixedPath path) {
		if ((path != null) && !path.isFinished()) {
			setSpeed(getBaseSpeed());

			if (!this.has(PATHSET)) {
				this.put(PATHSET, "");
			}

			guide.path = path;
			guide.pathPosition = 0;
			guide.followPath(this);
			
			return;
		}
		
		if (this.has(PATHSET)) {
			this.remove(PATHSET);
		}
		guide.clearPath();
	}
	
	/**
     * Set a path for this entity to follow. Any previous path is cleared and
     * the entity starts at the first node (so the first node should be its
     * position, of course). The speed will be set to the default for the
     * entity.
	 * 
	 * @param path
	 *         The path.
	 * @param position
	 *         The position of the path where the entity should start
	 */
    public final void setPath(final FixedPath path, final int position) {
        if ((path != null) && !path.isFinished()) {
            setSpeed(getBaseSpeed());

			if (!this.has(PATHSET)) {
				this.put(PATHSET, "");
			}

            guide.path = path;
            guide.pathPosition = position;
            guide.followPath(this);
            
            return;
        }
        
		if (this.has(PATHSET)) {
			this.remove(PATHSET);
		}
        guide.clearPath();
    }
    
	/**
	 * Changed the entity's path to walk in the oppisite direction
	 */
	public void reversePath() {
	    if (!randomPath && guide.path.isLoop()) {
	        List<Node> reverseNodes = guide.path.getNodeList();
	        
	        // Sets the position for the reversed path
	        int reversePosition = (guide.path.getNodeList().size() - 1) - guide.getPreviousPosition();
	        
	        Collections.reverse(reverseNodes);
	        setPath(new FixedPath(reverseNodes, guide.path.isLoop()), reversePosition);
	        }
	    else {
	    	stop();
	    }
	}
	
	/**
	 * Clear the entity's path and remove PATHSET attribute if available and
	 * stop entity movement.
	 */
	@Override
	public void stop() {
		/* Clear entity's path if set. */
		if (this.hasPath() || this.has(PATHSET)) {
			this.clearPath();
		}
		super.stop();
	}

	public void setCollisionAction(final CollisionAction action) {
		collisionAction = action;
	}

	/**
	 * function return current entity's path.
	 * @return path
	 */
	public FixedPath getPath() {
		return guide.path;
	}

	/**
	 * Clear the entity's path and remove PATHSET attribute if available.
	 */
	public void clearPath() {
		if (this.has(PATHSET)) {
			this.remove(PATHSET);
		}

		guide.clearPath();

	}

	/**
	 * Determine if the entity has a path.
	 *
	 * @return <code>true</code> if there is a path.
	 */
	public boolean hasPath() {
		return (guide.path != null);
	}

	/**
	 * Is the path a loop.
	 * @return true if running in circles
	 */
	public boolean isPathLoop() {
		if (guide.path == null) {
			return false;
		} else {
			return guide.path.isLoop();
		}
	}

	/**
	 * Get the path nodes position.
	 * @return position in path
	 */
	public int getPathPosition() {
		return guide.pathPosition;
	}

	/**
	 * Set the path nodes position.
	 * @param pathPos
	 */
	public void setPathPosition(final int pathPos) {
		guide.pathPosition = pathPos;
	}

	/**
	 * Plan a new path to the old destination.
	 */
	public void reroute() {
		if (getPath().isLoop()) {
			// If entity cannot be rerouted use reversePath
			reversePath();
		} else if (hasPath()) {
			Node node = guide.path.getDestination();
			final List<Node> path = Path.searchPath(this, node.getX(), node.getY());

			if (path.size() >= 1) {
				setPath(new FixedPath(path, false));
			} else {
				/*
				 * It can happen that some other entity goes to occupy the
				 * target position after the path has been planned. Just
				 * stop if that happens and we are next to the goal.
				 */
				stop(); // Calls clearPath()
			}
		}
	}

	//
	// ActiveEntity
	//

	/**
	 * Apply movement and process it's reactions.
	 */
	@Override
	public void applyMovement() {
		if (hasPath()) {
			followPath();
			super.applyMovement();
			faceNext();
		} else {
			super.applyMovement();
		}
	}

	/**
	 * Set facing to next <code>Node</code>, if any
	 */
	private void faceNext() {
		guide.faceNext(this);
	}

	public boolean followPath() {
		return guide.followPath(this);
	}

	public EntityGuide getGuide() {
		return guide;
	}

	@Override
	protected void onMoved(final int oldX, final int oldY, final int newX, final int newY) {
		super.onMoved(oldX, oldY, newX, newY);

		/*
		 * Adjust speed based on the resisting entities at the same coordinate.
		 */
		if (getSpeed() > 0) {
			int resistance = getLocalResistance();

			if ((getSpeed() < getBaseSpeed()) || (resistance != 0)) {
				setSpeed(getBaseSpeed() * (100 - resistance) / 100.0);
			}
		}
	}

	/**
	 *
	 */
	public void onFinishedPath() {
		pathnotifier.setChanges();
		pathnotifier.notifyObservers();
	}

	/**
	 * Get resistance caused by other entities occupying the same, or part
	 * of the same space.
	 *
	 * @return resistance
	 */
	private int getLocalResistance() {
		int resistance = 0;
		double size = getWidth() * getHeight();

		Rectangle2D thisArea = getArea();
		Rectangle2D otherArea;
		Rectangle2D intersect = new Rectangle2D.Double();
		for (final RPObject obj : getZone()) {
			final Entity entity = (Entity) obj;
			if (this != entity) {
				otherArea = entity.getArea();
				Rectangle2D.intersect(thisArea, otherArea, intersect);
				// skip entities far away
				if (!intersect.isEmpty()) {
					int r = getResistance(entity);
					if (r != 0) {
						/*
						 * Only count resistance by the proportion the resisting
						 * entity covers the area of this entity. Allows large
						 * monsters trample over small obstacles faster than a
						 * small one trying to run right through it.
						 */
						double part = intersect.getWidth() * intersect.getHeight() / size;
						r *= part;

						/*
						 * Add up like probabilities to avoid small resistance
						 * quickly resulting in a massive slow down.
						 */
						resistance = 100 - ((100 - resistance)) * (100 - r) / 100;
					}
				}
			}
		}

		return resistance;
	}

	@Override
	protected void handleObjectCollision() {
		stop(); // Calls clearPath()
	}

	public void updateModifiedAttributes() {
		//TODO base speed does not get transfered to the client? testing showed, that speed is used at client side
	}

	//
	// START - Methods controlling random movement (alphabetical)
	// Currently may only work for PassiveNPC.
	//
	
    /**
     * Checks if the entity has reached a set radius
     * 
     * @return <code>true</code> if the entity has moved outside its movement
     * 	area, othwewise <code>false</code>
     */
    public final boolean atMovementRadius() {
        Point difference = getDistanceFromOrigin();
        
        // Set the maximum movement distance at exact radius
        int max = movementRadius - 1;
        if (movementRadius > 0 && (difference.getX() > max || difference.getY() > max)) {
            return true;
        }
        
        return false;
    }
    
    protected final Direction getDirectionFromOrigin() {
        Direction dir;
        dir = Direction.LEFT;
        
        return dir;
    }
    /**
     * Get the distance that the entity has moved away from its starting point
     * 
     * @return The distance from entity's starting point
     */
    protected final Point getDistanceFromOrigin() {
        int originX = getOrigin().x;
        int originY = getOrigin().y;
        int currentX = getX();
        int currentY = getY();
        
        int Xdiff = Math.abs(currentX - originX);
        int Ydiff = Math.abs(currentY - originY);
        
        return new Point(Xdiff, Ydiff);
    }
    
    /**
     * @return Action to take on collision
     */
    public CollisionAction getCollisionAction() {
    	return collisionAction;
    }
    
	/**
	 * Changed path of entity when radius is reached
	 */
    public void onOutsideMovementRadius() {
        List<Node> nodes = new LinkedList<Node>();
        
        if (returnToOrigin) {
            nodes.add(new Node(getOrigin().x, getOrigin().y));
        } else {
            // FIXME: Does not change direction smoothly
            // Generate a random distanct to walk somwhere within the radius
            // We should already know from atMovementRadius() that movementRadius is greater than 0
            int walkBack = Rand.randUniform(1, movementRadius);
            int newX = getX();
            int newY = getY();
            
            Direction dir = getDirection();
            if (dir == Direction.RIGHT) {
                newX -= walkBack;
            } else if (dir == Direction.LEFT) {
                newX += walkBack;
            } else if (dir == Direction.DOWN) {
                newY -= walkBack;
            } else if (dir == Direction.UP) {
                newY += walkBack;
            }
            
            nodes.add(new Node(newX, newY));
        }
        
        this.setPath(new FixedPath(nodes, false));
    }
    
    /**
     * Sets the maximum distance the entity will move from its origin
     * 
     * @param radius max movable distance
     */
    public void setRandomMovementRadius(final int radius) {
        movementRadius = radius;
    }
    
    /**
     * Sets the maximum distance an entity will move away from its original
     * position
     * 
     * @param radius
     *     distance entity will move away from its origin
     * @param ret
     *     if "true" entity will return to origin when radius border reached
     */
    public void setRandomMovementRadius(final int radius, final boolean ret) {
        movementRadius = radius;
        returnToOrigin = ret;
    }
    
    /**
     * Sets or unsets entity's path as random.
     * 
     * @param random
     *      <code>true</code> if entity's path is random
     */
    protected void setUsesRandomPath(boolean random) {
        randomPath = random;
    }
    
	/**
	 * Determines whether the entity is using a random path.
	 * 
	 * @return <code>true</code> if the entity uses random paths, otherwise
	 *  <code>false</code>
	 */
	protected boolean usesRandomPath() {
	    return randomPath;
	}

	//
	// END - Methods controlling random movement
	//
}