package games.stendhal.server.util;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

public class StringUtilsTest {

	@Test
	public void testCountUpperCase() {
		assertThat(StringUtils.countUpperCase(""), equalTo(0));
		assertThat(StringUtils.countUpperCase("1"), equalTo(0));
		assertThat(StringUtils.countUpperCase("**"), equalTo(0));
		assertThat(StringUtils.countUpperCase("a"), equalTo(0));
		assertThat(StringUtils.countUpperCase("**A*B*"), equalTo(2));
	}

	@Test
	public void testCountLowerCase() {
		assertThat(StringUtils.countLowerCase(""), equalTo(0));
		assertThat(StringUtils.countLowerCase("1"), equalTo(0));
		assertThat(StringUtils.countLowerCase("**"), equalTo(0));
		assertThat(StringUtils.countLowerCase("A"), equalTo(0));
		assertThat(StringUtils.countLowerCase("**a*b*"), equalTo(2));
	}

}
