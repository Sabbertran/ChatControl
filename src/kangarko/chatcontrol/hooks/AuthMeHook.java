package kangarko.chatcontrol.hooks;

import org.bukkit.entity.Player;

import fr.xephi.authme.api.API;
import kangarko.chatcontrol.utils.Common;

public class AuthMeHook extends Hook {
	
	private AuthMeHook() {
	}
	
	public static String getCountryCode(Player pl) {
		if (!HOOKED)
			return "";
		
		String ip = pl.getAddress().toString().replace("/", "");
		return API.instance.getCountryCode(ip);
	}

	public static String getCountryName(Player pl) {
		if (!HOOKED)
			return "";
		
		String ip = pl.getAddress().toString().replace("/", "");
		return API.instance.getCountryName(ip);
	}
	
	public static boolean isLogged(Player pl) {
		if (!HOOKED)
			return true;
		
		return API.isAuthenticated(pl);
	}
	
	static {
		HOOKED = Common.doesPluginExist("AuthMe");
	}
}
