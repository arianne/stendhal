
[TOC]

## Introduction

Exposes merchant handling classes & functions to Lua.

## Members

---
### merchants.shops
<span style="color:brown; font-weight:bold;">merchants.shops</span>

- This is the {@link games.stendhal.server.entity.npc.ShopList} instance.
- Public methods: TODO

## Methods

---
### merchants:add
<span style="color:green; font-weight:bold;">merchants:add</span>(merchantType, npc, prices, addOffer)

- Adds merchant behavior to a {@link games.stendhal.server.entity.npc.SpeakerNPC}.
- Parameters:
    - ***merchantType:*** If set to "buyer", will add buyer behavior, otherwise will be "seller".
    - ***npc:*** The SpeakerNPC to add the behavior to.
    - ***prices:*** List of items & their prices (can be instance of either Map<String, Integer> or a Lua table).
    - ***addOffer:*** If <code>true</code>, will add default replies for "offer" (default: <code>true</code>).

---
### merchants:addSeller
<span style="color:green; font-weight:bold;">merchants:addSeller</span>(npc, prices, addOffer)

- Adds seller behavior to a {@link games.stendhal.server.entity.npc.SpeakerNPC}.
- Parameters:
    - ***npc:*** The SpeakerNPC to add the behavior to.
    - ***prices:*** List of items & their prices (can be instance of either Map<String, Integer> or a Lua table).
    - ***addOffer:*** If <code>true</code>, will add default replies for "offer" (default: <code>true</code>).

---
### merchants:addBuyer
<span style="color:green; font-weight:bold;">merchants:addBuyer</span>(npc, prices, addOffer)

- Adds buyer behavior to a {@link games.stendhal.server.entity.npc.SpeakerNPC}.
- Parameters:
    - ***npc:*** The SpeakerNPC to add the behavior to.
    - ***prices:*** List of items & their prices (can be instance of either Map<String, Integer> or a Lua table).
    - ***addOffer:*** If <code>true</code>, will add default replies for "offer" (default: <code>true</code>).
