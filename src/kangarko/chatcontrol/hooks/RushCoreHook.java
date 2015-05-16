package kangarko.chatcontrol.hooks;

import kangarko.rushcore.misc.PlayerInfo;

// A hook with my custom plugin on my server.
public class RushCoreHook {

	public boolean moznoPrehratZvuk(String hraca) {
		return PlayerInfo.of(hraca).zvukRp;
	}
	
	public boolean moznoZobrazitSpravu(String hraca) {
		return PlayerInfo.of(hraca).spravyTip;
	}
}
