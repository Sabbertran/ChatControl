package kangarko.chatcontrol.hooks;

import org.bukkit.entity.Player;

import fr.xephi.authme.api.API;

public class AuthMeHook {

	public static boolean enabled = false;

	public static String getCountryCode(Player pl) {
		if (!enabled)
			return "";
			
		String ip = pl.getAddress().toString().replace("/", "");
		return API.instance.getCountryCode(ip);
	}

	public static String getCountryName(Player pl) {
		if (!enabled)
			return "";
			
		String ip = pl.getAddress().toString().replace("/", "");
		return API.instance.getCountryName(ip);
	}
}