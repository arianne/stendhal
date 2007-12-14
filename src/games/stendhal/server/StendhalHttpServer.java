/* $Id$ */

/** StendhalHttpServer Extension is copyright of Jo Seiler, 2006
 *  @author intensifly
 *  See the sample website in the web directory and the README file
 *  in this directory on how to use this extension.
 *
 *  Download the latest simple.jar from sourceforge and add it to
 *  the classpath of marauroa / stendhal. Development was done and
 *  tested with simple-3.1.1.jar.
 *
 *  Enable the StendhalServerExtension in the marauroa.ini file:
 # load StendhalServerExtension(s)
 groovy=games.stendhal.server.scripting.StendhalGroovyRunner
 http=games.stendhal.server.StendhalHttpServer
 server_extension=groovy,http
 **/

package games.stendhal.server;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;

import marauroa.common.Configuration;

import org.apache.log4j.Logger;
import simple.http.PipelineHandler;
import simple.http.PipelineHandlerFactory;
import simple.http.ProtocolHandler;
import simple.http.Request;
import simple.http.Response;
import simple.http.connect.Connection;
import simple.http.connect.ConnectionFactory;
import simple.http.load.LoaderEngine;
import simple.http.serve.CacheContext;
import simple.http.serve.Content;
import simple.http.serve.Context;
import simple.http.serve.FileContext;
import simple.http.serve.ProtocolHandlerFactory;
import simple.template.View;

/*
 * TODO: Refactor - Remove Groovy - Make it work - Integrate with
 * mblanch.homeip.net/stendhal_website
 */

public class StendhalHttpServer extends StendhalServerExtension implements
		ProtocolHandler {

	private static final Logger logger = Logger.getLogger(StendhalHttpServer.class);

	/** what to do with the http request if we don't handle it ourselves * */
	private ProtocolHandler handler;

	// /** our context to retrieve files * */
	// static Context dataContext;

	/** the port where we listen to http * */
	private static int PORT;

	/** default expiration time * */
	private static final int EXPIRES = 300000; // 5 minutes

	/** GroovyScriptEngine * */
	private static GroovyScriptEngine scriptEngine;

	/** Context to retrieve Groovy scripts * */
	private static Context scriptContext;

	/** Context to retrieve files * */
	private static Context fileContext;

	/** GroovyVariableBinding * */
	private static Binding scriptBinding;

	/** initialize the server with the game object connection * */
	public StendhalHttpServer() {
		super();
		try {
			logger.info("StendhalHttpServer starting...");
			scriptContext = new FileContext(new File("web/script/"));
			fileContext = new CacheContext(new File("web/html/"));
			String[] roots = new String[2];
			roots[0] = scriptContext.getRealPath("/");
			roots[1] = fileContext.getRealPath("/");
			scriptEngine = new GroovyScriptEngine(roots);
			scriptBinding = new Binding();
			// scriptBinding.setVariable("rules", rules);
			// scriptBinding.setVariable("world", world);
			if (Configuration.getConfiguration().has("http.port")) {
				PORT = Integer.parseInt(Configuration.getConfiguration().get(
						"http.port").trim());
			} else {
				PORT = 80; // default http-port
			}
		} catch (Exception e) {
			logger.error(e, e);
		}
	}

	/** process a http request and add convenience headers * */
	public void handle(Request req, Response resp) {
		resp.set("Server", "Stendhal http (Simpleweb)");
		resp.setDate("Date", System.currentTimeMillis());
		resp.setDate("Expires", System.currentTimeMillis() + EXPIRES);
		resp.setDate("Last-Modified", System.currentTimeMillis());
		handler.handle(req, resp);
	}

	/** convenience method to copy stream contents * */
	public static void streamCopy(InputStream in, OutputStream out)
			throws Exception {
		try {
			in = new BufferedInputStream(in);
			while (true) {
				int data = in.read();
				if (data == -1) {
					break;
				}
				out.write(data);
			}
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	/** writing HTML to the respons stream while filtering and replacing * */
	/** ssi <!--#include --> directives * */
	public static void outputHTML(StringBuffer s, PrintStream out, Request req,
			String dir) throws Exception {
		int left = 0, right;
		while ((left = s.indexOf("<!--#include", left)) >= 0) {
			right = s.indexOf("-->", left + 13);
			String[] result = s.substring(left + 13, right).trim().split(
					"[=\"]");
			s.delete(left, right + 3);
			if (result.length > 0) {
				String modeInclude = result[0].trim();
				String fileInclude = null;
				for (int x = 1; x < result.length; x++) {
					if (result[x].length() > 0) {
						fileInclude = result[x].trim();
						break;
					}
				}
				try {
					ByteArrayOutputStream outStream = new ByteArrayOutputStream(
							2048);
					if ("file".equals(modeInclude)) {
						InputStream in = new FileInputStream(new File(
								fileInclude));
						streamCopy(in, outStream);
						in.close();
					} else {
						IncludableView service;
						if (!fileInclude.startsWith("/")) {
							fileInclude = dir + fileInclude;
						}
						if (fileInclude.endsWith(".groovy")) {
							fileInclude = "." + fileInclude;
							service = new SecureScriptService(fileContext);
						} else if (fileInclude.startsWith("/stendhal.")) {
							service = new GameScriptService(scriptContext);
							fileInclude = "./" + fileInclude.substring(10)
									+ ".groovy";
						} else {
							service = new FileService(fileContext);
						}
						service.process(req, null, fileInclude,
								new PrintStream(outStream));
					}
					s.insert(left, new StringBuffer(outStream.toString()));
				} catch (Exception e) {
					s.insert(left, "<!-- " + e.getMessage() + " -->");
					logger.error(e, e);
				}
			}
		}
		out.print(s.toString());
	}

	public abstract static class IncludableView extends View {

		public IncludableView(Context context) {
			super(context);
		}

		@Override
		public void process(Request req, Response resp) throws Exception {
			process(req, resp, req.getPath().getPath(),
					resp.getPrintStream(1024));
		}

		public abstract void process(Request req, Response resp,
				String resource, PrintStream outStream) throws Exception;
	}

	/** Serve Groovy scripts that don't have access to game objects * */
	public static class SecureScriptService extends IncludableView {

		public SecureScriptService(Context context) {
			super(context); // this context will be ignored
		}

		public Binding getBinding() {
			return (new Binding());
		}

		public String getScriptName(Request req) {
			return ("." + req.getPath().getPath());
		}

		@Override
		public void process(Request req, Response resp) throws Exception {
			process(req, resp, getScriptName(req), resp.getPrintStream(1024));
		}

		@Override
		public void process(Request req, Response resp, String resource,
				PrintStream outStream) throws Exception {
			ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
			Binding binding = getBinding();
			if (resp != null) {
				// default content-type, can be overwritten in script if
				// the script isn't included (response == null)
				resp.set("Content-Type", "text/html");
				binding.setVariable("response", resp);
			}
			binding.setVariable("out", out);
			binding.setVariable("request", req);
			// access the file through the ScriptEngine, not the context object
			scriptEngine.run(resource, binding);
			if ((resp != null)
					&& !"text/html".equals(resp.getValue("Content-Type"))) {
				out.writeTo(outStream);
			} else {
				outputHTML(new StringBuffer(out.toString()), outStream, req,
						req.getPath().getDirectory());
			}
			outStream.close();
		}
	}

	/** Serve Groovy scripts that have access to game objects * */
	public static class GameScriptService extends SecureScriptService {

		public GameScriptService(Context context) {
			super(context); // this context will be ignored
		}

		@Override
		public Binding getBinding() {
			return (new Binding(scriptBinding.getVariables()));
		}

		@Override
		public String getScriptName(Request req) {
			return ("./" + req.getPath().getExtension() + ".groovy");
		}
	}

	/** Serve normal files from the document root (fileContext) * */
	public static class FileService extends IncludableView {

		public FileService(Context context) {
			super(fileContext);
		}

		@Override
		public void process(Request req, Response resp) throws Exception {
			process(req, resp, req.getPath().getPath(),
					resp.getPrintStream(1024));
		}

		@Override
		public void process(Request req, Response resp, String resource,
				PrintStream outStream) throws Exception {
			File file = new File(context.getRealPath(resource));

			if (file.exists()
					&& file.isDirectory()
					&& context.getRealPath(resource).contains(
							resource.substring(1, resource.length()))) {
				if (!resource.endsWith("/")) {
					resource += "/";
				}
				resource += "index.html";
				file = context.getFile(resource);
			}
			if (!file.isDirectory() && (file.length() > 0)) {
				Content content = context.getContent(resource);
				boolean isHTML = "text/html".equals(content.getContentType());
				OutputStream out;
				if (isHTML) {
					out = new ByteArrayOutputStream(2048);
				} else {
					out = outStream;
				}
				if (resp != null) {
					resp.setDate("Last-Modified", file.lastModified());
					resp.set("Content-Type", content.getContentType());
				}
				content.write(out);
				out.close();
				if (isHTML) {
					PrintStream rOut = outStream;
					outputHTML(new StringBuffer(out.toString()), rOut, req,
							context.getPath(resource).getDirectory());
					rOut.close();
				}
			} else {
				throw new java.io.IOException("Invalid file: " + resource);
			}
		}
	}

	/** Serve resources from the classpath * */
	public static class DataService extends View {

		public DataService(Context context) {
			super(context);
		}

		@Override
		public void process(Request req, Response resp) throws Exception {
			PrintStream out = resp.getPrintStream(1024);
			String resource = req.getPath().getPath().substring(1);
			if (getClass().getClassLoader().getResource(resource) != null) {
				InputStream in = getClass().getClassLoader().getResourceAsStream(
						resource);
				resp.set("Content-Type", context.getContentType(resource));
				resp.set("Cache-Control", "public");
				resp.setDate("Expires", System.currentTimeMillis() + 900000); // 15
																				// minutes
				streamCopy(in, out);
				out.close();
			} else {
				throw new java.io.IOException("Invalid resource: " + resource);
			}
		}
	}

	/** start listening to http requests * */
	@Override
	public void init() {
		try {
			LoaderEngine engine = new LoaderEngine();
			engine.load("file",
					"games.stendhal.server.StendhalHttpServer$FileService");
			engine.link("*", "file");
			engine.load("data",
					"games.stendhal.server.StendhalHttpServer$DataService");
			engine.link("/data/*", "data");
			engine.link("*.jar", "data");
			engine.load("script",
					"games.stendhal.server.StendhalHttpServer$GameScriptService");
			engine.link("/stendhal.*", "script");
			engine.load("securescript",
					"games.stendhal.server.StendhalHttpServer$SecureScriptService");
			engine.link("*.groovy", "securescript");
			this.handler = ProtocolHandlerFactory.getInstance(engine);
			// ProcessQueue.getInstance().resize(1);

			PipelineHandler piplelineHandler = PipelineHandlerFactory.getInstance(
					this, 1, 1000);

			Connection connection = ConnectionFactory.getConnection(piplelineHandler);
			connection.connect(new ServerSocket(PORT));
			logger.info("Started http server on port " + PORT);
		} catch (Exception e) {
			logger.error(e, e);
		}
	}
}
