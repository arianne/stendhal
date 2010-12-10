#!/bin/sh
set STENDHAL_VERSION="0.90.5"

call ant compile_stendhaltools server_build

cd build\build_stendhaltools

set LOCALCLASSPATH=.;../../build/lib/stendhal-server-$STENDHAL_VERSION.jar;../../libs/marauroa.jar;../../libs/log4j.jar;../../libs/swing-layout.jar

javaw -cp "%LOCALCLASSPATH%" games.stendhal.tools.npcparsertestenv.TestEnvDlg

