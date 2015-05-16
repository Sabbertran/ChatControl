package kangarko.chatcontrol.hooks;

import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;

import kangarko.chatcontrol.utils.Common;

public class TownyHook {

	public static final boolean HOOKED;

	private TownyHook() {
	}

	public static String getNation(Player pl) {
		if (!HOOKED)
			return "";
		
		try {
			Town t = getTown(pl);

			return t != null ? t.getNation().getName() : "";
		} catch (Exception e) {
			return "";
		}
	}

	public static String getTownName(Player pl) {
		if (!HOOKED)
			return "";
		
		Town t = getTown(pl);
		return t != null ? t.getName() : "";
	}

	private static Town getTown(Player pl) {
		try {
			Resident res = TownyUniverse.getDataSource().getResident(pl.getName());

			if (res != null)
				return res.getTown();
		} catch (Throwable e) {
		}

		return null;
	}
	
	static {
		HOOKED = Common.doesPluginExist("Towny");
	}
}
