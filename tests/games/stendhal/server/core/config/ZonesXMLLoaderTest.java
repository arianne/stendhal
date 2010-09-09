/**
 * 
 */
package games.stendhal.server.core.config;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.maps.MockStendlRPWorld;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import static org.hamcrest.CoreMatchers.is;

import static org.junit.Assert.assertThat;

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
		Collection<StendhalRPZone> region = world.getAllZonesFromRegion("kanmararn", Boolean.TRUE, Boolean.FALSE, Boolean.TRUE);
		assertThat(region.isEmpty(), is(Boolean.FALSE));
	}

}
