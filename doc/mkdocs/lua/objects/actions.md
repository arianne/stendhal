
[TOC]

## Introduction

Object for creating {@link games.stendhal.server.entity.npc.ChatAction} instances.

## Methods

---
### actions:create
<span style="color:green; font-weight:bold;">actions:create</span>(function)

- Creates a custom {@link games.stendhal.server.entity.npc.ChatAction}.
- Parameters:
    - ***function:*** A Lua function to be executed when <code>ChatAction.fire</code> is called.
- Returns: New <code>ChatAction</code> instance.

---
### actions:multiple
<span style="color:green; font-weight:bold;">actions:multiple</span>(actionList)

- Helper method for creating a {@link games.stendhal.server.entity.npc.action.MultipleActions} instance.
- Parameters:
    - ***actionList:*** A Lua table containing ChatAction instances.
- Returns: New <code>MultipleActions</code> instance.

[ChatAction]: https://stendhalgame.org/reference/java/
