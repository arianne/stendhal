@echo off
call ant compile_stendhaltools server_build

set LOCALCLASSPATH=libs\*;build\build_stendhaltools;build\build_server
javaw -cp "%LOCALCLASSPATH%" games.stendhal.tools.npcparser.WordListUpdate
