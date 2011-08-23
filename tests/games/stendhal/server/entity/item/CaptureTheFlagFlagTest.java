/*
 * CaptureTheFlagFlagTest.java
 */
package games.stendhal.server.entity.item;


import marauroa.common.Log4J;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.RPClass.ItemTestHelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.RPEntity;

import games.stendhal.server.entity.item.CaptureTheFlagFlag;
import games.stendhal.server.entity.item.Corpse;

import games.stendhal.server.entity.player.Player;

import games.stendhal.server.entity.slot.EntitySlot;

// import games.stendhal.server.actions.equip.SourceObject;

import games.stendhal.server.maps.MockStendlRPWorld;

import marauroa.common.game.SlotOwner;



public class CaptureTheFlagFlagTest {

	/**
	 * TODO: move this in to test utils
	 */
	class TestEntity extends RPEntity {
		public String name;
		public TestEntity(String name) {
			this.name = name;
			this.setOutfit(Outfit.getRandomOutfit());
		}
		public TestEntity() {
			this("NoName");
		}

		public void dropItemsOn(Corpse corpse) {}
		public void logic() {}
	}

	/*
	 * XXX hack, until i can figure out how to have item.getContainerOwner()
	 *     to work normally (need slots, ...)
	 *
	 */
	class CheatingOwnerCaptureTheFlagFlag extends CaptureTheFlagFlag {

		RPEntity owner = null;
		
		@Override
		public boolean onEquipped(RPEntity equipper, String slot) {
			boolean result = super.onEquipped(equipper, slot);
			if (!result) {
				return result;
			}
			this.owner = equipper;
			return result;
		}
		
		public SlotOwner getContainerOwner() {
			return owner;
		}
		
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Log4J.init();
		MockStendlRPWorld.get();
		ItemTestHelper.generateRPClasses();
	}


	@Test
	public void testOnEquipped() {

		RPEntity           player = new TestEntity();
		// CaptureTheFlagFlag flag   = new CaptureTheFlagFlag();
		CaptureTheFlagFlag flag   = new CheatingOwnerCaptureTheFlagFlag();
		boolean            result;
		
		// note: not taking the complete, correct, path through code to put the item in player's equipment
		//     DestinationObject does a lot of work to find slot in container, ...
		//     - flag/item cannot check that they are really owned by the owner, ...
		//
		// also note that we are not able to test that flag cannot be put in 
		// bag.  change occurs when flag is equipped *anywhere*.
		// in the game, there are restrictions - can only equip to hands 
		// 
		
		int origDetail     = player.getOutfit().getDetail();
		int flagColorValue = flag.getColorValue();
		
		assertEquals(origDetail, (int) player.getOutfit().getDetail());
		
		result = flag.onEquipped(player, "lhand");

		assertEquals(true, result);
		
		// confirm change to outfit 		
		assertEquals(flag.getColorValue(), (int) player.getOutfit().getDetail());

		
	}
	

	@Test
	public void testOnUnequipped() {

		RPEntity           player   = new TestEntity();
		// RPEntity           entity   = new TestEntity();
		// Player             player   = new Player(entity);
		// CaptureTheFlagFlag flag     = new CaptureTheFlagFlag();
		CaptureTheFlagFlag flag   = new CheatingOwnerCaptureTheFlagFlag();
		String             slot     = "lhand";
		boolean            result;
		
		// note: not taking the complete, correct, path through code to put the item in player's equipment
		//     DestinationObject does a lot of work to find slot in container, ...
		//     - flag/item cannot check that they are really owned by the owner, ...
		//
		// also note that we are not able to test that flag cannot be put in 
		// bag.  change occurs when flag is equipped *anywhere*.
		// in the game, there are restrictions - can only equip to hands 
		// 
		
		int origDetail     = player.getOutfit().getDetail();
		int flagColorValue = flag.getColorValue();
		
		// System.out.println("  slot: " + player.getSlot(slot));
		// RPSlot rpslot = ((EntitySlot) player.getSlot(slot)).getWriteableSlot();
		
		// unequipping from a player that never owned the item should
		// not cause problems
		result = flag.onUnequipped();
		assertEquals(false, result);
		
		result = flag.onEquipped(player, slot);

		assertEquals(true, result);
		
		// confirm change to outfit 		
		assertEquals(flag.getColorValue(), (int) player.getOutfit().getDetail());

		// flag.removeFromWorld();
		flag.onUnequipped();
		// flag.onUnequipped(player, slot, true);

		// XXX this is failing, because flag.getContainerOwner() 
		//     is returning null - we did not 
		//     properly transfer to container
		
		// confirm back to original value
		assertEquals(origDetail, (int) player.getOutfit().getDetail());
		
	}
		
	
	/**
	 * a little bit bigger than a unit test, but this is what's
	 * important to test ...
	 */
	@Test
	public void test_transferBetweenPlayers() {
	
		RPEntity           player1 = new TestEntity("Player1");
		RPEntity           player2 = new TestEntity("Player2");
		// CaptureTheFlagFlag flag    = new CaptureTheFlagFlag();
		CaptureTheFlagFlag flag   = new CheatingOwnerCaptureTheFlagFlag();		
		String             slot     = "lhand";
		boolean            result;
		
		// note: not taking the complete, correct, path through code to put the item in player's equipment
		//     DestinationObject does a lot of work to find slot in container, ...
		//     - flag/item cannot check that they are really owned by the owner, ...
		//
		// also note that we are not able to test that flag cannot be put in 
		// bag.  change occurs when flag is equipped *anywhere*.
		// in the game, there are restrictions - can only equip to hands 
		// 
		
		int origDetail     = player1.getOutfit().getDetail();
		int flagColorValue = flag.getColorValue();
		
		// System.out.println("  slot: " + player.getSlot(slot));
		// RPSlot rpslot = ((EntitySlot) player.getSlot(slot)).getWriteableSlot();
		
		result = flag.onEquipped(player1, slot);
		assertEquals(true, result);
		
		// confirm change to outfit 		
		assertEquals(flag.getColorValue(), (int) player1.getOutfit().getDetail());

		// flag.removeFromWorld();
		flag.onUnequipped();

		// confirm player1 back to original value
		assertEquals(origDetail, (int) player1.getOutfit().getDetail());
			
		// TODO: confirm player2 outfit changed
		// TODO: confirm player2 outfit back to default

		result = flag.onEquipped(player2, slot);
		assertEquals(true, result);
		
		// confirm change to outfit 		
		assertEquals(flag.getColorValue(), (int) player2.getOutfit().getDetail());

		// flag.removeFromWorld();
		flag.onUnequipped();

		// confirm player2 back to original value
		assertEquals(origDetail, (int) player2.getOutfit().getDetail());
		
		

	}
	
	
}
