package kangarko.chatcontrol.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;

import kangarko.chatcontrol.utils.Common;

public class EssentialsHook extends Hook {
	
	private static final Essentials ess;
	
	private EssentialsHook() {
	}

	public static boolean isAfk(String pl) {
		if (!HOOKED)
			return false;
		
		User user = getUser(pl);
		return user != null ? user.isAfk() : false;
	}

	public static Player getReplyTo(String pl) {
		if (!HOOKED)
			return null;
		
		CommandSource source = getUser(pl).getReplyTo();

		if (source != null && source.isPlayer()) {
			Player player = source.getPlayer();
			if (player != null && player.isOnline())
				return player;
		}

		return null;
	}
	
	private static User getUser(String pl) {
		return ess.getUserMap().getUser(pl);
	}
	
	static {
		HOOKED = Common.doesPluginExist("Essentials");
		ess = HOOKED ? (Essentials) Bukkit.getPluginManager().getPlugin("Essentials") : null;
	}
}
