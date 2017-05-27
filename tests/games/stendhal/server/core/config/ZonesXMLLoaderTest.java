/**
 *
 */
package games.stendhal.server.core.config;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.game.IRPZone;
import marauroa.server.game.db.DatabaseFactory;

/**
 * @author madmetzger
 *
 */
public class ZonesXMLLoaderTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		MockStendlRPWorld.get();
		new DatabaseFactory().initializeDatabase();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link games.stendhal.server.core.config.ZonesXMLLoader#readZone(org.w3c.dom.Element)}.
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws SAXException
	 */
	@Test
	public void testReadZone() throws URISyntaxException, SAXException, IOException {
		ZonesXMLLoader loader = new ZonesXMLLoader(new URI("testsemos.xml"));
		loader.load();
		StendhalRPWorld world = SingletonRepository.getRPWorld();
		Collection<StendhalRPZone> regionKanmararn = world.getAllZonesFromRegion("kanmararn", Boolean.TRUE, Boolean.FALSE, Boolean.TRUE);
		assertThat(regionKanmararn.isEmpty(), is(Boolean.FALSE));
		assertThat(regionKanmararn.size(), is(3));
		Collection<StendhalRPZone> regionWofolExteriors = world.getAllZonesFromRegion("wofol city", Boolean.TRUE, Boolean.FALSE, Boolean.TRUE);
		assertThat(regionWofolExteriors.isEmpty(), is(Boolean.TRUE));
		Collection<StendhalRPZone> regionWofolInteriors = world.getAllZonesFromRegion("wofol city", Boolean.FALSE, Boolean.FALSE, Boolean.TRUE);
		assertThat(regionWofolInteriors.isEmpty(), is(Boolean.FALSE));
		assertThat(regionWofolInteriors.size(), is(19));
		Collection<StendhalRPZone> regionSemosInteriors = world.getAllZonesFromRegion("semos", Boolean.FALSE, Boolean.FALSE, Boolean.TRUE);
		IRPZone rpZone = world.getRPZone("int_semos_house");
		assertThat(regionSemosInteriors.contains(rpZone), is(Boolean.FALSE));
	}

}
