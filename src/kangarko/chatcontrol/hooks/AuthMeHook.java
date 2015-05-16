package kangarko.chatcontrol.hooks;

import org.bukkit.entity.Player;

import fr.xephi.authme.api.API;
import kangarko.chatcontrol.utils.Common;

public class AuthMeHook extends Hook {
	
	public AuthMeHook() {
		hooked = Common.doesPluginExist("AuthMe");
	}
	
	public String getCountryCode(Player pl) {
		if (!hooked)
			return "";
		
		String ip = pl.getAddress().toString().replace("/", "");
		return API.instance.getCountryCode(ip);
	}

	public String getCountryName(Player pl) {
		if (!hooked)
			return "";
		
		String ip = pl.getAddress().toString().replace("/", "");
		return API.instance.getCountryName(ip);
	}
	
	public boolean isLogged(Player pl) {
		if (!hooked)
			return true;
		
		return API.isAuthenticated(pl);
	}
}
