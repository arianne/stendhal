package games.stendhal.server.entity.creature.impl;

import games.stendhal.common.Direction;
import games.stendhal.common.Rand;
import games.stendhal.server.entity.creature.Creature;
import marauroa.common.Log4J;

class Patroller implements Idlebehaviour {
	/** the logger instance. */
	private static final marauroa.common.Logger logger = Log4J.getLogger(Patroller.class);

	private int minX;
	private int maxX;
	private int minY;
	private int maxY;
	private boolean inited;


	

	private void initArea(Creature creature) {
		minX = creature.getX() - 3;
		maxX = creature.getX() + 3;
		minY = creature.getY() - 3;
		maxY = creature.getY() + 3;
		 inited=true;
	}

	public void perform(Creature creature) {
		
		if (creature.hasPath()) {
			creature.followPath();
		} else {
			if(weWouldLeaveArea(creature, Direction.STOP)){
			//	logger.info(inited +" x:" +  minX+ " y: "+minY+"newX"+ creature.getX()+"newY"+creature.getY()+ " resetting patroler idle area for"+creature);
				initArea(creature);
				
			}
			assert(!weWouldLeaveArea(creature, Direction.STOP));
			Direction currentDir = creature.getDirection();
			if(currentDir == Direction.STOP || weWouldLeaveArea(creature, creature.getDirection()) || creature.getZone().collides(creature.getX()+currentDir.getdx(),creature.getY()+currentDir.getdy())){
				for (int i=0;i<4;i++){
					currentDir = currentDir.nextDirection();
				
					if(!weWouldLeaveArea(creature,currentDir)&& !creature.getZone().collides(creature.getX()+currentDir.getdx(),creature.getY()+currentDir.getdy())){
						creature.setDirection(currentDir);
						continue;
					}
				}
			}
			
			
			creature.setSpeed(creature.getBaseSpeed());
            
		}
		creature.applyMovement();
	}

	private boolean weWouldLeaveArea(Creature creature, Direction d) {
		return (creature.getY() + d.getdy() < minY)
				|| (creature.getY() + d.getdy() > maxY)
				|| (creature.getX() + d.getdx() < minX)
				|| (creature.getX() + d.getdx() > maxX);
	}

}
