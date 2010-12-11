@echo off
call ant compile_stendhaltools server_build

cd build\build_stendhaltools
set LOCALCLASSPATH=.;..\..\build\build_server;..\..\libs\marauroa.jar;..\..\libs\log4j.jar;..\..\libs\swing-layout.jar
javaw -cp "%LOCALCLASSPATH%" games.stendhal.tools.npcparsertestenv.TestEnvDlg
cd ..\..
