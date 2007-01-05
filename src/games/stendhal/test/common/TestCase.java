package games.stendhal.test.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.log4j.Logger;

/**
 * A simple test case based on the ideas of JUnit. It is compatible
 * and can be replaced with the full featured on from JUnit later.
 *
 * @author hendrik
 */
public class TestCase {
	private int testCaseCounter = 0;
	private int errorCounter = 0;
	private static Logger logger = Logger.getLogger(TestCase.class);

	/**
	 * asserts that a condition is true
	 *
	 * @param text error message
	 * @param condition condition to check
	 */
	protected void assertTrue(String text, boolean condition) {
		if (!condition) {
			String msg = "assertTrue failed (" + text + "): " + condition;
			throw new AssertionError(msg);
		}
	}

	/**
	 * asserts that two objects are equal (please consider to provide an error message).
	 * 
	 * @param object1 first object
	 * @param object2 second object
	 */
	protected void assertEquals(Object object1, Object object2) {
		assertEquals(null, object1, object2);
	}

	/**
	 * asserts that two objects are equal (please consider to provide an error message).
	 *
	 * @param text error message
	 * @param object1 first object
	 * @param object2 second object
	 */
	protected void assertEquals(Object text, Object object1, Object object2) {
		boolean ok = false;
		if (object1 == null) {
			ok = (object2 == null);
		} else {
			ok = object1.equals(object2);
		}
		
		if (!ok) {
			String t = "";
			if (text != null) {
				 t = " (" + text + ")";
			}
			String msg = "assertEquals failed" + t + ": \"" + object1 + "\" \"" + object2 + "\"";
			throw new AssertionError(msg);
		}
	}

	/**
	 * is invoked before each test
	 */
	protected void setUp() {
		// do nothing, can be overriden by subclases
	}

	/**
	 * is invoked after each test
	 */
	protected void tearDown() {
		// do nothing, can be overriden by subclases
	}

	/**
	 * Executes a testXXX method
	 *
	 * @param testCase test case class
	 * @param method Method name
	 */
	public void runTestMethod(TestCase testCase, Method method) {
		testCaseCounter++;

		// setup
		try {
			testCase.setUp();
		} catch (RuntimeException e) {
			logger.error(testCase.getClass().getName() + ".setup()", e);
			errorCounter++;
			return;
		}

		// test case
		Object[] args = new Object[0];
		try {
			method.invoke(testCase, args);
		} catch (IllegalArgumentException e) {
			errorCounter++;
			logger.error(e, e);
		} catch (IllegalAccessException e) {
			errorCounter++;
			logger.error(e, e);
		} catch (InvocationTargetException e) {
			errorCounter++;
			Throwable cause = e.getCause();
			System.err.println(testCase.getClass().getName() + "." + method.getName() + "()");
			cause.printStackTrace();
		}

		// tearDown
		try {
			testCase.tearDown();
		} catch (RuntimeException e) {
			logger.error(testCase.getClass().getName() + ".tearDown()", e);
			errorCounter++;
			return;
		}
	}

	/**
	 * Executes a test case
	 *
	 * @param clazz test case class
	 */
	public void runTestCase(Class<? extends TestCase> clazz) {
		
		try {
			Method[] methods = clazz.getMethods();
			for (Method method : methods) {
				if (method.getName().startsWith("test") && (method.getParameterTypes().length == 0)) {
					TestCase testCase = clazz.newInstance();
					runTestMethod(testCase, method);
				}
			}

		} catch (Exception e) {
			logger.error(e, e);
		}
		
		System.err.println();
		System.err.println();
		System.err.println("Tests: " + testCaseCounter + "  Errors: " + errorCounter);
		System.err.println();
		if (errorCounter > 0) {
			System.err.println("FFFFFF    AA       II    LL       EEEEEE    DDDD  ");
			System.err.println("FF       AAAA      II    LL       EE        DD  DD");
			System.err.println("FFFF    AA  AA     II    LL       EEEE      DD  DD");
			System.err.println("FF      AAAAAA     II    LL       EE        DD  DD");
			System.err.println("FF      AA  AA     II    LLLLLL   EEEEEE    DDDD  ");
		} else {
			System.err.println("PASSED");
		}
	}
}
