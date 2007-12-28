/**
 * 
 */
package tiled.mapeditor.util;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;

import tiled.mapeditor.MapEditor;

/**
 * Keeps track of all the actions.
 * 
 * @author mtotz
 */
public class ActionManager {
	private Map<Class< ? extends Action>, Map<Params, Action>> actionMap;
	private MapEditor mapEditor;

	/**
	 */
	public ActionManager(MapEditor mapEditor) {
		actionMap = new HashMap<Class< ? extends Action>, Map<Params, Action>>();
		this.mapEditor = mapEditor;
	}

	/** returns the appropriate action. creates the action if necessary. */
	public Action getAction(Class< ? extends Action> clazz, Object... params) {
		Map<Params, Action> paramsMap = actionMap.get(clazz);
		if (paramsMap == null) {
			paramsMap = new HashMap<Params, Action>();
			actionMap.put(clazz, paramsMap);
		}

		Params p = new Params(params);
		Action action = paramsMap.get(p);
		if (action == null) {
			action = createAction(clazz, params);
			paramsMap.put(p, action);
		}

		return action;
	}

	/**
	 * @param clazz
	 * @param params
	 * @return
	 */
	private Action createAction(Class< ? extends Action> clazz, Object[] params) {
		try {
			Constructor< ? extends Action> c;
			Object[] paramsMap;
			if (params != null && params.length > 0) {
				c = clazz.getConstructor(new Class< ? >[] { MapEditor.class, Object[].class });
				paramsMap = new Object[] { mapEditor, params };
			} else {
				c = clazz.getConstructor(new Class< ? >[] { MapEditor.class });
				paramsMap = new Object[] { mapEditor };
			}

			return c.newInstance(paramsMap);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static class Params {
		Object[] params;

		/**
		 * @param params2
		 */
		public Params(Object[] params) {
			this.params = params;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Params) {
				return Arrays.equals(params, ((Params) obj).params);
			}
			return false;
		}

		@Override
		public int hashCode() {
			int hash = super.hashCode();
			for (Object param : params) {
				hash += param.hashCode();
			}
			return hash;
		}
	}
}
