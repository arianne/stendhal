
[TOC]

## Introduction

Manages logging in Lua via the [org.apache.log4j.Logger](https://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/Logger.html) class.

## Methods

---
### logger:info
<span style="color:green; font-weight:bold;">logger:info</span>(message)

- Prints an information level message to the console.
- Parameters:
    - ***message:*** Text to be printed.

---
### logger:warn
<span style="color:green; font-weight:bold;">logger:warn</span>(message)

- Prints a warning level message to the console.
- Parameters:
    - ***message:*** Text to be printed.

---
### logger:error
<span style="color:green; font-weight:bold;">logger:error</span>(message)

- Prints an error level message to the console.
- Parameters:
    - ***message:*** Text to be printed.

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
