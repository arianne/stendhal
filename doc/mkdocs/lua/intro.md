
<h1>Introduction to Lua</h1>


---
# Contents

[TOC]


---
# About

<span style="color: red; font-style: italic;">this page is a work-in progress</span>

Stendhal supports [Lua scripting](https://www.lua.org/) via the
[LuaJ library](https://sourceforge.net/projects/luaj/).

Lua scripts end with the `.lua` extension &amp; are stored in the `data/script` directory.


---
# Lua Basics

For more detailed information, see the [Lua reference manual](https://www.lua.org/docs.html).


## Comments

Lua uses double dashes (`--`) for single line comments &amp; double dashes followed by double square
brackets (`--[[`) &amp; closed with double square brackets (`]]`) for multi-line comments:

```lua
-- a single line comment

--[[
a multi-line comment
]]
```


## Variables

By default, variables are set in [__global__ scope](https://en.wikipedia.org/wiki/Global_variable)
(meaning it is exposed to the entire Lua engine). To create a variable in
[__local__ scope](https://en.wikipedia.org/wiki/Local_variable), the `local` keyword must be used:

```lua
-- a global variable
var1 = "Hello world!"

-- a local variable
local var2 = "Hello world!"
```


## Data Types

Some common data types in Lua are _string_, _integer_, _boolean_, &amp; _table_. Type names do not
need to be declared when setting variables.

Examples:

```lua
-- string variable
local var1 = "Hello world!"

-- integer variable
local var2 = 11

-- boolean variable
local var3 = true

-- table variable
local var4 = {}
```


### Strings

#### String Concatenation

String concatenation is simple, much like Java uses a plus operator (`+`) to join strings, Lua uses
two periods (`..`).

Example:

```lua
-- create a string variable
local var = "Hello"

-- append another string
var = var .. " world!"

print(var) -- prints "Hello world!"
```


### Tables

A Lua table is a data type similar to a Java list or map. Tables can be indexed or use key=value
pairs.

_(<span style="color:red;"><span style="font-weight:bold;">IMPORTANT NOTE:</span> Lua table indexes
begin at 1, not 0</span>)_


#### Creating Tables

An empty table is initialized with a pair of curly braces (`{}`):

```lua
local mytable = {}
```

You can add values to indexed tables at initialization or with the `table.insert` method:

```lua
-- create a table with values
local mytable = {"foo"}

-- add value
table.insert(mytable, "bar")
```

To create a key=value table, any of the following methods can be used to add values:

```lua
-- all of these do the same thing, that is, assigning "bar" to mytable.foo
local mytable {
    foo = "bar",
    ["foo"] = "bar",
}
mytable.foo = "bar"
mytable["foo"] = "bar"
```


#### Accessing Table Values

Square brackets (`[]`) enclosing an index number are used to access values in indexed tables
(_remember that Lua table indexes start at "1" not "0"_):

```lua
local mytable = {"foo", "bar"}

print(mytable[1]) -- prints "foo"
print(mytable[2]) -- prints "bar"
```

In a key=value table, values can be accessed by either enclosing the key string in square brackets
or concatenating the key member using a `.`:

```lua
local mytable = {foo="bar"}

-- using square brackets
print(mytable["foo"]) -- prints "bar"

-- using concatenated member
print(mytable.foo) -- prints "bar"
```


#### Iterating Tables

Tables can be iterated in a `for` loop using the `pairs` or `ipairs` iterators. Loops are terminated
with the `end` keyword:

```lua
local mytable = {"foo", "bar"}

print("indexes:")
for idx in pairs(mytable) do
    print(idx)
end

print("\nvalues:")
for idx, value in pairs(mytable) do
    print(value)
end
```

Output:

```lua
indexes:
1
2

values:
foo
bar
```

Using a key=value table:

```lua
local mytable = {
    ["foo"] = "hello",
    ["bar"] = " world!",
}

print("keys:")
for key in pairs(mytable) do
    print(key)
end

print("\nvalues:")
for key, value in pairs(mytable) do
    print(value)
end
```

Output:

```lua
keys:
foo
bar

values:
hello
 world!
```

See also: [Lua Tables Tutorial](http://lua-users.org/wiki/TablesTutorial)


### Functions

Like normal variables, functions can be declared as __global__ or __local__ &amp; must be terminated
with the `end` keyword.

There are two ways to define functions with the `function` keyword:

```lua
local function myFunction()
    print("Hello world!")
end
```

or

```lua
local myFunction = function()
    print("Hello world!")
end
```

Functions can also be members of a table:

```lua
local myTable = {}
function myTable.myFunction()
    print("Hello world!")
end
```

or

```lua
local myTable = {}
myTable.myFunction = function()
    print("Hello world!")
end
```

or

```lua
local myTable = {
    myFunction = function()
        print("Hello world!")
    end,
}

-- execute with
myTable.myFunction()
```


## Comparison Operators

### Logical Operators

| Operator | Description        | Java Equivalent |
| -------- | ------------------ | --------------- |
| and      | logical _and_      | &amp;&amp;      |
| or       | logical _or_       | \|\|            |
| not      | logical _inverse_  | !               |


### Relational Operators

| Operator | Description              | Java Equivalent |
| -------- | ------------------------ | --------------- |
| &lt;     | less than                | &lt;            |
| &gt;     | greater than             | &gt;            |
| &lt;=    | less than or equal to    | &lt;=           |
| &gt;=    | greater than or equal to | &gt;=           |
| ==       | equal to                 | ==              |
| ~=       | not equal to             | !=              |


---
# Stendhal Application

## Zones

### Setting Zone

To set a zone to work with, use [game:setZone] object:

```lua
game:setZone("0_semos_city")
```


### Create New Zone

For creating a permanent zone it is recommended to use the XML configurations in
[data/conf/zones](https://github.com/arianne/stendhal/blob/master/data/conf/zones).

Currently creating new zones via Lua is not supported.


### Add Zone Music

Music can be added to zones with the [game:setMusic] function. It supports the following arguments:

- <span class="param">filename:</span> Basename of the OGG audio file to use stored in
  [data/music](https://github.com/arianne/stendhal/blob/master/data/music).
- <span class="param">args:</span> A table of key=value integers.
- Valid keys:
    - <span class="table-attr">volume:</span> Volume level (default: 100).
    - <span class="table-attr">x:</span> The horizontal point for the source of the music (default:
      1).
    - <span class="table-attr">y:</span> The vertical point for the source of the music (default:
      1).
    - <span class="table-attr">radius:</span> The radial range at which the music can be heard
      (default: 10000).

Example:

```lua
if game:setZone("0_semos_plains_n") then
    game:setMusic("pleasant_creek_loop", {volume=85, radius=100})
end
```


## Adding Entities

Entities can be added to the game using the [entities] object.


### Adding Signs

Signs can be created with the "Sign", "Reader", &amp; "ShopSign" types:

```lua
local zone = "0_semos_city"
if game:setZone(zone) then
    -- create the sign instance
    local sign = entities:create({
        type = "Sign",
        class = "signpost",
        pos = {12, 55},
        text = "Meet Lua!"
    })

    -- add to the world
    game:add(sign)
else
    logger:error("Could not set zone: " .. zone)
end
```


### Adding NPCs

Use the "SpeakerNPC" type to create an interactive NPC:

```lua
local zone = "0_semos_city"
if game:setZone(zone) then
    -- create the NPC instance
    local npc = entities:create({
        type = "SpeakerNPC",
        name = "Lua",
        class = "littlegirlnpc",
        pos = {10, 55},
        path = {
            nodes = {
                {10, 55},
                {11, 55},
                {11, 56},
                {10, 56},
            },
            collisionAction = CollisionAction.STOP
        },
        speed = 0.1
    })

    -- dialogue
    npc:addJob("Actually, I am jobless.")
    npc:addGoodbye();

    -- add to the world
    game:add(npc)
else
    logger:error("Could not set zone: " .. zone)
end
```


#### Adding Transitions

A simple example of adding a chat transition can be done without any special functionality:

```lua
local frank = entities:create({
    type = "SpeakerNPC",
    name = "Frank"
})

frank:add(ConversationStates.IDLE,
    ConversationPhrases.GREETING_MESSAGES,
    nil,
    ConversationStates.ATTENDING,
    "Hello.",
    nil)
```

This simply adds a response to saying "hello" &amp; sets the NPC to attend to the player (equivalent
of `frank:addGreeting("Hello")`).

For more advanced behavior, we need to use some helper methods. If we want to check a condition
we use the [conditions:create] method. The first parameter is the string name of the [ChatCondition]
we want to instantiate. The second parameter is a table that contains the values that should be
passed to the ChatCondition constructor.

Example:

```lua
frank:add(ConversationStates.IDLE,
    ConversationPhrases.GREETING_MESSAGES,
    conditions:create("PlayerHasItemWithHimCondition", {"money"}),
    ConversationStates.ATTENDING,
    "Hello.",
    nil)
```

In this scenario, the NPC will only respond if the player is carrying
[money](https://stendhalgame.org/item/money/money.html).

A [NotCondition] instance can be created with the [actions:notCondition] method:

Example usage:

```lua
local condition = conditions.notCondition(conditions:create("PlayerHasItemWithHimCondition", {"money"})
```

To add a [ChatAction], we use the [actions:create] method. Its usage is identical to
[conditions:create].

Example:

```lua
frank:add(ConversationStates.IDLE,
    ConversationPhrases.GREETING_MESSAGES,
    conditions:create("PlayerHasItemWithHimCondition", {"money"}),
    ConversationStates.ATTENDING,
    "Hello.",
    actions:create("NPCEmoteAction", {"looks greedily at your pouch of money.", false}))
```

Lua tables can be used to add multiple conditions or actions (___NOTE:__ this only works for NPCs
created from Lua or instances of [LuaSpeakerNPC]_):

___FIXME:__ add global helper methods for managing [SpeakerNPC] instances not created in Lua_

```lua
frank:add(ConversationStates.IDLE,
    ConversationPhrases.GREETING_MESSAGES,
    {
        conditions:create("PlayerHasItemWithHimCondition", {"money"}),
        conditions:notCondition(conditions:create("NakedCondition")),
    },
    ConversationStates.ATTENDING,
    nil,
    {
        actions:create("SayTextAction", {"Hello."}),
        actions:create("NPCEmoteAction", {"looks greedily at your pouch of money.", false}),
    })
```

In this scenario, the NPC will respond if the player has money &amp; is not naked.

Nested tables are supported as well:

```lua
local conditions = {
    conditions:create("PlayerHasItemWithHimCondition", {"money"}),
    {
        conditions:notCondition(conditions:create("NakedCondition")),
    },
}

frank:add(ConversationStates.IDLE,
    ConversationPhrases.GREETING_MESSAGES,
    conditions,
    ConversationStates.ATTENDING,
    nil,
    {
        actions:create("SayTextAction", {"Hello."}),
        actions:create("NPCEmoteAction", {"looks greedily at your pouch of money.", false}),
    })
```


#### Adding Merchant Behavior

The [merchants] object is used for adding merchant behavior (buying/selling) to an NPC.

___FIXME:__ should use `ShopType` enum_

Example of adding seller behavior to an NPC:

```lua
if game:setZone("0_semos_city") then
    local frank = entities.create({
        type = "SpeakerNPC",
        name = "Frank"
    })
    merchants:addSeller(frank, merchants.shops:get("shopname"), true)

    game:add(frank)
end
```

To create a custom shop list, you can use a Lua table:

```lua
local priceList = {
    meat = 50,
    ham = 70,
}
```

The helper methods have special handling for underscore characters as well (the following are all
the same):

```lua
local priceList = {
    smoked_ham = 100,
    ["smoked ham"] = 100,
}
priceList.smoked_ham = 100
priceList["smoked ham"] = 100
```

Then add the seller behavior using the custom list:

```lua
merchants:addSeller(frank, priceList, true)
```


## System Properties

Java's system properties are exposed to Lua with the [properties] object.

Examples:

```lua
-- property state
if properties:enabled("stendhal.testserver") then
    print("Test server enabled")
    if properties:equals("stendhal.testserver", "junk") then
        print("Junk enabled")
    else
        print("Junk disabled")
    end
else
    print("Test server disabled")
end

-- property value
local prop = properties:getValue("stendhal.testserver")
if prop ~= nil then
    print("Test server enabled")
    if prop == "junk" then
        print("Junk enabled")
    else
        print("Junk disabled")
    end
else
    print("Test server disabled")
end
```


## Misc

### Typecasting

Lua does not support typecasting (as far as I know), but if the class you want to cast to has a copy
constructor, achieving the same functionality is quite simple.

```lua
-- "entities:getItem" returns an instance of Item
local bestiary = entities:getItem("bestiary")

-- in order to use the bestiary's "setOwner" method, we must convert it to an "OwnedItem" instance
-- by calling its copy constructor
bestiary = luajava.newInstance("games.stendhal.server.entity.item.OwnedItem", bestiary)
bestiary:setOwner("Ted")
```


[actions]: /reference/lua/objects/actions/
[actions:create]: /reference/lua/objects/actions/#actionscreate
[actions:notCondition]: /reference/lua/objects/actions/#actionsnotcondition
[conditions]: /reference/lua/objects/conditions/
[conditions:create]: /reference/lua/objects/conditions/#conditionscreate
[entities]: /reference/lua/objects/entities/
[game]: /reference/lua/objects/game/
[game:setMusic]: http://stendhal.localhost/reference/lua/objects/game/#gamesetmusic
[game:setZone]: http://stendhal.localhost/reference/lua/objects/game/#gamesetzone
[LuaSpeakerNPC]: /reference/lua/objects/entities/#luaspeakernpc
[merchants]: /reference/lua/objects/merchants/
[properties]: /reference/lua/objects/properties/

[ChatAction]: /reference/java/games/stendhal/server/entity/npc/ChatAction.html
[ChatCondition]: /reference/java/games/stendhal/server/entity/npc/ChatCondition.html
[NotCondition]: /reference/java/games/stendhal/server/entity/npc/condition/NotCondition.html
[SpeakerNPC]: /reference/java/games/stendhal/server/entity/npc/SpeakerNPC.html
