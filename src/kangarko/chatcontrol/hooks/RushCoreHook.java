package kangarko.chatcontrol.hooks;

import kangarko.chatcontrol.utils.Common;
import kangarko.rushcore.misc.PlayerInfo;

// A hook with my custom plugin on my server.
public class RushCoreHook extends Hook {
	
	public RushCoreHook() {
		hooked = Common.doesPluginExist("RushCore");
	}
	
	public boolean moznoPrehratZvuk(String hraca) {
		if (!hooked)
			return true;
		
		return PlayerInfo.of(hraca).zvukRp;
	}
	
	public boolean moznoZobrazitSpravu(String hraca) {
		if (!hooked)
			return true;

		return PlayerInfo.of(hraca).spravyTip;
	}
}
