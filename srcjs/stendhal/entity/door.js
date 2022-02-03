/***************************************************************************
 *                      (C) Copyright 2020 - Stendhal                      *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/


"use strict";

var marauroa = window.marauroa = window.marauroa || {};

var Door = require("../../../build/ts/entity/Door").Door;

marauroa.rpobjectFactory["door"] = Door;
