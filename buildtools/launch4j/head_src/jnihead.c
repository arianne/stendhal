/*
	Launch4j (http://launch4j.sourceforge.net/)
	Cross-platform Java application wrapper for creating Windows native executables.

	Copyright (c) 2007 Ryan Rusaw

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

#include "jnihead.h"

/* Java Invocation API stuff */
typedef jint (JNICALL CreateJavaVM_t)(JavaVM **pvm, void **env, void *args);
JavaVM* g_pJavaVM = NULL;
JNIEnv* g_pJNIEnv = NULL;
JavaVMInitArgs g_sJavaVMInitArgs;
char    g_rgcMnClsArgs[MAX_ARGS]  = {0};
char	g_rgcMnCls[_MAX_PATH] = {0};
char	g_rgcCurrJrePth[_MAX_PATH]  = {0};
HINSTANCE  g_hInstance;
const char* g_pcSep = " \t\f\r\n\v";

int getArgCount(const char* pcArgStr) 
{
	const char *pCopy;
	int iArgCnt= 0;
	int bInWtSpc = 1;
	for(pCopy = pcArgStr; *pCopy; pCopy++)
	{
		if (!isspace(*pCopy) && bInWtSpc)
		{
			iArgCnt++;
		}
		bInWtSpc = isspace(*pCopy);
	}
	return iArgCnt;
}

void saveJvmOptions(const char *jrePath, const char *mainClass, const char *pcOpts)
{
	strcpy(g_rgcCurrJrePth, jrePath);
	strcpy(g_rgcMnCls, mainClass);

	char 				rgcOptCpy[MAX_ARGS] = {0};
	int 				iArgCnt = 0, iCurrArg = 0, iSkipArgCnt = 0;
	char 				*pcCurrOpt;
	char 				**prgcVmArgs = NULL;
	strncpy(rgcOptCpy, pcOpts, MAX_ARGS - 1);
	

	iArgCnt = getArgCount(rgcOptCpy);
	if (iArgCnt > 0)
	{
		/* Allocate iArgCnt char pointers */
		prgcVmArgs = malloc(iArgCnt * sizeof(char *)); 
		for (pcCurrOpt = strtok(rgcOptCpy, g_pcSep); pcCurrOpt; pcCurrOpt = strtok(NULL, g_pcSep), iCurrArg++)
		{
			/* Use the allocated pointers to make an array of substrings */
			prgcVmArgs[iCurrArg] = pcCurrOpt;
		}
		/* Allocat iArgCnt JavaVMOptions for the g_sJavaVMInitArgs struct */
		g_sJavaVMInitArgs.options = malloc(iArgCnt * sizeof(JavaVMOption));
		memset(g_sJavaVMInitArgs.options, 0, iArgCnt * sizeof(JavaVMOption));
		char* rgcClsPth = 0;
		/* Copy the tokenized array into the allocated JavaVMOption array,
		 * with some special handling for classpath related arguments */
		for (iCurrArg = 0; iCurrArg < iArgCnt; iCurrArg++)
		{
			if ((strcmp(prgcVmArgs[iCurrArg], "-classpath") == 0) ||
					  (strcmp(prgcVmArgs[iCurrArg], "-jar") == 0))
			{
				iCurrArg++;
				iSkipArgCnt++;
				if (iCurrArg < iArgCnt)
				{
					int iOffset = *prgcVmArgs[iCurrArg] == '"' ? 1 : 0;
					char rgcTmp[MAX_ARGS] = {0};
					/* Remove leading and trailing "'s */\
					strncpy(rgcTmp, prgcVmArgs[iCurrArg] + iOffset, 
						strlen(prgcVmArgs[iCurrArg]) - iOffset);
					if (rgcTmp[strlen(rgcTmp)-1] == '"')
						rgcTmp[strlen(rgcTmp)-1] = '\0';
					/* If we haven't defined a classpath yet start one, otherwise
					 * we just append the this classpath to it */
					if (!rgcClsPth)
					{
						rgcClsPth = malloc(MAX_ARGS * sizeof(char));
						memset(rgcClsPth, 0, MAX_ARGS * sizeof(char));
						sprintf(rgcClsPth,"-Djava.class.path=%s", rgcTmp);
						g_sJavaVMInitArgs.options[iCurrArg - iSkipArgCnt].optionString = rgcClsPth;
					}
					else
					{
						iSkipArgCnt++;
						strcat(rgcClsPth,";");
						strcat(rgcClsPth,rgcTmp);
					}
					
				}
			}
			else
			{
				g_sJavaVMInitArgs.options[iCurrArg - iSkipArgCnt].optionString
					= malloc(strlen(prgcVmArgs[iCurrArg]) + 1);
				strcpy(g_sJavaVMInitArgs.options[iCurrArg - iSkipArgCnt].optionString, 
						prgcVmArgs[iCurrArg]);
			}
		}
		g_sJavaVMInitArgs.nOptions = iArgCnt - iSkipArgCnt;
		/* Free the malloc'd memory, we dont want to leak */
		free(prgcVmArgs);
	}
}

JNIEnv* createVm()
{
	int				iRetVal;
	CreateJavaVM_t	*pfnCreateJavaVM;
	char			rgcLibPth[_MAX_PATH + 18];
	// sprintf(rgcLibPth, "%s\\bin\\client\\jvm.dll", g_rgcCurrJrePth); // TODO - could be client or server
	sprintf(rgcLibPth, "%s\\bin\\client\\jvm.dll", g_rgcCurrJrePth);
	
	/* Get a handle to the jvm dll */
	if ((g_hInstance = LoadLibrary(rgcLibPth)) == NULL)
	{
		return NULL;
	}

	/* Get the CreateJavaVM() function */
	pfnCreateJavaVM = (CreateJavaVM_t *)GetProcAddress(g_hInstance, "JNI_CreateJavaVM");

	if (pfnCreateJavaVM == NULL)
	{
		return NULL;
	}

	g_sJavaVMInitArgs.version				= JNI_VERSION_1_2;
	g_sJavaVMInitArgs.ignoreUnrecognized	= JNI_TRUE;
	/* Start the VM */
	iRetVal = pfnCreateJavaVM(&g_pJavaVM, (void **)&g_pJNIEnv, &g_sJavaVMInitArgs);

	if (iRetVal != 0)
	{
		return NULL;
	}
	
	return g_pJNIEnv;
}

int invokeMainClass(JNIEnv* psJNIEnv) 
{
	jclass			jcMnCls;
	jmethodID		jmMnMthd;
	jobjectArray	joAppArgs;
	jstring			jsAppArg;
	jthrowable 		jtExcptn;
	char			*pcCurrArg;
	int				iArgCnt= 0, iOption = -1;
	char			rgcMnClsCpy[MAX_ARGS] = {0};

	/* Ensure Java JNI Env is set up */
	if(psJNIEnv == NULL)
	{
		return -1;
	}
	/* We need a class name */
	if (g_rgcMnCls[0] == '\0')
	{
		return -1;
	}
	else
	{
		/* Replace . with / in fully qualified class name */
		char *pClsNm;
		for(pClsNm = g_rgcMnCls; *pClsNm; pClsNm++)
		{
			if(*pClsNm == '.')
				*pClsNm = '/';
		}
	}
	/* Find the class */
	jcMnCls = (*psJNIEnv)->FindClass(psJNIEnv, g_rgcMnCls);
	jtExcptn = (*psJNIEnv)->ExceptionOccurred(psJNIEnv);
	if (jtExcptn != NULL)
	{
		(*psJNIEnv)->ExceptionDescribe(psJNIEnv);
		return -1;
	}
	if (jcMnCls == NULL)
	{
		return -1;
	}
	/* Get the static main method */
	jmMnMthd = (*psJNIEnv)->GetStaticMethodID(psJNIEnv, jcMnCls, "main", "([Ljava/lang/String;)V");
	jtExcptn = (*psJNIEnv)->ExceptionOccurred(psJNIEnv);
	if (jtExcptn != NULL)
	{
		(*psJNIEnv)->ExceptionDescribe(psJNIEnv);
	}
	if (jmMnMthd == NULL)
	{
		return -1;
	}
	/* Build the String[] array if we need one */
	strncpy(rgcMnClsCpy, g_rgcMnClsArgs, MAX_ARGS);
	iArgCnt = getArgCount(rgcMnClsCpy);
	joAppArgs = (jobjectArray)(*psJNIEnv)->NewObjectArray(psJNIEnv, iArgCnt, 
		(*psJNIEnv)->FindClass(psJNIEnv, "java/lang/String"), NULL);
	jtExcptn = (*psJNIEnv)->ExceptionOccurred(psJNIEnv);
	if (jtExcptn != NULL)
	{
		(*psJNIEnv)->ExceptionDescribe(psJNIEnv);
		return -1;
	}
	for (pcCurrArg = strtok(rgcMnClsCpy, g_pcSep); pcCurrArg; pcCurrArg = strtok(NULL, g_pcSep))
	{
		iOption++;
		jsAppArg = (*psJNIEnv)->NewStringUTF(psJNIEnv, pcCurrArg);
		(*psJNIEnv)->SetObjectArrayElement(psJNIEnv, joAppArgs, iOption, jsAppArg);
		jtExcptn = (*psJNIEnv)->ExceptionOccurred(psJNIEnv);
		if(jtExcptn != NULL)
		{
			(*psJNIEnv)->ExceptionDescribe(psJNIEnv);
			return -1;
		}
	}
	/* Execute the class */
	(*psJNIEnv)->CallStaticVoidMethod(psJNIEnv, jcMnCls, jmMnMthd, joAppArgs);
	return 0;
}

void cleanupVm()
{
	/* Destroy the VM */
	(*g_pJavaVM)->DestroyJavaVM(g_pJavaVM);
}

BOOL executeVm(DWORD *dwExitCode)
{
	BOOL result = TRUE;
	*dwExitCode = -1;

	int iIdx;
	/* Use Invocation API */
	if (createVm())
	{
		*dwExitCode = invokeMainClass(g_pJNIEnv);
		cleanupVm();
	}
	else
	{
		result = FALSE;
	}

	/* Free the allocated memory */
	for (iIdx = 0; iIdx < g_sJavaVMInitArgs.nOptions; iIdx++)
	{
		free(g_sJavaVMInitArgs.options[iIdx].optionString);
	}
	free(g_sJavaVMInitArgs.options);

	return result;
}

