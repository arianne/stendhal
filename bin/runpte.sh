#!/bin/sh
ant compile_stendhaltools server_build

LOCALCLASSPATH=build/build_stendhaltools:build/build_server:build/build_server_maps:libs/marauroa.jar:libs/log4j.jar:libs/commons-lang.jar:libs/groovy.jar:libs/mysql-connector-java-5.1.5-bin.jar:libs/h2.jar:libs/swing-layout.jar:.
java -cp "${LOCALCLASSPATH}" games.stendhal.tools.npcparser.TestEnvDlg
