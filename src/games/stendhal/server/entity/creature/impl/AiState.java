package games.stendhal.server.entity.creature.impl;

/** Enum classifying the possible (AI) states a creature can be in */
public enum AiState {
	/** sleeping as there is no enemy in sight */
	SLEEP,
	/** doin' nothing */
	IDLE,
	/** patroling, watching for an enemy */
	PATROL,
	/** moving towards a moving target */
	APPROACHING_MOVING_TARGET,
	/** moving towards a stopped target */
	APPROACHING_STOPPED_TARGET,
	/** attacking */
	ATTACKING;
}
