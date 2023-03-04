
[TOC]

## Introduction

Object for creating [ChatAction][] instances.

## Methods

---
### actions:create
<span style="color:green; font-weight:bold;">actions:create</span>(function)

- Creates a custom [ChatAction][].
- Parameters:
    - ***function:*** A Lua function to be executed when `ChatAction.fire` is called.
- Returns: New `ChatAction` instance.

---
### actions:multiple
<span style="color:green; font-weight:bold;">actions:multiple</span>(actionList)

- Helper method for creating a [MultipleActions][] instance.
- Parameters:
    - ***actionList:*** A Lua table containing ChatAction instances.
- Returns: New `MultipleActions` instance.


[ChatAction]: ../../java/games/stendhal/server/entity/npc/ChatAction.html
[MultipleActions]: ../../java/games/stendhal/server/entity/npc/action/MultipleActions.html
