
---
# Contents

[TOC]


---
# Introduction

Object instance: `actions`


---
## Description

Creates [`ChatAction`][ChatAction] instances.


---
# Methods

---
## actions:clearQuest
<div class="function">
    actions:clearQuest <span class="paramlist">slot</span>
</div>

- Removes quest slot from player. This is needed because it's impossible to pass [`nil`][LuaNil]
  values in a Lua [`table`][LuaTable].
- Parameters:
    - <span class="param">slot</span>
      <span class="datatype">[string][LuaString]</span>
      Quest identifier.
- Returns:
  <span class="datatype">[SetQuestAction]</span>
  Action that sets quest state to [`nil`][LuaNil].
- __TODO:__
    - Move to [quests:clearQuest][quests].


---
## actions:create
<div class="function">
    actions:create <span class="paramlist">func</span>
</div>
<div class="function">
    actions:create <span class="paramlist">func, args</span>
</div>

- Creates a custom [ChatAction].
- Parameters:
    - <span class="param">func</span>
      <span class="datatype">[function][LuaFunction]</span>
      Function to be executed when `ChatAction.fire` is called.
    - <span class="param">args</span>
      <span class="datatype">[table][LuaTable]&lt;[Object][java.lang.Object]&gt;</span>
      List of objects passed to the constructor.
- Returns:
  <span class="datatype">[ChatAction]</span>
  New action or [`nil`][LuaNil] if failed.


---
## actions:multiple
<div class="function">
    actions:multiple <span class="paramlist">actionList</span>
</div>

- Helper method for creating a [MultipleActions] instance.
- Parameters:
    - <span class="param">actionList</span>
      <span class="datatype">[table][LuaTable]&lt;[ChatAction]&gt;</span>
      List of action instances.
- Returns:
  <span class="datatype">[MultipleActions]</span>
  New action instance.


[quests]: /reference/lua/objects/quests

[ChatAction]: /reference/java/games/stendhal/server/entity/npc/ChatAction.html
[MultipleActions]: /reference/java/games/stendhal/server/entity/npc/action/MultipleActions.html
[SetQuestAction]: /reference/java/games/stendhal/server/entity/npc/action/SetQuestAction.html

[java.lang.Object]: https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Object.html

[LuaFunction]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaFunction.html
[LuaNil]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaNil.html
[LuaString]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaString.html
[LuaTable]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaTable.html
