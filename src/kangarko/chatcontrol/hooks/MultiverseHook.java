package kangarko.chatcontrol.hooks;

import org.bukkit.Bukkit;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;

import kangarko.chatcontrol.utils.Common;

public class MultiverseHook extends Hook {

	private final MultiverseCore multiVerse;

	public MultiverseHook() {
		hooked = Common.doesPluginExist("Multiverse-Core");
		multiVerse = hooked ? (MultiverseCore) Bukkit.getPluginManager().getPlugin("Multiverse-Core") : null;
	}

	public String getWorldAlias(String world) {
		if (!hooked)
			return world;
		
		MultiverseWorld mvWorld = multiVerse.getMVWorldManager().getMVWorld(world);

		if (mvWorld != null)
			return mvWorld.getColoredWorldString();

		return world;
	}
}
