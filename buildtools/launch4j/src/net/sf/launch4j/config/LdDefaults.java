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
 * Created on Sep 3, 2005
 */
package net.sf.launch4j.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LdDefaults {
	private static final List<String> GUI_OBJECTS = Arrays.asList(new String[] {
			"w32api/crt2.o",
			"head/guihead.o",
			"head/head.o" });

	private static final List<String> CONSOLE_OBJECTS = Arrays.asList(new String[] {
			"w32api/crt2.o",
			"head/consolehead.o",
			"head/head.o" });
	
	private static final List<String> JNI_GUI_32_OBJECTS = Arrays.asList(new String[] {
			"w32api_jni/crt2.o",
			"head_jni_BETA/jniguihead.o",
			"head_jni_BETA/head.o",
			"head_jni_BETA/jnihead.o" });

	private static final List<String> JNI_CONSOLE_32_OBJECTS = Arrays.asList(new String[] {
			"w32api_jni/crt2.o",
			"head_jni_BETA/jniconsolehead.o",
			"head_jni_BETA/head.o",
			"head_jni_BETA/jnihead.o" });

	private static final List<List<String>> HEADER_OBJECTS;

	private static final List<String> LIBS = Arrays.asList(new String[] {
			"w32api/libmingw32.a",
			"w32api/libgcc.a",
			"w32api/libmsvcrt.a",
			"w32api/libkernel32.a",
			"w32api/libuser32.a",
			"w32api/libadvapi32.a",
			"w32api/libshell32.a" });

	private static final List<String> JNI_LIBS = Arrays.asList(new String[] {
			"w32api_jni/libmingw32.a",
			"w32api_jni/libmingwex.a",
			"w32api_jni/libgcc.a",
			"w32api_jni/libmsvcrt.a",
			"w32api_jni/libmoldname.a",
			"w32api_jni/libkernel32.a",
			"w32api_jni/libuser32.a",
			"w32api_jni/libadvapi32.a",
			"w32api_jni/libshell32.a" });

	static {
		HEADER_OBJECTS = new ArrayList<List<String>>();
		HEADER_OBJECTS.add(GUI_OBJECTS);
		HEADER_OBJECTS.add(CONSOLE_OBJECTS);
		HEADER_OBJECTS.add(JNI_GUI_32_OBJECTS);
		HEADER_OBJECTS.add(JNI_CONSOLE_32_OBJECTS);
	}

	public static List<String> getHeaderObjects(int headerTypeIndex) {
		if (headerTypeIndex < 0 || headerTypeIndex > 3) {
			throw new IllegalArgumentException("headerTypeIndex is out of range: " + headerTypeIndex);
		}

		return HEADER_OBJECTS.get(headerTypeIndex);
	}
	
	public static List<String> getLibs(String headerType) {
		if (Config.GUI_HEADER.equals(headerType)
				|| Config.CONSOLE_HEADER.equals(headerType)) {
			return LIBS;
		}

		if (Config.JNI_GUI_HEADER_32.equals(headerType)
				|| Config.JNI_CONSOLE_HEADER_32.equals(headerType)) {
			return JNI_LIBS;
		}
		
		throw new IllegalArgumentException("Unknown headerType: " + headerType);
	}
}
