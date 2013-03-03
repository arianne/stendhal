/**
 * 
 */
package utilities.RPClass;

import marauroa.common.game.RPClass;
import games.stendhal.server.entity.mapstuff.area.AreaEntity;
import games.stendhal.server.entity.mapstuff.block.Block;
import games.stendhal.server.entity.mapstuff.block.BlockTest;

/**
 * Test helper for the {@link BlockTest} to generate the necessary RPClasses
 * 
 * @author madmetzger
 */
public class BlockTestHelper {
	
	public static void generateRPClasses() {
		EntityTestHelper.generateRPClasses();
		AreaEntity.generateRPClass();
		if(!RPClass.hasRPClass("block")) {
			Block.generateRPClass();
		}
	}

}
