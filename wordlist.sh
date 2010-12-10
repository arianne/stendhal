#!/bin/bash
STENDHAL_VERSION="0.90.5"

LOCALCLASSPATH=.:stendhal-server-$STENDHAL_VERSION.jar:marauroa.jar:mysql-connector.jar:log4j.jar:commons-lang.jar

java -cp "${LOCALCLASSPATH}" games.stendhal.server.entity.npc.parser.WordListUpdate
