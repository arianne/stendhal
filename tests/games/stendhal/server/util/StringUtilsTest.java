package games.stendhal.server.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * Tests for StringUtils
 *
 * @author hendrik
 */
public class StringUtilsTest {

	/**
	 * Tests for countUpperCase.
	 */
	@Test
	public void testCountUpperCase() {
		assertThat(StringUtils.countUpperCase(""), equalTo(0));
		assertThat(StringUtils.countUpperCase("1"), equalTo(0));
		assertThat(StringUtils.countUpperCase("**"), equalTo(0));
		assertThat(StringUtils.countUpperCase("a"), equalTo(0));
		assertThat(StringUtils.countUpperCase("**A*B*"), equalTo(2));
	}

	/**
	 * Tests for countLowerCase.
	 */
	@Test
	public void testCountLowerCase() {
		assertThat(StringUtils.countLowerCase(""), equalTo(0));
		assertThat(StringUtils.countLowerCase("1"), equalTo(0));
		assertThat(StringUtils.countLowerCase("**"), equalTo(0));
		assertThat(StringUtils.countLowerCase("A"), equalTo(0));
		assertThat(StringUtils.countLowerCase("**a*b*"), equalTo(2));
	}

	/**
	 * Tests for subst
	 *
	 * @throws SQLException 
	 */
	@Test
	public void testSubst() throws SQLException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("o", "0");
		params.put("p", "0, 1");
		params.put("x", "0, y, 1");

		assertThat(StringUtils.subst("", null), equalTo(""));
		assertThat(StringUtils.subst("Hallo", null), equalTo("Hallo"));
		assertThat(StringUtils.subst("Hall[o", params), equalTo("Hall0"));
		assertThat(StringUtils.subst("Hall[o]", params), equalTo("Hall0"));
		assertThat(StringUtils.subst("[o]Hall[o]", params), equalTo("0Hall0"));
		assertThat(StringUtils.subst("Hal[l]o", params), equalTo("Halo"));
		assertThat(StringUtils.subst("id IN ([o])", params), equalTo("id IN (0)"));
		assertThat(StringUtils.subst("id IN ([p])", params), equalTo("id IN (0, 1)"));

		assertThat(StringUtils.subst("id = '[x]'", params), equalTo("id = '0, y, 1'"));
	}
}
