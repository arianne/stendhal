package games.stendhal.common;

import static org.junit.Assert.*;

import org.junit.Test;

public class Base64Test {

	@Test
	public void testEncode() {
		assertEquals("", new String(Base64.encode("".getBytes())));
		assertEquals("YQ==", new String(Base64.encode("a".getBytes())));
		assertEquals("YWI=", new String(Base64.encode("ab".getBytes())));
		assertEquals("YWJj", new String(Base64.encode("abc".getBytes())));
		assertEquals("YWJjZA==", new String(Base64.encode("abcd".getBytes())));
	}


	@Test
	public void testDencode() {
		assertEquals("", new String(Base64.decode("".toCharArray())));
		assertEquals("a", new String(Base64.decode("YQ==".toCharArray())));
		assertEquals("ab", new String(Base64.decode("YWI=".toCharArray())));
		assertEquals("abc", new String(Base64.decode("YWJj".toCharArray())));
		assertEquals("abcd", new String(Base64.decode("YWJjZA==".toCharArray())));
	}
}
