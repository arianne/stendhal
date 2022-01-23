
actions {#lua_actions}
=======

[TOC]

## Introduction

Object for creating {{StendhalFile|master|src/games/stendhal/server/entity/npc/ChatAction.java|games.stendhal.server.entity.npc.ChatAction}} instances.

## Methods

; ''<span style="color:green;">actions:create</span>(function)''
: Creates a custom {{StendhalFile|master|src/games/stendhal/server/entity/npc/ChatAction.java|ChatAction}}.
: '''''function:''''' A Lua function to be executed when <code>ChatAction.fire</code> is called.
: ''returns:'' New <code>ChatAction</code> instance.

; ''<span style="color:green;">actions:multiple</span>(actionList)''
: Helper method for creating a {{StendhalFile|master|src/games/stendhal/server/entity/npc/action/MultipleActions.java|MultipleActions}} instance.
: '''''actionList:''''' A Lua table containing ChatAction instances.
: ''returns:'' New <code>MultipleActions</code> instance.
