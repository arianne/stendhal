package games.stendhal.client.gui.login;

import static org.junit.Assert.assertThat;

import org.hamcrest.CoreMatchers;
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
		assertThat(Profile.createFromCommandline(args).toString(), CoreMatchers.equalTo("user/char@host:1"));
	}
}
