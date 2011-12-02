/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.update;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

/**
 * Tests for HttpClient
 *
 * @author hendrik
 */
public class HttpClientTest {

	/**
	 * Tests for fetchFirstLine
	 */
	@Test
	public void testFetchFirstLine() {
		HttpClient client = new HttpClient("http://arianne.sourceforge.net/");
//TODO: reactive me		assertThat(client.fetchFirstLine(), equalTo("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\"> "));

		client = new HttpClient("http://sf.net/projects/arianne/file-does-not-exist");
		assertThat(client.fetchFirstLine(), nullValue());

		client = new HttpClient("http://domain-does-not-exist");
		assertThat(client.fetchFirstLine(), nullValue());

		client = new HttpClient("http://sf.net:81/projects/arianne");
		assertThat(client.fetchFirstLine(), nullValue());
	}

	/**
	 * Tests for getInputStream
	 */
	@Test
	public void testGetInputStream() {
		HttpClient client = new HttpClient("http://sf.net/projects/arianne");
		InputStream is = client.getInputStream();
		assertThat(is, notNullValue());
		client.close();

		client = new HttpClient("http://sf.net/projects/arianne/file-does-not-exist");
		assertThat(client.getInputStream(), nullValue());
		client.close();

		client = new HttpClient("http://domain-does-not-exist");
		assertThat(client.getInputStream(), nullValue());

		client = new HttpClient("http://sf.net:81/projects/arianne");
		assertThat(client.getInputStream(), nullValue());
	}

	/**
	 * Tests for fetchFile
	 *
	 * @throws IOException in  case of an input/output error
	 */
	@Test
	public void testFetchFile() throws IOException {
		File file = File.createTempFile("test", ".txt");
		file.deleteOnExit();

		HttpClient client = new HttpClient("http://sf.net/projects/arianne");
		client.fetchFile(file.getAbsolutePath());
		assertTrue(file.length() > 100);
		if (!file.delete()) {
			System.err.println("file not deleted: " + file.getAbsolutePath());
		}

		file = File.createTempFile("test", ".txt");
		file.deleteOnExit();
		client = new HttpClient("http://sf.net/projects/arianne/file-does-not-exist");
		client.fetchFile(file.getAbsolutePath());
		assertEquals(0, file.length());

		client = new HttpClient("http://domain-does-not-exist");
		client.fetchFile(file.getAbsolutePath());
		assertEquals(0, file.length());

		client = new HttpClient("http://sf.net:81/projects/arianne");
		client.fetchFile(file.getAbsolutePath());
		assertEquals(0, file.length());

		client = new HttpClient("http://sourceforge.net/projects/arianne/files/z_old/stendhal-updates/stendhal-diff-0.75-0.75.1.jar/download");
		client.fetchFile(file.getAbsolutePath());
		assertTrue(file.length() > 10000);

		if (!file.delete()) {
			System.err.println("file not deleted: " + file.getAbsolutePath());
		}
	}
}
