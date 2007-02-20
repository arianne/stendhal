package games.stendhal.client.sound;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SoundEffectMapTest {
	SoundEffectMap sem ;
	SoundEffectMap sem2;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
	
	
	@Before
	public final void setup(){
		 sem = SoundEffectMap.getInstance();
		 sem2 = SoundEffectMap.getInstance();
		
	
	}
	@After
	public final void tearDown(){
		sem=null;
		sem2=null;
	}

	@Test
	public final void testGetInstance() {
	
			assertEquals("Singleton instance is equal",sem, sem2);
	}

	@Test
	public final void testGetByName() {
		String key1= "testGetByNameStringvalue";
		String value="testGetByName";
		SoundEffectMap.getInstance().put(key1, value);
		assertEquals("stringValue",value,SoundEffectMap.getInstance().getByName(key1));
		String key= "testPutStringClipRunner";
		ClipRunner cvalue= new ClipRunner("value");
		SoundEffectMap.getInstance().put(key, cvalue);
		assertEquals("ClipRunnerValue",cvalue,SoundEffectMap.getInstance().getByName(key));
	}



	@Test
	public final void testPutStringString() {
		String key1= "testPutStringString";
		String value="value";
		SoundEffectMap.getInstance().put(key1, value);
		assertTrue(SoundEffectMap.getInstance().containsKey(key1));

		}

	@Test
	public final void testPutStringClipRunner() {
		String key= "testPutStringClipRunner";
		ClipRunner value= new ClipRunner("value");
		SoundEffectMap.getInstance().put(key, value);
		assertTrue(SoundEffectMap.getInstance().containsKey(key));
	}

	@Test
	public final void testSize() {
		int size = SoundEffectMap.getInstance().size();
		SoundEffectMap.getInstance().put("empty", "");
		assertEquals("should be grown by own after adding a new one",size + 1, SoundEffectMap.getInstance().size());
	}

}
