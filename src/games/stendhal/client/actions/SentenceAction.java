package games.stendhal.client.actions;

import games.stendhal.client.StendhalClient;
import marauroa.common.game.RPAction;

/**
 * Writes the sentence that appears on Website.
 */
class SentenceAction implements SlashAction {

       /**
        * Execute a chat command.
        *
        * @param params
        *            The formal parameters.
        * @param remainder
        *            Line content after parameters.
        *
        * @return <code>true</code> if command was handled.
        */
       public boolean execute(String[] params, String remainder) {
               if (params == null) {
                       return false;
               }
               RPAction add = new RPAction();

               add.put("type", "sentence");
               add.put("value", remainder);

               StendhalClient.get().send(add);

               return true;
       }

       /**
        * Get the maximum number of formal parameters.
        *
        * @return The parameter count.
        */
       public int getMaximumParameters() {
               return 0;
       }

       /**
        * Get the minimum number of formal parameters.
        *
        * @return The parameter count.
        */
       public int getMinimumParameters() {
               return 0;
       }
}
