package kangarko.chatcontrol.group;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;

import kangarko.chatcontrol.config.Settings;
import kangarko.chatcontrol.utils.Common;

public class GroupManager {
	
	public static List<Group> loadGroupsFor(Player pl) {
		Set<Group> playerGroups = new HashSet<>();
		
		for (Group group : Settings.Groups.LOADED_GROUPS)
			if (Common.hasPerm(pl, "chatcontrol.group." + group.getName()))
				playerGroups.add(group);
	
		return new ArrayList<>(playerGroups);
	}
}