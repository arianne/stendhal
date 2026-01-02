#!/usr/bin/env bash

STENDHAL_VERSION="1.49.5"
SERVER_JAR="stendhal-server-${STENDHAL_VERSION}.jar"

# change to server directory
#cd "$(dirname "$0")" || exit

# parse arguments
vm_args=()
app_args=()
for arg in "$@"; do
	if [[ "${arg}" =~ ^-D ]]; then
		vm_args+=("${arg}")
	else
		app_args+=("${arg}")
	fi
done

if [ -f "${SERVER_JAR}" ]; then
	LOCALCLASSPATH="./${SERVER_JAR}:./libs/*"
else
	LOCALCLASSPATH=".:./build/build_server:./build/build_server_maps:./build/build_server_script:./build/build_server_xmlconf:./libs/*"
fi

java -Xmx400m -cp "${LOCALCLASSPATH}" "${vm_args[@]}" games.stendhal.server.StendhalServer "${app_args[@]}"
