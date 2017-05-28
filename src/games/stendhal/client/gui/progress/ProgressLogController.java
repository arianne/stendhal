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
package games.stendhal.client.gui.progress;

import java.awt.Window;
import java.util.Collection;
import java.util.List;

import javax.swing.SwingUtilities;

import games.stendhal.client.StendhalClient;
import games.stendhal.client.entity.User;
import games.stendhal.client.gui.WindowUtils;
import games.stendhal.common.constants.Actions;
import games.stendhal.common.grammar.Grammar;
import marauroa.common.game.RPAction;

public class ProgressLogController {
	/** Controller instance */
	private static ProgressLogController instance;

	/**
	 * Progress window. This should be accessed only through getProgressLog(),
	 * which ensures the window has been created.
	 */
	private ProgressLog progressLog;

	/**
	 * Get the book controller instance.
	 *
	 * @return controller instance
	 */
	public static synchronized ProgressLogController get() {
		if (instance == null) {
			instance = new ProgressLogController();
		}
		return instance;
	}

	/**
	 * Create a new ProgressLogController.
	 */
	private ProgressLogController() {
	}

	/**
	 * Set the repeatable quest names.
	 *
	 * @param repeatable repeatable quests
	 */
	public void setRepeatable(final Collection<String> repeatable) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				getProgressLog().setRepeatable(repeatable);
			}
		});
	}
	/**
	 * Show available progress categories.
	 *
	 * @param categories
	 */
	public void showCategories(final List<String> categories) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// Prepare appropriate action for content clicks
				RequestAction contentAction = new RequestAction();
				contentAction.setDataKey("progress_type");

				getProgressLog().setPages(categories, contentAction);
				showWindow();
			}
		});
	}

	/**
	 * Show a list of items in a category.
	 *
	 * @param category
	 * @param items
	 */
	public void showCategorySummary(final String category, final List<String> items) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// Prepare appropriate action for content clicks
				RequestAction contentAction = new RequestAction();
				contentAction.setDataKey("item");
				contentAction.setProgressType(category);

				getProgressLog().setPageIndex(category, items, contentAction);
				showWindow();
			}
		});
	}

	/**
	 * Show the description of an item in a category.
	 *
	 * @param category
	 * @param item item to be described
	 * @param description description
	 * @param information information
	 * @param details paragraphs
	 */
	public void showDescription(final String category, final String item, final String description, final String information, final List<String> details) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				getProgressLog().setPageContent(category, item, description, information, details);
				showWindow();
			}
		});
	}

	/**
	 * Get the log window, and create it if it has not been created before.
	 * This method must be called in the event dispatch thread.
	 *
	 * @return log window
	 */
	private ProgressLog getProgressLog() {
		if (progressLog == null) {
			progressLog = new ProgressLog(Grammar.suffix_s(User.getCharacterName()) + " travel log");
		}
		return progressLog;
	}

	/**
	 * Show the window, if it's not already visible. This must not be called
	 * outside the event dispatch thread.
	 */
	private void showWindow() {
		Window window = getProgressLog().getWindow();
		WindowUtils.restoreSize(window);
		window.setVisible(true);
	}

	/**
	 * A class for storing progress query information.
	 */
	private static class RequestAction implements ProgressStatusQuery {
		private String progressType;
		private String dataKey;

		/**
		 * Send the progress query to the server.
		 *
		 * @param data the value of dataKey, if set
		 */
		@Override
		public void fire(String data) {
			RPAction action = new RPAction();
			action.put("type", Actions.PROGRESS_STATUS);
			if (progressType != null) {
				action.put("progress_type", progressType);
			}
			if (dataKey != null) {
				action.put(dataKey, data);
			}

			StendhalClient.get().send(action);
		}

		/**
		 * Set the action key whose value will be passed as a parameter to
		 * fire()
		 *
		 * @param key
		 */
		void setDataKey(String key) {
			dataKey = key;
		}

		/**
		 * Set the "progress_type" value for the query.
		 *
		 * @param type
		 */
		void setProgressType(String type) {
			progressType = type;
		}
	}
}
