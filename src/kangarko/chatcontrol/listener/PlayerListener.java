package kangarko.chatcontrol.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import kangarko.chatcontrol.ChatControl;
import kangarko.chatcontrol.PlayerCache;
import kangarko.chatcontrol.config.ConfHelper.ChatMessage;
import kangarko.chatcontrol.config.Localization;
import kangarko.chatcontrol.config.Settings;
import kangarko.chatcontrol.hooks.HookManager;
import kangarko.chatcontrol.utils.Common;
import kangarko.chatcontrol.utils.CompatProvider;
import kangarko.chatcontrol.utils.LagCatcher;
import kangarko.chatcontrol.utils.Permissions;
import kangarko.chatcontrol.utils.UpdateCheck;

public class PlayerListener implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onPreLogin(AsyncPlayerPreLoginEvent e) {
		PlayerCache plData = ChatControl.getDataFor(e);
		long difference = (System.currentTimeMillis() / 1000L) - plData.lastLogin;
		
		if (plData.lastLogin > 0 && difference < Settings.AntiBot.REJOIN_TIME) {
			long time = Settings.AntiBot.REJOIN_TIME - difference;
			String msg = Common.colorize(Localization.ANTIBOT_REJOIN_WAIT_MESSAGE.replace("%time", String.valueOf(time)).replace("%seconds", Localization.Parts.SECONDS.formatNumbers(time)));
			msg.split("\n");

			e.disallow(Result.KICK_OTHER, msg);
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		LagCatcher.start("Join event");

		long now = System.currentTimeMillis() / 1000L;
		PlayerCache plData = ChatControl.getDataFor(e.getPlayer());
		
		if (!Common.hasPerm(e.getPlayer(), Permissions.Bypasses.REJOIN))
			plData.lastLogin = now;

		plData.loginLocation = e.getPlayer().getLocation();

		if (e.getPlayer().getName().equals("kangarko") && !HookManager.isRushCoreLoaded())
			Common.tellLater(e.getPlayer(), 30,
					Common.consoleLine(),
					"&e Na serveri je nainstalovany ChatControl v" + ChatControl.instance().getDescription().getVersion() + "!",
					Common.consoleLine());

		if (UpdateCheck.needsUpdate && Settings.Updater.NOTIFY)
			for (Player pl : CompatProvider.getAllPlayers())
				if (Common.hasPerm(pl, Permissions.Notify.UPDATE_AVAILABLE)) {
					String sprava = Common.colorize(Localization.UPDATE_AVAILABLE).replace("%current", ChatControl.instance().getDescription().getVersion()).replace("%new", UpdateCheck.newVersion);
					sprava.split("\n");
					Common.tellLater(pl, 4 * 20, sprava);
				}

		LagCatcher.end("Join event");

		if (ChatControl.muted && Settings.Mute.SILENT_JOIN) {
			e.setJoinMessage(null);
			return;
		}

		ChatMessage joinMessage = Settings.Messages.JOIN.getFor(plData);
		
		switch (joinMessage.getType()) {
			case HIDDEN:
				e.setJoinMessage(null);
				break;
			case CUSTOM:
				e.setJoinMessage(replacePlayerVariables(joinMessage.getMessage(), e.getPlayer()));
				break;
			default:
				break;
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		if (ChatControl.muted && Settings.Mute.SILENT_QUIT) {
			e.setQuitMessage(null);
			return;
		}

		if (Settings.Messages.QUIT_ONLY_WHEN_LOGGED && !HookManager.isLogged(e.getPlayer())) {
			e.setQuitMessage(null);
			return;
		}
		
		PlayerCache plData = ChatControl.getDataFor(e.getPlayer());
		ChatMessage leaveMessage = Settings.Messages.QUIT.getFor(plData);
		
		switch (leaveMessage.getType()) {
			case HIDDEN:
				e.setQuitMessage(null);
				break;
			case CUSTOM:
				e.setQuitMessage(replacePlayerVariables(leaveMessage.getMessage(), e.getPlayer()));
				break;
			default:
				break;
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onKick(PlayerKickEvent e) {
		if (ChatControl.muted && Settings.Mute.SILENT_KICK) {
			e.setLeaveMessage(null);
			return;
		}

		PlayerCache plData = ChatControl.getDataFor(e.getPlayer());
		ChatMessage kickMessage = Settings.Messages.KICK.getFor(plData);
		
		switch (kickMessage.getType()) {
			case HIDDEN:
				e.setLeaveMessage(null);
				break;
			case CUSTOM:
				e.setLeaveMessage(replacePlayerVariables(kickMessage.getMessage(), e.getPlayer()));
				break;
			default:
				break;
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		if (ChatControl.muted && Settings.Mute.SILENT_DEATHS)
			e.setDeathMessage(null);
	}

	@EventHandler(ignoreCancelled = true)
	public void onSignChange(SignChangeEvent e) {
		if (CompatProvider.getAllPlayers().size() < Settings.MIN_PLAYERS_TO_ENABLE)
			return;

		LagCatcher.start("Sign event");

		Player pl = e.getPlayer();
		PlayerCache plData = ChatControl.getDataFor(pl);
		String msg = e.getLine(0) + e.getLine(1) + e.getLine(2) + e.getLine(3);

		msg = msg.trim();

		if (Settings.Signs.DUPLICATION_CHECK && plData.lastSignText.equalsIgnoreCase(msg) && !Common.hasPerm(pl, Permissions.Bypasses.SIGN_DUPLICATION)) {
			if (Settings.Signs.DUPLICATION_ALERT_STAFF)
				for (Player online : CompatProvider.getAllPlayers())
					if (!online.getName().equals(pl.getName()) && Common.hasPerm(online, Permissions.Notify.SIGN_DUPLICATION))
						Common.tell(online, Localization.SIGNS_DUPLICATION_STAFF.replace("%message", msg), pl.getName());

			Common.tell(pl, Localization.SIGNS_DUPLICATION);
			e.setCancelled(true);

			if (Settings.Signs.DROP_SIGN)
				e.getBlock().breakNaturally();	

			LagCatcher.end("Sign event");
			return;
		}

		if (Settings.Rules.CHECK_SIGNS && !Common.hasPerm(e.getPlayer(), Permissions.Bypasses.RULES)) {
			ChatControl.instance().chatCeaser.parseRules(e, pl, msg);

			if (e.isCancelled()) {
				Common.tellLater(pl, 2, Localization.SIGNS_BROKE); // display at the bottom
				e.setCancelled(true);

				if (Settings.Signs.DROP_SIGN)
					e.getBlock().breakNaturally();
			}
		}

		LagCatcher.end("Sign event");
	}

	private String replacePlayerVariables(String msg, Player pl) {
		msg = msg.replace("%player", pl.getName());

		if (ChatControl.instance().formatter != null)
			msg = ChatControl.instance().formatter.replacePlayerVariables(pl, msg);

		return Common.colorize(msg);
	}
}