package games.stendhal.client.gui.j2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.junit.BeforeClass;
import org.junit.Test;
/**
 * Justs tests that various inputs don't throw exceptions. Not that the Bubble would look correct.
 */
public class TextBoxFactoryTest {
	private static TextBoxFactory factory;
	
	@BeforeClass
	public static void setup() {
		BufferedImage junk = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		factory = new TextBoxFactory((Graphics2D) junk.getGraphics());
	}
	
	/**
	 * Short messages
	 */
	@Test
	public void basic() {
		factory.createTextBox("short", 240, Color.black, Color.black, false);
		factory.createTextBox("dumdidum blah", 240, Color.black, Color.black, false);
	}
	
	/**
	 * Basic long word tests
	 */
	@Test
	public void longWords() {
		factory.createTextBox("someridiculouslylongsentencethatisnotproperlysplittowordsbutthetokenizershouldnotchrashanyway", 
				240, Color.black, Color.black, false);
		factory.createTextBox("prefix someridiculouslylongsentencethatisnotproperlysplittowordsbutthetokenizershouldnotchrashanyway", 
				240, Color.black, Color.black, false);
		factory.createTextBox("someridiculouslylongsentencethatisnotproperlysplittowordsbutthetokenizershouldnotchrashanyway postfix", 
				240, Color.black, Color.black, false);
		factory.createTextBox("prefix someridiculouslylongsentencethatisnotproperlysplittowordsbutthetokenizershouldnotchrashanyway postfix", 
				240, Color.black, Color.black, false);
	}
	
	/**
	 * /listquest outputs that have at least at some point of time crashed the tokenizer
	 */
	@Test
	public void listQuestCrashes() {
		String msg = "[01:30] Open Quests: DailyMonsterQuestMeetHackimSevenCherubsStuffForVulcanusSuntanCreamForZaraToysCollector\nCompleted Quests: ArmorForDagobertCampfireHerbsForCarmenIntroducePlayersLearnAboutKarmaLearnAboutOrbsMeetHayunnMeetIoPlinksToyReverseArrowZooFood";
		factory.createTextBox(msg, 240, Color.black, Color.black, false);
	}
}
