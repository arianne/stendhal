/**
 * the scripting backend.
 *
 * <p>The <b>ScriptRunner</b> is invoken on server startup and /script command.
 * This class loads, unloads and executes StendhalGroovyScripts.</p>
 *
 * <p><b>StendhalGroovyScript</b> is the interface for code written in Groovy</p>
 *
 * <p>It extends <b>ScriptingSandbox</b> which takes care of recording and
 * undoing all changes to the world. Note: If you bypass this class, your
 * script will not be unloadable.</p>
 *
 * <p><b>ScriptingJava</b> is an unfinished experiment to write scripts in
 * java code which can be reload at runtime like there groovy sisters.</p>
 */
package games.stendhal.server.scripting;

