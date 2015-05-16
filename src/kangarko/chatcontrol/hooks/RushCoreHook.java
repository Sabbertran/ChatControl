package kangarko.chatcontrol.hooks;

import kangarko.chatcontrol.utils.Common;
import kangarko.rushcore.misc.PlayerInfo;

// A hook with my custom plugin on my server.
public class RushCoreHook {

	public static final boolean HOOKED;
	
	private RushCoreHook() {
	}
	
	public static boolean moznoPrehratZvuk(String hraca) {
		if (!HOOKED)
			return true;
		
		return PlayerInfo.of(hraca).zvukRp;
	}
	
	public static boolean moznoZobrazitSpravu(String hraca) {
		if (!HOOKED)
			return true;

		return PlayerInfo.of(hraca).spravyTip;
	}
	
	static {
		HOOKED = Common.doesPluginExist("RushCore");
	}
}
