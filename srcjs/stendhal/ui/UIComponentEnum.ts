/***************************************************************************
 *                (C) Copyright 2022-2024 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/


/**
 * well known user interface components
 */
export class UIComponentEnum {

	// this should be an enum, but after TypeScript and closure compiler are done, all
	// enum members resolve to undefined. That is both in JavaScript and TypeScript code

	// Panel
	public static readonly TopPanel = 0;
	public static readonly LeftPanel = 1;
	public static readonly RightPanel = 2;
	public static readonly BottomPanel = 3;

	// Components
	public static readonly Bag = 101;
	public static readonly BuddyList = 102;
	public static readonly ChatInput = 103;
	public static readonly ChatLog = 104;
	public static readonly GroupPanel = 105;
	public static readonly PlayerEquipment = 106;
	public static readonly Keyring = 107;
	public static readonly MiniMap = 108;
	public static readonly ZoneInfo = 109;
	public static readonly PlayerStats = 110;
	public static readonly SocialPanel = 111;
	public static readonly StatusesList = 112;

	// Dialog
	public static readonly TradeDialog = 201;
	public static readonly TravelLogDialog = 202;
	public static readonly OutfitDialog = 203;

	// Quick menu
	public static readonly QMChat = 301;
	public static readonly QMJoystick = 302;
	public static readonly QMSound = 303;
}
