package games.stendhal.server.entity.creature.impl;

import games.stendhal.common.Debug;
import games.stendhal.common.Direction;
import games.stendhal.common.Rand;
import games.stendhal.server.StendhalRPAction;
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.pathfinder.Path;

import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

public class CreatureLogic {
	private static Logger logger = Logger.getLogger(CreatureLogic.class);

	/**
	 * the number of rounds the creature should wait when the path to the target
	 * is blocked and the target is not moving
	 */
	protected static final int WAIT_ROUNDS_BECAUSE_TARGET_IS_BLOCKED = 9;

	private Creature creature;

	private RPEntity target;

	/** the current (logic)state */
	private AiState aiState;

	/** the number of rounds to wait for a path the target */
	private int waitRounds;

	private List<Path.Node> patrolPath;

	private int attackTurn;


	// creature will keep track of the logic so the client can display it
	private StringBuilder debug = new StringBuilder(100);

	public CreatureLogic(Creature creature) {
		this.creature = creature;
		attackTurn = Rand.rand(5);
	}

	private void logicHeal() {
		if (creature.getAIProfile("heal") != null) {
			if (creature.getHP() < creature.getBaseHP()) {
				String[] healingAttributes = creature.getAIProfile("heal").split(",");
				int amount = Integer.parseInt(healingAttributes[0]);
				int frequency = Integer.parseInt(healingAttributes[1]);
				creature.healSelf(amount, frequency);
			}
		}
	}

	/**
	 * Checks whether we have to do some again or sleeps in case no player is near.
	 *
	 * @return true, if additional action is required; false if we may sleep
	 */
	private boolean logicSleep() {
		// if there is no player near and none will see us...
		// sleep so we don't waste cpu resources
		if (!creature.isEnemyNear(30)) {

			// If we are already sleeping, than don't modify the Entity.
			if (aiState == AiState.SLEEP) {
				return false;
			}

			creature.stopAttack();
			creature.stop();

			if (Debug.CREATURES_DEBUG_SERVER) {
				creature.put("debug", "sleep");
			}

			aiState = AiState.SLEEP;
			creature.notifyWorldAboutChanges();
			return false;
		}
		return true;
	}

	private void logicWeAreNotAttackingButGotAttacked() {
		// Yep, we're attacked
		creature.clearPath();

		// hit the attacker, but prefer players
		target = creature.getNearestEnemy(8);
		if (target == null) {
			/*
			 * Use the first attacking RPEntity found (if any)
			 */
			for (Entity entity : creature.getAttackSources()) {
				if (entity instanceof RPEntity) {
					target = (RPEntity) entity;
					break;
				}
			}
		}

		if (Debug.CREATURES_DEBUG_SERVER) {
			debug.append("attacked;").append(target.getID().getObjectID()).append('|');
		}

		if (logger.isDebugEnabled()) {
			logger.debug(creature.getIDforDebug() + " Creature(" + creature.get("type") + ") has been attacked by " + target.get("type"));
		}
	}

	/**
	 * Forgets the current attack target.
	 */
	private void logicForgetCurrentTarget() {
		if (creature.isAttacking()) {
			// stop the attack...
			if (Debug.CREATURES_DEBUG_SERVER) {
				debug.append("cancelattack|");
			}
			target = null;
			creature.clearPath();
			creature.stopAttack();
			waitRounds = 0;
		}
	}

	/**
	 * Finds a new target to attack 
	 */
	private void logicFindNewTarget() {
		// ...and find another target
		target = creature.getNearestEnemy(7 + Math.max(creature.getWidth(), creature.getHeight()));
		if (target != null) {
			if (logger.isDebugEnabled()) {
				logger.debug(creature.getIDforDebug() + " Creature(" + creature.get("type") + ") gets a new target.");
			}
			if (Debug.CREATURES_DEBUG_SERVER) {
				debug.append("newtarget;").append(target.getID().getObjectID()).append('|');
			}
		}
	}

	/**
	 * Creates a patroling path used if we are not attacking.
	 */
	private void logicCreatePatrolPath() {
		// Create a patrolpath
		if (logger.isDebugEnabled()) {
			logger.debug(creature.getIDforDebug() + " Creating Path for creature entity");
		}
		List<Path.Node> nodes = new LinkedList<Path.Node>();
		long time = System.nanoTime();
		if (creature.getAIProfile("patrolling") != null) {

			int size = patrolPath.size();

			for (int i = 0; i < size; i++) {
				Path.Node actual = patrolPath.get(i);
				Path.Node next = patrolPath.get((i + 1) % size);

				nodes.addAll(Path.searchPath(creature, actual.x + creature.getX(), actual.y + creature.getY(), new Rectangle2D.Double(next.x
				        + creature.getX(), next.y + creature.getY(), 1.0, 1.0)));
			}
		}
		long time2 = System.nanoTime() - time;

		if (logger.isDebugEnabled()) {
			logger.debug(creature.getIDforDebug() + " Path is: " + nodes);
		}
		creature.setPath(nodes, true);

		if (Debug.CREATURES_DEBUG_SERVER) {
			debug.append("generatepatrolpath;").append(time2).append("|");
		}
	}

	/**
	 * Follow the patrolling path
	 */
	private void logicFollowPatrolPath() {
		if (logger.isDebugEnabled()) {
			logger.debug(creature.getIDforDebug() + " Following path");
		}
		if (creature.hasPath()) {
			Path.followPath(creature, creature.getSpeed());
		}
		aiState = AiState.PATROL;
		if (Debug.CREATURES_DEBUG_SERVER) {
			debug.append("patrol;").append(creature.pathToString()).append('|');
		}
	}

	/**
	 * Stops attacking the current target and logs that it got out of reach.
	 */
	private void logicStopAttackBecauseTargetOutOfReach() {
		// target out of reach
		if (logger.isDebugEnabled()) {
			logger.debug(creature.getIDforDebug() + " Attacker is too far. Creature stops attack");
		}
		target = null;
		creature.clearPath();
		creature.stopAttack();
		creature.stop();

		if (Debug.CREATURES_DEBUG_SERVER) {
			debug.append("outofreachstopped|");
		}
	}

	/**
	 * Create a path to the target because it moved.
	 */
	private void logicCreateNewPathToMovingTarget() {
		// target not near but in reach and is moving
		if (logger.isDebugEnabled()) {
			logger.debug(creature.getIDforDebug() + " Moving to target. Searching new path");
		}
		creature.clearPath();
		creature.setMovement(target, 0, 0, 20.0);

		if ((creature.getPath() == null) || (creature.getPath().size() == 0)) {
			if (!creature.nextTo(target)) {
				creature.stopAttack();
				target = null;
				if (logger.isDebugEnabled()) {
					logger.debug(creature.getIDforDebug() + " Large creature wall bug workaround");
				}
				return;
			}
		}

		creature.moveto(creature.getSpeed());
		waitRounds = 0; // clear waitrounds
		aiState = AiState.APPROACHING_MOVING_TARGET; // update ai state
		if (Debug.CREATURES_DEBUG_SERVER) {
			List path = creature.getPath();
			if (path != null) {
				debug.append("targetmoved;").append(creature.pathToString()).append("|");
			}
		}
	}

	/**
	 * attackts the target
	 */
	private void logicAttack() {
		if (Debug.CREATURES_DEBUG_SERVER) {
			debug.append("attacking|");
		}
		// target is near
		if (logger.isDebugEnabled()) {
			logger.debug(creature.getIDforDebug() + " Next to target. Creature stops and attacks");
		}
		creature.stop();
		creature.attack(target);
		creature.faceTo(target);
		aiState = AiState.ATTACKING;
	}

	/**
	 * Checks if the postion (x, y) is a good position for range combat.
	 * @param x x value of the position
	 * @param y y value of the position
	 * @return true if creature is a good position for range combat
	 */
	private boolean isGoodRangeCombatPosition(int x, int y) {
		StendhalRPZone zone = creature.getZone();

		double distance = target.squaredDistance(x, y);
		if (distance > 7 * 7) {
			return false;
		}
		// TODO: work only for 1x2 size creature attacks 1x2 size target
		if (distance <= 2) {
			return false;
		}
		if (zone.collides(creature, x, y)) {
			return false;
		}
		if (zone.collidesOnLine(x, y, target.getX(), target.getY())) {
			return false;
		}
		return true;
	}

	/**
	 * Attackts the target from distance. The creature does some pseudo random
	 * movement. The moves are done with a path with the size of 1. The higher
	 * the distance, the higher is the chance to move and the pseudo random move
	 * prefers potitions which are closer to the target.
	 */
	private void logicRangeAttack() {
		if (Debug.CREATURES_DEBUG_SERVER) {
			debug.append("rangeattack|");
		}
		if (logger.isDebugEnabled()) {
			logger.debug(creature.getIDforDebug() + " Range Attack");
		}

		if (creature.collides()) {
			creature.clearPath();
		}

		// the path can be the path to the target or the pseudo random move
		if (creature.hasPath()) {
			if (creature.getPath().size() == 1) {
				// pseudo random move. complete it
				if (!Path.followPath(creature, creature.getSpeed())) {
					return;
				}
				creature.clearPath();
			}
		}

		double distance = creature.squaredDistance(target);
		int rand = Rand.roll1D100();
		Direction nextDir = Direction.STOP;

		// force move if creature is not a good position
		if (!isGoodRangeCombatPosition(creature.getX(), creature.getY())) {
			distance += rand;
		}
		// The higher the distance, the higher is the chance to move
		if (distance > rand) {

			// move randomly but give closer postions a higher chance
			double nextRndDistance = distance + Rand.rand(7);

			for (Direction dir : Direction.values()) {
				if (dir != Direction.STOP) {

					int nx = creature.getX() + dir.getdx();
					int ny = creature.getY() + dir.getdy();

					if (isGoodRangeCombatPosition(nx, ny)) {

						distance = target.squaredDistance(nx, ny);
						double rndDistance = distance + Rand.rand(7);

						if (rndDistance < nextRndDistance) {
							nextDir = dir;
							nextRndDistance = rndDistance;
						}
					}
				}
			}
		}
		if (nextDir == Direction.STOP) {
			// TODO: use pathfinder if creature is not a good position
			logicAttack();
		} else {
			List<Path.Node> nodes = new LinkedList<Path.Node>();
			int nx = creature.getX() + nextDir.getdx();
			int ny = creature.getY() + nextDir.getdy();
			nodes.add(new Path.Node(nx, ny));
			creature.setPath(nodes, false);
			Path.followPath(creature, creature.getSpeed());
		}
	}

	private void logicMoveToTargetAndAttack() {
		// target in reach and not moving
		if (logger.isDebugEnabled()) {
			logger.debug(creature.getIDforDebug() + " Moving to target. Creature attacks");
		}
		if (Debug.CREATURES_DEBUG_SERVER) {
			debug.append("movetotarget");
		}
		aiState = AiState.APPROACHING_STOPPED_TARGET;
		creature.attack(target);

		if (waitRounds == 0) {
			creature.faceTo(target);
		}

		// our current Path is blocked...mostly by the target or another
		// attacker
		if (creature.collides()) {
			if (Debug.CREATURES_DEBUG_SERVER) {
				debug.append(";blocked");
			}
			// invalidate the path and stop
			creature.clearPath();

			// Try to fix the issue by moving randomly.
			Direction dir = Direction.rand();
			creature.setDirection(dir);
			creature.setSpeed(creature.getSpeed());

			// wait some rounds so the path can be cleared by other
			// creatures
			// (either they move away or die)
			waitRounds = WAIT_ROUNDS_BECAUSE_TARGET_IS_BLOCKED;
		}

		// be sure to let the blocking creatures pass before trying to find a new path
		if (waitRounds > 0) {
			if (Debug.CREATURES_DEBUG_SERVER) {
				debug.append(";waiting");
			}
			waitRounds--;
		} else {
			// Are we still patrolling?
			if (creature.isPathLoop() || (aiState == AiState.PATROL)) {
				// yep, so clear the patrol path
				creature.clearPath();
			}

			creature.setMovement(target, 0, 0, 20.0);
			creature.moveto(creature.getSpeed());
			if (Debug.CREATURES_DEBUG_SERVER) {
				debug.append(";newpath");
			}

			if ((creature.getPath() == null) || (creature.getPath().size() == 0)) {
				// If creature is blocked, choose a new target
				// TODO: if we are an archer and in range, creature is ok
				//       don't get to near to the enemy. 
				if (Debug.CREATURES_DEBUG_SERVER) {
					debug.append(";blocked");
				}
				if (logger.isDebugEnabled()) {
					logger.debug(creature.getIDforDebug() + " Blocked. Choosing a new target.");
				}

				target = null;
				creature.clearPath();
				creature.stopAttack();
				creature.stop();
				waitRounds = WAIT_ROUNDS_BECAUSE_TARGET_IS_BLOCKED;
			} else {
				if (Debug.CREATURES_DEBUG_SERVER) {
					debug.append(';').append(creature.getPath());
				}
			}
		}

		if (Debug.CREATURES_DEBUG_SERVER) {
			debug.append(";dummy|");
		}
	}

	private void logicDoMove() {
		if (!creature.stopped()) {
			StendhalRPAction.move(creature);
		}

	}

	private void logicDoAttack() {
		if ((StendhalRPRuleProcessor.get().getTurn() % StendhalRPAction.getAttackRate(creature) == attackTurn) && creature.isAttacking()) {
			StendhalRPAction.attack(creature, creature.getAttackTarget());
			creature.tryToPoison();
		}
	}

	private void logicDoNoice() {
		// with a probability of 1 %, a random noise is made.
		if (Rand.roll1D100() == 1) {
			creature.makeNoice();
		}
	}

	public void logic() {
		StendhalRPWorld world = StendhalRPWorld.get();
		// Log4J.startMethod(logger, "logic");

		logicHeal();
		if (!logicSleep()) {
			return;
		}

		// are we attacked and we don't attack ourself?
		if (creature.isAttacked() && (target == null)) {
			logicWeAreNotAttackingButGotAttacked();
		} else if ((target == null) || (!target.get("zoneid").equals(creature.get("zoneid")) && world.has(target.getID()))
		        || !world.has(target.getID()) || target.has("invisible")) {
			// no target or current target left the zone (or is dead) or target became invisible (admin)
			logicForgetCurrentTarget();
			logicFindNewTarget();
		}

		// now we check our current target
		if (target == null) {
			// No target, so patrol along
			if ((aiState != AiState.PATROL) || !creature.hasPath()) {
				logicCreatePatrolPath();
			}
			logicFollowPatrolPath();
		} else if (creature.squaredDistance(target) > 18 * 18) {
			logicStopAttackBecauseTargetOutOfReach();
		} else if (creature.nextTo(target) && !creature.canDoRangeAttack(target)) {
			logicAttack();
		} else if (creature.canDoRangeAttack(target)) {
			logicRangeAttack();
		} else if (!target.stopped()) {
			logicCreateNewPathToMovingTarget();
		} else {
			logicMoveToTargetAndAttack();
		}

		logicDoMove();
		logicDoAttack();
		logicDoNoice();

		if (Debug.CREATURES_DEBUG_SERVER) {
			creature.put("debug", debug.toString());
		}
		creature.notifyWorldAboutChanges();
		//Log4J.finishMethod(logger, "logic");
	}

	/**
	 * resets the AI state
	 */
	public void resetAIState() {
		aiState = AiState.IDLE;
	}

	public void createPath() {
		/** TODO: Create paths in other way */
		patrolPath = new LinkedList<Path.Node>();
		patrolPath.add(new Path.Node(0, 0));
		patrolPath.add(new Path.Node(-6, 0));
		patrolPath.add(new Path.Node(-6, 6));
		patrolPath.add(new Path.Node(0, 6));
		resetAIState();
	}
}
