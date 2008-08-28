package games.stendhal.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BadWordsStringFilter {

	private static final String QUESTION_MARK = "?";

	private static final String CLOSE_BRACKET = "]";

	private static final String OPEN_BRACKET = "[";
	
	private static final String WORD_BOUNDARY = "\\b";

	private final List<String> badWords;
	
	private final Map<String,List<String>> possibleLetterReplacements = buildReplacements();
	private final Set<String> possibleInterLetterFillings = buildInterLetterFillings();

	private String fillingString;

	public BadWordsStringFilter(List<String> badWords) {
		this.badWords = new LinkedList<String>();
		for(String word : badWords) {
			this.badWords.add(this.buildRegEx(word));
		}
	}

	private String buildRegEx(final String word) {
		StringBuilder sb = new StringBuilder();
		String lowerCaseWord = word.toLowerCase();
		sb.append(WORD_BOUNDARY);
		sb.append(this.getPossibleInterLetterFilling());
		for (int i = 0; i < word.length(); i++) {
			sb.append(OPEN_BRACKET);
			char currentChar = lowerCaseWord.charAt(i);
			sb.append(currentChar);
			if (this.possibleLetterReplacements.containsKey(Character.toString(currentChar))) {
				for (String replacer : this.possibleLetterReplacements.get(Character.toString(currentChar))) {
					sb.append(replacer);
				}
			}
			sb.append(CLOSE_BRACKET);
			if (i < word.length() - 1) {
				sb.append(this.getPossibleInterLetterFilling());
			}
		}
		sb.append(WORD_BOUNDARY);
		return sb.toString();
	}

	private String getPossibleInterLetterFilling() {
		if (this.fillingString == null) {
			StringBuilder sb = new StringBuilder();
			sb.append(OPEN_BRACKET);
			for (String filler : this.possibleInterLetterFillings) {
				sb.append(filler);
			}
			sb.append(CLOSE_BRACKET);
			sb.append(QUESTION_MARK);
			this.fillingString = sb.toString();
		}
		return this.fillingString;
	}

	private Set<String> buildInterLetterFillings() {
		Set<String> fillings = new HashSet<String>();
		fillings.add(".");
		fillings.add("_");
		fillings.add("-");
		fillings.add("#");
		return fillings;
	}

	private Map<String, List<String>> buildReplacements() {
		Map<String,List<String>> replacement = new HashMap<String, List<String>>();
		String[] aArray = {"4","@"};
		replacement.put("a", Arrays.asList(aArray));
		String[] iArray = {"1"};
        replacement.put("i", Arrays.asList(iArray));
        String[] sArray = {"5"};
		replacement.put("s", Arrays.asList(sArray));
		String[] eArray = {"3"};
		replacement.put("e", Arrays.asList(eArray));
		String[] lArray = {"7"};
		replacement.put("l", Arrays.asList(lArray));
		String [] bArray = {"8"};
		replacement.put("b", Arrays.asList(bArray));
		return replacement;
	}

	public boolean containsBadWord (final String text) {
		StringTokenizer st = new StringTokenizer(text);
		while (st.hasMoreTokens()) {
			if(this.isBadWord(st.nextToken())) {
				return true;
			}
		}
		return false;
	}

	public boolean isBadWord (final String word) {
		String lowerCaseWord = word.toLowerCase();
		for(String badWord:this.badWords) {
			Pattern p = Pattern.compile(badWord);
			Matcher m = p.matcher(lowerCaseWord);
			if (m.matches()) {
				return true;
			}
		}
		return false;
	}

	public String censorBadWords(final String text) {
		String returnString = text;
		for(String replacer : this.badWords) {
			returnString = returnString.replaceAll(replacer, "*CENSORED*");
		}
		return returnString;
	}

	public List<String> listBadWordsInText(String text) {
		List<String> returnList = new LinkedList<String>();
		StringTokenizer st = new StringTokenizer(text);
		while (st.hasMoreTokens()) {
			String word = st.nextToken();
			if(this.isBadWord(word)) {
				returnList.add(word);
			}
		}
		return returnList;
	}

}
