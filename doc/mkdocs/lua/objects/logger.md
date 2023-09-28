
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
## logger:debug
<div class="function">
    logger:debug <span class="paramlist">message</span>
</div>

- Logs a message at debug level.
- Parameters:
    - <span class="param">message</span>
      <span class="datatype">[string][LuaString]</span>
      Text in logged message.


---
## logger:deprecated
<div class="function">
    logger:deprecated <span class="paramlist">old</span>
</div>
<div class="function">
    logger:deprecated <span class="paramlist">old, alt</span>
</div>

- Logs a deprecation warning.
- Parameters:
    - <span class="param">old</span>
      <span class="datatype">[string][LuaString]</span>
      The deprecated item.
    - <span class="param">alt</span>
      <span class="datatype">[string][LuaString]</span>
      Alternative to use.


---
## logger:error
<div class="function">
    logger:error <span class="paramlist">message</span>
</div>
<div class="function">
    logger:error <span class="paramlist">message, throwable</span>
</div>
<div class="function">
    logger:error <span class="paramlist">throwable</span>
</div>

- Logs a message at error level &amp; optionally raises an exception.
- Parameters:
    - <span class="param">message</span>
      <span class="datatype">[string][LuaString]</span>
      Text in logged message.
    - <span class="param">throwable</span>
      <span class="datatype">[Throwable][java.lang.Throwable]</span>
      Exception to raise.


---
## logger:info
<div class="function">
    logger:info <span class="paramlist">message</span>
</div>

- Logs a message at info level.
- Parameters:
    - <span class="param">message</span>
      <span class="datatype">[string][LuaString]</span>
      Text in logged message.


---
## logger:warn
<div class="function">
    logger:warn <span class="paramlist">message</span>
</div>

- Logs a message at warning level.
- Parameters:
    - <span class="param">message</span>
      <span class="datatype">[string][LuaString]</span>
      Text in logged message.


---
# Usage Examples

```lua
local zone_name = "0_semos_city"
if game:setZone(zone_name) then
    -- do something
else
    logger:error("could not set zone: " .. zone_name)
end
```


[LuaString]: http://luaj.org/luaj/3.0/api/org/luaj/vm2/LuaString.html

[java.lang.Throwable]: https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Throwable.html

[log4j.Logger]: https://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/Logger.html
