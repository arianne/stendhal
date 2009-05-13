package games.stendhal.server.entity.creature.impl;

import games.stendhal.common.Direction;
import games.stendhal.server.entity.creature.Creature;

class Patroller extends StandOnIdle {

	private int minX;
	private int maxX;
	private int minY;
	private int maxY;
	
	private void initArea(final Creature creature) {
		minX = creature.getX() - 3;
		maxX = creature.getX() + 2 + (int) (creature.getWidth());
		minY = creature.getY() - 3;
		maxY = creature.getY() + 2 + (int) (creature.getHeight());
	}

	public void perform(final Creature creature) {
		if (!creature.getZone().getPlayerAndFriends().isEmpty()) {
			if (creature.hasPath()) {
				creature.followPath();
			} else {
				if (retreatUnderFire(creature)) {
					return;
				}
				
				if (weWouldLeaveArea(creature, Direction.STOP)) {
					initArea(creature);
				}
				assert (!weWouldLeaveArea(creature, Direction.STOP));
				Direction currentDir = creature.getDirection();
				if ((currentDir == Direction.STOP)
						|| weWouldLeaveArea(creature, creature.getDirection())
						|| creature.getZone().collides(creature, creature.getX() + currentDir.getdx(),
								creature.getY() + currentDir.getdy())) {
					for (int i = 0; i < 4; i++) {
						currentDir = currentDir.nextDirection();

						if (!weWouldLeaveArea(creature, currentDir)
								&& !creature.getZone().collides(creature, creature.getX() + currentDir.getdx(),
										creature.getY() + currentDir.getdy())) {
							creature.setDirection(currentDir);
							continue;
						}
					}
				}

				if (creature.getDirection() != Direction.STOP) {
					creature.setSpeed(creature.getBaseSpeed());
				}

			}
			creature.applyMovement();
		}
	}

	private boolean weWouldLeaveArea(final Creature creature, final Direction d) {
		return (creature.getY() + d.getdy() < minY)
				|| (creature.getY() + d.getdy() > maxY)
				|| (creature.getX() + d.getdx() < minX)
				|| (creature.getX() + d.getdx() > maxX);
	}
}
