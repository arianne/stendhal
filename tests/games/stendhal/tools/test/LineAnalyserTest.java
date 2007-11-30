package games.stendhal.tools.test;

import org.junit.Assert;
import org.junit.Test;

public class LineAnalyserTest {

	@Test
	public void testEmpty() {
		LineAnalyser analyser = new LineAnalyser("");
		Assert.assertEquals("", analyser.getLine());
		Assert.assertEquals("", analyser.getStripped());
		Assert.assertNull(analyser.getProtagonist());
		Assert.assertEquals("", analyser.getText());
		Assert.assertTrue(analyser.isEmpty());
		Assert.assertFalse(analyser.isNPCSpeaking());
		Assert.assertFalse(analyser.isPlayerSpeaking());
		Assert.assertFalse(analyser.isStatus());
	}

	@Test
	public void testSpace() {
		LineAnalyser analyser = new LineAnalyser(" ");
		Assert.assertEquals(" ", analyser.getLine());
		Assert.assertEquals("", analyser.getStripped());
		Assert.assertNull(analyser.getProtagonist());
		Assert.assertEquals("", analyser.getText());
		Assert.assertTrue(analyser.isEmpty());
		Assert.assertFalse(analyser.isNPCSpeaking());
		Assert.assertFalse(analyser.isPlayerSpeaking());
		Assert.assertFalse(analyser.isStatus());
	}

	@Test
	public void testPlayer() {
		LineAnalyser analyser = new LineAnalyser("[21:24] <player> hi");
		Assert.assertEquals("[21:24] <player> hi", analyser.getLine());
		Assert.assertEquals("<player> hi", analyser.getStripped());
		Assert.assertEquals("player", analyser.getProtagonist());
		Assert.assertEquals("hi", analyser.getText());
		Assert.assertFalse(analyser.isEmpty());
		Assert.assertFalse(analyser.isNPCSpeaking());
		Assert.assertTrue(analyser.isPlayerSpeaking());
		Assert.assertFalse(analyser.isStatus());
	}

	@Test
	public void testNPC() {
		LineAnalyser analyser = new LineAnalyser("[21:24] <Plink> *cries* There were wolves in the park! *sniff* I ran away, but I dropped my teddy! Please will you get it for me? *sniff* Please?");
		Assert.assertEquals("[21:24] <Plink> *cries* There were wolves in the park! *sniff* I ran away, but I dropped my teddy! Please will you get it for me? *sniff* Please?", analyser.getLine());
		Assert.assertEquals("<Plink> *cries* There were wolves in the park! *sniff* I ran away, but I dropped my teddy! Please will you get it for me? *sniff* Please?", analyser.getStripped());
		Assert.assertEquals("Plink", analyser.getProtagonist());
		Assert.assertEquals("*cries* There were wolves in the park! *sniff* I ran away, but I dropped my teddy! Please will you get it for me? *sniff* Please?", analyser.getText());
		Assert.assertFalse(analyser.isEmpty());
		Assert.assertTrue(analyser.isNPCSpeaking());
		Assert.assertFalse(analyser.isPlayerSpeaking());
		Assert.assertFalse(analyser.isStatus());
	}

	@Test
	public void testStatus() {
		LineAnalyser analyser = new LineAnalyser("[21:25] player earns 10 experience points.");
		Assert.assertEquals("[21:25] player earns 10 experience points.", analyser.getLine());
		Assert.assertEquals("player earns 10 experience points.", analyser.getStripped());
		Assert.assertNull(analyser.getProtagonist());
		Assert.assertEquals("player earns 10 experience points.", analyser.getText());
		Assert.assertFalse(analyser.isEmpty());
		Assert.assertFalse(analyser.isNPCSpeaking());
		Assert.assertFalse(analyser.isPlayerSpeaking());
		Assert.assertTrue(analyser.isStatus());
	}
}
