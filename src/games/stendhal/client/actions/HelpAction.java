/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.actions;

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.gui.chatlog.HeaderLessEventLine;
import games.stendhal.common.NotificationType;

/**
 * Display command usage. Eventually replace this with ChatCommand.usage().
 */
class HelpAction implements SlashAction {

	/**
	 * Execute a chat command.
	 *
	 * @param params
	 *            The formal parameters.
	 * @param remainder
	 *            Line content after parameters.
	 *
	 * @return <code>true</code> if was handled.
	 */
	@Override
	public boolean execute(final String[] params, final String remainder) {
		final String[] lines = {
				"要查看详细游戏帮助,可以访问 #https://stendhalgame.org/wiki/Stendhal_Manual",
				"以下是一些常用命令:",
				"* 聊天命令:",
				"- /me <action> \t显示你正在做的事情.",
				"- /tell <玩家> <消息文本>",
				"\t\t给某玩家发送私信",
				"- /answer <消息文本>",
				"\t\t发送一条私信给最近与你联系的玩家",
				"- // <消息文本>\t发送一条私信给最近与你联系的玩家。",
				"- /storemessage <玩家> <消息文本>",
				"\t\t存储一条私信发送给离线 #玩家。",
				"- /who \t列出当前在线的所有玩家",
				"- /where <玩家> \t显示某个 ＃玩家 所在地",
				"- /sentence <文本信息> \t写一条 #文本信息 发送到 stendhalgame.org 个人详情页，当玩家使用 #Look 命令可以看到",
				"* 服务支持命令:",
				"- /support <文本信息>",
				"\t\t向游戏管理员寻求帮助.",
				"- /faq \t\t打开浏览器并显示Stendhal FAQs wiki页",
				"* 物品相关命令:",
				"- /drop [数量] <物品>",
				"\t\t扔掉某个数量的某个物品",
				"- /markscroll <文本信息>",
				"\t\t给空卷轴写上 ＃文本信息 作为标签",
				"* 好友与敌人命令:",
				"- /add <player> \t添加 #player 到好友列表",
				"- /remove <player>",
				"\t\t从好友列表中删除 #player ",
				"- /ignore <player> [minutes|*|- [reason...]]",
				"\t\t添加 #player 到你想禁言的列表",
				"- /ignore \t显示被你禁言的列表",
				"- /unignore <player>",
				"\t\t从你禁言列表中恢复 #player ",
				"* 状态命令:",
				"- /away <离开消息>",
				"\t\t设置离开时的信息",
				"- /away \t删除离开信息",
				"- /grumpy <消息文本>",
				"\t\t 给非好友设置一条不接受的消息。",
				"- /grumpy \t删除 #grumpy 状态.",
				"- /name <宠物> <名字>",
				"\t\t给你的宠物起名",
				"- /profile [name] \tO在网页中打开某玩家的个人信息",
				"* 玩家控制命令:",
				"- /clickmode \t切换鼠标状态为单击模式或双击模式",
				"- /walk \t打开／关闭自行行走",
				"- /stopwalk \t关闭自动行走",
				"- /movecont \t在地图转换后或发生传送后，按住方向键依然保持自动行走",
				"* 客户端设置命令:",
				"- /mute \t关闭／打开所有声音",
				"- /volume \t列出各种音量的状态",
				"* 其他命令:",
				"- /info \t\t显示服务器的当前时间",
				"- /clear \t消除消息日志",
				"- /help \t显示本帮助信息"
		};

		for (final String line : lines) {
			ClientSingletonRepository.getUserInterface().addEventLine(new HeaderLessEventLine(line, NotificationType.CLIENT));
		}

		return true;
	}

	/**
	 * Get the maximum number of formal parameters.
	 *
	 * @return The parameter count.
	 */
	@Override
	public int getMaximumParameters() {
		return 0;
	}

	/**
	 * Get the minimum number of formal parameters.
	 *
	 * @return The parameter count.
	 */
	@Override
	public int getMinimumParameters() {
		return 0;
	}
}
