package games.stendhal.server.entity.trade;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


public class EarningTest {

	@Test
	public void testEqualsAndHashCode() {
		Earning e1 = new Earning(Integer.valueOf(1),"bob", true);
		Earning e2 = new Earning(Integer.valueOf(1),"bob", true);
		assertThat(e1,is(e2));
		assertThat(e1.hashCode(), is(e2.hashCode()));
	}

}
