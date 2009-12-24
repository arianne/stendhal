package games.stendhal.common;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.matchers.JUnitMatchers.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class BadWordsStringFilterTest {
	
	private List<String> badWords;
	
	@Before
	public void setUpBadWords() {
		this.badWords = new LinkedList<String>();
		this.badWords.add("shit");
		this.badWords.add("asshole");
	}

	/**
	 * Tests for positiveDetectionOfBadWordInText.
	 */
	@Test
	public void testPositiveDetectionOfBadWordInText() {
		String badWord = "holy shit";
		BadWordsStringFilter checker = new BadWordsStringFilter(badWords);
		assertTrue(checker.containsBadWord(badWord));
	}
	
	/**
	 * Tests for negativeDetectionOfBadWordInContainedInAnotherWord.
	 */
	@Test
	public void testNegativeDetectionOfBadWordInContainedInAnotherWord() {
		String badWord = "bullshit adsfkassad";
		BadWordsStringFilter checker = new BadWordsStringFilter(badWords);
		assertFalse(checker.isBadWord(badWord));
	}
	
	/**
	 * Tests for positiveDetectionOfCamouflagedBadWordInText.
	 */
	@Test
	public void testPositiveDetectionOfCamouflagedBadWordInText() {
		String badWord = "holy sh1t";
		BadWordsStringFilter checker = new BadWordsStringFilter(badWords);
		assertTrue(checker.containsBadWord(badWord));
	}
	
	/**
	 * Tests for positiveDetectionOfCamouflagedBadWordInTextTwo.
	 */
	@Test
	public void testPositiveDetectionOfCamouflagedBadWordInTextTwo() {
		String badWord = "holy 5h1t";
		BadWordsStringFilter checker = new BadWordsStringFilter(badWords);
		assertTrue(checker.containsBadWord(badWord));
	}
	
	/**
	 * Tests for negativeDetectionOfGoodWordInText.
	 */
	@Test
	public void testNegativeDetectionOfGoodWordInText() {
		String badWord = "hello george";
		BadWordsStringFilter checker = new BadWordsStringFilter(badWords);
		assertFalse(checker.containsBadWord(badWord));
	}
	
	/**
	 * Tests for positiveDetectionOfBadWord.
	 */
	@Test
	public void testPositiveDetectionOfBadWord() {
		String badWord = "shit";
		BadWordsStringFilter checker = new BadWordsStringFilter(badWords);
		assertTrue(checker.isBadWord(badWord));
	}
	
	/**
	 * Tests for positiveDetectionOfBadWordTwo.
	 */
	@Test
	public void testPositiveDetectionOfBadWordTwo() {
		String badWord = "sh1t";
		BadWordsStringFilter checker = new BadWordsStringFilter(badWords);
		assertTrue(checker.isBadWord(badWord));
	}
	
	/**
	 * Tests for positiveDetectionOfBadWordThree.
	 */
	@Test
	public void testPositiveDetectionOfBadWordThree() {
		String badWord = "sh.i.t";
		BadWordsStringFilter checker = new BadWordsStringFilter(badWords);
		assertTrue(checker.isBadWord(badWord));
	}
	
	/**
	 * Tests for positiveDetectionOfBadWordFour.
	 */
	@Test
	public void testPositiveDetectionOfBadWordFour() {
		String badWord = "Shit";
		BadWordsStringFilter checker = new BadWordsStringFilter(badWords);
		assertTrue(checker.isBadWord(badWord));
	}
	
	/**
	 * Tests for negativeDetectionOfGoodWord.
	 */
	@Test
	public void testNegativeDetectionOfGoodWord() {
		String badWord = "george";
		BadWordsStringFilter checker = new BadWordsStringFilter(badWords);
		assertFalse(checker.isBadWord(badWord));
	}
	
	/**
	 * Tests for replacementOfBadWords.
	 */
	@Test
	public void testReplacementOfBadWords() {
		String mixedText = "George did some shit!";
		BadWordsStringFilter checker = new BadWordsStringFilter(badWords);
		assertThat(checker.censorBadWords(mixedText), equalTo("George did some *CENSORED*!"));
	}
	
	/**
	 * Tests for listOfBadWordsInAText.
	 */
	@Test
	public void testListOfBadWordsInAText() {
		String mixedText = "The asshole George did some shit";
		BadWordsStringFilter checker = new BadWordsStringFilter(badWords);
		List<String> listBadWordsInText = checker.listBadWordsInText(mixedText);
		assertThat(listBadWordsInText, hasItem("shit"));
		assertThat(listBadWordsInText, hasItem("asshole"));
	}
	
}
