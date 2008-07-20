package games.stendhal.server.core.reflectiondebugger;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;

import marauroa.common.Pair;

import org.apache.log4j.Logger;

/**
 * Lists the contents all fields of a class and its super classes
 *
 * @author hendrik
 */
public class FieldLister {
	private static final String NULL_STRING = "null";
	private static Logger logger = Logger.getLogger(FieldLister.class);

	private TreeMap<String, Pair<String, String>> fieldsTypesValues;
	private Object object = null;
	
	public FieldLister(Object object) {
		this.object = object;
	}

	private void list(Class<?> clazz) {
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			String name = field.getName();
			String type = field.getType().toString();
			String value = getValue(field);
			fieldsTypesValues.put(name, new Pair<String, String>(type, value));
		}
	}

	private String getValue(Field field) {
		Object objValue = null;
		try {
			objValue = field.get(object);
		} catch (IllegalArgumentException e) {
			logger.error(e, e);
		} catch (IllegalAccessException e) {
			logger.error(e, e);
		}
		String value = NULL_STRING;
		if (objValue != null) {
			value = objValue.toString();
		}
		return value;
	}

	public void scan() {
		fieldsTypesValues = new TreeMap<String, Pair<String, String>>();
		if (object == null) {
			return;
		}

		Class<?> clazz = object.getClass();
		do {
			list(clazz);
			clazz = clazz.getSuperclass();
		} while (clazz != null);
	}

	@SuppressWarnings("unchecked")
	public Map<String, Pair<String, String>> getResult() {
		return (Map<String, Pair<String, String>>) fieldsTypesValues.clone();
	}
}
