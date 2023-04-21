
[TOC]

---
## Introduction

Object instance: `actions`

---
### Description

Creates [`ChatAction`][ChatAction] instances.

---
## Methods

---
### actions:clearQuest
<span style="color:green; font-wegith:bold;">actions:clearQuest</span>(questSlot)

- Removes quest slot from player. This is needed because it's impossible to pass [`nil`][LuaNil]
  values in a [`LuaTable`][LuaTable].
- Parameters:
    ***questSlot:*** Quest string identifier.
- Returns: New [`SetQuestAction`][SetQuestAction] that sets quest state to `nil`.
- **TODO:**
    - Move to [quests:clearQuest][quests].

---
### actions:create
<div class="function">
    actions:create <span class="params">(function)</span>
</div>
<div class="function">
    actions:create <span class="params">(function, args)</span>
</div>

- Creates a custom [`ChatAction`][ChatAction].
- Parameters:
    - ***function:*** A [`LuaFunction`][LuaFunction] to be executed when `ChatAction.fire` is
      called.
    - ***args:*** [`LuaTable`][LuaTable] of objects passed to the constructor.
- Returns: New `ChatAction` instance or [`nil`][LuaNil] if failed.

---
### actions:multiple
<div class="function">
    actions:multiple <span class="params">(actionList)</span>
</div>

- Helper method for creating a [`MultipleActions`][MultipleActions] instance.
- Parameters:
    - ***actionList:*** A [`LuaTable`][LuaTable] containing [`ChatAction`][ChatAction] instances.
- Returns: New `MultipleActions` instance.


[quests]: /reference/lua/objects/quests

[ChatAction]: /reference/java/games/stendhal/server/entity/npc/ChatAction.html
[MultipleActions]: /reference/java/games/stendhal/server/entity/npc/action/MultipleActions.html
[SetQuestAction]: /reference/java/games/stendhal/server/entity/npc/action/SetQuestAction.html

[LuaFunction]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaFunction.html
[LuaNil]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaNil.html
[LuaTable]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaTable.html
