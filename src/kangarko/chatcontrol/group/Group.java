package kangarko.chatcontrol.group;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.Validate;

import kangarko.chatcontrol.group.GroupSetting.Type;

public class Group {

	private final String name;
	private final HashMap<GroupSetting.Type, GroupSetting> settings;
	
	public Group(String name, GroupSetting... settings) {
		this(name, Arrays.asList(settings));
	}
	
	public Group(String name, List<GroupSetting> settings) {
		this.name = name;
		
		HashMap<GroupSetting.Type, GroupSetting> map = new HashMap<>();
		for (GroupSetting setting : settings)
			map.put(setting.getType(), setting);
		
		this.settings = map;
	}
	
	public String getName() {
		return name;
	}
	
	public Collection<GroupSetting> getSettings() {
		return settings.values();
	}
	
	/**
	 * Call be null.
	 */
	public GroupSetting getSetting(GroupSetting.Type type) {
		return settings.get(type);
	}
	
	public void addSetting(Type type, Object value) {
		Validate.isTrue(!settings.containsKey(type), "Duplicate setting: " + type + " for: " + name);
		
		settings.put(type, new GroupSetting(type, value));
	}
}