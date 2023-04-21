
[TOC]

---
## Introduction

Object instance: `logger`

---
### Description

Manages logging messages in Lua via the [org.apache.log4j.Logger][log4j.Logger] class.

---
## Methods

---
### logger:info
<div class="function">
    logger:info <span class="params">(message)</span>
</div>

- Prints an information message to the console.
- Parameters:
    - ___message:__ ([string][LuaString])_ Text to be printed.

---
### logger:warn
<div class="function">
    logger:warn <span class="params">(message)</span>
</div>

- Prints a warning message to the console.
- Parameters:
    - ___message:__ ([string][LuaString])_ Text to be printed.

---
### logger:error
<div class="function">
    logger:error <span class="params">(message)</span>
</div>

- Prints an error message to the console.
- Parameters:
    - ___message:__ ([string][LuaString])_ Text to be printed.

---
### logger:debug
<div class="function">
    logger:debug <span class="params">(message)</span>
</div>

- Prints a debug message to the console.
- Parameters:
    - ___message:__ ([string][LuaString])_ Text to be printed.

---
### logger:deprecated
<div class="function">
    logger:deprecated <span class="params">(old)</span>
</div>
<div class="function">
    logger:deprecated <span class="params">(old, alt)</span>
</div>

- Prints a deprecation message to the console.
- Parameters:
    - ___old:__ ([string][LuaString])_ The deprecated object.
    - ___alt:__ ([string][LuaString])_ Alternative object to use.

# Usage

## Examples

```
local zone_name = "0_semos_city"
if game:setZone(zone_name) then
    -- do something
else
    logger:error("could not set zone: " .. zone_name)
end
```


[LuaString]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaString.html

[log4j.Logger]: https://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/Logger.html
