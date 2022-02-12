/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2013 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.status;


import org.apache.log4j.Logger;

public class StatusAttackerFactory {
	private static Logger logger = Logger.getLogger(StatusAttackerFactory.class);

	public static StatusAttacker get(final String profile) {
		if (profile != null) {
            final String[] statusParams = profile.split(",");
            final String statusName = statusParams[0];
            final double probability = Double.parseDouble(statusParams[1]);

            String className = "games.stendhal.server.entity.status." + statusName;

            Status status;
            try {
                status = (Status) Class.forName(className).getDeclaredConstructor().newInstance();
                return new StatusAttacker(status, probability);
            } catch (InstantiationException e) {
            	logger.error(e, e);
            } catch (IllegalAccessException e) {
            	logger.error(e, e);
            } catch (ClassNotFoundException e) {
                logger.error(e, e);
            } catch (final NoSuchMethodException e) {
                logger.error(e, e);
            } catch (final java.lang.reflect.InvocationTargetException e) {
                logger.error(e, e);
            }
            return null;
		}
		return null;
	}
}
