#!/bin/sh
ant compile_stendhaltools server_build

LOCALCLASSPATH=:./libs/*:./build/build_stendhaltools:./build/build_server
java -cp "${LOCALCLASSPATH}" games.stendhal.tools.npcparser.WordListUpdate
