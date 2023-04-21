
[TOC]

---
## Introduction

Object instance: `properties`

---
### Description

Defines functions for accessing Java system properties.

---
## Methods

---
### poperties:enabled
<div class="function">
    properties:enabled <span class="params">(p)</span>
</div>

- Checks if a property is enabed.
- Parameters:
    - ___p:__ ([string][LuaString])_ Property name.
- Returns: _([bool][LuaBoolean])_
    - `true` if enabled.

---
### poperties:equals
<div class="function">
    properties:equals <span class="params">(p, v)</span>
</div>

- Checks if a property is set to a specified value.
- Parameters:
    - ___p:__ ([string][LuaString])_ Property name.
    - ___v:__ ([string][LuaString])_ Value to compare with.
- Returns: _([bool][LuaBoolean])_
    - `true` if the value of the property is the same as ___v___.

---
### poperties:getValue
<div class="function">
    properties:getValue <span class="params">(p)</span>
</div>

- Retrieves the value of a property.
- Parameters:
    - ___p:__ ([string][LuaString])_ Property name.
- Returns: _([string][LuaString])_
    - Property value or [nil][LuaNil].

## Usage Examples

```
-- example of only executing script contents on test server

if not properties:enabed("stendhal.testserver") then
    do return end
end
```


[LuaBoolean]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaBoolean.html
[LuaNil]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaNil.html
[LuaString]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaString.html
