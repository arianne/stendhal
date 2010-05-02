package games.stendhal.client.gui.login;

import static org.junit.Assert.assertThat;

import static org.hamcrest.CoreMatchers.*;
import org.junit.Test;

/**
 * Tests for Profile
 *
 * @author hendrik
 */
public class ProfileTest {

	/**
	 * Tests for createFromCommandline()
	 */
	@Test
	public void testCreateFromCommandline() {
		String[] args = new String[]{"-h", "host", "-P", "1", "-u", "user", "-c", "char", "-p", "password"};
		assertThat(Profile.createFromCommandline(args).toString(), equalTo("user/char@host:1"));
	}

	/**
	 * Tests for isValid
	 */
	@Test
	public void testIsValid() {
		String[] args = new String[]{"-h", "host", "-P", "1", "-u", "user", "-c", "char", "-p", "password"};
		assertThat(Boolean.valueOf(Profile.createFromCommandline(args).isValid()), equalTo(Boolean.TRUE));

		args = new String[]{"-P", "1", "-u", "user", "-c", "char", "-p", "password"};
		assertThat(Boolean.valueOf(Profile.createFromCommandline(args).isValid()), equalTo(Boolean.FALSE));
	}
}
