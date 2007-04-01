package games.stendhal.client.entity;

import org.junit.Test;

import marauroa.common.game.RPObject;



public class ResizeableCreatureTest {
@Test 
public final void onChangedAddedTest(){

	RPObject rp = new RPObject();
	rp.put("type", 1);
	rp.put("class", 1);
	rp.put("name", "hugo");
	rp.put("width", 1);
	rp.put("height", 1);
	
	new ResizeableCreature(rp);
	

}
}