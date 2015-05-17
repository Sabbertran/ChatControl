package kangarko.chatcontrol.group;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

public class GroupSetting {

	private final Type type;
	private final Object value;
	
	public GroupSetting(Type type, Object valueRaw) {
		Object value = null;

		try {
			value = Integer.parseInt(String.valueOf(valueRaw));			
		} catch (Exception ex) {
		}
		try {
			value = Double.parseDouble(String.valueOf(valueRaw));
		} catch (Exception ex) {
		}
		try {
			value = valueRaw.equals("true") || valueRaw.equals("false") ? Boolean.parseBoolean(String.valueOf(valueRaw)) : null;
		} catch (Exception ex) {
		}

		if (value == null)
			value = valueRaw;
		
		Validate.isTrue(value.getClass().isAssignableFrom(type.validValue), value + " has to be " + type.validValue.getSimpleName() + "!");

		this.type = type;
		this.value = value;
	}

	public Type getType() {
		return type;
	}

	public Object getValue() {
		return value;
	}

	public static enum Type {
		MESSAGE_DELAY(Integer.class) {
			@Override
			public String toString() {
				return "Message_Delay";
			}
		},
		COMMAND_DELAY(Integer.class) {
			@Override
			public String toString() {
				return "Command_Delay";
			}
		};

		final Class<?> validValue;

		Type(Class<?> validValue) {
			this.validValue = validValue;
		}

		public static Type parseType(String name) {			
			for (Type type : values())
				if (type.name().equalsIgnoreCase(name) || type.toString().equalsIgnoreCase(name))
					return type;

			throw new RuntimeException("Unknown group setting: '" + name + "', use one of these: " + StringUtils.join(values(), ", "));
		}
	}
}