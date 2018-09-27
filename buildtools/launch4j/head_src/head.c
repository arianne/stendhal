/*
	Launch4j (http://launch4j.sourceforge.net/)
	Cross-platform Java application wrapper for creating Windows native executables.

	Copyright (c) 2004, 2015 Grzegorz Kowal,
							 Ian Roberts (jdk preference patch)
							 Sylvain Mina (single instance patch)

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

#include "resource.h"
#include "head.h"

HMODULE hModule;
FILE* hLog;
BOOL debugAll = FALSE;
BOOL console = FALSE;
BOOL wow64 = FALSE;
char oldPwd[_MAX_PATH];

PROCESS_INFORMATION processInformation;
DWORD processPriority;

struct
{
	char title[STR];
	char msg[BIG_STR];
	char url[256];
} error;

struct
{
	int runtimeBits;
	int foundJava;
	BOOL bundledJreAsFallback;
	BOOL corruptedJreFound;
	char originalJavaMinVer[STR];
	char originalJavaMaxVer[STR];
	char javaMinVer[STR];
	char javaMaxVer[STR];
	char foundJavaVer[STR];
	char foundJavaKey[_MAX_PATH];
	char foundJavaHome[_MAX_PATH];
} search;

struct
{
	char mainClass[_MAX_PATH];
	char cmd[_MAX_PATH];
	char args[MAX_ARGS];
} launcher;

BOOL initGlobals()
{
	hModule = GetModuleHandle(NULL);

	if (hModule == NULL)
	{
		return FALSE;
	}

	strcpy(error.title, LAUNCH4j);

	search.runtimeBits = INIT_RUNTIME_BITS;
	search.foundJava = NO_JAVA_FOUND;
	search.bundledJreAsFallback = FALSE;
	search.corruptedJreFound = FALSE;
	
	return TRUE;
}

FILE* openLogFile(const char* exePath, const int pathLen)
{
	char path[_MAX_PATH] = {0};
	strncpy(path, exePath, pathLen);
	strcat(path, "\\launch4j.log");
	return fopen(path, "a");
}

void closeLogFile()
{
	if (hLog != NULL)
	{
		fclose(hLog);	
	}
}

BOOL initializeLogging(const char *lpCmdLine, const char* exePath, const int pathLen)
{
	char varValue[MAX_VAR_SIZE] = {0};
	GetEnvironmentVariable(LAUNCH4j, varValue, MAX_VAR_SIZE);

    if (strstr(lpCmdLine, "--l4j-debug") != NULL
			|| strstr(varValue, "debug") != NULL)
	{
		hLog = openLogFile(exePath, pathLen);

		if (hLog == NULL)
		{
			return FALSE;
		}

		debugAll = strstr(lpCmdLine, "--l4j-debug-all") != NULL
				|| strstr(varValue, "debug-all") != NULL;
	}
	
	debug("\n\nVersion:\t%s\n", VERSION);
	debug("CmdLine:\t%s %s\n", exePath, lpCmdLine);

	return TRUE;
}

void setWow64Flag()
{
	LPFN_ISWOW64PROCESS fnIsWow64Process = (LPFN_ISWOW64PROCESS)GetProcAddress(
			GetModuleHandle(TEXT("kernel32")), "IsWow64Process");

	if (fnIsWow64Process != NULL)
	{
		fnIsWow64Process(GetCurrentProcess(), &wow64);
	}

	debug("WOW64:\t\t%s\n", wow64 ? "yes" : "no"); 
}

void setConsoleFlag()
{
     console = TRUE;
}

void msgBox(const char* text)
{
    if (console)
	{
        if (*error.title)
        {
            printf("%s: %s\n", error.title, text);
        }
        else
        {
            printf("%s\n", text);
        }
    }
	else
	{
    	MessageBox(NULL, text, error.title, MB_OK);
    }
}

void signalError()
{
	DWORD err = GetLastError();
	debug("Error msg:\t%s\n", error.msg);

	if (err)
	{
		LPVOID lpMsgBuf;
		FormatMessage(FORMAT_MESSAGE_ALLOCATE_BUFFER
						| FORMAT_MESSAGE_FROM_SYSTEM
						| FORMAT_MESSAGE_IGNORE_INSERTS,
				NULL,
				err,
				MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), // Default language
			    (LPTSTR) &lpMsgBuf,
			    0,
			    NULL);
		debug(ERROR_FORMAT, (LPCTSTR) lpMsgBuf);
		strcat(error.msg, "\n\n");
		strcat(error.msg, (LPCTSTR) lpMsgBuf);
		LocalFree(lpMsgBuf);
	}
	
	msgBox(error.msg);

	if (*error.url)
	{
		debug("Open URL:\t%s\n", error.url);
		ShellExecute(NULL, "open", error.url, NULL, NULL, SW_SHOWNORMAL);
	}

	closeLogFile();
}

BOOL loadString(const int resID, char* buffer)
{
	HRSRC hResource;
	HGLOBAL hResourceLoaded;
	LPBYTE lpBuffer;
	debugAll("Resource %d:\t", resID);

	hResource = FindResourceEx(hModule, RT_RCDATA, MAKEINTRESOURCE(resID),
			MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT));
	if (NULL != hResource)
	{
		hResourceLoaded = LoadResource(hModule, hResource);
		if (NULL != hResourceLoaded)
		{
			lpBuffer = (LPBYTE) LockResource(hResourceLoaded);            
			if (NULL != lpBuffer)
			{     
				int x = 0;
				do
				{
					buffer[x] = (char) lpBuffer[x];
				} while (buffer[x++] != 0);
				
				debugAll("%s\n", buffer);
				return TRUE;
			}
		}    
	}
	else
	{
		SetLastError(0);
		buffer[0] = 0;
	}
	
	debugAll("<NULL>\n");
	return FALSE;
}

BOOL loadBool(const int resID)
{
	char boolStr[20] = {0};
	loadString(resID, boolStr);
	return strcmp(boolStr, TRUE_STR) == 0;
}

int loadInt(const int resID)
{
	char intStr[20] = {0};
	loadString(resID, intStr);
	return atoi(intStr);
}

BOOL regQueryValue(const char* regPath, unsigned char* buffer,
		unsigned long bufferLength)
{
	HKEY hRootKey;
	char* key;
	char* value;

	if (strstr(regPath, HKEY_CLASSES_ROOT_STR) == regPath)
	{
		hRootKey = HKEY_CLASSES_ROOT;
	}
	else if (strstr(regPath, HKEY_CURRENT_USER_STR) == regPath)
	{
		hRootKey = HKEY_CURRENT_USER;
	}
	else if (strstr(regPath, HKEY_LOCAL_MACHINE_STR) == regPath)
	{
		hRootKey = HKEY_LOCAL_MACHINE;
	}
	else if (strstr(regPath, HKEY_USERS_STR) == regPath)
	{
		hRootKey = HKEY_USERS;
	}
	else if (strstr(regPath, HKEY_CURRENT_CONFIG_STR) == regPath)
	{
		hRootKey = HKEY_CURRENT_CONFIG;
	}
	else
	{
		return FALSE;
	}

	key = strchr(regPath, '\\') + 1;
	value = strrchr(regPath, '\\') + 1;
	*(value - 1) = 0;

	HKEY hKey;
	unsigned long datatype;
	BOOL result = FALSE;
	if ((wow64 && RegOpenKeyEx(hRootKey,
								key,
								0,
	        					KEY_READ | KEY_WOW64_64KEY,
								&hKey) == ERROR_SUCCESS)
			|| RegOpenKeyEx(hRootKey,
								key,
								0,
	        					KEY_READ,
								&hKey) == ERROR_SUCCESS)
	{
		result = RegQueryValueEx(hKey, value, NULL, &datatype, buffer, &bufferLength)
				== ERROR_SUCCESS;
		RegCloseKey(hKey);
	}
	*(value - 1) = '\\';
	return result;
}

int findNextVersionPart(const char* startAt)
{
	if (startAt == NULL || strlen(startAt) == 0)
    {
		return 0;
	}

	char* firstSeparatorA = strchr(startAt, '.');
	char* firstSeparatorB = strchr(startAt, '_');
	char* firstSeparator;
	if (firstSeparatorA == NULL)
    {
		firstSeparator = firstSeparatorB;
	}
    else if (firstSeparatorB == NULL)
    {
		firstSeparator = firstSeparatorA;
	}
    else
    {
		firstSeparator = min(firstSeparatorA, firstSeparatorB);
	}

	if (firstSeparator == NULL)
    {
		return strlen(startAt);
	}

	return firstSeparator - startAt;
}

/**
 * This method will take java version from `originalVersion` string and convert/format it
 * into `version` string that can be used for string comparison with other versions.
 *
 * Due to different version schemas <=8 vs. >=9 it will "normalize" versions to 1 format
 * so we can directly compare old and new versions.
 */
void formatJavaVersion(char* version, const char* originalVersion)
{
	strcpy(version, "");
	if (originalVersion == NULL || strlen(originalVersion) == 0)
    {
		return;
	}

	int partsAdded = 0;
	int i;
	char* pos = (char*) originalVersion;
	int curPartLen;

	while ((curPartLen = findNextVersionPart(pos)) > 0)
    {
		char number[curPartLen + 1];
		memset(number, 0, curPartLen + 1);
		strncpy(number, pos, curPartLen);

		if (partsAdded == 0 && (curPartLen != 1 || number[0] != '1'))
        {
			// NOTE: When it's java 9+ we'll add "1" as the first part of the version
			strcpy(version, "1");
			partsAdded++;
		}

		if (partsAdded < 3)
        {
			if (partsAdded > 0)
            {
				strcat(version, ".");
			}
			for (i = 0;
					(partsAdded > 0)
							&& (i < JRE_VER_MAX_DIGITS_PER_PART - strlen(number));
					i++)
            {
				strcat(version, "0");
			}
			strcat(version, number);
		}
        else if (partsAdded == 3)
        {
			// add as an update
			strcat(version, "_");
			for (i = 0; i < JRE_VER_MAX_DIGITS_PER_PART - strlen(number); i++)
            {
				strcat(version, "0");
			}
			strcat(version, number);
		}
        else if (partsAdded >= 4)
        {
			debug("Warning:\tformatJavaVersion() too many parts added.\n");
			break;
		}
		partsAdded++;

		pos += curPartLen + 1;
		if (pos >= originalVersion + strlen(originalVersion))
        {
			break;
		}
	}

	for (i = partsAdded; i < 3; i++)
    {
		strcat(version, ".");
		int j;
		for (j = 0; j < JRE_VER_MAX_DIGITS_PER_PART; j++)
        {
			strcat(version, "0");
		}
	}
}

void regSearch(const char* keyName, const int searchType)
{
	HKEY hKey;
	const DWORD wow64KeyMask = searchType & KEY_WOW64_64KEY;

	debug("%s-bit search:\t%s...\n", wow64KeyMask ? "64" : "32", keyName);

	if (!RegOpenKeyEx(HKEY_LOCAL_MACHINE,
			keyName,
			0,
	        KEY_READ | wow64KeyMask,
			&hKey) == ERROR_SUCCESS)
	{
		return;
	}

	DWORD x = 0;
	unsigned long versionSize = _MAX_PATH;
	FILETIME time;
	char fullKeyName[_MAX_PATH] = {0};
	char originalVersion[_MAX_PATH] = {0};
	char version[_MAX_PATH] = {0};

	while (RegEnumKeyEx(
				hKey,			// handle to key to enumerate
				x++,			// index of subkey to enumerate
				originalVersion,// address of buffer for subkey name
				&versionSize,	// address for size of subkey buffer
				NULL,			// reserved
				NULL,			// address of buffer for class string
				NULL,			// address for size of class buffer
				&time) == ERROR_SUCCESS)
	{
		strcpy(fullKeyName, keyName);
		appendPath(fullKeyName, originalVersion);
		debug("Check:\t\t%s\n", fullKeyName);
        formatJavaVersion(version, originalVersion);

		if (strcmp(version, search.javaMinVer) >= 0
				&& (!*search.javaMaxVer || strcmp(version, search.javaMaxVer) <= 0)
				&& strcmp(version, search.foundJavaVer) > 0
				&& isJavaHomeValid(fullKeyName, searchType))
		{
			strcpy(search.foundJavaVer, version);
			strcpy(search.foundJavaKey, fullKeyName);
			search.foundJava = searchType;
			debug("Match:\t\t%s\n", version);
		}
		else
		{
			debug("Ignore:\t\t%s\n", version);
		}

		versionSize = _MAX_PATH;
	}

	RegCloseKey(hKey);
}

BOOL isJavaHomeValid(const char* keyName, const int searchType)
{
	BOOL valid = FALSE;
	HKEY hKey;
	char path[_MAX_PATH] = {0};

	if (RegOpenKeyEx(HKEY_LOCAL_MACHINE,
			keyName,
			0,
            KEY_READ | (searchType & KEY_WOW64_64KEY),
			&hKey) == ERROR_SUCCESS)
	{
		unsigned char buffer[_MAX_PATH] = {0};
		unsigned long bufferlength = _MAX_PATH;
		unsigned long datatype;

		if (RegQueryValueEx(hKey, "JavaHome", NULL, &datatype, buffer,
				&bufferlength) == ERROR_SUCCESS)
		{
			int i = 0;
			do
			{
				path[i] = buffer[i];
			} while (path[i++] != 0);
			
			valid = isLauncherPathValid(path);
		}
		RegCloseKey(hKey);
	}

	if (valid)
	{
		strcpy(search.foundJavaHome, path);
	}
	else
	{
		search.corruptedJreFound = TRUE;
	}

	return valid;
}

BOOL isLauncherPathValid(const char* path)
{
	struct _stat statBuf;
	char launcherPath[_MAX_PATH] = {0};
	BOOL result = FALSE;

	if (*path)
	{
		strcpy(launcherPath, path);
		appendLauncher(launcherPath);
		result = _stat(launcherPath, &statBuf) == 0;

		if (!result)
		{
			// Don't display additional info in the error popup.
			SetLastError(0);
		}
	}

	debug("Check launcher:\t%s %s\n", launcherPath, result ? "(OK)" : "(not found)");
	return result;
}

void regSearchWow(const char* keyName, const int searchType)
{
	if (search.runtimeBits == INIT_RUNTIME_BITS)
	{
		search.runtimeBits = loadInt(RUNTIME_BITS);
	}

	switch (search.runtimeBits)
	{
		case USE_64_BIT_RUNTIME:
			if (wow64)
			{
				regSearch(keyName, searchType | KEY_WOW64_64KEY);
			}
			break;

		case USE_64_AND_32_BIT_RUNTIME:
			if (wow64)
			{
				regSearch(keyName, searchType | KEY_WOW64_64KEY);
				
				if ((search.foundJava & KEY_WOW64_64KEY) != NO_JAVA_FOUND)
				{
					break;
				}
			}

			regSearch(keyName, searchType);
			break;

		case USE_32_AND_64_BIT_RUNTIME:
			regSearch(keyName, searchType);

			if (search.foundJava != NO_JAVA_FOUND
				&& (search.foundJava & KEY_WOW64_64KEY) == NO_JAVA_FOUND)
			{
				break;
			}

			if (wow64)
			{
				regSearch(keyName, searchType | KEY_WOW64_64KEY);
			}
			break;

		case USE_32_BIT_RUNTIME:
			regSearch(keyName, searchType);
			break;
			
		default:
            debug("Runtime bits:\tFailed to load.\n");
            break;
	}
}

void regSearchJreSdk(const char* jreKeyName, const char* sdkKeyName,
		const int jdkPreference)
{
	if (jdkPreference == JDK_ONLY || jdkPreference == PREFER_JDK)
	{
		regSearchWow(sdkKeyName, FOUND_SDK);
		if (jdkPreference != JDK_ONLY)
		{
			regSearchWow(jreKeyName, FOUND_JRE);
		}
	}
	else
	{
		// jdkPreference == JRE_ONLY or PREFER_JRE
		regSearchWow(jreKeyName, FOUND_JRE);
		if (jdkPreference != JRE_ONLY)
		{
			regSearchWow(sdkKeyName, FOUND_SDK);
		}
	}
}

BOOL findJavaHome(char* path, const int jdkPreference)
{
    debugAll("findJavaHome()\n");
	regSearchJreSdk("SOFTWARE\\JavaSoft\\Java Runtime Environment",
					"SOFTWARE\\JavaSoft\\Java Development Kit",
					jdkPreference);

    // Java 9 support
	regSearchJreSdk("SOFTWARE\\JavaSoft\\JRE",
					"SOFTWARE\\JavaSoft\\JDK",
					jdkPreference);

    // IBM Java 1.8
	if (search.foundJava == NO_JAVA_FOUND)
	{
		regSearchJreSdk("SOFTWARE\\IBM\\Java Runtime Environment",
						"SOFTWARE\\IBM\\Java Development Kit",
						jdkPreference);
	}
	
	// IBM Java 1.7 and earlier
	if (search.foundJava == NO_JAVA_FOUND)
	{
		regSearchJreSdk("SOFTWARE\\IBM\\Java2 Runtime Environment",
						"SOFTWARE\\IBM\\Java Development Kit",
						jdkPreference);
	}
	
	if (search.foundJava != NO_JAVA_FOUND)
	{
		strcpy(path, search.foundJavaHome);
		debug("Runtime used:\t%s (%s-bit)\n", search.foundJavaVer,
				(search.foundJava & KEY_WOW64_64KEY) != NO_JAVA_FOUND ? "64" : "32");
		return TRUE;	
	}
	
	return FALSE;
}

/*
 * Extract the executable name, returns path length.
 */
int getExePath(char* exePath)
{
    if (GetModuleFileName(hModule, exePath, _MAX_PATH) == 0)
	{
        return -1;
    }
	return strrchr(exePath, '\\') - exePath;
}

void appendPath(char* basepath, const char* path)
{
	if (basepath[strlen(basepath) - 1] != '\\')
	{
		strcat(basepath, "\\");
	}
	strcat(basepath, path);
}

void appendLauncher(char* jrePath)
{
    if (console)
	{
	    appendPath(jrePath, "bin\\java.exe");
    }
	else
	{
        appendPath(jrePath, "bin\\javaw.exe");
    }
}

void appendAppClasspath(char* dst, const char* src)
{
	strcat(dst, src);
	strcat(dst, ";");
}

/* 
 * Expand environment %variables%
 */
BOOL expandVars(char *dst, const char *src, const char *exePath, const int pathLen)
{
    char varName[STR] = {0};
    char varValue[MAX_VAR_SIZE] = {0};

    while (strlen(src) > 0)
	{
        char *start = strchr(src, '%');
        if (start != NULL)
		{
            char *end = strchr(start + 1, '%');
            if (end == NULL)
			{
                return FALSE;
            }
            // Copy content up to %VAR%
            strncat(dst, src, start - src);
            // Insert value of %VAR%
            *varName = 0;
            strncat(varName, start + 1, end - start - 1);
            // Remember value start for logging
            char *currentVarValue = dst + strlen(dst);
            
			if (strcmp(varName, "EXEDIR") == 0)
			{
                strncat(dst, exePath, pathLen);
            }
			else if (strcmp(varName, "EXEFILE") == 0)
			{
                strcat(dst, exePath);
            }
			else if (strcmp(varName, "PWD") == 0)
			{
                GetCurrentDirectory(_MAX_PATH, dst + strlen(dst));
            }
			else if (strcmp(varName, "OLDPWD") == 0)
			{
                strcat(dst, oldPwd);
			}
            else if (strcmp(varName, "JREHOMEDIR") == 0)
			{
                strcat(dst, search.foundJavaHome);
			}
			else if (strstr(varName, HKEY_STR) == varName)
			{
				regQueryValue(varName, dst + strlen(dst), BIG_STR);
            }
			else if (strcmp(varName, "") == 0)
			{
                strcat(dst, "%");
            }
			else if (GetEnvironmentVariable(varName, varValue, MAX_VAR_SIZE) > 0)
			{
                strcat(dst, varValue);
            }

            debug("Substitute:\t%s = %s\n", varName, currentVarValue);
            src = end + 1;
        }
		else
		{
            // Copy remaining content
            strcat(dst, src);
            break;
        }
	}
	return TRUE;
}

void appendHeapSizes(char *dst)
{
	MEMORYSTATUSEX statex;
	statex.dwLength = sizeof(statex);
	GlobalMemoryStatusEx(&statex);

	appendHeapSize(dst, INITIAL_HEAP_SIZE, INITIAL_HEAP_PERCENT,
			statex.ullAvailPhys, "-Xms");
	appendHeapSize(dst, MAX_HEAP_SIZE, MAX_HEAP_PERCENT,
			statex.ullAvailPhys, "-Xmx");
}

void appendHeapSize(char *dst, const int megabytesID, const int percentID,
		const DWORDLONG availableMemory, const char *option)
{
	const int mb = 1048576;			// 1 MB
	const int mbLimit32 = 1024;  	// Max heap size in MB on 32-bit JREs
	const int megabytes = loadInt(megabytesID);
	const int percent = loadInt(percentID);
	const int availableMb = availableMemory * percent / (100 * mb);	// 100% * 1 MB
    int heapSizeMb = availableMb > megabytes ? availableMb : megabytes;

	if (heapSizeMb > 0)
	{
		if (!(search.foundJava & KEY_WOW64_64KEY) && heapSizeMb > mbLimit32)
		{
			debug("Heap limit:\tReduced %d MB heap size to 32-bit maximum %d MB\n",
					heapSizeMb, mbLimit32);
			heapSizeMb = mbLimit32;
		}

		debug("Heap %s:\tRequested %d MB / %d%%, Available: %d MB, Heap size: %d MB\n",
				option, megabytes, percent, (int)(availableMemory / mb), heapSizeMb);
		strcat(dst, option);
		_itoa(heapSizeMb, dst + strlen(dst), 10);				// 10 -- radix
		strcat(dst, "m ");
	}
}

void setJvmOptions(char *jvmOptions, const char *exePath)
{
	if (loadString(JVM_OPTIONS, jvmOptions))
	{
		strcat(jvmOptions, " ");
	}

	/*
	 * Load additional JVM options from .l4j.ini file
	 * Options are separated by spaces or CRLF
	 * # starts an inline comment
	 */
	char iniFilePath[_MAX_PATH] = {0};
	strncpy(iniFilePath, exePath, strlen(exePath) - 3);
	strcat(iniFilePath, "l4j.ini");
	long hFile;

	if ((hFile = _open(iniFilePath, _O_RDONLY)) != -1)
	{
		debug("Loading:\t%s\n", iniFilePath);
		const int jvmOptLen = strlen(jvmOptions);
		char* src = jvmOptions + jvmOptLen;
		char* dst = src;
		const int len = _read(hFile, src, MAX_ARGS - jvmOptLen - BIG_STR);
		BOOL copy = TRUE;
		int i;
		for (i = 0; i < len; i++, src++)
		{
			if (*src == '#')
			{
				copy = FALSE;
			}
			else if (*src == 13 || *src == 10)
			{
				copy = TRUE;
				if (dst > jvmOptions && *(dst - 1) != ' ')
				{
					*dst++ = ' ';
				}
			}
			else if (copy)
			{
				*dst++ = *src;
			}
		}
		*dst = 0;
		if (len > 0 && *(dst - 1) != ' ')
		{
			strcat(jvmOptions, " ");
		}
		_close(hFile);
	}	
}

BOOL createMutex()
{
	char mutexName[STR] = {0};

	loadString(MUTEX_NAME, mutexName);

	if (*mutexName)
	{
        debug("Create mutex:\t%s\n", mutexName);
		SECURITY_ATTRIBUTES security;
		security.nLength = sizeof(SECURITY_ATTRIBUTES);
		security.bInheritHandle = TRUE;
		security.lpSecurityDescriptor = NULL;
		CreateMutexA(&security, FALSE, mutexName);

		if (GetLastError() == ERROR_ALREADY_EXISTS)
		{
			debug(ERROR_FORMAT, "Instance already exists.");
			return FALSE;
		}
	}
	
	return TRUE;
}

void setWorkingDirectory(const char *exePath, const int pathLen)
{
	char workingDir[_MAX_PATH] = {0};
	char tmpPath[_MAX_PATH] = {0};

	GetCurrentDirectory(_MAX_PATH, oldPwd);

	if (loadString(CHDIR, tmpPath))
	{
		strncpy(workingDir, exePath, pathLen);
		appendPath(workingDir, tmpPath);
		_chdir(workingDir);
		debug("Working dir:\t%s\n", workingDir);
	}
}

BOOL bundledJreSearch(const char *exePath, const int pathLen)
{
    debugAll("bundledJreSearch()\n");
	char tmpPath[_MAX_PATH] = {0};
    BOOL is64BitJre = loadBool(BUNDLED_JRE_64_BIT);

    if (!wow64 && is64BitJre)
    {
        debug("Bundled JRE:\tCannot use 64-bit runtime on 32-bit OS.\n");
        return FALSE;
    }
    
	if (loadString(JRE_PATH, tmpPath))
	{
		char jrePath[MAX_ARGS] = {0};
		expandVars(jrePath, tmpPath, exePath, pathLen);
		debug("Bundled JRE:\t%s\n", jrePath);

		if (jrePath[0] == '\\' || jrePath[1] == ':')
		{
			// Absolute
			strcpy(launcher.cmd, jrePath);
		}
		else
		{
			// Relative
			strncpy(launcher.cmd, exePath, pathLen);
			appendPath(launcher.cmd, jrePath);
		}

		if (isLauncherPathValid(launcher.cmd))
		{
            search.foundJava = is64BitJre ? FOUND_BUNDLED | KEY_WOW64_64KEY : FOUND_BUNDLED;
			strcpy(search.foundJavaHome, launcher.cmd);
			return TRUE;
		}
    }

    return FALSE;
}

BOOL installedJreSearch()
{
    debugAll("installedJreSearch()\n");
	return *search.javaMinVer && findJavaHome(launcher.cmd, loadInt(JDK_PREFERENCE));
}

void createJreSearchError()
{
	if (*search.javaMinVer)
	{
		loadString(JRE_VERSION_ERR, error.msg);
		strcat(error.msg, " ");
		strcat(error.msg, search.originalJavaMinVer);
	
		if (*search.javaMaxVer)
		{
			strcat(error.msg, " - ");
			strcat(error.msg, search.originalJavaMaxVer);
		}
	
		if (search.runtimeBits == USE_64_BIT_RUNTIME
				|| search.runtimeBits == USE_32_BIT_RUNTIME)
		{
			strcat(error.msg, " (");
			strcat(error.msg, search.runtimeBits == USE_64_BIT_RUNTIME ? "64" : "32");
			strcat(error.msg, "-bit)");
		}			
		
		if (search.corruptedJreFound)
		{
			char launcherErrMsg[BIG_STR] = {0};
	
			if (loadString(LAUNCHER_ERR, launcherErrMsg))
			{
				strcat(error.msg, "\n");
				strcat(error.msg, launcherErrMsg);
			}
		}
	
		loadString(DOWNLOAD_URL, error.url);
	}
	else
	{
		loadString(BUNDLED_JRE_ERR, error.msg);
	}
}

BOOL jreSearch(const char *exePath, const int pathLen)
{
    debugAll("jreSearch()\n");
	BOOL result = TRUE;

	search.bundledJreAsFallback = loadBool(BUNDLED_JRE_AS_FALLBACK);
	loadString(JAVA_MIN_VER, search.originalJavaMinVer);
	formatJavaVersion(search.javaMinVer, search.originalJavaMinVer);
	debug("Java min ver:\t%s\n", search.javaMinVer);
	loadString(JAVA_MAX_VER, search.originalJavaMaxVer);
    formatJavaVersion(search.javaMaxVer, search.originalJavaMaxVer);
    debug("Java max ver:\t%s\n", search.javaMaxVer);

	if (search.bundledJreAsFallback)
	{
		if (!installedJreSearch())
		{
			result = bundledJreSearch(exePath, pathLen);
		}
	}
	else
	{
		if (!bundledJreSearch(exePath, pathLen))
		{
			result = installedJreSearch();
		}
	}
	
	if (!result)
	{
		createJreSearchError();
	}

	return result;
}

/*
 * Append a path to the Path environment variable
 */
BOOL appendToPathVar(const char* path)
{
	char chBuf[MAX_VAR_SIZE] = {0};
	const int pathSize = GetEnvironmentVariable("Path", chBuf, MAX_VAR_SIZE);

	if (MAX_VAR_SIZE - pathSize - 1 < strlen(path))
	{
		return FALSE;
	}

	strcat(chBuf, ";");
	strcat(chBuf, path);
	return SetEnvironmentVariable("Path", chBuf);
}

BOOL appendJreBinToPathVar()
{
	// Append a path to the Path environment variable
	char jreBinPath[_MAX_PATH] = {0};
	strcpy(jreBinPath, launcher.cmd);
	strcat(jreBinPath, "\\bin");

	if (!appendToPathVar(jreBinPath))
	{
		debug(ERROR_FORMAT, "appendToPathVar failed.");
		return FALSE;
	}
	
	return TRUE;
}

void setEnvironmentVariables(const char *exePath, const int pathLen)
{
	char tmp[MAX_ARGS] = {0};
	char envVars[MAX_VAR_SIZE] = {0};
	loadString(ENV_VARIABLES, envVars);
	char *var = strtok(envVars, "\t");

	while (var != NULL)
	{
		char *varValue = strchr(var, '=');
		*varValue++ = 0;
		*tmp = 0;
		expandVars(tmp, varValue, exePath, pathLen);
		debug("Set var:\t%s = %s\n", var, tmp);
		SetEnvironmentVariable(var, tmp);
		var = strtok(NULL, "\t"); 
	}
}

void setMainClassAndClassPath(const char *exePath, const int pathLen)
{
	char classPath[MAX_ARGS] = {0};
	char expandedClassPath[MAX_ARGS] = {0};
	char jar[_MAX_PATH] = {0};
	char fullFileName[_MAX_PATH] = {0};
	const BOOL wrapper = loadBool(WRAPPER);
	loadString(JAR, jar);

	if (loadString(MAIN_CLASS, launcher.mainClass))
	{
        debug("Main class:\t%s\n", launcher.mainClass);

		if (!loadString(CLASSPATH, classPath))
		{
			debug("Info:\t\tClasspath not defined.\n");
		}
		
		expandVars(expandedClassPath, classPath, exePath, pathLen);
		strcat(launcher.args, "-classpath \"");

		if (wrapper)
		{
			appendAppClasspath(launcher.args, exePath);
		}
		else if (*jar)
		{
			appendAppClasspath(launcher.args, jar);
		}

		// Deal with wildcards or >> strcat(launcherArgs, exp); <<
		char* cp = strtok(expandedClassPath, ";");

		while(cp != NULL)
		{
			debug("Add classpath:\t%s\n", cp);
			if (strpbrk(cp, "*?") != NULL)
			{
				char* lastBackslash = strrchr(cp, '\\');
				int pathLen = lastBackslash != NULL ? lastBackslash - cp + 1 : 0;
				*fullFileName = 0;
				strncpy(fullFileName, cp, pathLen);
				char* fileName = fullFileName + pathLen;
				*fileName = 0;
				struct _finddata_t c_file;
				long hFile;

				if ((hFile = _findfirst(cp, &c_file)) != -1L)
				{
					do
					{
						strcpy(fileName, c_file.name);
						appendAppClasspath(launcher.args, fullFileName);
						debug("      \"      :\t%s\n", fullFileName);
					} while (_findnext(hFile, &c_file) == 0);
				}

				_findclose(hFile);
			}
			else
			{
				appendAppClasspath(launcher.args, cp);
			}
			cp = strtok(NULL, ";");
		}

		*(launcher.args + strlen(launcher.args) - 1) = 0;
		strcat(launcher.args, "\" ");
		strcat(launcher.args, launcher.mainClass);
	}
	else if (wrapper)
	{
       	strcat(launcher.args, "-jar \"");
		strcat(launcher.args, exePath);
   		strcat(launcher.args, "\"");
    }
	else
	{
       	strcat(launcher.args, "-jar \"");
        strncat(launcher.args, exePath, pathLen);
        appendPath(launcher.args, jar);
       	strcat(launcher.args, "\"");
    }
}

void setCommandLineArgs(const char *lpCmdLine)
{
	char tmp[MAX_ARGS] = {0};

	// Constant command line arguments
	if (loadString(CMD_LINE, tmp))
	{
		strcat(launcher.args, " ");
		strcat(launcher.args, tmp);
	}

	// Command line arguments
	if (*lpCmdLine)
	{
		strcpy(tmp, lpCmdLine);
		char* dst;
		while ((dst = strstr(tmp, "--l4j-")) != NULL)
		{
			char* src = strchr(dst, ' ');
			if (src == NULL || *(src + 1) == 0)
			{
				*dst = 0;
			}
			else
			{
				strcpy(dst, src + 1);
			}
		}
		if (*tmp)
		{
			strcat(launcher.args, " ");
			strcat(launcher.args, tmp);
		}
	}
}

int prepare(const char *lpCmdLine)
{
	if (!initGlobals())
	{
		return FALSE;
	}

	// Get executable path
	char exePath[_MAX_PATH] = {0};
	int pathLen = getExePath(exePath);

	if (pathLen == -1)
	{
		return FALSE;
	}

	if (!initializeLogging(lpCmdLine, exePath, pathLen))
	{
		return FALSE;
	}

    setWow64Flag();

	// Set default error message, title and optional support web site url.
	loadString(ERR_TITLE, error.title);
	loadString(SUPPORT_URL, error.url);

	if (!loadString(STARTUP_ERR, error.msg))
	{
		debug(ERROR_FORMAT, "Startup error message not defined.");
		return FALSE;			
	}

	// Single instance
	if (!createMutex())
	{
		return ERROR_ALREADY_EXISTS;
	}

	setWorkingDirectory(exePath, pathLen);
    
	if (!jreSearch(exePath, pathLen))
    {
		return FALSE;
	}

	if (!appendJreBinToPathVar())
	{
		return FALSE;
	}

	setEnvironmentVariables(exePath, pathLen);
	processPriority = loadInt(PRIORITY_CLASS);
	appendLauncher(launcher.cmd);
	appendHeapSizes(launcher.args);

	char jvmOptions[MAX_ARGS] = {0};
	setJvmOptions(jvmOptions, exePath);
	expandVars(launcher.args, jvmOptions, exePath, pathLen);
	setMainClassAndClassPath(exePath, pathLen);
	setCommandLineArgs(lpCmdLine);

	debug("Launcher:\t%s\n", launcher.cmd);
	debug("Launcher args:\t%s\n", launcher.args);
	debug("Args length:\t%d/32768 chars\n", strlen(launcher.args));
	return TRUE;
}

void closeProcessHandles()
{
	CloseHandle(processInformation.hThread);
	CloseHandle(processInformation.hProcess);
}

BOOL execute(const BOOL wait, DWORD *dwExitCode)
{
	STARTUPINFO si;

    memset(&processInformation, 0, sizeof(processInformation));
    memset(&si, 0, sizeof(si));
    si.cb = sizeof(si);

	char cmdline[MAX_ARGS] = {0};
    strcpy(cmdline, "\"");
	strcat(cmdline, launcher.cmd);
	strcat(cmdline, "\" ");
	strcat(cmdline, launcher.args);

	if (CreateProcess(NULL, cmdline, NULL, NULL,
			TRUE, processPriority, NULL, NULL, &si, &processInformation))
	{
		if (wait)
		{
			WaitForSingleObject(processInformation.hProcess, INFINITE);
			GetExitCodeProcess(processInformation.hProcess, dwExitCode);
			closeProcessHandles();
		}
		else
		{
			*dwExitCode = 0;
		}
		
		return TRUE;
	}

	*dwExitCode = -1;
	return FALSE;
}

const char* getJavaHome()
{
	return search.foundJavaHome;
}

const char* getMainClass()
{
	return launcher.mainClass;	
}

const char* getLauncherArgs()
{
    return launcher.args;    
}

