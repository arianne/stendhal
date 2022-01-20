
Properties {#lua_properties}
==========

[TOC]

## properties

Defines functions for accessing Java system properties.

Methods:

### getValue

<span style="color:green; font-weight:bold;">properties:getValue</span>(p)
* Retrieves the value of a property.
* ***p:*** (string) Property name.
* ***returns:*** (string) Property value or `nil`.

### enabled

<span style="color:green; font-weight:bold;">properties:enabled</span>(p)
* Checks if a property is enabed.
* ***p:*** (string) Property name.
* ***returns:*** (boolean) `true` if enabled.

### equals

<span style="color:green; font-weight:bold;">properties:equals</span>(p, v)
* Checks if a property is set to a specified value.
* ***p:*** (string) Property name.
* ***v:*** (string) Value to compare with.
* ***returns:*** (boolean) `true` if the value of the property is the same as `v`

Examples usage:

```
-- example of only executing script contents on test server

if not properties:enabed("stendhal.testserver") then
    do return end
end
```
