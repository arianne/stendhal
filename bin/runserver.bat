@echo off

set STENDHAL_VERSION=1.49.5
set SERVER_JAR=stendhal-server-%STENDHAL_VERSION%.jar

:: change to server directory
::for %%F in ("%0") do set server_root=%%~dpF
::cd "%server_root%"

:: parse arguments
set vm_args=
set app_args=
setlocal EnableDelayedExpansion
for %%A in (%*) do (
	set arg=%%A
	if ["-D"] == ["!arg:~0,2!"] (
		if [] == [!vm_args!] (
			set vm_args=!arg!
		) else (
			set vm_args=!vm_args! !arg!
		)
	) else (
		if [] == [!app_args!] (
			set app_args=!arg!
		) else (
			set app_args=!app_args! !arg!
		)
	)
)

if exist "%SERVER_JAR%" (
	set LOCALCLASSPATH=.\%SERVER_JAR%;.\libs\*
) else (
	set LOCALCLASSPATH=.;.\build\build_server;.\build\build_server_maps;.\build\build_server_script;.\build\build_server_xmlconf;.\libs\*;
)

java -Xmx400m -cp "%LOCALCLASSPATH%" %vm_args% games.stendhal.server.StendhalServer %app_args%

:end
@pause
