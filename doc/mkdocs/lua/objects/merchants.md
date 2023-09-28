
---
# Contents

[TOC]


---
# Introduction

Object instance: `merchants`


---
## Description

Exposes merchant handling classes &amp; functions to Lua.


---
# Members


---
## merchants.shops
<div class="member">
    merchants.shops
</div>

- The [ShopsList] instance.


---
# Methods


---
## merchants:add
<div class="function">
    merchants:add <span class="paramlist">merchantType, npc, prices</span>
</div>
<div class="function">
    merchants:add <span class="paramlist">merchantType, npc, prices, addOffer</span>
</div>

- Adds merchant behavior to a [SpeakerNPC].
- Parameters:
    - <span class="param">merchantType</span>
      <span class="datatype">[string][LuaString]</span>
      If set to "buyer", will add buyer behavior, otherwise will be "seller".
    - <span class="param">npc</span>
      <span class="datatype">[SpeakerNPC]</span>
      The NPC to add the behavior to.
    - <span class="param">prices</span>
      List of items &amp; their prices (can be instance of either
      [Map<String, Integer>][java.util.Map] or a [table][LuaTable]).
    - <span class="param">addOffer</span>
      <span class="datatype">[bool][LuaBoolean]</span>
      If `true`, will add default replies for "offer" (default: `true`).
- TODO:
    - Use [ShopType] for ___merchantType___ parameter.
    - <span class="fixme">Lua table not working for "prices" parameter</span>


---
## merchants:addBuyer
<div class="function">
    merchants:addBuyer <span class="paramlist">npc, prices</span>
</div>
<div class="function">
    merchants:addBuyer <span class="paramlist">npc, prices, addOffer</span>
</div>

- Adds buyer behavior to a [SpeakerNPC].
- Parameters:
    - <span class="param">npc</span>
      <span class="datatype">[SpeakerNPC]</span>
      The NPC to add the behavior to.
    - <span class="param">prices</span>
      List of items &amp; their prices (can be instance of either
      [Map<String, Integer>][java.util.Map] or a [table][LuaTable]).
    - <span class="param">addOffer</span>
      <span class="datatype">[bool][LuaBoolean]</span>
      If `true`, will add default replies for "offer" (default: `true`).


---
## merchants:addSeller
<div class="function">
    merchants:addSeller <span class="paramlist">npc, prices</span>
</div>
<div class="function">
    merchants:addSeller <span class="paramlist">npc, prices, addOffer</span>
</div>

- Adds seller behavior to a [SpeakerNPC].
- Parameters:
    - <span class="param">npc</span>
      <span class="datatype">[SpeakerNPC]</span>
      The NPC to add the behavior to.
    - <span class="param">prices</span>
      List of items &amp; their prices (can be instance of either
      [Map<String, Integer>][java.util.Map] or a [LuaTable]).
    - <span class="param">addOffer</span>
      <span class="datatype">[bool][LuaBoolean]</span>
      If `true`, will add default replies for "offer" (default: `true`).


---
# TODO

- Add support for [OutfitShopsList].


[OutfitShopsList]: /reference/java/games/stendhal/server/entity/npc/shop/OutfitShopsList.html
[ShopType]: /reference/java/games/stendhal/server/entity/npc/shop/ShopType.html
[ShopsList]: /reference/java/games/stendhal/server/entity/npc/shop/ShopsList.html
[SpeakerNPC]: /reference/java/games/stendhal/server/entity/npc/SpeakerNPC.html

[java.util.Map]: https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Map.html

[LuaBoolean]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaBoolean.html
[LuaString]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaString.html
[LuaTable]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaTable.html
