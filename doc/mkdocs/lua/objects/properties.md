
[TOC]

## Introduction

Defines functions for accessing Java system properties.

## Methods

---
### poperties:getValue
<span style="color:green; font-weight:bold;">properties:getValue</span>(p)

- Retrieves the value of a property.
- Parameters:
    - ***p:*** (string) Property name.
- Returns: (string) Property value or `nil`.

---
### poperties:enabled
<span style="color:green; font-weight:bold;">properties:enabled</span>(p)

- Checks if a property is enabed.
- Parameters:
    - ***p:*** (string) Property name.
- Returns: (boolean) `true` if enabled.

---
### poperties:equals
<span style="color:green; font-weight:bold;">properties:equals</span>(p, v)

- Checks if a property is set to a specified value.
- Parameters:
    - ***p:*** (string) Property name.
    - ***v:*** (string) Value to compare with.
- Returns: (boolean) `true` if the value of the property is the same as `v`

## Examples usage:

```
-- example of only executing script contents on test server

if not properties:enabed("stendhal.testserver") then
    do return end
end
```
