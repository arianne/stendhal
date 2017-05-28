/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

/**
 * Game loop thread.
 */
public class GameLoop {
	private static final Logger logger = Logger.getLogger(GameLoop.class);

	private static class Holder {
		static GameLoop instance = new GameLoop();
	}

	/** The actual game loop thread. */
	private final Thread loopThread;
	/** Main game loop content. Run at every cycle.*/
	private PersistentTask persistentTask;
	/** Run once tasks, requested by other threads. */
	private final Queue<Runnable> temporaryTasks = new ConcurrentLinkedQueue<Runnable>();
	/** Tasks to be run when the game loop exits. */
	private final List<Runnable> cleanupTasks = new ArrayList<Runnable>();
	/**
	 * <code>true</code> when the game loop should keep looping,
	 * <code>false</code>, when it should continue to the cleanup tasks.
	 */
	private volatile boolean running;

	/**
	 * Create a new GameLoop.
	 */
	private GameLoop() {
		loopThread = new Thread(new Runnable() {
			@Override
			public void run() {
				loop();
				// gameLoop runs until the client quit
				for (Runnable cleanup : cleanupTasks) {
					cleanup.run();
				}
			}
		}, "Game loop");
	}

	/**
	 * Get the GameLoop instance.
	 *
	 * @return game loop instance
	 */
	public static GameLoop get() {
		return Holder.instance;
	}

	/**
	 * Check if the code is running in the game loop.
	 *
	 * @return <code>true</code> if the called from the game loop thread,
	 * 	<code>false</code> otherwise.
	 */
	public static boolean isGameLoop() {
		return Thread.currentThread() == get().loopThread;
	}

	/**
	 * Start the game loop.
	 */
	public void start() {
		running = true;
		loopThread.start();
	}

	/**
	 * Call at client quit. Tells the game loop to continue to the cleanup
	 * tasks.
	 */
	public void stop() {
		running = false;
	}

	/**
	 * Set the task that should be run at every loop iteration. This <b>must
	 * not</b> be called after the GameLoop has been started.
	 *
	 * @param task
	 */
	public void runAllways(PersistentTask task) {
		persistentTask = task;
	}

	/**
	 * Add a task that should be run when the client quits. This <b>must
	 * not</b> be called after the GameLoop has been started.
	 *
	 * @param task
	 */
	public void runAtQuit(Runnable task) {
		cleanupTasks.add(task);
	}

	/**
	 * Add a task that should be run once in the game loop thread.
	 *
	 * @param task
	 */
	public void runOnce(Runnable task) {
		temporaryTasks.add(task);
	}

	/**
	 * The actual game loop.
	 */
	private void loop() {
		final int frameLength = (int) (1000.0 / stendhal.FPS_LIMIT);
		int fps = 0;

		// keep looping until the game ends
		long refreshTime = System.currentTimeMillis();
		long lastFpsTime = refreshTime;

		while (running) {
			try {
				fps++;
				final long now = System.currentTimeMillis();
				final int delta = (int) (now - refreshTime);
				refreshTime = now;

				// process the persistent task
				persistentTask.run(delta);

				// process the temporary tasks queue
				Runnable tempTask = temporaryTasks.poll();
				while (tempTask != null) {
					tempTask.run();
					tempTask = temporaryTasks.poll();
				}

				if (logger.isDebugEnabled()) {
					reportClientInfo(refreshTime, lastFpsTime, fps);
					fps = 0;
					lastFpsTime = refreshTime;
				}

				logger.debug("Start sleeping");
				// we know how long we want per screen refresh (40ms) then
				// we add the refresh time and subtract the current time
				// leaving us with the amount we still need to sleep.
				long wait = frameLength + refreshTime - System.currentTimeMillis();

				if (wait > 0) {
					if (wait > 100L) {
						logger.info("Waiting " + wait + " ms");
						wait = 100L;
					}

					try {
						Thread.sleep(wait);
					} catch (final InterruptedException e) {
						logger.error(e, e);
					}
				}

				logger.debug("End sleeping");
			} catch (RuntimeException e) {
				logger.error(e, e);
			}
		}
	}

	/**
	 * Write debugging data about the client memory usage and running speed.
	 *
	 * @param refreshTime
	 * @param lastFpsTime
	 * @param fps
	 */
	private void reportClientInfo(long refreshTime, long lastFpsTime, int fps) {
		if ((refreshTime - lastFpsTime) >= 1000L) {
			logger.debug("FPS: " + fps);
			final long freeMemory = Runtime.getRuntime().freeMemory() / 1024;
			final long totalMemory = Runtime.getRuntime().totalMemory() / 1024;

			logger.debug("Total/Used memory: " + totalMemory + "/"
					+ (totalMemory - freeMemory));
		}
	}

	/**
	 * Interface for the main game loop task.
	 */
	public interface PersistentTask {
		/**
		 * Run the task.
		 *
		 * @param delta time since the last run
		 */
		void run(int delta);
	}
}
