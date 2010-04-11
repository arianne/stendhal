package games.stendhal.server.entity.trade;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * test class for earnings entity
 *  
 * @author madmetzger
 */
public class EarningTest {

	@Test
	public void testEqualsAndHashCode() {
		Earning e1 = new Earning(Integer.valueOf(1),"bob", true);
		Earning e2 = new Earning(Integer.valueOf(1),"bob", true);
		e1.put("timestamp", 0);
		e2.put("timestamp", 0);
		assertThat(e1,is(e2));
		assertThat(e1.hashCode(), is(e2.hashCode()));
	}
	
	@Test
	public void testSellername() {
		Earning e1 = new Earning(Integer.valueOf(1),"bob", true);
		Earning e2 = new Earning(Integer.valueOf(1),"bob", true);
		assertThat(e1.getSeller(),is("bob"));
		assertThat(e2.getSeller(),is("bob"));
	}

	@Test
	public void testValue() {
		Earning e1 = new Earning(Integer.valueOf(1),"bob", true);
		Earning e2 = new Earning(Integer.valueOf(1),"bob", true);
		assertThat(e1.getValue(),is(Integer.valueOf(1)));
		assertThat(e2.getValue(),is(Integer.valueOf(1)));
	}
}
