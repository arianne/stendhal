/**
 *
 */
package utilities.RPClass;

import games.stendhal.server.entity.mapstuff.area.AreaEntity;
import games.stendhal.server.entity.mapstuff.block.Block;
import games.stendhal.server.entity.mapstuff.block.BlockTarget;
import games.stendhal.server.entity.mapstuff.block.BlockTest;
import marauroa.common.game.RPClass;

/**
 * Test helper for the {@link BlockTest} to generate the necessary RPClasses
 *
 * @author madmetzger
 */
public class BlockTestHelper {

	public static void generateRPClasses() {
		EntityTestHelper.generateRPClasses();
		if(!RPClass.hasRPClass("area")) {
			AreaEntity.generateRPClass();
		}
		if(!RPClass.hasRPClass("block")) {
			Block.generateRPClass();
		}
		if(!RPClass.hasRPClass("blocktarget")) {
			BlockTarget.generateRPClass();
		}
	}

}
