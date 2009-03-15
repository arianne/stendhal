/**
 *
 */
package games.stendhal.server.maps;

import utilities.PlayerTestHelper;
import utilities.RPClass.ArrestWarrentTestHelper;
import utilities.RPClass.BloodTestHelper;
import utilities.RPClass.CorpseTestHelper;
import utilities.RPClass.CreatureTestHelper;
import utilities.RPClass.ItemTestHelper;
import utilities.RPClass.PassiveEntityRespawnPointTestHelper;
import utilities.RPClass.PortalTestHelper;
import utilities.RPClass.SheepFoodTestHelper;
import utilities.RPClass.SheepTestHelper;
import games.stendhal.server.core.engine.StendhalRPWorld;
import marauroa.common.game.RPObject;

public class MockStendlRPWorld extends StendhalRPWorld {

	@Override
	public void modify(final RPObject object) {
	}

	
	
	protected void createRPClasses() {
		
		PassiveEntityRespawnPointTestHelper.generateRPClasses();
		ItemTestHelper.generateRPClasses();
		PlayerTestHelper.generateNPCRPClasses();
		ArrestWarrentTestHelper.generateRPClasses();
		CreatureTestHelper.generateRPClasses();
		CorpseTestHelper.generateRPClasses();
		PlayerTestHelper.generateNPCRPClasses();
		PlayerTestHelper.generatePlayerRPClasses();
		SheepFoodTestHelper.generateRPClasses();
		BloodTestHelper.generateRPClasses();
		SheepTestHelper.generateRPClasses();
		PortalTestHelper.generateRPClasses();
	
	}
	
	public static StendhalRPWorld get() {
		if (!(instance instanceof MockStendlRPWorld)) {
			instance = new MockStendlRPWorld();
			((MockStendlRPWorld) instance).createRPClasses();
		}
		return instance;
	}

	
	
	@Override
	protected void initialize() {

	}
	
	public static void  reset() {
		instance = null;
	}

}
