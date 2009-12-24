package games.stendhal.client.gui.bag;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import static org.junit.Assert.*;

import java.awt.event.MouseEvent;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class PopupMenuListenerTest {
	
	private final class PopupMenuListenerExtension extends PopupMenuListener {
		@Override
		protected void createAndShowPopup(final MouseEvent e) {
			called = true;
			
		}
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		called = false;
	}

	@After
	public void tearDown() throws Exception {
	}

	
	boolean called;
	/**
	 * Tests for mousePressedMouseEvent.
	 */
	@Test
	public final void testMousePressedMouseEvent() {
		
		final PopupMenuListener  listener = new PopupMenuListenerExtension();
		final MouseEvent e = createMock(MouseEvent.class);
		expect(e.isPopupTrigger()).andReturn(false);
		replay(e);
		listener.mousePressed(e);
		verify(e);
		assertFalse(called);
	}
	
	/**
	 * Tests for mousePressedMouseEventIsTrigger.
	 */
	@Test
	public final void testMousePressedMouseEventIsTrigger() {
		final PopupMenuListener  listener = new PopupMenuListenerExtension();
		final MouseEvent e = createMock(MouseEvent.class);
		expect(e.isPopupTrigger()).andReturn(true);
		replay(e);
		listener.mousePressed(e);
		verify(e);
		assertTrue(called);
	}

	/**
	 * Tests for mouseReleasedMouseEvent.
	 */
	@Test
	public final void testMouseReleasedMouseEvent() {
		final PopupMenuListener  listener = new PopupMenuListenerExtension();
		final MouseEvent e = createMock(MouseEvent.class);
		expect(e.isPopupTrigger()).andReturn(false);
		replay(e);
		listener.mouseReleased(e);
		verify(e);
		assertFalse(called);

	}

	/**
	 * Tests for mouseReleasedMouseEventIsTrigger.
	 */
	@Test
	public final void testMouseReleasedMouseEventIsTrigger() {
		final PopupMenuListener  listener = new PopupMenuListenerExtension();
		final MouseEvent e = createMock(MouseEvent.class);
		expect(e.isPopupTrigger()).andReturn(true);
		replay(e);
		listener.mouseReleased(e);
		verify(e);
		assertTrue(called);

	}


}
