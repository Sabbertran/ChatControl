package kangarko.chatcontrol.group;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;

import kangarko.chatcontrol.config.Settings;
import kangarko.chatcontrol.group.GroupOption.Option;
import kangarko.chatcontrol.utils.Common;

public class Group {

	private final String name;
	private final HashMap<Option, GroupOption> settings;
	
	public Group(String name, GroupOption... settings) {
		this(name, Arrays.asList(settings));
	}
	
	public Group(String name, List<GroupOption> settings) {
		this.name = name;
		
		HashMap<Option, GroupOption> map = new HashMap<>();
		
		for (GroupOption setting : settings)
			map.put(setting.getOption(), setting);
		
		this.settings = map;
	}
	
	public String getName() {
		return name;
	}
	
	public Collection<GroupOption> getSettings() {
		return settings.values();
	}
	
	/**
	 * Call be null.
	 */
	public GroupOption getSetting(Option type) {
		return settings.get(type);
	}
	
	public void addSetting(Option type, Object value) {
		Validate.isTrue(!settings.containsKey(type), "Duplicate setting: " + type + " for: " + name);
		
		settings.put(type, type.create(value));
	}
	
	public static List<Group> loadFor(Player pl) {
		Set<Group> playerGroups = new HashSet<>();
		
		for (Group group : Settings.Groups.LOADED_GROUPS)
			if (Common.hasPerm(pl, "chatcontrol.group." + group.name)) {
				Common.Debug("Adding " + pl.getName() + " to group " + group.name);
				playerGroups.add(group);
			}
	
		return new ArrayList<>(playerGroups);
	}
}