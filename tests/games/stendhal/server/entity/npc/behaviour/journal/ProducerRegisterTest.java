package games.stendhal.server.entity.npc.behaviour.journal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.behaviour.impl.ProducerBehaviour;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.ados.goldsmith.GoldsmithNPC;

import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;


public class ProducerRegisterTest {

	/**
	 * Tests for get.
	 */
	@Test
	public final void testGet() {
		final ProducerRegister producerRegister = new ProducerRegister() {
		};
		assertSame(producerRegister, SingletonRepository.getProducerRegister());
	}
	
	/**
	 * Tests for add.
	 */
	@Test
	public final void testAdd() {
		
		final ProducerRegister producerRegister = new ProducerRegister() {
		};
		// check first that it is empty
		assertTrue(producerRegister.getProducers().isEmpty());
		
		final Map<String, Integer> requiredResources = new TreeMap<String, Integer>();
		requiredResources.put("flour", 1);
		requiredResources.put("cod", 2);
		requiredResources.put("mackerel", 1);
		requiredResources.put("leek", 1);
		final ProducerBehaviour behaviour = new ProducerBehaviour("linzo_make_fish_pie", "make", "fish pie",
		        requiredResources, 5 * 60);
		
		producerRegister.add("Linzo", behaviour);
		
		assertFalse(producerRegister.getProducers().isEmpty());
		assertTrue(producerRegister.getProducers().size()==1);
	}

	/**
	 * Tests for adding from the producer adder method
	 */
	@Test
	public final void testAddFromProducerAdder() {
		final ProducerRegister producerRegister = new ProducerRegister() {
		};
		assertTrue(producerRegister.getProducers().isEmpty());
		
		MockStendlRPWorld.get();
		
		final StendhalRPZone zone = new StendhalRPZone("admin_test");

		// call NPC code which will make ProducerAdder add to register
		new GoldsmithNPC().configureZone(zone, null);

		assertFalse(producerRegister.getProducers().isEmpty());
	}
}
