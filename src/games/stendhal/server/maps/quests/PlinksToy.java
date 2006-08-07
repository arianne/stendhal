package games.stendhal.server.maps.quests;

import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.PlantGrower;
import marauroa.common.game.IRPZone;

/**
 * QUEST: Plink's Toy
 *
 * PARTICIPANTS:
 * - Plink
 * - some wolfs
 *
 * STEPS:
 * - Plink tells you that he got scared by some wolfs and
	 ran away dropping his teddy.
 * - Find the teddy in the Park Of Wolfs
 * - Bring it back to Plink
 *
 * REWARD:
 * - 20 XP
 *
 * REPETITIONS:
 * - None.
 */
public class PlinksToy extends AbstractQuest {

	private static final String QUEST_SLOT = "plinks_toy";

	@Override
	public void init(String name) {
		super.init(name, QUEST_SLOT);
	}

	private void step_1() {
		// TODO: plink asks for his teddy
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
	
	private void step_3() {
		// TODO: take the teddy back to Plink 
	}

	@Override
	public void addToWorld(StendhalRPWorld world, StendhalRPRuleProcessor rules) {
		super.addToWorld(world, rules);

		step_1();
		step_2();
		step_3();
	}

}
