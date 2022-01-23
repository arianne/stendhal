
merchants {#lua_merchants}
=========

[TOC]

## Introduction

Exposes merchant handling classes & functions to Lua.

## Members

; ''<span style="color:brown;">merchants.shops</span>''
: This is the {{StendhalFile|master|src/games/stendhal/server/entity/npc/ShopList.java|games.stendhal.server.entity.npc.ShopList}} instance.
: ''public methods: TODO''

## Methods

; ''<span style="color:green;">merchants:add</span>(merchantType, npc, prices, addOffer)''
: Adds merchant behavior to a {{StendhalFile|master|src/games/stendhal/server/entity/npc/SpeakerNPC.java|SpeakerNPC}}.
: '''''merchantType:''''' If set to "buyer", will add buyer behavior, otherwise will be "seller".
: '''''npc:''''' The SpeakerNPC to add the behavior to.
: '''''prices:''''' List of items & their prices (can be instance of either Map<String, Integer> or a Lua table).
: '''''addOffer:''''' If <code>true</code>, will add default replies for "offer" (default: <code>true</code>).

; ''<span style="color:green;">merchants:addSeller</span>(npc, prices, addOffer)''
: Adds seller behavior to a {{StendhalFile|master|src/games/stendhal/server/entity/npc/SpeakerNPC.java|SpeakerNPC}}.
: '''''npc:''''' The SpeakerNPC to add the behavior to.
: '''''prices:''''' List of items & their prices (can be instance of either Map<String, Integer> or a Lua table).
: '''''addOffer:''''' If <code>true</code>, will add default replies for "offer" (default: <code>true</code>).

; ''<span style="color:green;">merchants:addBuyer</span>(npc, prices, addOffer)''
: Adds buyer behavior to a {{StendhalFile|master|src/games/stendhal/server/entity/npc/SpeakerNPC.java|SpeakerNPC}}.
: '''''npc:''''' The SpeakerNPC to add the behavior to.
: '''''prices:''''' List of items & their prices (can be instance of either Map<String, Integer> or a Lua table).
: '''''addOffer:''''' If <code>true</code>, will add default replies for "offer" (default: <code>true</code>).
