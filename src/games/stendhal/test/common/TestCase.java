package games.stendhal.test.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.log4j.Logger;


public class TestCase {
	private int testCaseCounter = 0;
	private int errorCounter = 0;
	private static Logger logger = Logger.getLogger(TestCase.class);

	protected void assertTrue(String text, boolean condition) {
		if (!condition) {
			String msg = "asertTrue failed (" + text + "): " + condition;
			throw new AssertionError(msg);
		}
	}

	protected void assertEquals(String text, String string1, String string2) {
		boolean ok = false;
		if (string1 == null) {
			ok = (string2 == null);
		} else {
			ok = string1.equals(string2);
		}
		
		if (!ok) {
			String msg = "asertEquals failed (" + text + "): \"" + string1 + "\" \"" + string2 + "\"";
			throw new AssertionError(msg);
		}
	}

	protected void setUp() {
		// do nothing, can be overriden by subclases
	}

	protected void tearDown() {
		// do nothing, can be overriden by subclases
	}
	
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
