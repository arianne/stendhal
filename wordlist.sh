#!/bin/sh
ant compile_stendhaltools server_build

LOCALCLASSPATH=build/build_stendhaltools:build/build_server:libs/marauroa.jar:libs/log4j.jar:libs/mysql-connector-java-5.1.5-bin.jar:libs/h2.jar
java -cp "${LOCALCLASSPATH}" games.stendhal.tools.npcparser.WordListUpdate
