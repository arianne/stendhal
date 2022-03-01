
Introduction
============

[TOC]

<span style="color: red; font-style: italic;">this page is a work-in progress</span>

Stendhal supports [Lua scripting](https://www.lua.org/) via the [LuaJ library](https://sourceforge.net/projects/luaj/).

Lua scripts end in the `.lua` extension & are stored in the `data/script` directory.

# Lua Basics

For more detailed information, see the [Lua reference manual](https://www.lua.org/docs.html).

## Comments

Lua uses double dashes (`--`) for single line comments & double dashes followed by double square brackets (`[[`) & closed with double square brackets (`]]`) for multi-line comments:

```
-- a single line comment

--[[
a multi-line comment
]]
```

## Variables

By default, Lua variables are set in [**global** scope](https://en.wikipedia.org/wiki/Global_variable) (meaning it is exposed to the entire Lua engine). To create a variable in [**local** scope](https://en.wikipedia.org/wiki/Local_variable), the `local` keyword must be used:

```
-- a global variable
var1 = "Hello world!"

-- a local variable
local var2 = "Hello world!"
```

## Data Types

Some common data types in Lua are *string*, *integer*, *boolean*, & *table*. Type names do not need to be declared when setting variables.

Examples:
```
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

String concatenation is simple, much like Java uses a plus operator (`+`) to join strings, Lua uses two periods (`..`).

Example:
```
-- create a string variable
local var = "Hello"

-- append another string
var = var .. " world!"

print(var) -- prints "Hello world!"
```

### Tables

A Lua table is a data type similar to a Java list or map. Tables can be indexed or use key=value pairs.

(<span style="color:red; font-style:italic;">IMPORTANT NOTE: Lua table indexes begin at 1, not 0</span>)

#### Creating Tables

An empty table is initialized with a pair of curly braces (`{}`):
```
local mytable = {}
```

You can add values to indexed tables at initialization or with the `table.insert` method:
```
-- create a table with values
local mytable = {"foo"}

-- add value
table.insert(mytable, "bar")
```

To create a key=value table, any of the following methods can be used to add values:
```
-- all of these do the same thing, that is, assigning "bar" to mytable.foo
local mytable {
	foo = "bar",
	["foo"] = "bar",
}
mytable.foo = "bar"
mytable["foo"] = "bar"
```

#### Accessing Table Values

Square brackets (`[]`) enclosing an index number are used to access values in indexed tables (*remember that Lua table indexes start at "1" not "0"*):
```
local mytable = {"foo", "bar"}

print(mytable[1]) -- prints "foo"
print(mytable[2]) -- prints "bar"
```

In a key=value table, values can be accessed by either enclosing the key string in square brackets or concatenating the key member using a `.`:
```
local mytable = {foo="bar"}

-- using square brackets
print(mytable["foo"]) -- prints "bar"

-- using concatenated member
print(mytable.foo) -- prints "bar"
```

#### Iterating Tables

Tables can be iterated in a `for` loop using the `pairs` or `ipairs` iterators. Loops are terminated with the `end` keyword:
```
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
```
indexes:
1
2

values:
foo
bar
```

Using a key=value table:
```
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
```
keys:
foo
bar

values:
hello
 world!
```

See also: [Lua Tables Tutorial](http://lua-users.org/wiki/TablesTutorial)

### Functions

Like normal variables, functions can be declared as **global** or **local** & must be terminated with the `end` keyword.

There are two ways to define functions with the `function` keyword:
```
local function myFunction()
	print("Hello world!")
end
```

or
```
local myFunction = function()
	print("Hello world!")
end
```

Functions can also be members of a table:
```
local myTable = {}
function myTable.myFunction()
	print("Hello world!")
end
```

or
```
local myTable = {}
myTable.myFunction = function()
	print("Hello world!")
end
```

or
```
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
| and      | logical *and*      | &amp;&amp;      |
| or       | logical *or*       | \|\|            |
| not      | logical *inverse*  | !               |

### Relational Operators

| Operator | Description              | Java Equivalent |
| -------- | ------------------------ | --------------- |
| &lt;     | less than                | &lt;            |
| &gt;     | greater than             | &gt;            |
| &lt;=    | less than or equal to    | &lt;=           |
| &gt;=    | greater than or equal to | &gt;=           |
| ==       | equal to                 | ==              |
| ~=       | not equal to             | !=              |

# Stendhal Application

## Zones

### Setting Zone

To set the zone to work with, use the `game` object:

```
game:setZone("0_semos_city")
```

### Create New Zone

It is recommended to create new zones in the XML configurations in [data/conf/zones](https://github.com/arianne/stendhal/blob/master/data/conf/zones).

Currently creating new zones via Lua is not supported.

### Add Zone Music

Music can be added to zones with the `game:setMusic` function. It supports the following arguments:
* <span style="color:darkgreen; font-style:italic;">filename:</span> Basename of the OGG audio file to use stored in [data/music](https://github.com/arianne/stendhal/blob/master/data/music).
* <span style="color:darkgreen; font-style:italic;">args:</span> A table of key=value integers.
* Valid keys:
  * <span style="color:darkblue; font-style:italic;">volume:</span> Volume level (default: 100).
  * <span style="color:darkblue; font-style:italic;">x:</span> The horizontal point for the source of the music (default: 1).
  * <span style="color:darkblue; font-style:italic;">y:</span> The vertical point for the source of the music (default: 1).
  * <span style="color:darkblue; font-style:italic;">radius:</span> The radial range at which the music can be heard (default: 10000).

Example:
```
if game:setZone("0_semos_plains_n") then
	game:setMusic("pleasant_creek_loop", {volume=85, radius=100})
end
```

## Adding Entities

### Signs

Signs can be created with `entities:createSign` and `entities:createShopSign`:

```
local zone = "0_semos_city"
if game:setZone(zone) then
	-- create the sign instance
	local sign = entities:createSign()
	sign:setEntityClass("signpost")
	sign:setPosition(12, 55)
	sign:setText("Meet Lua!")

	-- Add it to the world
	game:add(sign)
else
	logger:error("Could not set zone: " .. zone)
end
```

### NPCs

Use the `entities:createSpeakerNPC` method to create an interactive NPC:

```
local zone = "0_semos_city"
if game:setZone(zone) then
	-- Use helper object to create a new NPC
	local npc = entities:createSpeakerNPC("Lua")
	npc:setEntityClass("littlegirlnpc")
	npc:setPosition(10, 55)
	npc:setBaseSpeed(0.1)
	npc:setCollisionAction(CollisionAction.STOP)

	local nodes = {
		{10, 55},
		{11, 55},
		{11, 56},
		{10, 56},
	}

	npc:setPath(nodes)

	-- Dialogue
	npc:addJob("Actually, I am jobless.")
	npc:addGoodbye();

	-- Add to the world
	game:add(npc)
else
	logger:error("Could not set zone: " .. zone)
end
```

#### Adding Transitions

A simple example of adding a chat transition can be done without any special functionality:
```
local frank = entities:createSpeakerNPC("Frank")
frank:add(ConversationStates.IDLE,
	ConversationPhrases.GREETING_MESSAGES,
	nil,
	ConversationStates.ATTENDING,
	"Hello.",
	nil)
```

This simply adds a response to saying "hello" & sets the NPC to attend to the player (equivalent of `frank:addGreeting("Hello")`).

For more complicated behavior, we need to use some helper methods. If we want to check a condition we use the `conditions:create` method. The first parameter is the string name of the ChatCondition we want to instantiate. The second parameter is a table that contains the values that should be passed to the ChatCondition constructor.

Example:
```
frank:add(ConversationStates.IDLE,
	ConversationPhrases.GREETING_MESSAGES,
	conditions:create("PlayerHasItemWithHimCondition", {"money"}),
	ConversationStates.ATTENDING,
	"Hello.",
	nil)
```

In this scenario, the NPC will only respond if the player is carrying [money](https://stendhalgame.org/item/money/money.html).

A NotCondition instance can be created with the `actions:notCondition` method:

Example usage:
```
local condition = conditions.notCondition(conditions:create("PlayerHasItemWithHimCondition", {"money"})
```

To add a ChatAction, we use the `actions:create` method. Its usage is identical to `conditions:create`.

Example:
```
frank:add(ConversationStates.IDLE,
	ConversationPhrases.GREETING_MESSAGES,
	conditions:create("PlayerHasItemWithHimCondition", {"money"}),
	ConversationStates.ATTENDING,
	"Hello.",
	actions:create("NPCEmoteAction", {"looks greedily at your pouch of money.", false}))
```

Lua tables can be used to add multiple conditions or actions:
```
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

In this scenario, the NPC will respond if the player has money & is not naked.

Nested tables are supported as well:
```
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

The `merchants` object is used for adding merchant behavior (buying/selling) to an NPC.

Example of adding seller behavior to an NPC:
```
if game:setZone("0_semos_city") then
	local frank = entities.createSpeakerNPC("Frank")
	merchants:addSeller(frank, merchants.shops:get("shopname"), true)

	game:add(frank)
end
```

To create a custom shop list, you can use a Lua table (there are multiple ways to add elements to a Lua table):

Method 1:
```
local priceList = {
	meat = 50,
	["ham"] = 70,
}
```

Method 2:
```
local priceList = {}
priceList.meat = 50
priceList["ham"] = 70
```

The helper methods have special handling for underscore characters as well (the following are all the same):
```
local priceList = {
	smoked_ham = 100,
	["smoked ham"] = 100,
}
priceList.smoked_ham = 100
priceList["smoked ham"] = 100
```

Then add the seller behavior using the custom list:
```
merchants:addSeller(frank, priceList, true)
```

## System Properties

Java's system properties are exposed to Lua with the `properties` object.

Examples:
```
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

Lua does not support typecasting (as far as I know), but if the class you want to cast to has a copy constructor, achieving the same functionality is quite simple.

```
-- "entities:getItem" returns an instance of Item
local bestiary = entities:getItem("bestiary")

-- in order to use the bestiary's "setOwner" method, we must convert it to an "OwnedItem" instance by calling its copy constructor
bestiary = luajava.newInstance("games.stendhal.server.entity.item.OwnedItem", bestiary)
bestiary:setOwner("Ted")
```
