package kangarko.chatcontrol;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import kangarko.chatcontrol.group.Group;
import kangarko.chatcontrol.group.GroupManager;

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
		groups = GroupManager.loadGroupsFor(pl);
	}
}
