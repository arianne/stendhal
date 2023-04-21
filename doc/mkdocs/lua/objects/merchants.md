
[TOC]

---
## Introduction

Object instance: `merchants`

---
### Description

Exposes merchant handling classes &amp; functions to Lua.

---
## Members

---
### merchants.shops
<div class="member">
    merchants.shops
</div>

- The [ShopsList][] instance.

---
## Methods

---
### merchants:add
<div class="function">
    merchants:add <span class="params">(merchantType, npc, prices)</span>
</div>
<div class="function">
    merchants:add <span class="params">(merchantType, npc, prices, addOffer)</span>
</div>

- Adds merchant behavior to a [SpeakerNPC][].
- Parameters:
    - ___merchantType:__ ([string][LuaString])_ If set to "buyer", will add buyer behavior, otherwise will be "seller".
    - ___npc:__ ([SpeakerNPC][])_ The NPC to add the behavior to.
    - ___prices:___ List of items &amp; their prices (can be instance of either
      [Map<String, Integer>][java.util.Map] or a [LuaTable][]).
    - ___addOffer:__ ([bool][LuaBoolean])_ If `true`, will add default replies for "offer" (default: `true`).
- TODO:
    - Use [ShopType][] for ___merchantType___ parameter.

---
### merchants:addBuyer
<div class="function">
    merchants:addBuyer <span class="params">(npc, prices)</span>
</div>
<div class="function">
    merchants:addBuyer <span class="params">(npc, prices, addOffer)</span>
</div>

- Adds buyer behavior to a [SpeakerNPC][].
- Parameters:
    - ___npc:__ ([SpeakerNPC][])_ The NPC to add the behavior to.
    - ___prices:___ List of items &amp; their prices (can be instance of either
      [Map<String, Integer>][java.util.Map] or a [LuaTable][]).
    - ___addOffer:__ ([bool][LuaBoolean])_ If `true`, will add default replies for "offer" (default:
      `true`).

---
### merchants:addSeller
<div class="function">
    merchants:addSeller <span class="params">(npc, prices)</span>
</div>
<div class="function">
    merchants:addSeller <span class="params">(npc, prices, addOffer)</span>
</div>

- Adds seller behavior to a [SpeakerNPC][].
- Parameters:
    - ___npc:__ ([SpeakerNPC][])_ The NPC to add the behavior to.
    - ___prices:___ List of items &amp; their prices (can be instance of either
      [Map<String, Integer>][java.util.Map] or a [LuaTable][]).
    - ___addOffer:__ ([bool][LuaBoolean])_ If `true`, will add default replies for "offer" (default:
      `true`).

---
## TODO

- Add support for [OutfitShopsList][].


[OutfitShopsList]: /reference/java/games/stendhal/server/entity/npc/shop/OutfitShopsList.html
[ShopType]: /reference/java/games/stendhal/server/entity/npc/shop/ShopType.html
[ShopsList]: /reference/java/games/stendhal/server/entity/npc/shop/ShopsList.html
[SpeakerNPC]: /reference/java/games/stendhal/server/entity/npc/SpeakerNPC.html

[java.util.Map]: https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Map.html

[LuaBoolean]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaBoolean.html
[LuaString]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaString.html
[LuaTable]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaTable.html
