package kangarko.chatcontrol;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import kangarko.chatcontrol.config.Settings;
import kangarko.chatcontrol.group.Group;
import kangarko.chatcontrol.utils.Common;

public class PlayerCache {

	public String lastMessage = "";
	public String lastCommand = "";

	public String lastSignText = "";

	public long lastMessageTime = 0;
	public long lastCommandTime = 0;

	public Location loginLocation = null;
	public long lastLogin = 0;

	public List<Group> groups = null;

	// Reason for this: I cannot get the player instance from PreLoginEvent.
	public void assignGroups(Player pl) {
		if (!Settings.Groups.ENABLED)
			return;
		
		if (groups == null || Settings.Groups.ALWAYS_CHECK_UPDATES) {
			Common.Debug("&bLoading group for &f" + pl.getName() + "&b ...");
			groups = Group.loadFor(pl);
		}
	}
}
