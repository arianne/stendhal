/*
	Launch4j (http://launch4j.sourceforge.net/)
	Cross-platform Java application wrapper for creating Windows native executables.

	Copyright (c) 2004, 2015 Grzegorz Kowal
	All rights reserved.

	Redistribution and use in source and binary forms, with or without modification,
	are permitted provided that the following conditions are met:
	
	1. Redistributions of source code must retain the above copyright notice,
	   this list of conditions and the following disclaimer.
	
	2. Redistributions in binary form must reproduce the above copyright notice,
	   this list of conditions and the following disclaimer in the documentation
	   and/or other materials provided with the distribution.
	
	3. Neither the name of the copyright holder nor the names of its contributors
	   may be used to endorse or promote products derived from this software without
	   specific prior written permission.
	
	THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
	AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
	THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
	ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
	FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
	(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
	LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
	AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
	OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
	OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

/*
 * Created on Apr 21, 2005
 */
package net.sf.launch4j.config;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import net.sf.launch4j.binding.IValidatable;
import net.sf.launch4j.binding.Validator;

/**
 * @author Copyright (C) 2005 Grzegorz Kowal
 */
public class Config implements IValidatable {

	// 1.x config properties_____________________________________________________________
	public static final String HEADER = "header";
	public static final String JAR = "jar";
	public static final String OUTFILE = "outfile";
	public static final String ERR_TITLE = "errTitle";
	public static final String JAR_ARGS = "jarArgs";
	public static final String CHDIR = "chdir";
	public static final String STAY_ALIVE = "stayAlive";
	public static final String ICON = "icon";

	// __________________________________________________________________________________
	public static final String DOWNLOAD_URL = "http://java.com/download";

	public static final String GUI_HEADER = "gui";
	public static final String CONSOLE_HEADER = "console";
	public static final String JNI_GUI_HEADER_32 = "jniGui32";
	public static final String JNI_CONSOLE_HEADER_32 = "jniConsole32";

	private static final String[] HEADER_TYPES = new String[] { GUI_HEADER,
																CONSOLE_HEADER,
																JNI_GUI_HEADER_32,
																JNI_CONSOLE_HEADER_32 };

	private static final String[] PRIORITY_CLASS_NAMES = new String[] { "normal",
																		"idle",
																		"high" };

	private static final int[] PRIORITY_CLASSES = new int[] { 0x00000020,
															0x00000040,
															0x00000080 };

	private boolean dontWrapJar;
	private String headerType = GUI_HEADER;
	private List<String> headerObjects;
	private List<String> libs;
	private File jar;
	private File outfile;

	// Runtime header configuration
	private String errTitle;
	private String cmdLine;
	private String chdir;
	private String priority;
	private String downloadUrl;
	private String supportUrl;
	private boolean stayAlive;
	private boolean restartOnCrash;
	private File manifest;
	private File icon;
	private List<String> variables;
	private SingleInstance singleInstance;
	private ClassPath classPath;
	private Jre jre;
	private Splash splash;
	private VersionInfo versionInfo;
	private Msg	messages;

	public void checkInvariants() {
		Validator.checkTrue(outfile != null && outfile.getPath().endsWith(".exe"),
				"outfile", Messages.getString("Config.specify.output.exe"));
		if (dontWrapJar) {
			if (jar != null && !jar.getPath().equals("")) {
				Validator.checkRelativeWinPath(jar.getPath(), "jar",
						Messages.getString("Config.application.jar.path"));
			} else {
				Validator.checkTrue(classPath != null, "classPath",
						Messages.getString("ClassPath.or.jar"));
			}
		} else {
			Validator.checkFile(jar, "jar",
					Messages.getString("Config.application.jar"));
		}
		if (!Validator.isEmpty(chdir)) {
			Validator.checkRelativeWinPath(chdir, "chdir",
					Messages.getString("Config.chdir.relative"));
			Validator.checkFalse(chdir.toLowerCase().equals("true")
					|| chdir.toLowerCase().equals("false"),
					"chdir", Messages.getString("Config.chdir.path"));
		}
		Validator.checkOptFile(manifest, "manifest", Messages.getString("Config.manifest"));
		Validator.checkOptFile(icon, "icon", Messages.getString("Config.icon"));
		Validator.checkOptString(cmdLine, Validator.MAX_BIG_STR, "jarArgs",
				Messages.getString("Config.jar.arguments"));
		Validator.checkOptString(errTitle, Validator.MAX_STR, "errTitle",
				Messages.getString("Config.error.title"));
		Validator.checkOptString(downloadUrl, 256,
				"downloadUrl", Messages.getString("Config.download.url"));
		Validator.checkOptString(supportUrl, 256,
				"supportUrl", Messages.getString("Config.support.url"));
		Validator.checkIn(getHeaderType(), HEADER_TYPES, "headerType",
				Messages.getString("Config.header.type"));
		Validator.checkFalse(!isGuiApplication() && splash != null,
				"headerType",
				Messages.getString("Config.splash.not.impl.by.console.hdr"));
		Validator.checkOptStrings(variables,
				Validator.MAX_ARGS,
				Validator.MAX_ARGS,
				"[^=%\t]+=[^=\t]+",
				"variables",
				Messages.getString("Config.variables"),
				Messages.getString("Config.variables.err"));
		Validator.checkIn(getPriority(), PRIORITY_CLASS_NAMES, "priority",
				Messages.getString("Config.priority"));
		checkJniInvariants();
		jre.checkInvariants();
	}
	
	private void checkJniInvariants() {
		// TODO: Remove once JNI is fully implemented.
		if (isJniApplication()) {
			Validator.checkTrue(".".equals(chdir), "chdir",
					"Only '.' is allowed in change directory.");
			Validator.checkTrue(Validator.isEmpty(cmdLine), "cmdLine",
					"Constant command line arguments not supported.");
			Validator.checkFalse(stayAlive, "stayAlive",
					"Stay alive option is not used in JNI, this is the default behavior.");
			Validator.checkFalse(restartOnCrash, "restartOnCrash",
					"Restart on crash not supported.");
			Validator.checkIn(getPriority(), new String[] { "normal" }, "priority",
					"Process priority is not supported,");
			Validator.checkNotNull(classPath, "classpath", "classpath");
			Validator.checkFalse(jre.getBundledJre64Bit(), "jre.bundledJre64Bit",
					"64-bit bundled JRE not supported.");
			Validator.checkTrue(Jre.RUNTIME_BITS_32.equals(jre.getRuntimeBits()), "jre.runtimeBits", 
					"64-bit JRE not supported.");
		}
	}
	
	public void validate() {
		checkInvariants();
		if (classPath != null) {
			classPath.checkInvariants();
		}
		if (splash != null) {
			splash.checkInvariants();
		}
		if (versionInfo != null) {
			versionInfo.checkInvariants();
		}
	}

	/** Change current directory to EXE location. */
	public String getChdir() {
		return chdir;
	}

	public void setChdir(String chdir) {
		this.chdir = chdir;
	}

	/** Constant command line arguments passed to the application. */
	public String getCmdLine() {
		return cmdLine;
	}

	public void setCmdLine(String cmdLine) {
		this.cmdLine = cmdLine;
	}

	/** Optional, error message box title. */
	public String getErrTitle() {
		return errTitle;
	}

	public void setErrTitle(String errTitle) {
		this.errTitle = errTitle;
	}
	
	public boolean isGuiApplication() {
		return GUI_HEADER.equals(headerType) || JNI_GUI_HEADER_32.equals(headerType);
	}
	
	public boolean isJniApplication() {
		return JNI_GUI_HEADER_32.equals(headerType)
				|| JNI_CONSOLE_HEADER_32.equals(headerType);
	}

	/** launch4j header file. */
	public String getHeaderType() {
		return headerType;
	}

	public void setHeaderType(String headerType) {
		this.headerType = headerType;
	}

	/** launch4j header file index - used by GUI. */
	public int getHeaderTypeIndex() {
		int x = Arrays.asList(HEADER_TYPES).indexOf(getHeaderType());
		return x != -1 ? x : 0;
	}

	public void setHeaderTypeIndex(int headerTypeIndex) {
		headerType = HEADER_TYPES[headerTypeIndex];
	}

	public boolean isCustomHeaderObjects() {
		return headerObjects != null && !headerObjects.isEmpty();
	}

	public List<String> getHeaderObjects() {
		return isCustomHeaderObjects() ? headerObjects
				: LdDefaults.getHeaderObjects(getHeaderTypeIndex());
	}

	public void setHeaderObjects(List<String> headerObjects) {
		this.headerObjects = headerObjects;
	}

	public boolean isCustomLibs() {
		return libs != null && !libs.isEmpty();
	}

	public List<String> getLibs() {
		return isCustomLibs() ? libs : LdDefaults.getLibs(headerType);
	}

	public void setLibs(List<String> libs) {
		this.libs = libs;
	}

	/** Wrapper's manifest for User Account Control. */ 
	public File getManifest() {
    	return manifest;
    }

	public void setManifest(File manifest) {
    	this.manifest = manifest;
    }

	/** ICO file. */
	public File getIcon() {
		return icon;
	}

	public void setIcon(File icon) {
		this.icon = icon;
	}

	/** Jar to wrap. */
	public File getJar() {
		return jar;
	}

	public void setJar(File jar) {
		this.jar = jar;
	}

	public List<String> getVariables() {
		return variables;
	}

	public void setVariables(List<String> variables) {
		this.variables = variables;
	}

	public ClassPath getClassPath() {
		return classPath;
	}
	
	public void setClassPath(ClassPath classpath) {
		this.classPath = classpath;
	}

	/** JRE configuration */
	public Jre getJre() {
		return jre;
	}

	public void setJre(Jre jre) {
		this.jre = jre;
	}

	/** Output EXE file. */
	public File getOutfile() {
		return outfile;
	}

	public void setOutfile(File outfile) {
		this.outfile = outfile;
	}

	/** Splash screen configuration. */
	public Splash getSplash() {
		return splash;
	}

	public void setSplash(Splash splash) {
		this.splash = splash;
	}

	/** Stay alive after launching the application. */
	public boolean isStayAlive() {
		return stayAlive;
	}

	public void setStayAlive(boolean stayAlive) {
		this.stayAlive = stayAlive;
	}
	
	/** Restart the application after a crash (i.e. exit code other than 0) */
	public boolean isRestartOnCrash() {
		return restartOnCrash;
	}
	
	public void setRestartOnCrash(boolean restartOnCrash) {
		this.restartOnCrash = restartOnCrash;
	}
	
	public VersionInfo getVersionInfo() {
		return versionInfo;
	}

	public void setVersionInfo(VersionInfo versionInfo) {
		this.versionInfo = versionInfo;
	}

	public boolean isDontWrapJar() {
		return dontWrapJar;
	}

	public void setDontWrapJar(boolean dontWrapJar) {
		this.dontWrapJar = dontWrapJar;
	}

	public int getPriorityIndex() {
		int x = Arrays.asList(PRIORITY_CLASS_NAMES).indexOf(getPriority());
		return x != -1 ? x : 0;
	}
	
	public void setPriorityIndex(int x) {
		priority = PRIORITY_CLASS_NAMES[x];
	}

	public String getPriority() {
		return Validator.isEmpty(priority) ? PRIORITY_CLASS_NAMES[0] : priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}
	
	public int getPriorityClass() {
		return PRIORITY_CLASSES[getPriorityIndex()];
	}
	
	public String getDownloadUrl() {
		return downloadUrl == null ? DOWNLOAD_URL : downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public String getSupportUrl() {
		return supportUrl;
	}

	public void setSupportUrl(String supportUrl) {
		this.supportUrl = supportUrl;
	}

	public Msg getMessages() {
		return messages;
	}

	public void setMessages(Msg messages) {
		this.messages = messages;
	}
	
	public SingleInstance getSingleInstance() {
    	return singleInstance;
    }

	public void setSingleInstance(SingleInstance singleInstance) {
    	this.singleInstance = singleInstance;
    }
}
