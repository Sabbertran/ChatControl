package kangarko.chatcontrol.hooks;

import org.bukkit.Bukkit;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;

import kangarko.chatcontrol.utils.Common;

public class MultiverseHook extends Hook {

	private static final MultiverseCore multiVerse;

	private MultiverseHook() {
	}

	public static String getWorldAlias(String world) {
		if (!HOOKED)
			return world;
		
		MultiverseWorld mvWorld = multiVerse.getMVWorldManager().getMVWorld(world);

		if (mvWorld != null)
			return mvWorld.getColoredWorldString();

		return world;
	}
	
	static {
		HOOKED = Common.doesPluginExist("Multiverse-Core");
		multiVerse = HOOKED ? (MultiverseCore) Bukkit.getPluginManager().getPlugin("Multiverse-Core") : null;
	}
}