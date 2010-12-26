#!/bin/sh
ant compile_stendhaltools server_build

cd build/build_stendhaltools
LOCALCLASSPATH=.:../../build/build_server:../../libs/marauroa.jar:../../libs/log4j.jar:../../libs/swing-layout.jar:../..
java -cp "${LOCALCLASSPATH}" games.stendhal.tools.npcparsertestenv.TestEnvDlg
