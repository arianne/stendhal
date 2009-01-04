#!/bin/bash
export   
LOCALCLASSPATH=.:stendhal-server-0.71.jar:marauroa.jar:jython.jar:mysql-connector.jar:log4j.jar:simple.jar:groovy.jar:commons-lang.jar
 java -cp "${LOCALCLASSPATH}" games.stendhal.server.entity.npc.parser.WordListUpdate
