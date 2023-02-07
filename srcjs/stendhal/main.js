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

var Client = require("../../build/ts/Client").Client;
var singletons = singletons || require("../../build/ts/SingletonRepo").SingletonRepo;

var CacheManager = require("../../build/ts/data/CacheManager").CacheManager;
var CStatus = require("../../build/ts/data/CStatus").CStatus;
var Outfit = require("../../build/ts/data/Outfit").Outfit;

var Ground = require("../../build/ts/entity/Ground").Ground;
var Zone = require("../../build/ts/entity/Zone").Zone;

var Chat = require("../../build/ts/util/Chat").Chat;

var HTMLManager = require("../../build/ts/ui/HTMLManager").HTMLManager;
var InventoryManager = require("../../build/ts/ui/InventoryManager").InventoryManager;
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

stendhal.data = stendhal.data || {};
stendhal.data.cache = new CacheManager();
stendhal.data.cache.init();
stendhal.data.cstatus = new CStatus();
stendhal.data.cstatus.init();
stendhal.data.group = singletons.getGroupManager();
stendhal.data.outfit = new Outfit();
stendhal.data.sprites = singletons.getSpriteStore();
stendhal.data.map = singletons.getMap();

stendhal.config = stendhal.config || singletons.getConfigManager();
stendhal.paths = stendhal.paths || singletons.getPaths();
stendhal.session = stendhal.session || singletons.getSessionManager();

stendhal.slashActionRepository = stendhal.slashActionRepository || singletons.getSlashActionRepository();

stendhal.ui = stendhal.ui || {}
stendhal.ui.equip = new InventoryManager();
stendhal.ui.html = new HTMLManager();
stendhal.ui.touch = new TouchHandler();
stendhal.ui.soundMan = singletons.getSoundManager();
stendhal.ui.gamewindow = singletons.getViewPort();

stendhal.zone = new Zone();
stendhal.zone.ground = new Ground();

stendhal.main = new Client();

document.addEventListener('DOMContentLoaded', stendhal.main.startup);
window.addEventListener('error', stendhal.main.onerror);
