/*
 * GameConsoleAppender.java
 *
 * Created on 1. August 2005, 21:12
 *
 */

package games.stendhal.client;

import games.stendhal.client.gui.j2DClient;
import games.stendhal.client.gui.chatlog.HeaderLessEventLine;
import games.stendhal.common.NotificationType;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

/**
 * Log4J appender which logs to the game console.
 * 
 * @author Matthias Totz
 */
public class GameConsoleAppender extends AppenderSkeleton {

	@Override
	protected void append(final LoggingEvent loggingEvent) {
		final StringBuilder buf = new StringBuilder();
		buf.append(getLayout().format(loggingEvent));
		final ThrowableInformation ti = loggingEvent.getThrowableInformation();

		if (ti != null) {
			final String[] cause = ti.getThrowableStrRep();

			for (final String line : cause) {
				buf.append(line).append('\n');
			}
		}

		j2DClient.get().addEventLine(new HeaderLessEventLine(buf.toString(), NotificationType.CLIENT));
	}

	public void close() {
		// implementation of abstract method
		// yet nothing do to
	}

	public boolean requiresLayout() {
		return true;
	}

}
