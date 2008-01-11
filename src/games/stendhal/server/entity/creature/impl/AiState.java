package games.stendhal.server.entity.creature.impl;

/** Enum classifying the possible (AI) states a creature can be in. */
public enum AiState {
	/** Sleeping as there is no enemy in sight. */
	SLEEP,
	/** Doing nothing. */
	IDLE,
	/** Patroling, watching for an enemy. */
	PATROL,
	/** Moving towards a moving target. */
	APPROACHING_MOVING_TARGET,
	/** Moving towards a stopped target. */
	APPROACHING_STOPPED_TARGET,
	/** Attacking. */
	ATTACKING;
}
