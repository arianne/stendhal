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

/**
 * Log4J appender which logs to the game console
 * @author Matthias Totz
 */
public class GameConsoleAppender extends AppenderSkeleton
{
  protected void append(LoggingEvent loggingEvent)
  {
    StendhalClient.get().addEventLine(getLayout().format(loggingEvent),Color.GRAY);
  }

  public void close()
  {
  }

  public boolean requiresLayout()
  {
    return true;
  }
  
}
