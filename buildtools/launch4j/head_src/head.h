/*
	Launch4j (http://launch4j.sourceforge.net/)
	Cross-platform Java application wrapper for creating Windows native executables.

	Copyright (c) 2004, 2015 Grzegorz Kowal,
							 Ian Roberts (jdk preference patch)

	Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:

	The above copyright notice and this permission notice shall be included in
	all copies or substantial portions of the Software.

	Except as contained in this notice, the name(s) of the above copyright holders
	shall not be used in advertising or otherwise to promote the sale, use or other
	dealings in this Software without prior written authorization.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
	THE SOFTWARE.
*/

#ifndef _WIN32_WINNT
#define _WIN32_WINNT 0x0501
#endif // _WIN32_WINNT

#ifndef _LAUNCH4J_HEAD__INCLUDED_
#define _LAUNCH4J_HEAD__INCLUDED_

#define WIN32_LEAN_AND_MEAN		// VC - Exclude rarely-used stuff from Windows headers

// Windows Header Files:
#include <windows.h>

// C RunTime Header Files
#include <stdlib.h>
#include <malloc.h>
#include <memory.h>
#include <tchar.h>
#include <shellapi.h>
#include <direct.h>
#include <fcntl.h>
#include <stdio.h>
#include <sys/stat.h>
#include <io.h>
#include <process.h>

#define LAUNCH4j "Launch4j"
#define VERSION "3.12"

#define JRE_VER_MAX_DIGITS_PER_PART 3

#define NO_JAVA_FOUND 0
#define FOUND_JRE 1
#define FOUND_SDK 2
#define FOUND_BUNDLED 4

#define JRE_ONLY 0
#define PREFER_JRE 1
#define PREFER_JDK 2
#define JDK_ONLY 3

#define USE_64_BIT_RUNTIME 1
#define USE_64_AND_32_BIT_RUNTIME 2
#define USE_32_AND_64_BIT_RUNTIME 3
#define USE_32_BIT_RUNTIME 4
#define INIT_RUNTIME_BITS 9

#define KEY_WOW64_64KEY 0x0100

#define HKEY_STR "HKEY"
#define HKEY_CLASSES_ROOT_STR "HKEY_CLASSES_ROOT"
#define HKEY_CURRENT_USER_STR "HKEY_CURRENT_USER"
#define HKEY_LOCAL_MACHINE_STR "HKEY_LOCAL_MACHINE"
#define HKEY_USERS_STR "HKEY_USERS"
#define HKEY_CURRENT_CONFIG_STR "HKEY_CURRENT_CONFIG"

#define STR 128
#define BIG_STR 1024
#define MAX_VAR_SIZE 32767
#define MAX_ARGS 32768

#define TRUE_STR "true"
#define FALSE_STR "false"

#define ERROR_FORMAT "Error:\t\t%s\n"
#define debug(args...) if (hLog != NULL) fprintf(hLog, ## args);
#define debugAll(args...) if (debugAll && hLog != NULL) fprintf(hLog, ## args);

typedef void (WINAPI *LPFN_ISWOW64PROCESS) (HANDLE, PBOOL);

BOOL initGlobals();
FILE* openLogFile(const char* exePath, const int pathLen);
void closeLogFile();
BOOL initializeLogging(const char *lpCmdLine, const char* exePath, const int pathLen);
void msgBox(const char* text);
void signalError();
BOOL loadString(const int resID, char* buffer);
BOOL loadBool(const int resID);
int loadInt(const int resID);
BOOL regQueryValue(const char* regPath, unsigned char* buffer,
		unsigned long bufferLength);
void formatJavaVersion(char* version, const char* originalVersion);
void regSearch(const char* keyName, const int searchType);
BOOL isJavaHomeValid(const char* keyName, const int searchType);
BOOL isLauncherPathValid(const char* path);
void regSearchWow(const char* keyName, const int searchType);
void regSearchJreSdk(const char* jreKeyName, const char* sdkKeyName,
		const int jdkPreference);
BOOL findJavaHome(char* path, const int jdkPreference);
int getExePath(char* exePath);
void appendPath(char* basepath, const char* path);
void appendLauncher(char* jrePath);
void appendAppClasspath(char* dst, const char* src);
BOOL expandVars(char *dst, const char *src, const char *exePath, const int pathLen);
void appendHeapSizes(char *dst);
void appendHeapSize(char *dst, const int megabytesID, const int percentID,
		const DWORDLONG availableMemory, const char *option);
void setJvmOptions(char *jvmOptions, const char *exePath);
BOOL createMutex();
void setWorkingDirectory(const char *exePath, const int pathLen);
BOOL bundledJreSearch(const char *exePath, const int pathLen);
BOOL installedJreSearch();
void createJreSearchError();
BOOL jreSearch(const char *exePath, const int pathLen);
BOOL appendToPathVar(const char* path);
BOOL appendJreBinToPathVar();
void setEnvironmentVariables(const char *exePath, const int pathLen);
void setMainClassAndClassPath(const char *exePath, const int pathLen);
void setCommandLineArgs(const char *lpCmdLine);
int prepare(const char *lpCmdLine);
void closeProcessHandles();
BOOL execute(const BOOL wait, DWORD *dwExitCode);
const char* getJavaHome();
const char* getMainClass();
const char* getLauncherArgs();

#endif // _LAUNCH4J_HEAD__INCLUDED_
