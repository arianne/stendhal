/***************************************************************************
 *                   (C) Copyright 2003-2023 - Stendhal                    *
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
var stendhal = window.stendhal = window.stendhal || {};

var Main = require("../../build/ts/Main").Main;

var Ground = require("../../build/ts/entity/Ground").Ground;
var Zone = require("../../build/ts/entity/Zone").Zone;

var Chat = require("../../build/ts/util/Chat").Chat;

var ui = require("../../build/ts/ui/UI").ui;
var UIComponentEnum = require("../../build/ts/ui/UIComponentEnum").UIComponentEnum;
var TouchHandler = require("../../build/ts/ui/TouchHandler").TouchHandler;
var DesktopUserInterfaceFactory = require("../../build/ts/ui/factory/DesktopUserInterfaceFactory").DesktopUserInterfaceFactory;

var FloatingWindow = require("../../build/ts/ui/toolkit/FloatingWindow").FloatingWindow;

var ChatLogComponent = require("../../build/ts/ui/component/ChatLogComponent").ChatLogComponent;
var ItemInventoryComponent = require("../../build/ts/ui/component/ItemInventoryComponent").ItemInventoryComponent;

var ActionContextMenu = require("../../build/ts/ui/dialog/ActionContextMenu").ActionContextMenu;
var ApplicationMenuDialog = require("../../build/ts/ui/dialog/ApplicationMenuDialog").ApplicationMenuDialog;
var DropQuantitySelectorDialog = require("../../build/ts/ui/dialog/DropQuantitySelectorDialog").DropQuantitySelectorDialog;
var ImageViewerDialog = require("../../build/ts/ui/dialog/ImageViewerDialog").ImageViewerDialog;
var OutfitDialog = require("../../build/ts/ui/dialog/outfit/OutfitDialog").OutfitDialog;

var singletons = singletons || require("../../build/ts/util/SingletonRepo").SingletonRepo;
stendhal.config = stendhal.config || singletons.getConfigManager();
stendhal.paths = stendhal.paths || require("../../build/ts/data/Paths").Paths;
stendhal.session = stendhal.session || singletons.getSessionManager();

stendhal.ui = stendhal.ui || {}
stendhal.ui.touch = new TouchHandler();

stendhal.zone = new Zone();
stendhal.zone.ground = new Ground();

stendhal.main = new Main();

document.addEventListener('DOMContentLoaded', stendhal.main.startup);
window.addEventListener('error', stendhal.main.onerror);
