/*
 * GameConsoleAppender.java
 *
 * Created on 1. August 2005, 21:12
 *
 */

package games.stendhal.client;

import java.awt.Color;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

/**
 * Log4J appender which logs to the game console
 * 
 * @author Matthias Totz
 */
public class GameConsoleAppender extends AppenderSkeleton {
	protected void append(LoggingEvent loggingEvent) {
		StringBuilder buf = new StringBuilder();
		buf.append(getLayout().format(loggingEvent));
		ThrowableInformation ti = loggingEvent.getThrowableInformation();

		if (ti != null) {
			String cause[] = ti.getThrowableStrRep();

			for (String line : cause) {
				buf.append(line).append('\n');
			}
		}

		StendhalClient.get().addEventLine(buf.toString(), Color.GRAY);
	}

	public void close() {
	}

	public boolean requiresLayout() {
		return true;
	}

}
