/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.script;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;

/**
 * Lists raid scripts.
 */
public class ListRaids extends ScriptImpl {
	private static Logger logger = Logger.getLogger(ListRaids.class);

	@Override
	public void execute(final Player admin, final List<String> args) {
		StringBuilder textToSend = new StringBuilder("Known RaidScripts:\n");
		try {
			ArrayList<Class<?>> dir = getClasses("games.stendhal.server.script");
			Collections.sort(dir, new Comparator<Class<?>>() {
				@Override
				public int compare(Class<?> o1, Class<?> o2) {
					return o1.getSimpleName().compareTo(o2.getSimpleName());
				}
			});

			for (final Class<?> clazz : dir) {
				// CreateRaid is abstract and useless for users by itself.
				if (CreateRaid.class.isAssignableFrom(clazz) && (CreateRaid.class != clazz)) {
					textToSend.append(clazz.getSimpleName()).append("\n");
				}
			}

		} catch (final ClassNotFoundException e) {
			logger.error(e, e);
		} catch (final SecurityException e) {
			logger.error(e, e);
		}
		admin.sendPrivateText(textToSend.toString());
	}

	/**
	 * Fetch classes of available scripts.
	 *
	 * @param pckgname the package name of scripts
	 * @return list of script classes
	 * @throws ClassNotFoundException if getting the class loader or reading the
	 * 	script resources fail
	 */
	private static ArrayList<Class<?>> getClasses(final String packageName) throws ClassNotFoundException {
		final ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		try {
			ClassLoader classLoader = ListRaids.class.getClassLoader();
			ImmutableSet<ClassInfo> infos = ClassPath.from(classLoader).getTopLevelClasses(packageName);
			for (ClassInfo info : infos) {
				classes.add(info.load());
			}
			return classes;
		} catch (IOException e) {
			throw new ClassNotFoundException("failed to list classes");
		}
	}
}
