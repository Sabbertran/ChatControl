package kangarko.chatcontrol.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import kangarko.chatcontrol.utils.Common;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

public class SimpleClansHook {

	public static final boolean HOOKED;
	private static final SimpleClans clans;

	private SimpleClansHook() {
	}

	public static String getClanTag(Player pl) {
		if (!HOOKED)
			return "";
		
		ClanPlayer clanPl = clans.getClanManager().getClanPlayer(pl);

		if (clanPl != null) {
			Clan clan = clanPl.getClan();

			if (clan != null)
				clan.getColorTag();
		}

		return "";
	}
	
	static {
		HOOKED = Common.doesPluginExist("SimpleClans");
		clans = HOOKED ? (SimpleClans) Bukkit.getPluginManager().getPlugin("SimpleClans") : null;
	}
}
