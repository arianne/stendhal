/**
 * 
 */
package utilities.RPClass;

import marauroa.common.game.RPClass;
import games.stendhal.server.entity.mapstuff.block.Block;

/**
 * 
 * @author madmetzger
 */
public class BlockTestHelper {
	
	public static void generateRPClasses() {
		EntityTestHelper.generateRPClasses();
		if(!RPClass.hasRPClass("block")) {
			Block.generateRPClass();
		}
	}

}
