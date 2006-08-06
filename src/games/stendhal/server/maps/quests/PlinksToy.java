package games.stendhal.server.maps.quests;

import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.PlantGrower;
import marauroa.common.game.IRPZone;

/**
 * @author hendrik
 */
public class PlinksToy extends AQuest {
	
	private static final String QUEST_SLOT = "plinks_toy";

	@Override
	public void init(String name) {
		super.init(name, QUEST_SLOT);
	}
	
	private void step_2() {
		StendhalRPZone zone = (StendhalRPZone) world.getRPZone(new IRPZone.ID("0_semos_plains_n"));
		PlantGrower plantGrower = new PlantGrower("teddy", 1500);
		zone.assignRPObjectID(plantGrower);
		plantGrower.setx(107);
		plantGrower.sety(84);
		plantGrower.setDescription("Plink lost his teddy here.");
		zone.add(plantGrower);
		
		rules.getPlantGrowers().add(plantGrower);
	}

	@Override
	public void addToWorld(StendhalRPWorld world, StendhalRPRuleProcessor rules) {
		super.addToWorld(world, rules);

		step_2();
	}


	

}
