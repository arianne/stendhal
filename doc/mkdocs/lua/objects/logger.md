
---
# Contents

[TOC]


---
# Introduction

Object instance: `logger`


---
## Description

Manages logging messages in Lua via the [org.apache.log4j.Logger][log4j.Logger] class.


---
# Methods


---
## logger:info
<div class="function">
    logger:info <span class="paramlist">(message)</span>
</div>

- Prints an information message to the console.
- Parameters:
    - <span class="param">message:</span> _([string][LuaString])_ Text to be printed.


---
## logger:warn
<div class="function">
    logger:warn <span class="paramlist">(message)</span>
</div>

- Prints a warning message to the console.
- Parameters:
    - <span class="param">message:</span> _([string][LuaString])_ Text to be printed.


---
## logger:error
<div class="function">
    logger:error <span class="paramlist">(message)</span>
</div>

- Prints an error message to the console.
- Parameters:
    - <span class="param">message:</span> _([string][LuaString])_ Text to be printed.


---
## logger:debug
<div class="function">
    logger:debug <span class="paramlist">(message)</span>
</div>

- Prints a debug message to the console.
- Parameters:
    - <span class="param">message:</span> _([string][LuaString])_ Text to be printed.


---
## logger:deprecated
<div class="function">
    logger:deprecated <span class="paramlist">(old)</span>
</div>
<div class="function">
    logger:deprecated <span class="paramlist">(old, alt)</span>
</div>

- Prints a deprecation message to the console.
- Parameters:
    - <span class="param">old:</span> _([string][LuaString])_ The deprecated object.
    - <span class="param">alt:</span> _([string][LuaString])_ Alternative object to use.


---
# Usage

# Examples

```lua
local zone_name = "0_semos_city"
if game:setZone(zone_name) then
    -- do something
else
    logger:error("could not set zone: " .. zone_name)
end
```


[LuaString]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaString.html

[log4j.Logger]: https://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/Logger.html
