
Static Classes & Enumerations
=============================

[TOC]

## ConversationStates

The {@link games.stendhal.server.entity.npc.ConversationStates} enum.

Example usage:
```
local npc = entities:createSpeakerNPC("foo")
npc:setCurrentState(ConversationStates.IDLE)
```

## ConversationPhrases

The {@link games.stendhal.server.entity.npc.ConversationPhrases} class.

Example usage:
```
local npc = entities:createSpeakerNPC("foo")
npc:add(ConversationStates.IDLE,
	ConversationPhrases.GREETING_MESSAGES,
	nil,
	ConversationStates.ATTENDING,
	"Hello! How can I help you.",
	nil)
```

## CollisionAction

The {@link games.stendhal.server.entity.CollisionAction} enum.

Example usage:
```
local npc = entities:createSilentNPC()
npc:setCollisionAction(CollisionAction.STOP)
```

## SkinColor

The {@link games.stendhal.common.constants.SkinColor} enum.

Example usage:
```
local npc = entities:createSpeakerNPC("foo")
npc:setOutfit("body=0,head=0,hair=3,dress=5")
npc:setOutfitColor("skin", SkinColor.DARK)
```

## Direction

The {@link games.stendhal.common.Direction} enum.

Example usage:
```
local npc = entities:createSpeakerNPC("foo")
npc:setDirection(Direction.DOWN)
```

## DaylightPhase

The {@link games.stendhal.server.core.rp.DaylightPhase} enum.

## Region

The {@link games.stendhal.server.maps.Region} class.

## MathHelper

The {@link games.stendhal.common.MathHelper} class.

## Color

The {@link java.awt.Color} class.

Example usage:
```
local npc = entities:createSpeakerNPC("foo")
npc:setOutfit("body=0,head=0,hair=3,dress=5")
npc:setOutfitColor("dress", Color.BLUE)
```

## SingletonRepository

The {@link game.stendhal.server.core.engine.SingletonRepository} static instance.

Implemented as <code>singletons</code> object.

## CloneManager

The {@link games.stendhal.server.entity.npc.CloneManager} static instance.

Implemented as <code>clones</code> object.

Example usage:
```
local myClone = clones:clone(orig)
```

## Rand

The {@link games.stendhal.common.Rand} random number generator.

Implemented as <code>random</code> object.
