#!/bin/sh
STENDHAL_VERSION="0.90.5"

ant compile_stendhaltools server_build

cd build/build_stendhaltools

LOCALCLASSPATH=.:../../build/lib/stendhal-server-$STENDHAL_VERSION.jar:../../libs/marauroa.jar:../../libs/log4j.jar:../../libs/swing-layout.jar

java -cp "${LOCALCLASSPATH}" games.stendhal.tools.npcparsertestenv.TestEnvDlg

