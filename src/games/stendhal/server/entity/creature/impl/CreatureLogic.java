package games.stendhal.server.entity.creature.impl;

import games.stendhal.common.Direction;
import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.core.pathfinder.Path;
import games.stendhal.server.core.rp.StendhalRPAction;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.Creature;

import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;

public class CreatureLogic {
	private int turnReaction;

	/**
	 * the number of rounds the creature should wait when the path to the target
	 * is blocked and the target is not moving.
	 */
	protected static final int WAIT_ROUNDS_BECAUSE_TARGET_IS_BLOCKED = 9;
	/** the number of rounds to wait for a path the target. */
	private int waitRounds;

	private Creature creature;

	private Healingbehaviour healer = Healingbehaviour.get(null);

	private RPEntity target;

	/** the current (logic)state .*/
	private AiState aiState;


	private List<Node> patrolPath;

	private int attackTurn;

	public CreatureLogic(Creature creature) {
		this.creature = creature;

		turnReaction = Rand.rand(3);
		attackTurn = Rand.rand(5);

	}

	/**
	 * Checks whether we have to do some again or sleeps in case no player is
	 * near.
	 * 
	 * @return true, if additional action is required; false if we may sleep
	 */
	private boolean logicSleep() {
		// if there is no player near and none will see us...
		// sleep so we don't waste cpu resources
		if (creature.getZone().getPlayerAndFriends().isEmpty()) {
			fallAsleep();
			return false;
		} else {
			if (creature.isEnemyNear(30)) {
				return true;
			} else {
				fallAsleep();
				return false;	
			}
		}

		
	}

	private void fallAsleep() {
		if (aiState != AiState.SLEEP) {
			creature.stopAttack();
			creature.stop();
	
			aiState = AiState.SLEEP;
			creature.notifyWorldAboutChanges();
		}
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

	}

	/**
	 * Forgets the current attack target.
	 */
	private void logicForgetCurrentTarget() {
		if (creature.isAttacking()) {
			// stop the attack...
			target = null;
			creature.clearPath();
			creature.stopAttack();
			waitRounds = 0;
		}
	}

	/**
	 * Finds a new target to attack.
	 */
	private void logicFindNewTarget() {
		// ...and find another target
		target = creature.getNearestEnemy(7 + Math.max(creature.getWidth(),
				creature.getHeight()));
	}

	/**
	 * Creates a patroling path used if we are not attacking.
	 */
	private void logicCreatePatrolPath() {
		// Create a patrolpath
		List<Node> nodes = new LinkedList<Node>();
		if (creature.getAIProfile("patrolling") != null) {

			int size = patrolPath.size();

			for (int i = 0; i < size; i++) {
				Node actual = patrolPath.get(i);
				Node next = patrolPath.get((i + 1) % size);

				nodes.addAll(Path.searchPath(creature, actual.getX()
						+ creature.getX(), actual.getY() + creature.getY(),
						new Rectangle2D.Double(next.getX() + creature.getX(),
								next.getY() + creature.getY(), 1.0, 1.0)));
			}
		}
		creature.setPath(new FixedPath(nodes, true));

	}

	/**
	 * Follow the patrolling path.
	 */
	private void logicFollowPatrolPath() {
		if (creature.hasPath()) {
			creature.followPath();
		}
	}

	/**
	 * Stops attacking the current target and logs that it got out of reach.
	 */
	private void logicStopAttackBecauseTargetOutOfReach() {
		target = null;
		creature.clearPath();
		creature.stopAttack();
		creature.stop();
	}

	/**
	 * Create a path to the target because it moved.
	 */
	private void logicCreateNewPathToMovingTarget() {
		// target not near but in reach and is moving
		creature.clearPath();
		creature.setMovement(target, 0, 0, 20.0);

		if (!creature.hasPath()) {
			if (!creature.nextTo(target)) {
				creature.stopAttack();
				target = null;
				return;
			}
		}

		waitRounds = 0; // clear waitrounds
		aiState = AiState.APPROACHING_MOVING_TARGET; // update ai state

	}

	/**
	 * attacks the target.
	 */
	private void logicAttack() {
		// target is near
		creature.stop();
		creature.attack(target);
		creature.faceToward(target);
		aiState = AiState.ATTACKING;
	}

	/**
	 * Checks if the position (x, y) is a good position for range combat.
	 * 
	 * @param x
	 *			  x value of the position
	 * @param y
	 *			  y value of the position
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
	 * Attacks the target from distance. The creature does some pseudo random
	 * movement. The moves are done with a path with the size of 1. The higher
	 * the distance, the higher is the chance to move and the pseudo random move
	 * prefers positions which are closer to the target.
	 */
	private void logicRangeAttack() {
		if (creature.collides()) {
			creature.clearPath();
		}

		// the path can be the path to the target or the pseudo random move
		if (creature.hasPath()) {
			// TODO: Remove path size assumption/dependency
			if (creature.getPathsize() == 1) {
				// pseudo random move. complete it
				if (!creature.followPath()) {
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

			// move randomly but give closer positions a higher chance
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
			List<Node> nodes = new LinkedList<Node>();
			int nx = creature.getX() + nextDir.getdx();
			int ny = creature.getY() + nextDir.getdy();
			nodes.add(new Node(nx, ny));
			creature.setPath(new FixedPath(nodes, false));
			// Path.followPath(creature);
		}
	}

	private void logicMoveToTargetAndAttack() {
		// target in reach and not moving
		aiState = AiState.APPROACHING_STOPPED_TARGET;
		creature.attack(target);

		if (waitRounds == 0) {
			creature.faceToward(target);
		}

		// our current Path is blocked...mostly by the target or another
		// attacker
		if (creature.collides()) {
			// invalidate the path and stop
			creature.clearPath();

			// TODO: Use setRandomPath()?
			// Try to fix the issue by moving randomly.
			Direction dir = Direction.rand();
			creature.setDirection(dir);
			creature.setSpeed(creature.getBaseSpeed());

			// wait some rounds so the path can be cleared by other
			// creatures
			// (either they move away or die)
			waitRounds = WAIT_ROUNDS_BECAUSE_TARGET_IS_BLOCKED;
		}

		// be sure to let the blocking creatures pass before trying to find a
		// new path
		if (waitRounds > 0) {
			waitRounds--;
		} else {
			// Are we still patrolling?
			// TODO: Adapt for opaque 'Path' objects
			if (creature.isPathLoop() || (aiState == AiState.PATROL)) {
				// yep, so clear the patrol path
				creature.clearPath();
			}

			creature.setMovement(target, 0, 0, 20.0);


			if (!creature.hasPath()) {
				// If creature is blocked, choose a new target
				// TODO: if we are an archer and in range, creature is ok
				// don't get to near to the enemy.
				target = null;
				creature.clearPath();
				creature.stopAttack();
				creature.stop();
				waitRounds = WAIT_ROUNDS_BECAUSE_TARGET_IS_BLOCKED;

			}
		}

	}

	private void logicDoMove() {
		creature.applyMovement();
	}

	private void logicDoAttack() {
		if (!creature.isAttacking()) {
			return;
		}

		if ((SingletonRepository.getRuleProcessor().getTurn() % StendhalRPAction.getAttackRate(creature)) == attackTurn) {
			StendhalRPAction.attack(creature, creature.getAttackTarget());
			creature.tryToPoison();
		}
	}

	private void logicDoNoice() {
		// with a probability of 1 %, a random noise is made.
		if (Rand.roll1D100() == 1) {
			creature.makeNoise();
		}
	}

	
	
	public void logic() {
		StendhalRPWorld world = SingletonRepository.getRPWorld();
		healer.heal(creature);

		
		

		/*
		 * We only *think* once each few turns. So we save CPU time. Each
		 * creature [logic] uses a random turn slice. TODO: Improve this in a
		 * event oriented way.
		 */
		if (shallWeThink()) {
			if (!logicSleep()) {
				return;
			}

			// are we attacked and we don't attack ourself?
			if (creature.isAttacked() && (target == null)) {
				logicWeAreNotAttackingButGotAttacked();
			} else if ((target == null)
					|| (target.getZone() != creature.getZone())
					|| target.isInvisible() || !world.has(target.getID())) {
				// no target or current target left the zone (or is dead) or
				// target became invisible (admin)
				logicForgetCurrentTarget();
				logicFindNewTarget();
			}

			// now we check our current target
			if (target == null) {
				// No target, so patrol along
				if ((aiState != AiState.PATROL) || !creature.hasPath()) {
					logicCreatePatrolPath();
					aiState = AiState.PATROL;
				}
				logicFollowPatrolPath();
			} else if (creature.squaredDistance(target) > 18 * 18) {
				logicStopAttackBecauseTargetOutOfReach();
			} else if (creature.nextTo(target)
					&& !creature.canDoRangeAttack(target)) {
				logicAttack();
			} else if (creature.canDoRangeAttack(target)) {
				logicRangeAttack();
			} else if (!target.stopped()) {
				logicCreateNewPathToMovingTarget();
			} else {
				logicMoveToTargetAndAttack();
			}
		}

		logicDoMove();
		logicDoAttack();
		logicDoNoice();

		creature.notifyWorldAboutChanges();
	}

	private boolean shallWeThink() {
		
		return (SingletonRepository.getRuleProcessor().getTurn() % 3) == turnReaction;
	}

	/**
	 * resets the AI state.
	 */
	public void resetAIState() {
		aiState = AiState.IDLE;
	}

	public void createPath() {
		/** TODO: Create paths in other way */
		patrolPath = new LinkedList<Node>();
		patrolPath.add(new Node(0, 0));
		patrolPath.add(new Node(-6, 0));
		patrolPath.add(new Node(-6, 6));
		patrolPath.add(new Node(0, 6));
		resetAIState();
	}

	public void setHealer(String aiprofile) {
		this.healer = Healingbehaviour.get(aiprofile);
	}
}
