arrays {#lua_arrays}
======

[TOC]

## Introduction

Handles some conversion of Java arrays & lists to Lua tables.

## Methods

; ''<span style="color:green;">arrays:toTable</span>(list)''
: Converts a Java array or list to a Lua table.
: '''''list:''''' Java array or [https://docs.oracle.com/javase/8/docs/api/java/util/List.html list].
: ''returns:'' New Lua table with contents of <code>list</code> added.
